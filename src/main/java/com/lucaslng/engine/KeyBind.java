package com.lucaslng.engine;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashMap;

public class KeyBind {

	static final HashMap<Integer, String> KEY_NAMES = new HashMap<>();
	static {
		KEY_NAMES.put(GLFW_KEY_LEFT, "LEFT");
		KEY_NAMES.put(GLFW_KEY_RIGHT, "RIGHT");
		KEY_NAMES.put(GLFW_KEY_UP, "UP");
		KEY_NAMES.put(GLFW_KEY_DOWN, "DOWN");
		KEY_NAMES.put(GLFW_KEY_SPACE, "SPACE");
	}

	public final int defaultKey;
	public int key;

	public KeyBind(int defaultKey) {
		this.defaultKey = defaultKey;
		key = defaultKey;
	}

	public void reset() {
		key = defaultKey;
	}

	public String name() {
		String name = glfwGetKeyName(key, 0);
		if (name == null) {
			if (KEY_NAMES.containsKey(key))
				return KEY_NAMES.get(key);
			return glfwGetKeyScancode(key) + "";
		}
		return name;
	}
}
