package com.lucaslng.engine.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.lucaslng.engine.systems.Rotations;

class Camera {

	private final Matrix4f matrix;
	private final Vector3f up;
	private int entityId;

	protected Camera() {
		matrix = new Matrix4f();
		up = new Vector3f(0.0f, 1.0f, 0.0f);
	}

	protected void setCamera(Vector3f position, Vector3f rotation, int entityId) {
		if (rotation.x > 89.0f) {
			rotation.x = 89.0f;
		}
		if (rotation.x < -89.0f) {
			rotation.x = -89.0f;
		}
		matrix.setLookAt(position, position.add(Rotations.front(rotation), new Vector3f()), up);
	}
	
	protected Matrix4f matrix() {
		return matrix;
	}

	protected int entityId() {
		return entityId;
	}
	
}
