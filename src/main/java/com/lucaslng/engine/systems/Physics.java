package com.lucaslng.engine.systems;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.RigidBodyComponent;

public class Physics {

	private final EntityManager entityManager;

	public static final float G = 980f;

	public Physics(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	public void step(double dt) {
		// System.out.println(dt);
	}

	private static boolean isStatic(RigidBodyComponent rigidBodyComponent) {
		return rigidBodyComponent.mass() <= 0;
	}
}
