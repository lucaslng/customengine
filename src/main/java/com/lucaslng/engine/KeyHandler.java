package com.lucaslng.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

public class KeyHandler implements KeyListener {

	private final HashSet<Integer> pressedKeys;

	public KeyHandler() {
		pressedKeys = new HashSet<>();
	}

	public boolean isKeyPressed(int key) {
		return pressedKeys.contains(key);
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		pressedKeys.add(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		pressedKeys.remove(e.getKeyCode());
	}

}
