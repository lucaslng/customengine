package com.lucaslng.engine;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

import java.awt.Font;

import com.lucaslng.engine.components.HeadRotationComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.engine.renderer.FontAtlas;
import com.lucaslng.engine.renderer.Renderer;
import com.lucaslng.engine.ui.UIManager;

public final class Engine {
	public final Renderer renderer;
	public final EngineSettings settings;
	public final EntityManager entityManager;
	public final FontAtlas fontAtlas;
	public final UIManager uiManager;
	public final KeyHandler keyHandler;

	public Engine() {
		System.out.println("Initializing engine...");
		entityManager = new EntityManager();
		uiManager = new UIManager();
		settings = new EngineSettings();
		fontAtlas = new FontAtlas();
		fontAtlas.addFont(new Font("Arial", Font.PLAIN, 50));
		fontAtlas.dispose();
		renderer = new Renderer(settings, entityManager, uiManager, fontAtlas);
		keyHandler = new KeyHandler(renderer.getWindow());
		
		System.out.println("Engine initialized.");
	}

	public void setCamera(Vector3f position, Vector3f rotation, int entityId) {
		renderer.setCamera(position, rotation, entityId);
	}

	public void linkMouseToRotation(Vector3f rotation) {
		MouseMotionToRotationListener mouseHandler = new MouseMotionToRotationListener(this);
		mouseHandler.setRotation(rotation);
		
		// Setup mouse callback
		glfwSetCursorPosCallback(renderer.getWindow(), (window, xpos, ypos) -> {
			mouseHandler.mouseMoved(xpos, ypos);
		});
	}

	public void setCamera(Entity entity) {
		if (!entityManager.hasComponent(entity.id(), PositionComponent.class)
				|| !entityManager.hasComponent(entity.id(), HeadRotationComponent.class)) {
			throw new IllegalArgumentException(
					"Entity passed into setCamera() does not have position component or head rotation component.");
		}
		setCamera(entityManager.getComponent(entity.id(), PositionComponent.class).position(),
				entityManager.getComponent(entity.id(), HeadRotationComponent.class).rotation(), entity.id());
	}
	public void start(GameLoop gameLoop) {
		gameLoop.init(this);
		gameLoop.execute();
	}

	public void doLoop() {
		if (!renderer.isRendering()) {
			renderer.render();
		}
	}

	public int width() {
		return renderer.getFramebufferWidth();
	}

	public int height() {
		return renderer.getFramebufferHeight();
	}

	public boolean shouldClose() {
		return renderer.shouldClose();
	}
}