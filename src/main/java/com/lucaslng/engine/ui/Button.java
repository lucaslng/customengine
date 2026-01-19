package com.lucaslng.engine.ui;

import java.awt.Color;
import java.util.ArrayList;

import com.lucaslng.engine.utils.Operation;

public class Button extends ColoredRectElement {

	private final ArrayList<Operation> operations;
	public boolean hovered;

	public Button(float x, float y, float width, float height, XAlignment xAlignment, YAlignment yAlignment, Color color) {
		super(x, y, width, height, xAlignment, yAlignment, color);
		operations = new ArrayList<>();
		hovered = false;
	}

	public void addOperation(Operation operation) {
		operations.add(operation);
	}

	public void onPressed() {
		for (Operation operation : operations) {
			operation.execute();
		}
	}

	public void onHovered() {
		if (!hovered) {
			hovered = true;
		}
	}
	
	public void onNotHovered() {
		if (hovered) {
			hovered = false;
		}
	}
}
