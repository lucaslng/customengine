package com.lucaslng.engine;

import java.util.HashSet;

public class KeyHandler {

	private final HashSet<Integer> heldKeys;

	public KeyHandler() {
		heldKeys = new HashSet<>();
	}

	public boolean isKeyHeld(int key) {
		return heldKeys.contains(key);
	}

	public void keyPressed(int keyCode) {
		heldKeys.add(keyCode);
	}

	public void keyReleased(int keyCode) {
		heldKeys.remove(keyCode);
	}
}