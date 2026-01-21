package com.lucaslng.engine.components;

import org.joml.Vector3f;

public class ButtonMoveComponent {
	public final Vector3f startPosition;
	public final Vector3f pressedOffset;
	public final float speed;
	public final int flag;
	public final int requiredCoins;

	public ButtonMoveComponent(Vector3f startPosition, Vector3f pressedOffset, float speed, int flag, int requiredCoins) {
		this.startPosition = new Vector3f(startPosition);
		this.pressedOffset = new Vector3f(pressedOffset);
		this.speed = speed;
		this.flag = flag;
		this.requiredCoins = requiredCoins;
	}
}
