package com.lucaslng.engine.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

	private final Matrix4f matrix;
	private final Vector3f position, rotation;

	public Camera() {
		matrix = new Matrix4f();
		position = new Vector3f();
		rotation = new Vector3f();
	}

	public void setCamera(Vector3f position, Vector3f rotation) {
		position.get(this.position);
		rotation.get(this.rotation);
		updateMatrix();
	}

	private void updateMatrix() {
		matrix.rotationXYZ(-rotation.x, -rotation.y, -rotation.z).translate(position);
	}

	public Matrix4f matrix() {
		return matrix;
	}
	
}
