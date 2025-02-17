package com.lucaslng.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

public class KeyHandler implements KeyListener {

	private final HashSet<Integer> heldKeys;

	public KeyHandler() {
		heldKeys = new HashSet<>();
	}

	public boolean isKeyHeld(int key) {
		return heldKeys.contains(key);
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		heldKeys.add(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		heldKeys.remove(e.getKeyCode());
	}

}
