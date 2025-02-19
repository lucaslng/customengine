package com.lucaslng.engine.components;

import org.joml.Vector3f;

public record RotationComponent(Vector3f rotation) {
	
	public float pitch() {
		return rotation.x;
	}

	public float yaw() {
		return rotation.y;
	}

	public float roll() {
		return rotation.z;
	}

}
