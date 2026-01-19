package com.lucaslng.engine.ui;

import java.awt.Color;

import com.lucaslng.engine.InputHandler;
import com.lucaslng.engine.KeyBind;
import com.lucaslng.engine.SoundHandler;

public class KeySelector extends Button {

	public final KeyBind keyBind;
	private boolean waiting;

	public KeySelector(float x, float y, float width, float height, XAlignment xAlignment, YAlignment yAlignment,
			Color color, InputHandler inputHandler, KeyBind keyBind, Text text) {
		super(x, y, width, height, xAlignment, yAlignment, color);
		this.keyBind = keyBind;
		waiting = false;
		addOperation(() -> {
			waiting = true;
			text.text = "...";
			inputHandler.setKeyListener((keyCode) -> {
				keyBind.key = keyCode;
				waiting = false;
				SoundHandler.play("click");
				text.text = keyBind.name();
			});
		});
	}

	public boolean waiting() {
		return waiting;
	}

}
