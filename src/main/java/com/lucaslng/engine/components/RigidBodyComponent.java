package com.lucaslng.engine.components;

public record RigidBodyComponent(float mass, float restitution, float friction, float gravityScale) {

	public float invMass() {
		if (mass == 0f) return 0f;
		return 1f / mass;
	}

	public boolean isStatic() {
		return mass <= 0;
	}

}