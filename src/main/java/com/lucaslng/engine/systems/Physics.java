package com.lucaslng.engine.systems;

import java.util.ArrayList;

import org.joml.Vector3f;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.AABBComponent;
import com.lucaslng.engine.components.DisabledComponent;
import com.lucaslng.engine.components.GroundedComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.RigidBodyComponent;
import com.lucaslng.engine.components.VelocityComponent;

public class Physics {

	private final EntityManager entityManager;

	public static final float G = 60f;
	private static final float PENETRATION_ALLOWANCE = 0.005f;
	private static final float PENETRATION_CORRECTION = 1.0f;
	private static final float MAX_VELOCITY = 50f;

	public static boolean DEBUG_COLLISIONS = false;
	public static boolean DEBUG_VELOCITIES = false;
	public static boolean DEBUG_GROUNDED = false;
	public static boolean DEBUG_PENETRATION = false;

	public Physics(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void step(double dt) {
		if (DEBUG_VELOCITIES)
			System.out.println("=== Physics Step, dt=" + dt + " ===");

		// Reset grounded status for all entities, it gets recalculated every frame
		// Grounded status decides whether an entity is allowed to jump
		for (int entityId : entityManager.getEntitiesWith(GroundedComponent.class)) {
			if (entityManager.hasComponent(entityId, DisabledComponent.class))
				continue;
			GroundedComponent grounded = entityManager.getComponent(entityId, GroundedComponent.class);
			grounded.isGrounded = false;
		}

		// Apply gravity
		for (int entityId : entityManager.getEntitiesWith(RigidBodyComponent.class, VelocityComponent.class)) {
			if (entityManager.hasComponent(entityId, DisabledComponent.class))
				continue;
			RigidBodyComponent rigidBody = entityManager.getComponent(entityId, RigidBodyComponent.class);
			if (rigidBody.isStatic()) // static entities dont get gravity
				continue;
			Vector3f velocity = entityManager.getComponent(entityId, VelocityComponent.class).velocity();

			if (DEBUG_VELOCITIES)
				System.out.println("Entity " + entityId + " velocity before gravity: " + velocity);

			velocity.y -= G * rigidBody.gravityScale() * dt;

			// Clamp velocity to prevent tunneling (objects moving through walls because
			// they have too much velocity)
			float speed = velocity.length();
			if (speed > MAX_VELOCITY) {
				if (DEBUG_VELOCITIES)
					System.out.println("Entity " + entityId + " velocity clamped from " + speed + " to " + MAX_VELOCITY);

				velocity.normalize().mul(MAX_VELOCITY);
			}

			if (DEBUG_VELOCITIES)
				System.out.println("Entity " + entityId + " velocity after gravity: " + velocity);
		}

		// Apply velocity with swept collision detection per-axis
		for (int entityId : entityManager.getEntitiesWith(PositionComponent.class, VelocityComponent.class)) {
			if (entityManager.hasComponent(entityId, DisabledComponent.class))
				continue;
			Vector3f position = entityManager.getComponent(entityId, PositionComponent.class).position();
			Vector3f velocity = entityManager.getComponent(entityId, VelocityComponent.class).velocity();

			// If e doesn't have collision related components, just move normally without checking for collision
			if (!entityManager.hasComponent(entityId, RigidBodyComponent.class)
					|| !entityManager.hasComponent(entityId, AABBComponent.class)) {
				Vector3f change = new Vector3f();
				velocity.mul((float) dt, change);
				position.add(change);
				continue;
			}

			RigidBodyComponent rb = entityManager.getComponent(entityId, RigidBodyComponent.class);
			if (rb.isStatic())
				continue;
			Vector3f halfExtents = entityManager.getComponent(entityId, AABBComponent.class).halfExtents();
			
			boolean hitFloor = false;

			// regular movement before collisions
			Vector3f movement = new Vector3f(velocity).mul((float) dt);

			// Move on each axis separately to prevent one axis from blocking another
			// Order: X -> Y -> Z
			for (int axis = 0; axis < 3; axis++) {
				if (Math.abs(movement.get(axis)) < 0.0001f) // ignore tiny movement
					continue;

				// calculate endPos of moving only on this axis
				Vector3f startPos = new Vector3f(position);
				Vector3f axisMovement = new Vector3f();
				axisMovement.setComponent(axis, movement.get(axis));
				Vector3f endPos = new Vector3f(position).add(axisMovement);

				boolean collided = false;

				// Check all colliders
				for (int otherId : entityManager.getEntitiesWith(PositionComponent.class, RigidBodyComponent.class,
						AABBComponent.class)) {
					if (entityManager.hasComponent(entityId, DisabledComponent.class))
						continue;
					if (otherId == entityId)
						continue;
					Vector3f otherPos = entityManager.getComponent(otherId, PositionComponent.class).position();
					Vector3f otherExtents = entityManager.getComponent(otherId, AABBComponent.class).halfExtents();

					float collisionTime = sweptAABB(startPos, endPos, halfExtents, otherPos, otherExtents);

					if (collisionTime >= 0f && collisionTime < 1f) {
						// Collision on this axis
						Vector3f collisionPos = new Vector3f(startPos).lerp(endPos, collisionTime);
						Vector3f normal = getCollisionNormal(collisionPos, halfExtents, otherPos, otherExtents);

						if (normal != null && Math.abs(normal.get(axis)) > 0.5f) {
							// Push back slightly from collision point
							float epsilon = 0.001f;
							collisionPos.sub(new Vector3f(normal).mul(epsilon));
							position.setComponent(axis, collisionPos.get(axis));

							// Stop velocity on this axis if moving into surface
							float velOnAxis = velocity.get(axis);
							float normalOnAxis = normal.get(axis);
							if (velOnAxis * normalOnAxis > 0.01f) {
								velocity.setComponent(axis, 0f);
							}

							// Check if we hit a floor (Y axis, normal pointing down)
							if (axis == 1 && normal.y < -0.7f) {
								hitFloor = true;
							}

							collided = true;
							break;
						}
					}
				}

				if (!collided) {
					position.add(axisMovement);
				}
			}

			// Set grounded if we hit a floor during swept collision
			if (hitFloor && entityManager.hasComponent(entityId, GroundedComponent.class)) {
				GroundedComponent grounded = entityManager.getComponent(entityId, GroundedComponent.class);
				grounded.isGrounded = true;
				if (DEBUG_GROUNDED) {
					System.out.println("Entity " + entityId + " is GROUNDED via swept collision");
				}
			}
		}

		// Iterative collision resolution
		int iterations = 6;
		for (int iter = 0; iter < iterations; iter++) {
			ArrayList<Integer[]> contactIds = new ArrayList<>();
			ArrayList<Contact> contactData = new ArrayList<>();

			// Get all entities with collision components
			ArrayList<Integer> colliders = new ArrayList<>(entityManager.getEntitiesWith(
					PositionComponent.class, RigidBodyComponent.class, AABBComponent.class));

			// Check all pairs
			for (int i = 0; i < colliders.size(); i++) {
				int ida = colliders.get(i);
				if (entityManager.hasComponent(ida, DisabledComponent.class))
					continue;
				RigidBodyComponent ra = entityManager.getComponent(ida, RigidBodyComponent.class);
				Vector3f pa = entityManager.getComponent(ida, PositionComponent.class).position();
				Vector3f halfExtentsA = entityManager.getComponent(ida, AABBComponent.class).halfExtents();

				for (int j = i + 1; j < colliders.size(); j++) {
					int idb = colliders.get(j);
					if (entityManager.hasComponent(idb, DisabledComponent.class))
						continue;
					RigidBodyComponent rb = entityManager.getComponent(idb, RigidBodyComponent.class);

					// Skip if both are static
					if (ra.isStatic() && rb.isStatic())
						continue;

					Vector3f pb = entityManager.getComponent(idb, PositionComponent.class).position();
					Vector3f halfExtentsB = entityManager.getComponent(idb, AABBComponent.class).halfExtents();

					Contact contact = checkCollision(pa, halfExtentsA, pb, halfExtentsB);
					if (contact == null)
						continue;

					contactIds.add(new Integer[] { ida, idb });
					contactData.add(contact);

					// Set grounded (only on first iteration)
					if (iter == 0) {
						// Normal points from A to B
						// If normal.y > 0.7, B is above A, so B is grounded on A
						// If normal.y < -0.7, A is above B, so A is grounded on B
						if (contact.c().y > 0.7f && !rb.isStatic() && entityManager.hasComponent(idb, GroundedComponent.class)) {
							boolean canGround = true;
							Vector3f va = null;
							Vector3f vb = null;
							if (entityManager.hasComponent(idb, VelocityComponent.class)) {
								vb = entityManager.getComponent(idb, VelocityComponent.class).velocity();
								// Don't ground if the lower entity is moving upward (jumping into the upper
								// one).
								canGround = vb.y <= 0.1f;
							}
							if (canGround && entityManager.hasComponent(ida, VelocityComponent.class)) {
								va = entityManager.getComponent(ida, VelocityComponent.class).velocity();
								// Only ground if the upper entity isn't moving upward.
								canGround = va.y <= 0.1f;
							}

							if (!canGround) {
								continue;
							}

							GroundedComponent grounded = entityManager.getComponent(idb, GroundedComponent.class);
							grounded.isGrounded = true;

							// Snap vertical velocity to the supporting entity to prevent bounce.
							if (entityManager.hasComponent(idb, VelocityComponent.class)) {
								if (vb == null) {
									vb = entityManager.getComponent(idb, VelocityComponent.class).velocity();
								}
								if (ra.isStatic()) {
									if (vb.y < 0f) {
										vb.y = 0f;
									}
								} else if (entityManager.hasComponent(ida, VelocityComponent.class)) {
									if (va == null) {
										va = entityManager.getComponent(ida, VelocityComponent.class).velocity();
									}
									// Match the lower entity's vertical velocity when grounded.
									vb.y = va.y;
								}
							}

							if (DEBUG_GROUNDED) {
								System.out.println("Entity " + idb + " is GROUNDED on entity " + ida);
							}
						}
						if (contact.c().y < -0.7f && !ra.isStatic() && entityManager.hasComponent(ida, GroundedComponent.class)) {
							boolean canGround = true;
							Vector3f va = null;
							Vector3f vb = null;
							if (entityManager.hasComponent(ida, VelocityComponent.class)) {
								va = entityManager.getComponent(ida, VelocityComponent.class).velocity();
								// Only ground if the upper entity isn't moving upward.
								canGround = va.y <= 0.1f;
							}
							if (canGround && entityManager.hasComponent(idb, VelocityComponent.class)) {
								vb = entityManager.getComponent(idb, VelocityComponent.class).velocity();
								// Don't ground if the lower entity is moving upward (jumping into the upper
								// one).
								canGround = vb.y <= 0.1f;
							}

							if (!canGround) {
								continue;
							}

							GroundedComponent grounded = entityManager.getComponent(ida, GroundedComponent.class);
							grounded.isGrounded = true;

							// Snap vertical velocity to the supporting entity to prevent bounce.
							if (entityManager.hasComponent(ida, VelocityComponent.class)) {
								if (va == null) {
									va = entityManager.getComponent(ida, VelocityComponent.class).velocity();
								}
								if (rb.isStatic()) {
									if (va.y < 0f) {
										va.y = 0f;
									}
								} else if (entityManager.hasComponent(idb, VelocityComponent.class)) {
									if (vb == null) {
										vb = entityManager.getComponent(idb, VelocityComponent.class).velocity();
									}
									// Match the lower entity's vertical velocity when grounded.
									va.y = vb.y;
								}
							}

							if (DEBUG_GROUNDED) {
								System.out.println("Entity " + ida + " is GROUNDED on entity " + idb);
							}
						}
					}
				}
			}

			// Resolve contacts (only on first iteration)
			// Only apply impulses for dynamic-vs-dynamic collisions where both are moving
			// Static collisions are already handled by swept collision
			if (iter == 0) {
				for (int i = 0; i < contactIds.size(); i++) {
					Vector3f c = contactData.get(i).c;
					int ida = contactIds.get(i)[0];
					int idb = contactIds.get(i)[1];
					RigidBodyComponent ra = entityManager.getComponent(ida, RigidBodyComponent.class);
					RigidBodyComponent rb = entityManager.getComponent(idb, RigidBodyComponent.class);

					// Skip impulse resolution if either object is static
					// Swept collision already handles static collisions
					if (ra.isStatic() || rb.isStatic())
						continue;

					Vector3f va = entityManager.getComponent(ida, VelocityComponent.class).velocity();
					Vector3f vb = entityManager.getComponent(idb, VelocityComponent.class).velocity();

					resolveContact(ra, rb, va, vb, c);
				}
			}

			// Correct positions (every iteration)
			for (int i = 0; i < contactIds.size(); i++) {
				Contact contact = contactData.get(i);
				int ida = contactIds.get(i)[0];
				int idb = contactIds.get(i)[1];
				RigidBodyComponent ra = entityManager.getComponent(ida, RigidBodyComponent.class);
				Vector3f pa = entityManager.getComponent(ida, PositionComponent.class).position();
				RigidBodyComponent rb = entityManager.getComponent(idb, RigidBodyComponent.class);
				Vector3f pb = entityManager.getComponent(idb, PositionComponent.class).position();
				correctPosition(ra, rb, pa, pb, contact);
			}
		}
	}

	private static Contact checkCollision(Vector3f ca, Vector3f ha, Vector3f cb, Vector3f hb) {
		Vector3f d = new Vector3f();
		cb.sub(ca, d);

		float ox = (ha.x + hb.x) - Math.abs(d.x);
		float oy = (ha.y + hb.y) - Math.abs(d.y);
		float oz = (ha.z + hb.z) - Math.abs(d.z);

		if (ox <= 0f || oy <= 0f || oz <= 0f)
			return null;

		Vector3f normal = new Vector3f();
		float penetration;

		if (ox < oy && ox < oz) {
			penetration = ox;
			normal.x = d.x >= 0 ? 1f : -1f;
		} else if (oy < oz) {
			penetration = oy;
			normal.y = d.y >= 0 ? 1f : -1f;
		} else {
			penetration = oz;
			normal.z = d.z >= 0 ? 1f : -1f;
		}

		return new Contact(penetration, normal);
	}

	// Calculates collision time
	private static float sweptAABB(Vector3f startPos, Vector3f endPos, Vector3f movingExtents, Vector3f otherPos,
			Vector3f otherExtents) {
		Vector3f velocity = new Vector3f(endPos).sub(startPos);
		if (velocity.lengthSquared() < 0.0001f)
			return -1f; // no collision

		Vector3f expandedMin = new Vector3f(otherPos).sub(otherExtents).sub(movingExtents);
		Vector3f expandedMax = new Vector3f(otherPos).add(otherExtents).add(movingExtents);

		float tmin = 0f;
		float tmax = 1f;

		// for each axis
		for (int i = 0; i < 3; i++) {
			float v = velocity.get(i);
			float p = startPos.get(i);
			float min = expandedMin.get(i);
			float max = expandedMax.get(i);

			if (Math.abs(v) < 0.0001f) {
				if (p < min || p > max)
					return -1f;
			} else {
				float t1 = (min - p) / v;
				float t2 = (max - p) / v;

				if (t1 > t2) {
					float temp = t1;
					t1 = t2;
					t2 = temp;
				}

				tmin = Math.max(tmin, t1);
				tmax = Math.min(tmax, t2);

				if (tmin > tmax)
					return -1f;
			}
		}

		return tmin;
	}

	private static Vector3f getCollisionNormal(Vector3f movingPos, Vector3f movingExtents, Vector3f staticPos,
			Vector3f staticExtents) {
		Vector3f d = new Vector3f(staticPos).sub(movingPos);

		float ox = (movingExtents.x + staticExtents.x) - Math.abs(d.x);
		float oy = (movingExtents.y + staticExtents.y) - Math.abs(d.y);
		float oz = (movingExtents.z + staticExtents.z) - Math.abs(d.z);

		Vector3f normal = new Vector3f();

		if (ox < oy && ox < oz) {
			normal.x = d.x >= 0 ? 1f : -1f;
		} else if (oy < oz) {
			normal.y = d.y >= 0 ? 1f : -1f;
		} else {
			normal.z = d.z >= 0 ? 1f : -1f;
		}

		return normal;
	}

	private static void resolveContact(RigidBodyComponent ra, RigidBodyComponent rb, Vector3f va, Vector3f vb,
			Vector3f normal) {
		Vector3f rv = new Vector3f();
		va.sub(vb, rv);
		float rvAlongNormal = rv.dot(normal);

		// Objects are separating or barely touching, don't apply impulse
		// Use a larger threshold to prevent bouncing
		if (rvAlongNormal > -1.0f)
			return;

		// Use minimal restitution for player-player collisions to prevent bouncing
		float e = Math.min(ra.restitution(), rb.restitution()) * 0.1f;
		float invMassSum = ra.invMass() + rb.invMass();
		if (invMassSum == 0f)
			return;

		float j = -(1 + e) * rvAlongNormal;
		j /= invMassSum;

		// Clamp impulse much more aggressively
		float maxImpulse = 20f;
		j = Math.min(j, maxImpulse);

		Vector3f impulse = new Vector3f(normal).mul(j);

		if (!ra.isStatic()) {
			Vector3f change = new Vector3f(impulse).mul(ra.invMass());
			va.sub(change);
		}

		if (!rb.isStatic()) {
			Vector3f change = new Vector3f(impulse).mul(rb.invMass());
			vb.add(change);
		}

		// Friction
		va.sub(vb, rv);
		rvAlongNormal = rv.dot(normal);

		Vector3f tangent = new Vector3f(rv).sub(new Vector3f(normal).mul(rvAlongNormal));

		float tLen = tangent.length();
		if (tLen > 0.0001f) {
			tangent.normalize();

			float jt = -rv.dot(tangent);
			jt /= invMassSum;

			float mu = (float) Math.sqrt(ra.friction() * rb.friction());
			float maxFriction = Math.abs(j * mu);
			jt = Math.max(-maxFriction, Math.min(maxFriction, jt));

			Vector3f frictionImpulse = new Vector3f(tangent).mul(jt);

			if (!ra.isStatic()) {
				Vector3f change = new Vector3f(frictionImpulse).mul(ra.invMass());
				va.sub(change);
			}
			if (!rb.isStatic()) {
				Vector3f change = new Vector3f(frictionImpulse).mul(rb.invMass());
				vb.add(change);
			}
		}
	}

	private static void correctPosition(RigidBodyComponent ra, RigidBodyComponent rb, Vector3f pa, Vector3f pb,
			Contact contact) {
		float invMassSum = ra.invMass() + rb.invMass();
		if (invMassSum == 0f)
			return;

		float correctionMagnitude = Math.max(contact.penetration() - PENETRATION_ALLOWANCE, 0f) / invMassSum
				* PENETRATION_CORRECTION;
		Vector3f correction = new Vector3f(contact.c()).mul(correctionMagnitude);

		if (!ra.isStatic()) {
			Vector3f corrA = new Vector3f(correction).mul(ra.invMass());
			pa.sub(corrA);
		}
		if (!rb.isStatic()) {
			Vector3f corrB = new Vector3f(correction).mul(rb.invMass());
			pb.add(corrB);
		}
	}

	private record Contact(float penetration, Vector3f c) {
	}
}
