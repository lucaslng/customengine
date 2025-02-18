package com.lucaslng.engine.systems;

import org.joml.Vector3f;

public class Positions {

	public static void moveForward(Vector3f position, Vector3f rotation, float speed) {
		position.add(Rotations.front(rotation).mul(speed, new Vector3f()));
	}

	public static void moveBackward(Vector3f position, Vector3f rotation, float speed) {
		position.sub(Rotations.front(rotation).mul(speed, new Vector3f()));
	}

	public static void moveLeft(Vector3f position, Vector3f rotation, float speed) {
		position.sub(Rotations.right(rotation).mul(speed, new Vector3f()));
	}

	public static void moveRight(Vector3f position, Vector3f rotation, float speed) {
		position.add(Rotations.right(rotation).mul(speed, new Vector3f()));
	}

	public static void moveUp(Vector3f position, Vector3f rotation, float speed) {
		position.add(Rotations.up(rotation).mul(speed, new Vector3f()));
	}

	public static void moveDown(Vector3f position, Vector3f rotation, float speed) {
		position.sub(Rotations.up(rotation).mul(speed, new Vector3f()));
	}
	
}
