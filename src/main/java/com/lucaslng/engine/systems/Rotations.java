package com.lucaslng.engine.systems;

import org.joml.Vector3f;

public class Rotations {

	private static final Vector3f WORLD_UP = new Vector3f(0.0f, 1.0f, 0.0f);
	public static final float PI = (float) Math.PI;

	public static Vector3f front(Vector3f rotation) {
		return new Vector3f(
				(float) (Math.cos(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.x))),
				(float) Math.sin(Math.toRadians(rotation.x)),
				(float) (Math.sin(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.x)))).normalize();
	}

	public static Vector3f right(Vector3f rotation) {
		return front(rotation).cross(WORLD_UP, new Vector3f()).normalize();
	}

	public static Vector3f up(Vector3f rotation) {
		return right(rotation).cross(front(rotation), new Vector3f()).normalize();
	}

}
