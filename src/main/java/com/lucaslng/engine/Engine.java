package com.lucaslng.engine;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.entities.CubeEntityFactory;
import com.lucaslng.engine.entities.TriangleEntityFactory;
import com.lucaslng.engine.renderer.GameFrame;
import com.lucaslng.engine.renderer.Renderer;

public class Engine {

	private final JFrame frame;
	private final Renderer renderer;
	private final EngineSettings settings;
	private final EntityManager entityManager;

	public Engine() {
		entityManager = new EntityManager();
		settings = new EngineSettings();
		frame = new GameFrame(settings);
		renderer = new Renderer(settings, entityManager);
		frame.add(renderer, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		// while (renderer)
		renderer.render();
		entityManager.buildEntity(new TriangleEntityFactory());
		entityManager.buildEntity(new CubeEntityFactory(-0.5f, -0.5f, -0.5f, 1.0f));
	}

	public void buildEntity(AbstractEntityFactory entityFactory) {
		entityManager.buildEntity(entityFactory);
	}

	public void doLoop() {
		renderer.render();
	}

}
