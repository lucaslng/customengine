package com.lucaslng.engine;

import static org.lwjgl.glfw.GLFW.glfwGetKeyName;
import static org.lwjgl.glfw.GLFW.glfwGetKeyScancode;

public class KeyBind {

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
		if (name == null)
			return glfwGetKeyScancode(key) + "";
		return name;
	}
}
