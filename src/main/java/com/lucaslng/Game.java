package com.lucaslng;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameLoop;
import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.engine.systems.Levels;
import com.lucaslng.engine.systems.Physics;
import com.lucaslng.engine.systems.Rotations;
import com.lucaslng.entities.*;

class Game extends GameLoop {
	private Entity player1, player2, camera, catCube;
	private Physics physics;
	private Levels levels;
	private int level = 1;

	public Game(Engine engine) {
		super(engine);
	}

	@Override
	public void init(Engine engine) {
		Levels levels = new Levels();
		AbstractEntityFactory[] entityFactories = levels.getLevelEntities(level);
		player1 = engine.entityManager().buildEntity(entityFactories[0]);
		player2 = engine.entityManager().buildEntity(entityFactories[1]);
		for (int i=2;i<entityFactories.length;i++) {
			engine.entityManager().buildEntity(entityFactories[i]);
		}
		camera = engine.entityManager().buildEntity(new CameraEntityFactory());
		engine.setCamera(camera);
		catCube = engine.entityManager().buildEntity(new TexturedCubeEntityFactory(-12f, 0f, 0f, 1f));
		physics = new Physics(engine.entityManager());
	}

	@Override
	public void doLoop(Engine engine, double dt) {
		float baseSpeed = 2.5f;
		float speed = baseSpeed * (float) dt;
		
		// player 1 controls
		if (engine.keyHandler().isKeyHeld(GLFW_KEY_A)) {
			Vector3f position = engine.entityManager().getComponent(player1.id(), PositionComponent.class).position();
			Vector3f rotation = engine.entityManager().getComponent(player1.id(), RotationComponent.class).rotation();
			position.x -= speed;
			rotation.y = 0f;
		}
		if (engine.keyHandler().isKeyHeld(GLFW_KEY_D)) {
			Vector3f position = engine.entityManager().getComponent(player1.id(), PositionComponent.class).position();
			Vector3f rotation = engine.entityManager().getComponent(player1.id(), RotationComponent.class).rotation();
			position.x += speed;
			rotation.y = Rotations.PI; // TODO: fix the rotation order of operations prioritizing facing rightwards when pressing both keys down at the same time
		}
		if (engine.keyHandler().isKeyHeld(GLFW_KEY_SPACE)) {
			GroundedComponent grounded = engine.entityManager().getComponent(player1.id(), GroundedComponent.class);
			if (grounded.isGrounded) {
				Vector3f velocity = engine.entityManager().getComponent(player1.id(), VelocityComponent.class).velocity();
				velocity.y = 10f;
			}
		}

		// player 2 controls
		if (engine.keyHandler().isKeyHeld(GLFW_KEY_LEFT)) {
			Vector3f position = engine.entityManager().getComponent(player2.id(), PositionComponent.class).position();
			Vector3f rotation = engine.entityManager().getComponent(player2.id(), RotationComponent.class).rotation();
			position.x -= speed;
			rotation.y = 0f;
		}
		if (engine.keyHandler().isKeyHeld(GLFW_KEY_RIGHT)) {
			Vector3f position = engine.entityManager().getComponent(player2.id(), PositionComponent.class).position();
			Vector3f rotation = engine.entityManager().getComponent(player2.id(), RotationComponent.class).rotation();
			position.x += speed;
			rotation.y = Rotations.PI;
		}
		if (engine.keyHandler().isKeyHeld(GLFW_KEY_UP)) {
			GroundedComponent grounded = engine.entityManager().getComponent(player2.id(), GroundedComponent.class);
			if (grounded.isGrounded) {
				Vector3f velocity = engine.entityManager().getComponent(player2.id(), VelocityComponent.class).velocity();
				velocity.y = 10f;
			}
		}

		// System.out.println(engine.entityManager().getComponent(player1.id(), PositionComponent.class).position());

		physics.step(dt);
		
		updateCamera(engine);
	}

	private void updateCamera(Engine engine) {
		Vector3f pos1 = engine.entityManager().getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = engine.entityManager().getComponent(player2.id(), PositionComponent.class).position();
		
		// calculate midpoint between players
		Vector3f midpoint = new Vector3f((pos1.x + pos2.x) / 2f, (pos1.y + pos2.y) / 2f, (pos1.z + pos2.z) / 2f);
		
		// calculate distance between players
		float distance = pos1.distance(pos2);
		
		// calculate camera distance based on player separation
		float baseDistance = 15f;
		float cameraDistance = Math.max(baseDistance, distance * 0.8f + 3f);
		
		// position camera behind and above the midpoint
		Vector3f cameraPos = engine.entityManager().getComponent(camera.id(), PositionComponent.class).position();
		cameraPos.set(midpoint.x, midpoint.y + 2f, midpoint.z + cameraDistance);
		
		engine.setCamera(camera);
	}
}