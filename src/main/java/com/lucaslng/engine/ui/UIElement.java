package com.lucaslng.engine.ui;

import java.util.concurrent.atomic.AtomicInteger;

public class UIElement {

	private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
	public float x, y, width, height;
	public final XAlignment xAlignment;
	public final YAlignment yAlignment;

	private final int id;

	// all parameters should be in reference dimension coords
	public UIElement(float x, float y, float width, float height, XAlignment xAlignment, YAlignment yAlignment) {
		this.id = ID_GENERATOR.getAndIncrement();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.xAlignment = xAlignment;
		this.yAlignment = yAlignment;
	}

	public int id() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof UIElement uiElement) {
			return id == uiElement.id;
		}
		return false;
	}
}
