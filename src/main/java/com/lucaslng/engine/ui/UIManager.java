package com.lucaslng.engine.ui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

import java.util.ArrayList;

import com.lucaslng.engine.InputHandler;
import com.lucaslng.engine.renderer.Window;

public class UIManager {
	public final ArrayList<UIElement> elements = new ArrayList<>();

	public UIManager(Window window, InputHandler inputHandler) {
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
			if (element instanceof Button) {
				Button button = (Button) element;
				if (mouseX >= button.x && mouseX <= button.x + button.width && mouseY >= button.y && mouseY <= button.y + button.height) {
					System.out.println("HIT");
				}
			}
		}
	}
}
