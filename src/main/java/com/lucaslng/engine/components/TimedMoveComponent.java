package com.lucaslng.engine.components;

import org.joml.Vector3f;

public class TimedMoveComponent {
	public final Vector3f startPosition;
	public final Vector3f moveOffset;
	public final float speed;
	public final float pauseDuration;
	public boolean active;
	public float pauseTimer;
	public boolean arrived;

	public TimedMoveComponent(Vector3f startPosition, Vector3f moveOffset, float speed, float pauseDuration,
			boolean startOff) {
		this.startPosition = new Vector3f(startPosition);
		this.moveOffset = new Vector3f(moveOffset);
		this.speed = speed;
		this.pauseDuration = Math.max(0f, pauseDuration);
		this.active = !startOff;
		this.pauseTimer = 0f;
		this.arrived = false;
	}
}
