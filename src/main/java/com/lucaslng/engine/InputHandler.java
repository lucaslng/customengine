package com.lucaslng.engine;

import java.util.HashSet;

import com.lucaslng.engine.renderer.Window;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {

	private final HashSet<Integer> heldKeys;
	private double realMouseX, realMouseY;
	private final Window window;

	public InputHandler(Window window) {
		this.window = window;
		heldKeys = new HashSet<>();
		glfwSetKeyCallback(window.window, (window_, key, scancode, action, mods) -> {
			if (action == GLFW_PRESS) {
				keyPressed(key);
			} else if (action == GLFW_RELEASE) {
				keyReleased(key);
			}
		});
		glfwSetCursorPosCallback(window.window, (_window, xpos, ypos) -> {
			realMouseX = xpos;
			realMouseY = ypos;
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

	public double mouseX() {
		return 2 * realMouseX / window.uiScale();
	}

	public double mouseY() {
		return 2 * realMouseY / window.uiScale();
	}
}