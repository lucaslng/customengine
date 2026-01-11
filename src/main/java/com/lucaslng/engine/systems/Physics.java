package com.lucaslng.engine.systems;

import java.util.ArrayList;

import org.joml.Vector3f;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.AABBComponent;
import com.lucaslng.engine.components.GroundedComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.RigidBodyComponent;
import com.lucaslng.engine.components.VelocityComponent;

public class Physics {

	private final EntityManager entityManager;

	public static final float G = 9.8f;
	private static final float PENETRATION_ALLOWANCE = 0.005f;
	private static final float PENETRATION_CORRECTION = 1.0f;
	private static final float MAX_VELOCITY = 50f; // Prevent extreme velocities
	
	// debug
	public static boolean DEBUG_COLLISIONS = false;
	public static boolean DEBUG_VELOCITIES = false;
	public static boolean DEBUG_GROUNDED = false;
	public static boolean DEBUG_PENETRATION = false;

	public Physics(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void step(double dt) {
		if (DEBUG_VELOCITIES) {
			System.out.println("=== Physics Step, dt=" + dt + " ===");
		}
		
		// Reset grounded status
		for (int entityId : entityManager.getEntitiesWith(GroundedComponent.class)) {
			GroundedComponent grounded = entityManager.getComponent(entityId, GroundedComponent.class);
			grounded.isGrounded = false;
		}

		// Apply gravity
		for (int entityId : entityManager.getEntitiesWith(RigidBodyComponent.class, VelocityComponent.class)) {
			RigidBodyComponent rigidBody = entityManager.getComponent(entityId, RigidBodyComponent.class);
			if (rigidBody.isStatic())
				continue;
			Vector3f velocity = entityManager.getComponent(entityId, VelocityComponent.class).velocity();
			
			if (DEBUG_VELOCITIES) {
				System.out.println("Entity " + entityId + " velocity before gravity: " + velocity);
			}
			
			velocity.y -= G * rigidBody.gravityScale() * dt;
			
			// Clamp velocity to prevent tunneling
			float speed = velocity.length();
			if (speed > MAX_VELOCITY) {
				if (DEBUG_VELOCITIES) {
					System.out.println("Entity " + entityId + " velocity clamped from " + speed + " to " + MAX_VELOCITY);
				}
				velocity.normalize().mul(MAX_VELOCITY);
			}
			
			if (DEBUG_VELOCITIES) {
				System.out.println("Entity " + entityId + " velocity after gravity: " + velocity);
			}
		}

		// Apply velocity with swept collision detection
		for (int entityId : entityManager.getEntitiesWith(PositionComponent.class, VelocityComponent.class)) {
			Vector3f position = entityManager.getComponent(entityId, PositionComponent.class).position();
			Vector3f velocity = entityManager.getComponent(entityId, VelocityComponent.class).velocity();
			
			if (DEBUG_VELOCITIES) {
				System.out.println("Entity " + entityId + " position before: " + position);
			}
			
			Vector3f startPos = new Vector3f(position);
			Vector3f change = new Vector3f();
			velocity.mul((float) dt, change);
			position.add(change);
			
			// Swept AABB collision detection for fast-moving objects
			if (entityManager.hasComponent(entityId, RigidBodyComponent.class) && 
				entityManager.hasComponent(entityId, AABBComponent.class)) {
				RigidBodyComponent rb = entityManager.getComponent(entityId, RigidBodyComponent.class);
				
				if (!rb.isStatic()) {
					Vector3f halfExtents = entityManager.getComponent(entityId, AABBComponent.class).halfExtents();
					
					// Check all static colliders along the movement path
					for (int staticId : entityManager.getEntitiesWith(PositionComponent.class, RigidBodyComponent.class, AABBComponent.class)) {
						RigidBodyComponent staticRb = entityManager.getComponent(staticId, RigidBodyComponent.class);
						if (!staticRb.isStatic() || staticId == entityId)
							continue;
						
						Vector3f staticPos = entityManager.getComponent(staticId, PositionComponent.class).position();
						Vector3f staticExtents = entityManager.getComponent(staticId, AABBComponent.class).halfExtents();
						
						// Perform swept collision
						float collisionTime = sweptAABB(startPos, position, halfExtents, staticPos, staticExtents);
						
						if (DEBUG_VELOCITIES) {
							System.out.println("Swept AABB check: entity " + entityId + " vs " + staticId + ", collisionTime=" + collisionTime);
						}
						
						if (collisionTime >= 0f && collisionTime < 1f) {
							// Collision detected! Move to collision point with small offset
							Vector3f collisionPos = new Vector3f(startPos).lerp(position, collisionTime);
							
							// Determine collision normal
							Vector3f normal = getCollisionNormal(collisionPos, halfExtents, staticPos, staticExtents);
							if (normal != null) {
								// Add small offset in opposite direction of normal to prevent getting stuck
								float epsilon = 0.001f;
								collisionPos.sub(new Vector3f(normal).mul(epsilon));
								position.set(collisionPos);
								
								float velDotNormal = velocity.dot(normal);
								
								if (DEBUG_VELOCITIES) {
									System.out.println("  Normal=" + normal + ", velDotNormal=" + velDotNormal + ", collisionTime=" + collisionTime);
								}
								
								// Normal points from moving object toward static object
								// If velDotNormal > 0, we're moving toward the surface
								if (velDotNormal > 0.01f) {
									velocity.sub(new Vector3f(normal).mul(velDotNormal));
									
									if (DEBUG_VELOCITIES) {
										System.out.println("Entity " + entityId + " swept collision with " + staticId + ", velocity corrected");
									}
								}
								
								// Set grounded if we're resting on floor (even if not actively moving into it)
								// This handles the case where player is standing still on ground
								if (normal.y < -0.7f && entityManager.hasComponent(entityId, GroundedComponent.class)) {
									GroundedComponent grounded = entityManager.getComponent(entityId, GroundedComponent.class);
									grounded.isGrounded = true;
									if (DEBUG_GROUNDED) {
										System.out.println("Entity " + entityId + " is GROUNDED via swept collision (normal=" + normal + ")");
									}
								} else if (DEBUG_GROUNDED && entityManager.hasComponent(entityId, GroundedComponent.class)) {
									System.out.println("Entity " + entityId + " NOT grounded: normal.y=" + normal.y + " (need < -0.7)");
								}
							}
						}
					}
				}
			}
			
			if (DEBUG_VELOCITIES) {
				System.out.println("Entity " + entityId + " position after: " + position + " (moved by " + change + ")");
			}
		}

		// Iterative collision resolution (multiple passes for stability)
		int iterations = 8;
		for (int iter = 0; iter < iterations; iter++) {
			// Check collisions
			ArrayList<Integer[]> contactIds = new ArrayList<>();
			ArrayList<Contact> contactData = new ArrayList<>();
			
			if (DEBUG_COLLISIONS && iter == 0) {
				System.out.println("--- Collision Detection (iteration " + iter + ") ---");
			}
			
			// Get all entities with collision components
			ArrayList<Integer> colliders = new ArrayList<>(entityManager.getEntitiesWith(
				PositionComponent.class, RigidBodyComponent.class, AABBComponent.class));
			
			if (DEBUG_COLLISIONS && iter == 0) {
				System.out.println("Total colliders: " + colliders.size());
				for (int id : colliders) {
					Vector3f pos = entityManager.getComponent(id, PositionComponent.class).position();
					Vector3f ext = entityManager.getComponent(id, AABBComponent.class).halfExtents();
					RigidBodyComponent rb = entityManager.getComponent(id, RigidBodyComponent.class);
					System.out.println("  Entity " + id + ": pos=" + pos + ", halfExtents=" + ext + ", static=" + rb.isStatic());
				}
			}
			
			// Check all pairs
			for (int i = 0; i < colliders.size(); i++) {
				int ida = colliders.get(i);
				RigidBodyComponent ra = entityManager.getComponent(ida, RigidBodyComponent.class);
				Vector3f pa = entityManager.getComponent(ida, PositionComponent.class).position();
				Vector3f halfExtentsA = entityManager.getComponent(ida, AABBComponent.class).halfExtents();
				
				for (int j = i + 1; j < colliders.size(); j++) {
					int idb = colliders.get(j);
					RigidBodyComponent rb = entityManager.getComponent(idb, RigidBodyComponent.class);
					
					// Skip if both are static
					if (ra.isStatic() && rb.isStatic())
						continue;
					
					Vector3f pb = entityManager.getComponent(idb, PositionComponent.class).position();
					Vector3f halfExtentsB = entityManager.getComponent(idb, AABBComponent.class).halfExtents();
					
					Contact contact = checkCollision(pa, halfExtentsA, pb, halfExtentsB);
					if (contact == null)
						continue;

					if (DEBUG_COLLISIONS && iter == 0) {
						System.out.println("COLLISION: Entity " + ida + " <-> Entity " + idb);
						System.out.println("  Penetration: " + contact.penetration() + ", Normal: " + contact.c());
					}

					// Store which entity is A and which is B for grounded check
					contactIds.add(new Integer[] { ida, idb });
					contactData.add(contact);
					
					// set grounded (only on first iteration)
					if (iter == 0) {
						// The normal points from A to B
						// If normal.y is positive, B is above A, so A is standing on B
						// If normal.y is negative, A is above B, so B is standing on A
						if (contact.c().y > 0.7f && !ra.isStatic() && entityManager.hasComponent(ida, GroundedComponent.class)) {
							GroundedComponent grounded = entityManager.getComponent(ida, GroundedComponent.class);
							grounded.isGrounded = true;
							if (DEBUG_GROUNDED) {
								System.out.println("Entity " + ida + " is GROUNDED (normal.y=" + contact.c().y + ")");
							}
						}
						if (contact.c().y < -0.7f && !rb.isStatic() && entityManager.hasComponent(idb, GroundedComponent.class)) {
							GroundedComponent grounded = entityManager.getComponent(idb, GroundedComponent.class);
							grounded.isGrounded = true;
							if (DEBUG_GROUNDED) {
								System.out.println("Entity " + idb + " is GROUNDED (normal.y=" + contact.c().y + ")");
							}
						}
					}
				}
			}
			
			if (DEBUG_COLLISIONS && iter == 0) {
				System.out.println("Total contacts found: " + contactIds.size());
			}

			// resolve contacts (only on first iteration for impulse-based resolution)
			if (iter == 0) {
				for (int i = 0; i < contactIds.size(); i++) {
					Vector3f c = contactData.get(i).c;
					int ida = contactIds.get(i)[0];
					int idb = contactIds.get(i)[1];
					RigidBodyComponent ra = entityManager.getComponent(ida, RigidBodyComponent.class);
					Vector3f va = ra.isStatic() ? new Vector3f()
							: entityManager.getComponent(ida, VelocityComponent.class).velocity();
					RigidBodyComponent rb = entityManager.getComponent(idb, RigidBodyComponent.class);
					Vector3f vb = rb.isStatic() ? new Vector3f()
							: entityManager.getComponent(idb, VelocityComponent.class).velocity();
					resolveContact(ra, rb, va, vb, c);
				}
			}

			// correct positions (every iteration for stability)
			for (int i = 0; i < contactIds.size(); i++) {
				Contact contact = contactData.get(i);
				int ida = contactIds.get(i)[0];
				int idb = contactIds.get(i)[1];
				RigidBodyComponent ra = entityManager.getComponent(ida, RigidBodyComponent.class);
				Vector3f pa = entityManager.getComponent(ida, PositionComponent.class).position();
				RigidBodyComponent rb = entityManager.getComponent(idb, RigidBodyComponent.class);
				Vector3f pb = entityManager.getComponent(idb, PositionComponent.class).position();
				
				if (DEBUG_PENETRATION && iter == 0) {
					System.out.println("Correcting penetration between " + ida + " and " + idb);
					System.out.println("  Before: A=" + new Vector3f(pa) + ", B=" + new Vector3f(pb));
				}
				
				correctPosition(ra, rb, pa, pb, contact);
				
				if (DEBUG_PENETRATION && iter == 0) {
					System.out.println("  After: A=" + pa + ", B=" + pb);
				}
			}
		}
	}

	private static Contact checkCollision(Vector3f ca, Vector3f ha, Vector3f cb, Vector3f hb) {
		// Vector from center of A to center of B
		Vector3f d = new Vector3f();
		cb.sub(ca, d);

		// Calculate overlap on each axis
		float ox = (ha.x + hb.x) - Math.abs(d.x);
		float oy = (ha.y + hb.y) - Math.abs(d.y);
		float oz = (ha.z + hb.z) - Math.abs(d.z);

		if (DEBUG_COLLISIONS) {
			System.out.println("  Overlap check: ox=" + ox + ", oy=" + oy + ", oz=" + oz);
			System.out.println("  Distance: " + d);
		}

		// No collision if any axis has no overlap
		if (ox <= 0f || oy <= 0f || oz <= 0f) {
			if (DEBUG_COLLISIONS) {
				System.out.println("  No collision - no overlap on all axes");
			}
			return null;
		}

		// Find axis of minimum penetration
		// Normal points from A to B
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

		if (DEBUG_COLLISIONS) {
			System.out.println("  Chosen axis: " + (normal.x != 0 ? "X" : (normal.y != 0 ? "Y" : "Z")) + ", penetration=" + penetration);
		}

		return new Contact(penetration, normal);
	}

	// Swept AABB collision detection
	private static float sweptAABB(Vector3f startPos, Vector3f endPos, Vector3f movingExtents, Vector3f staticPos, Vector3f staticExtents) {
		Vector3f velocity = new Vector3f(endPos).sub(startPos);
		
		// Early out if not moving
		if (velocity.lengthSquared() < 0.0001f)
			return -1f;
		
		// Expand the static AABB by the moving AABB's extents
		Vector3f expandedMin = new Vector3f(staticPos).sub(staticExtents).sub(movingExtents);
		Vector3f expandedMax = new Vector3f(staticPos).add(staticExtents).add(movingExtents);
		
		// Ray-AABB intersection
		float tmin = 0f;
		float tmax = 1f;
		
		for (int i = 0; i < 3; i++) {
			float v = velocity.get(i);
			float p = startPos.get(i);
			float min = expandedMin.get(i);
			float max = expandedMax.get(i);
			
			if (Math.abs(v) < 0.0001f) {
				// Ray is parallel to slab
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
	
	// Get the collision normal after a swept collision
	private static Vector3f getCollisionNormal(Vector3f movingPos, Vector3f movingExtents, Vector3f staticPos, Vector3f staticExtents) {
		Vector3f d = new Vector3f(staticPos).sub(movingPos);
		
		float ox = (movingExtents.x + staticExtents.x) - Math.abs(d.x);
		float oy = (movingExtents.y + staticExtents.y) - Math.abs(d.y);
		float oz = (movingExtents.z + staticExtents.z) - Math.abs(d.z);
		
		Vector3f normal = new Vector3f();
		
		// Find the axis with minimum penetration (that's the collision normal)
		if (ox < oy && ox < oz) {
			normal.x = d.x >= 0 ? 1f : -1f;
		} else if (oy < oz) {
			normal.y = d.y >= 0 ? 1f : -1f;
		} else {
			normal.z = d.z >= 0 ? 1f : -1f;
		}
		
		return normal;
	}

	private static void resolveContact(RigidBodyComponent ra, RigidBodyComponent rb,
			Vector3f va, Vector3f vb, Vector3f normal) {
		Vector3f rv = new Vector3f();
		va.sub(vb, rv); // relative velocity
		float rvAlongNormal = rv.dot(normal);
		
		// Objects are separating, don't apply impulse
		if (rvAlongNormal > 0)
			return;

		float e = Math.min(ra.restitution(), rb.restitution());
		float invMassSum = ra.invMass() + rb.invMass();
		if (invMassSum == 0f)
			return;

		// Calculate impulse scalar
		float j = -(1 + e) * rvAlongNormal;
		j /= invMassSum;
		
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
		va.sub(vb, rv); // recalculate relative velocity
		rvAlongNormal = rv.dot(normal);
		
		// Tangent is perpendicular to normal
		Vector3f tangent = new Vector3f(rv).sub(new Vector3f(normal).mul(rvAlongNormal));
		
		float tLen = tangent.length();
		if (tLen > 0.0001f) {
			tangent.normalize();
			
			float jt = -rv.dot(tangent);
			jt /= invMassSum;
			
			// Coulomb's law: friction can't exceed normal force * friction coefficient
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
			
		// Penetration correction with slop
		float correctionMagnitude = Math.max(contact.penetration() - PENETRATION_ALLOWANCE, 0f) / invMassSum * PENETRATION_CORRECTION;
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