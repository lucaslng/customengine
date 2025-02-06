package com.lucaslng;


import org.joml.Vector3f;

public class Camera {

	private final Vector3f position = new Vector3f(0.0f, 0.0f, -7.0f);
	public float yaw = 0.0f;
	public float pitch = 0.0f;

	public void moveX(float distance) {
		position.x -= Math.cos(yaw) * distance;
    position.z += Math.sin(yaw) * distance;
	}

	public void moveY(float distance) {
		position.y += distance;
	}

	public void moveZ(float distance) {
		position.x += Math.sin(yaw) * distance;
    position.z += Math.cos(yaw) * distance;
	}

	public Vector3f getPosition() {
		return position;
	}
}
