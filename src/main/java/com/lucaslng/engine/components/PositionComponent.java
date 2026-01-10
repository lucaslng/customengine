package com.lucaslng.engine.components;

import org.joml.Vector3f;

public record PositionComponent(Vector3f position) {

	public float x() {
		return position.x();
	}

	public float y() {
		return position.y();
	}

	public float z() {
		return position.z();
	}
	
}
