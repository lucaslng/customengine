package com.lucaslng.engine;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;

class MouseMotionToRotationListener {

	private final Engine engine;
	private Vector3f rotation;
	private double lastX, lastY;
	private boolean firstMouse = true;

	public MouseMotionToRotationListener(Engine engine) {
		this.engine = engine;
		rotation = null;
		lastX = engine.w() / 2.0;
		lastY = engine.h() / 2.0;
		
		// Hide and capture cursor
		glfwSetInputMode(engine.window.window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public boolean isActive() {
		return rotation != null;
	}

	public void mouseMoved(double xpos, double ypos) {
		if (isActive()) {
			if (firstMouse) {
				lastX = xpos;
				lastY = ypos;
				firstMouse = false;
			}

			double xoffset = xpos - lastX;
			double yoffset = lastY - ypos; // Reversed since y-coordinates go from bottom to top

			lastX = xpos;
			lastY = ypos;

			rotation.add(
				(float) yoffset * engine.settings.sensitivity,
				(float) xoffset * engine.settings.sensitivity,
				0.0f
			);
		}
	}
}