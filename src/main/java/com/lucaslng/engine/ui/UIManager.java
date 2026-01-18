package com.lucaslng.engine.ui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

import java.util.HashSet;

import com.lucaslng.engine.InputHandler;
import com.lucaslng.engine.renderer.Window;

public class UIManager {
	public final HashSet<UIElement> elements;

	public UIManager(Window window, InputHandler inputHandler) {
		elements = new HashSet<>();
		glfwSetMouseButtonCallback(window.window, (_window, button, action, mods) -> {
			if (window.focused()) {
				if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE) {
					checkButtons(inputHandler.mouseX(), inputHandler.mouseY());
				}
			}
		});
	}

	private void checkButtons(double mouseX, double mouseY) {
		System.out.println(mouseX + " " + mouseY);
		for (UIElement element : elements) {
			if (element instanceof Button button) {
				if (mouseX >= button.x && mouseX <= button.x + button.width && mouseY >= button.y && mouseY <= button.y + button.height) {
					System.out.println("HIT");
				}
			}
		}
	}
}
