package com.lucaslng.engine.components;

public class ButtonComponent {
	public boolean isPressed;
	public boolean isActive;
	public final boolean isLatch;
	public final boolean isToggle;
	
	public ButtonComponent() {
		this(false, false);
	}

	public ButtonComponent(boolean isLatch, boolean isToggle) {
		this.isPressed = false;
		this.isActive = false;
		this.isLatch = isLatch;
		this.isToggle = isToggle;
	}
}
