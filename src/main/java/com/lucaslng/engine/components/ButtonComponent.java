package com.lucaslng.engine.components;

public class ButtonComponent {
	public boolean isPressed;
	public boolean isActive;
	public final boolean isLatch;
	public final boolean isToggle;
	public final int flag;
	
	public ButtonComponent() {
		this(false, false, 0);
	}

	public ButtonComponent(boolean isLatch, boolean isToggle) {
		this(isLatch, isToggle, 0);
	}

	public ButtonComponent(boolean isLatch, boolean isToggle, int flag) {
		this.isPressed = false;
		this.isActive = false;
		this.isLatch = isLatch;
		this.isToggle = isToggle;
		this.flag = flag;
	}
}
