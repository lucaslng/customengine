package com.lucaslng.engine.ui;

import java.awt.Color;
import java.util.ArrayList;

import com.lucaslng.engine.SoundHandler;
import com.lucaslng.engine.utils.Operation;

public class Button extends ColoredRectElement {

	private final ArrayList<Operation> operations;
	public boolean hovered;

	private static final float hoverScale = 1.05f;

	public float initX, initY, initWidth, initHeight;

	public Button(float x, float y, float width, float height, XAlignment xAlignment, YAlignment yAlignment, Color color) {
		super(x, y, width, height, xAlignment, yAlignment, color);
		this.initX = x;
		this.initY = y;
		this.initWidth = width;
		this.initHeight = height;
		operations = new ArrayList<>();
		hovered = false;
	}

	public void addOperation(Operation operation) {
		operations.add(operation);
	}

	public void onPressed() {
		SoundHandler.play("click");
		for (Operation operation : operations) {
			operation.execute();
		}
	}

	public void onHovered() {
		if (!hovered) {
			hovered = true;
			width *= hoverScale;
			height *= hoverScale;
			if (xAlignment != XAlignment.CENTER)
				x -= (width - initWidth) / 2f;
			if (yAlignment != YAlignment.CENTER)
				y -= (height - initHeight) / 2f;
		}
	}
	
	public void onNotHovered() {
		if (hovered) {
			hovered = false;
			x = initX;
			y = initY;
			width = initWidth;
			height = initHeight;
		}
	}
}
