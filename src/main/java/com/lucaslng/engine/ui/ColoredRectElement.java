package com.lucaslng.engine.ui;

import java.awt.Color;

public class ColoredRectElement extends RectElement {

	public Color color;

	public ColoredRectElement(float x, float y, float width, float height, XAlignment xAlignment, YAlignment yAlignment, Color color) {
		super(x, y, width, height, xAlignment, yAlignment);
		this.color = color;
	}

}
