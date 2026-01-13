package com.lucaslng.engine;

import java.util.HashSet;
import static org.lwjgl.glfw.GLFW.*;

public class KeyHandler {

	private final HashSet<Integer> heldKeys;

	public KeyHandler(long window) {
		heldKeys = new HashSet<>();
		glfwSetKeyCallback(window, (window_, key, scancode, action, mods) -> {
			if (action == GLFW_PRESS) {
				keyPressed(key);
			} else if (action == GLFW_RELEASE) {
				keyReleased(key);
			}
		});
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