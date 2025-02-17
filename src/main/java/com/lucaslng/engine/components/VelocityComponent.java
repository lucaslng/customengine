package com.lucaslng.engine.components;

import org.joml.Vector3f;

public record VelocityComponent(Vector3f velocity) {

	public float x() {
		return velocity.x;
	}

	public float y() {
		return velocity.y;
	}

	public float z() {
		return velocity.z;
	}
	
}
