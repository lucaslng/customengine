package com.lucaslng.engine;

import java.awt.BorderLayout;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

import org.joml.Vector3f;

import com.lucaslng.engine.components.HeadRotationComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.engine.renderer.GameFrame;
import com.lucaslng.engine.renderer.Renderer;

public final class Engine {

	private final JFrame frame;
	private final Renderer renderer;
	private final EngineSettings settings;
	private final EntityManager entityManager;
	private final KeyHandler keyHandler;

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
		MouseMotionToRotationListener mouseHandler = new MouseMotionToRotationListener(this);
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

	public void addMouseListener(MouseListener listener) {
		renderer.addMouseListener(listener);
	}

	public void addMouseMotionListener(MouseMotionListener listener) {
		renderer.addMouseMotionListener(listener);
	}

	public void addComponentListener(ComponentListener listener) {
		renderer.addComponentListener(listener);
	}

	public void addKeyListener(KeyListener listener) {
		renderer.addKeyListener(listener);
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
