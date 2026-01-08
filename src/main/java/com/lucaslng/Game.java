package com.lucaslng;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameLoop;
import com.lucaslng.engine.components.HeadRotationComponent;
import com.lucaslng.engine.components.PositionComponent;
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
		plane = engine.entityManager().buildEntity(new CubeEntityFactory(0.0f, -10.0f, 0.0f, 9.0f));
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
		
		updateCamera(engine);
	}

	private void updateCamera(Engine engine) {
		Vector3f pos1 = engine.entityManager().getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = engine.entityManager().getComponent(player2.id(), PositionComponent.class).position();
		
		// calculate midpoint between players
		Vector3f midpoint = new Vector3f((pos1.x + pos2.x) / 2.0f, (pos1.y + pos2.y) / 2.0f, (pos1.z + pos2.z) / 2.0f);
		
		// calculate distance between players
		float distance = pos1.distance(pos2);
		
		// calculate camera distance based on player separation
		float baseDistance = 7.0f;
		float cameraDistance = Math.max(baseDistance, distance * 0.8f + 3.0f);
		
		// position camera behind and above the midpoint
		Vector3f cameraPos = engine.entityManager().getComponent(camera.id(), PositionComponent.class).position();
		cameraPos.set(midpoint.x, midpoint.y + 2f, midpoint.z + cameraDistance);
		
		engine.setCamera(camera);
	}
}