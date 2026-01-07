package com.lucaslng;

import java.awt.event.KeyEvent;

import org.joml.Vector3f;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameLoop;
import com.lucaslng.engine.components.HeadRotationComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.RotationComponent;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.engine.systems.Physics;
import com.lucaslng.engine.systems.Positions;
import com.lucaslng.entities.*;

class Game extends GameLoop {

	private Entity player1, player2, plane, camera;
	private Physics physics;

	public Game(Engine engine) {
		super(engine);
	}

	@Override
	public void init(Engine engine) {
		player1 = engine.entityManager().buildEntity(new PlayerEntityFactory(-1.0f, 0.0f, 0.0f));
		player2 = engine.entityManager().buildEntity(new PlayerEntityFactory(1.0f, 0.0f, 0.0f));
		camera = engine.entityManager().buildEntity(new CameraEntityFactory(0.0f, 0.0f, 7.0f));
		plane = engine.entityManager().buildEntity(new CubeEntityFactory(0.0f,
		-10.0f, 0.0f, 9.0f));
		engine.setCamera(camera);
		physics = new Physics(engine.entityManager());
	}

	@Override
	public void doLoop(Engine engine) {
		float speed = 0.04f;
		// engine.entityManager().getComponent(cube.id(), RotationComponent.class).rotation().add(0.05f, 0.02f, 0.0f);
		if (engine.keyHandler().isKeyHeld(KeyEvent.VK_A)) {
			Vector3f position = engine.entityManager().getComponent(player1.id(), PositionComponent.class).position();
			Vector3f rotation = engine.entityManager().getComponent(player1.id(), HeadRotationComponent.class).rotation();
			Positions.moveLeft(position, rotation, speed);
		}
		if (engine.keyHandler().isKeyHeld(KeyEvent.VK_E)) {
			Vector3f position = engine.entityManager().getComponent(player1.id(), PositionComponent.class).position();
			Vector3f rotation = engine.entityManager().getComponent(player1.id(), HeadRotationComponent.class).rotation();
			Positions.moveRight(position, rotation, speed);
		}

		if (engine.keyHandler().isKeyHeld(KeyEvent.VK_LEFT)) {
			Vector3f position = engine.entityManager().getComponent(player2.id(), PositionComponent.class).position();
			Vector3f rotation = engine.entityManager().getComponent(player2.id(), HeadRotationComponent.class).rotation();
			Positions.moveLeft(position, rotation, speed);
		}
		if (engine.keyHandler().isKeyHeld(KeyEvent.VK_RIGHT)) {
			Vector3f position = engine.entityManager().getComponent(player2.id(), PositionComponent.class).position();
			Vector3f rotation = engine.entityManager().getComponent(player2.id(), HeadRotationComponent.class).rotation();
			Positions.moveRight(position, rotation, speed);
		}

		physics.step(dt);
		engine.setCamera(camera);
	}
}
