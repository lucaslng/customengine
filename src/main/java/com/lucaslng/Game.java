package com.lucaslng;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameLoop;
import com.lucaslng.engine.components.HeadRotationComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.engine.systems.Physics;
import com.lucaslng.engine.systems.Positions;
import com.lucaslng.entities.CameraEntityFactory;
import com.lucaslng.entities.CubeEntityFactory;
import com.lucaslng.entities.PlayerEntityFactory;

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
		Entity plane = engine.entityManager().buildEntity(new CubeEntityFactory(0.0f, -10.0f, 0.0f, 9.0f));
		engine.setCamera(camera);
		physics = new Physics(engine.entityManager());
	}

	@Override
	public void doLoop(Engine engine) {
		float speed = 0.04f;
		
		// Player 1 controls (A/D keys)
		if (engine.keyHandler().isKeyHeld(GLFW_KEY_A)) {
			Vector3f position = engine.entityManager().getComponent(player1.id(), PositionComponent.class).position();
			Vector3f rotation = engine.entityManager().getComponent(player1.id(), HeadRotationComponent.class).rotation();
			Positions.moveLeft(position, rotation, speed);
		}
		if (engine.keyHandler().isKeyHeld(GLFW_KEY_D)) {
			Vector3f position = engine.entityManager().getComponent(player1.id(), PositionComponent.class).position();
			Vector3f rotation = engine.entityManager().getComponent(player1.id(), HeadRotationComponent.class).rotation();
			Positions.moveRight(position, rotation, speed);
		}

		// Player 2 controls (Arrow keys)
		if (engine.keyHandler().isKeyHeld(GLFW_KEY_LEFT)) {
			Vector3f position = engine.entityManager().getComponent(player2.id(), PositionComponent.class).position();
			Vector3f rotation = engine.entityManager().getComponent(player2.id(), HeadRotationComponent.class).rotation();
			Positions.moveLeft(position, rotation, speed);
		}
		if (engine.keyHandler().isKeyHeld(GLFW_KEY_RIGHT)) {
			Vector3f position = engine.entityManager().getComponent(player2.id(), PositionComponent.class).position();
			Vector3f rotation = engine.entityManager().getComponent(player2.id(), HeadRotationComponent.class).rotation();
			Positions.moveRight(position, rotation, speed);
		}

		physics.step(dt);
		engine.setCamera(camera);
	}
}