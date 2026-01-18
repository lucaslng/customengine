package com.lucaslng.engine.ui;

import java.util.ArrayList;

import com.lucaslng.engine.utils.Operation;

public class Button extends UIElement {

	private final ArrayList<Operation> operations;

	public Button(float x, float y, float width, float height, XAlignment xAlignment, YAlignment yAlignment) {
		super(x, y, width, height, xAlignment, yAlignment);
		operations = new ArrayList<>();
	}

	public void addOperation(Operation operation) {
		operations.add(operation);
	}

	public void onPressed() {
		for (Operation operation : operations) {
			operation.execute();
		}
	}
}
