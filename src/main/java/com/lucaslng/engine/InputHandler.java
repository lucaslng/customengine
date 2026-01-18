package com.lucaslng.engine;

import java.util.HashSet;

import com.lucaslng.engine.renderer.Window;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {

	private final HashSet<Integer> heldKeys;
	private double mouseX, mouseY;

	public InputHandler(Window window) {
		heldKeys = new HashSet<>();
		glfwSetKeyCallback(window.window, (window_, key, scancode, action, mods) -> {
			if (action == GLFW_PRESS) {
				keyPressed(key);
			} else if (action == GLFW_RELEASE) {
				keyReleased(key);
			}
		});
		glfwSetCursorPosCallback(window.window, (_window, xpos, ypos) -> {
			mouseX = xpos;
			mouseY = ypos;
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
		return mouseX;
	}

	public double mouseY() {
		return mouseY;
	}
}