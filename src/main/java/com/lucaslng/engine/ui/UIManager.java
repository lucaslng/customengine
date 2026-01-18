package com.lucaslng.engine.ui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

import java.util.HashSet;

import com.lucaslng.engine.EngineSettings;
import com.lucaslng.engine.InputHandler;
import com.lucaslng.engine.renderer.Window;

public class UIManager {
	
	public final HashSet<UIElement> elements;
	private final EngineSettings engineSettings;
	public UIManager(EngineSettings engineSettings, Window window, InputHandler inputHandler) {
		elements = new HashSet<>();
		this.engineSettings = engineSettings;
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
				float x = button.x;
				float y = button.y;
				if (button.xAlignment == XAlignment.CENTER)
					x = engineSettings.referenceDimension.width / 2 - button.width / 2f;
				else if (button.xAlignment == XAlignment.RIGHT) {
					x = engineSettings.referenceDimension.width - x - button.width;
				}
				if (button.yAlignment == YAlignment.CENTER)
					y = engineSettings.referenceDimension.height / 2 - button.height / 2f;
				else if (button.yAlignment == YAlignment.BOTTOM) {
					y = engineSettings.referenceDimension.height - y - button.height;
				}
				if (mouseX >= x && mouseX <= x + button.width && mouseY >= y && mouseY <= y + button.height) {
					button.onPressed();
				}
			}
		}
	}
}
