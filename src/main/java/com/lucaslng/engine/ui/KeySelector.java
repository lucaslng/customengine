package com.lucaslng.engine.ui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import java.awt.Color;

import com.lucaslng.engine.InputHandler;
import com.lucaslng.engine.KeyBind;

public class KeySelector extends Button {

	public final KeyBind keyBind;
	private boolean waiting;

	public KeySelector(float x, float y, float width, float height, XAlignment xAlignment, YAlignment yAlignment,
			Color color, InputHandler inputHandler, KeyBind keyBind, Text text) {
		super(x, y, width, height, xAlignment, yAlignment, color);
		this.keyBind = keyBind;
		waiting = false;
		addOperation(() -> {
			if (waiting) {
				waiting = false;
				inputHandler.removeKeyListener();
				text.text = keyBind.name();
			} else {
				waiting = true;
				text.text = "...";
				inputHandler.setKeyListener((keyCode) -> {
					if (keyCode != GLFW_KEY_ESCAPE) {
						keyBind.key = keyCode;
					}
					waiting = false;
					text.text = keyBind.name();
				});
			}
		});
	}

	public boolean waiting() {
		return waiting;
	}

}
