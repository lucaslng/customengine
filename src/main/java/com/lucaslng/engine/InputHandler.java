package com.lucaslng.engine;

import java.util.HashSet;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import com.lucaslng.engine.renderer.Window;

public class InputHandler {

	private final HashSet<Integer> heldKeys;
	private double realMouseX, realMouseY;
	private final Window window;
	private final boolean isMac;

	public InputHandler(Window window) {
		this.window = window;
		isMac = System.getProperty("os.name").startsWith("Mac");
		heldKeys = new HashSet<>();
		window.addKeyCallback((window_, key, scancode, action, mods) -> {
			if (action == GLFW_PRESS) {
				keyPressed(key);
			} else if (action == GLFW_RELEASE) {
				keyReleased(key);
			}
		});
		window.addCursorPosCallback((_window, xpos, ypos) -> {
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

	public double scaleMousePos(double pos) {
		if (isMac) {
			return 2 * pos / window.uiScale();
		} else {
			return pos / window.uiScale();
		}
	}

	public double mouseX() {
		return scaleMousePos(realMouseX);
	}

	public double mouseY() {
		return scaleMousePos(realMouseY);
	}
}