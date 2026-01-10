package com.lucaslng.engine.systems;

import java.util.ArrayList;

import org.joml.Vector3f;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.*;

public class Physics {

	private final EntityManager entityManager;

	public static final float G = 9.8f;

	public Physics(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void step(double dt) {
		
		// Reset grounded status
		for (int entityId : entityManager.getEntitiesWith(GroundedComponent.class)) {
			GroundedComponent grounded = entityManager.getComponent(entityId, GroundedComponent.class);
			grounded.isGrounded = false;
		}

		// Apply gravity
		for (int entityId : entityManager.getEntitiesWith(RigidBodyComponent.class, VelocityComponent.class)) {
			RigidBodyComponent rigidBody = entityManager.getComponent(entityId, RigidBodyComponent.class);
			if (rigidBody.isStatic()) // Static object, no gravity needs to be applied
				continue;
			Vector3f velocity = entityManager.getComponent(entityId, VelocityComponent.class).velocity();
			velocity.y -= G * rigidBody.gravityScale() * dt;
		}

		// Apply velocity
		for (int entityId : entityManager.getEntitiesWith(PositionComponent.class, VelocityComponent.class)) {
			Vector3f position = entityManager.getComponent(entityId, PositionComponent.class).position();
			Vector3f velocity = entityManager.getComponent(entityId, VelocityComponent.class).velocity();
			Vector3f change = new Vector3f();
			velocity.mul((float) dt, change);
			position.add(change);
		}

		// Check collisions
		ArrayList<Integer[]> contactIds = new ArrayList<>();
		ArrayList<Contact> contactData = new ArrayList<>();
		for (int ida : entityManager.getEntitiesWith(PositionComponent.class, RigidBodyComponent.class,
				AABBComponent.class)) {
			RigidBodyComponent ra = entityManager.getComponent(ida, RigidBodyComponent.class);
			if (ra.isStatic())
				continue;
			Vector3f pa = entityManager.getComponent(ida, PositionComponent.class).position();
			Vector3f halfExtentsA = entityManager.getComponent(ida, AABBComponent.class).halfExtents();
			for (int idb : entityManager.getEntitiesWith(PositionComponent.class, RigidBodyComponent.class,
					AABBComponent.class)) {
				RigidBodyComponent rb = entityManager.getComponent(idb, RigidBodyComponent.class);
				if (!rb.isStatic())
					continue;
				Vector3f pb = entityManager.getComponent(idb, PositionComponent.class).position();
				Vector3f halfExtentsB = entityManager.getComponent(idb, AABBComponent.class).halfExtents();
				Contact contact = checkCollision(pa, halfExtentsA, pb, halfExtentsB);
				if (contact == null)
					continue;

				// set grounded
				if (contact.c().y < -0.001f && entityManager.hasComponent(ida, GroundedComponent.class)) {
					GroundedComponent grounded = entityManager.getComponent(ida, GroundedComponent.class);
          grounded.isGrounded = true;
        
				}
				contactIds.add(new Integer[] { ida, idb });
				contactData.add(contact);
			}
		}

		// resolve contacts
		for (int i = 0; i < contactIds.size(); i++) {
			Vector3f c = contactData.get(i).c;
			int ida = contactIds.get(i)[0];
			int idb = contactIds.get(i)[1];
			RigidBodyComponent ra = entityManager.getComponent(ida, RigidBodyComponent.class);
			Vector3f pa = entityManager.getComponent(ida, PositionComponent.class).position();
			Vector3f va = ra.isStatic() ? new Vector3f()
					: entityManager.getComponent(ida, VelocityComponent.class).velocity();
			RigidBodyComponent rb = entityManager.getComponent(idb, RigidBodyComponent.class);
			Vector3f pb = entityManager.getComponent(idb, PositionComponent.class).position();
			Vector3f vb = rb.isStatic() ? new Vector3f()
					: entityManager.getComponent(idb, VelocityComponent.class).velocity();
			resolveContact(ra, rb, pa, pb, va, vb, c);
		}

		// correct positions
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

	private static Contact checkCollision(Vector3f ca, Vector3f ha, Vector3f cb, Vector3f hb) {
		Vector3f out = new Vector3f();
		Vector3f d = new Vector3f();
		cb.sub(ca, d); // direction from A to B (don't make absolute yet)

		// Store the signs before taking absolute
		Vector3f signs = new Vector3f(
				d.x >= 0 ? 1f : -1f,
				d.y >= 0 ? 1f : -1f,
				d.z >= 0 ? 1f : -1f);

		d.absolute(); // now make absolute for overlap calculation

		float ox = (ha.x + hb.x) - d.x;
		float oy = (ha.y + hb.y) - d.y;
		float oz = (ha.z + hb.z) - d.z;

		if (ox <= 0f || oy <= 0f || oz <= 0f)
			return null;

		float penetration;
		if (ox < oy && ox < oz) {
			penetration = ox;
			out.x = signs.x * ox; // use the original sign
		} else if (oy < oz) {
			penetration = oy;
			out.y = signs.y * oy; // use the original sign
		} else {
			penetration = oz;
			out.z = signs.z * oz; // use the original sign
		}

		return new Contact(penetration, out);
	}

	private static void resolveContact(RigidBodyComponent ra, RigidBodyComponent rb, Vector3f pa, Vector3f pb,
			Vector3f va, Vector3f vb, Vector3f c) {
		Vector3f rv = new Vector3f();
		va.sub(vb, rv); // relative velocity
		float rvAlongNormal = rv.x() * c.x() + rv.y() * c.y() + rv.z() * c.z();
		if (rvAlongNormal > 0)
			return; // moving apart

		float e = Math.min(ra.restitution(), rb.restitution());
		float invMassSum = ra.invMass() + rb.invMass();
		if (invMassSum == 0f)
			return;

		// impulse scalar
		float j = -(1 + e) * rvAlongNormal;
		j /= invMassSum;
		Vector3f impulse = new Vector3f();
		c.mul(j, impulse);
		if (!ra.isStatic()) {
			Vector3f change = new Vector3f();
			impulse.mul(ra.invMass(), change);
			va.sub(change);
		}

		if (!rb.isStatic()) {
			Vector3f change = new Vector3f();
			impulse.mul(rb.invMass(), change);
			vb.add(change);
		}

		// friction
		va.sub(vb, rv); // new relative velocity
		rvAlongNormal = rv.x() * c.x() + rv.y() * c.y() + rv.z() * c.z();
		Vector3f t = new Vector3f(rv.x() - rvAlongNormal * c.x(), rv.y() - rvAlongNormal * c.y(),
				rv.z() - rvAlongNormal * c.z()); // tangent vector
		float tLen = t.length();
		if (tLen > 0.0001f) {
			t.div(tLen);
			float jt = -(rv.x() * t.x() + rv.y() * t.y() + rv.z() * t.z());
			jt /= invMassSum;
			float mu = (float) Math.sqrt(ra.friction() * rb.friction()); // combine friction
			float maxFriction = j * mu;
			if (jt > maxFriction)
				jt = maxFriction;
			if (jt < -maxFriction)
				jt = -maxFriction;
			Vector3f frictionImpulse = new Vector3f();
			t.mul(jt, frictionImpulse);
			if (!ra.isStatic()) {
				Vector3f change = new Vector3f();
				frictionImpulse.mul(ra.invMass(), change);
				va.sub(change);
			}
			if (!rb.isStatic()) {
				Vector3f change = new Vector3f();
				frictionImpulse.mul(rb.invMass(), change);
				vb.sub(change);
			}
		}
	}

	private static void correctPosition(RigidBodyComponent ra, RigidBodyComponent rb, Vector3f pa, Vector3f pb,
			Contact contact) {
		float invMassSum = ra.invMass() + rb.invMass();
		if (invMassSum == 0f)
			return;
		float correctionMagnitude = Math.max(contact.penetration() - 0.00000000000000f, 0f) / invMassSum * 0.5f;
		Vector3f correction = new Vector3f();
		contact.c().mul(correctionMagnitude, correction);
		if (!ra.isStatic()) {
			Vector3f corrA = new Vector3f();
			correction.mul(ra.invMass(), corrA);
			pa.sub(corrA);
		}
		if (!rb.isStatic()) {
			Vector3f corrB = new Vector3f();
			correction.mul(rb.invMass(), corrB);
			pb.add(corrB);
		}
	}

	private record Contact(float penetration, Vector3f c) {

	}
}
