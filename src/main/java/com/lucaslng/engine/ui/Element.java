package com.lucaslng.engine.ui;

import com.lucaslng.engine.renderer.Renderer;

abstract class RenderObject {
	protected double x, y, width, height;

	public void layout(double maxWidth, double maxHeight) {
		performLayout(maxWidth, maxHeight);
	}

	protected abstract void performLayout(double maxWidth, double maxHeight);

	public abstract void paint(Renderer renderer);

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
}