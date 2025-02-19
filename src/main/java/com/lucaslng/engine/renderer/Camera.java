package com.lucaslng.engine.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.lucaslng.engine.systems.Rotations;

public class Camera {

	private final Matrix4f matrix;
	private final Vector3f up;

	public Camera() {
		matrix = new Matrix4f();
		up = new Vector3f(0.0f, 1.0f, 0.0f);
	}

	public void setCamera(Vector3f position, Vector3f rotation) {
		if (rotation.x > 89.0f) {
			rotation.x = 89.0f;
		}
		if (rotation.x < -89.0f) {
			rotation.x = -89.0f;
		}
		matrix.setLookAt(position, position.add(Rotations.front(rotation), new Vector3f()), up);
	}
	
	public Matrix4f matrix() {
		return matrix;
	}
	
}
