package com.lucaslng.engine;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

import com.lucaslng.engine.components.HeadRotationComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.engine.renderer.Renderer;

public final class Engine {
	private final Renderer renderer;
	private final EngineSettings settings;
	private final EntityManager entityManager;
	private final KeyHandler keyHandler;

	public Engine() {
		System.out.println("Initializing engine...");
		entityManager = new EntityManager();
		settings = new EngineSettings();
		renderer = new Renderer(settings, entityManager);
		keyHandler = new KeyHandler();

		glfwSetKeyCallback(renderer.getWindow(), (window, key, scancode, action, mods) -> {
			if (action == GLFW_PRESS) {
				keyHandler.keyPressed(key);
			} else if (action == GLFW_RELEASE) {
				keyHandler.keyReleased(key);
			}
		});
		
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

	public KeyHandler keyHandler() {
		return keyHandler;
	}

	public Renderer renderer() {
		return renderer;
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

	public EngineSettings settings() {
		return settings;
	}

	public EntityManager entityManager() {
		return entityManager;
	}

	public int width() {
		return renderer.getWidth();
	}

	public int height() {
		return renderer.getHeight();
	}

	public boolean shouldClose() {
		return renderer.shouldClose();
	}
}