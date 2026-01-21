package com.lucaslng.engine.components;

public class BlinkComponent {
	public final float onDuration;
	public final float offDuration;
	public float timer;
	public boolean active;

	public BlinkComponent(float onDuration, float offDuration, boolean startOff) {
		this.onDuration = Math.max(0.01f, onDuration);
		this.offDuration = Math.max(0.01f, offDuration);
		this.active = !startOff;
		this.timer = this.active ? this.onDuration : this.offDuration;
	}
}
