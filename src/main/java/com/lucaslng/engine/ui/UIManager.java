package com.lucaslng.engine.ui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.util.ArrayList;
import com.lucaslng.engine.EngineSettings;
import com.lucaslng.engine.InputHandler;
import com.lucaslng.engine.renderer.Window;

public class UIManager {

	public final ArrayList<UIElement> elements;
	private final EngineSettings engineSettings;
	private final Window window;
	public boolean active;

	public UIManager(EngineSettings engineSettings, Window window, InputHandler inputHandler) {
		elements = new ArrayList<>();
		this.engineSettings = engineSettings;
		this.window = window;
		active = false;

		window.addCursorPosCallback((_window, posx, posy) -> {
			if (active)
				checkButtonsHovered(inputHandler.mouseX(), inputHandler.mouseY());
		});

		window.addMouseButtonCallback((_window, button, action, mods) -> {
			if (active && window.focused()) {
				if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE) {
					for (UIElement element : elements) {
						if (element instanceof Button b && b.hovered) {
							b.onPressed();
						}
					}
				}
			}
		});
	}

	private void checkButtonsHovered(double mouseX, double mouseY) {
		for (UIElement element : elements) {
			if (element.visible && element instanceof Button button) {
				float x = button.x;
				float y = button.y;
				if (button.xAlignment == XAlignment.CENTER)
					x += engineSettings.referenceDimension.width / 2 - button.width / 2f;
				else if (button.xAlignment == XAlignment.RIGHT) {
					x = engineSettings.referenceDimension.width - x - button.width;
				}
				if (button.yAlignment == YAlignment.CENTER)
					y += engineSettings.referenceDimension.height / 2 - button.height / 2f;
				else if (button.yAlignment == YAlignment.BOTTOM) {
					y = engineSettings.referenceDimension.height - y - button.height;
				}
				if (mouseX >= x && mouseX <= x + button.width && mouseY >= y && mouseY <= y + button.height) {
					button.onHovered(window);
				} else {
					button.onNotHovered(window);
				}
			}
		}
	}
}
