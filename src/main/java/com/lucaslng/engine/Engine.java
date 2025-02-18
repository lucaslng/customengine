package com.lucaslng.engine;

import java.awt.BorderLayout;
import java.util.Set;
import java.util.Timer;

import javax.swing.JFrame;

import org.joml.Vector3f;

import com.lucaslng.engine.components.HeadRotationComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.engine.renderer.GameFrame;
import com.lucaslng.engine.renderer.Renderer;

public final class Engine {

	private JFrame frame;
	private Renderer renderer;
	private EngineSettings settings;
	private EntityManager entityManager;
	private KeyHandler keyHandler;
	private Timer renderLoop;

	public Engine() {
		System.out.println("Initializing engine...");
		entityManager = new EntityManager();
		settings = new EngineSettings();
		frame = new GameFrame(settings);
		renderer = new Renderer(settings, entityManager);
		frame.add(renderer, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		frame.transferFocus();
		keyHandler = new KeyHandler();
		renderer.addKeyListener(keyHandler);
		System.out.println("Engine initialized.");

	}

	public void setCamera(Vector3f position, Vector3f rotation) {
		renderer.setCamera(position, rotation);
	}

	public void linkMouseToRotation(Vector3f rotation) {
		MouseHandler mouseHandler = new MouseHandler(this);
		mouseHandler.setRotation(rotation);
		renderer.addMouseMotionListener(mouseHandler);
	}

	public void setCamera(Entity entity) {
		if (!entityManager.hasComponent(entity.id(), PositionComponent.class)
				|| !entityManager.hasComponent(entity.id(), HeadRotationComponent.class)) {
			throw new IllegalArgumentException(
					"Entity passed into setCamera() does not have position component or head rotation component.");
		}
		setCamera(entityManager.getComponent(entity.id(), PositionComponent.class).position(),
				entityManager.getComponent(entity.id(), HeadRotationComponent.class).rotation());
	}

	public Entity buildEntity(AbstractEntityFactory entityFactory) {
		return entityManager.buildEntity(entityFactory);
	}

	public <T extends Record> void addComponent(int entityId, T component) {
		entityManager.addComponent(entityId, component);
	}

	public <T extends Record> T getComponent(int entityId, Class<T> componentClass) {
		return entityManager.getComponent(entityId, componentClass);
	}

	public <T extends Record> void removeComponent(int entityId, Class<T> componentClass) {
		entityManager.removeComponent(entityId, componentClass);
	}

	public Set<Integer> getEntitiesWith(Class<?>... componentClasses) {
		return entityManager.getEntitiesWith(componentClasses);
	}

	public boolean isKeyHeld(int key) {
		return keyHandler.isKeyHeld(key);
	}

	public Renderer renderer() {
		return renderer;
	}

	public void start(GameLoop gameLoop) {
		gameLoop.init(this);
		gameLoop.execute();
		// renderLoop.start();
	}

	public void doLoop() {
		if (renderer.isRendering()) {
			return;
		}
		renderer.render();
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

}
