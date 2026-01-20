package com.lucaslng.engine.components;

import org.joml.Vector3f;

public class ButtonMoveComponent {
	public final Vector3f startPosition;
	public final Vector3f pressedOffset;
	public final float speed;

	public ButtonMoveComponent(Vector3f startPosition, Vector3f pressedOffset, float speed) {
		this.startPosition = new Vector3f(startPosition);
		this.pressedOffset = new Vector3f(pressedOffset);
		this.speed = speed;
	}
}
