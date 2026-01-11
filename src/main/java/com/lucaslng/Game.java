package com.lucaslng;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

import java.util.Set;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameLoop;
import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.engine.systems.Levels;
import com.lucaslng.engine.systems.Levels.Level;
import com.lucaslng.entities.*;
import com.lucaslng.engine.systems.Physics;
import com.lucaslng.engine.systems.Rotations;

class Game extends GameLoop {
	private Entity player1, player2, camera, catCube;
	private Physics physics;
	private Levels levels;

	public Game(Engine engine) {
		super(engine);
	}

	@Override
	public void init(Engine engine) {
		levels = new Levels();
		Level level = levels.currentLevel();
		player1 = engine.entityManager().buildEntity(new PlayerEntityFactory(level.player1Spawn()));
		player2 = engine.entityManager().buildEntity(new PlayerEntityFactory(level.player2Spawn()));
		for (AbstractEntityFactory entityFactory : level.entityFactories())
			engine.entityManager().buildEntity(entityFactory);
		camera = engine.entityManager().buildEntity(new CameraEntityFactory());
		engine.setCamera(camera);
		catCube = engine.entityManager().buildEntity(new TexturedCubeEntityFactory(-12f, 0f, 0f, 1f));
		physics = new Physics(engine.entityManager());
	}

	@Override
	public void doLoop(Engine engine, double dt) {
		float baseSpeed = 4f;
		float speed = baseSpeed * (float) dt;
		
		handlePlayerMovement(engine, player1, speed, GLFW_KEY_A, GLFW_KEY_D, GLFW_KEY_SPACE);	// player 1 controls
		handlePlayerMovement(engine, player2, speed, GLFW_KEY_LEFT, GLFW_KEY_RIGHT, GLFW_KEY_UP);	// player 2 controls

		physics.step(dt);	

		Vector3f pos1 = engine.entityManager().getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = engine.entityManager().getComponent(player2.id(), PositionComponent.class).position();
		
		Set<Integer> exits = engine.entityManager().getEntitiesWith(ExitComponent.class);
		for (int exit : exits) {
			Vector3f exitPos = engine.entityManager().getComponent(exit, PositionComponent.class).position();
			float s = ExitEntityFactory.halfSize;
			if (pos1.x() > exitPos.x() - s && pos1.x() < exitPos.x() + s && pos1.y() > exitPos.y() - s && pos1.y() < exitPos.y() + s) {
				System.out.println("player 1 exit");
			}
			if (pos2.x() > exitPos.x() - s && pos2.x() < exitPos.x() + s && pos2.y() > exitPos.y() - s && pos2.y() < exitPos.y() + s) {
				System.out.println("player 2 exit");
			}
		}


		if (pos1.y < 0f) {
			System.out.println("player 1 died!");
			DeathsComponent deaths = engine.entityManager().getComponent(player1.id(), DeathsComponent.class);
			deaths.deaths++;
			pos1.set(levels.currentLevel().player1Spawn(), 0f);
			pos2.set(levels.currentLevel().player2Spawn(), 0f);
		}

		if (pos2.y < 0f) {
			System.out.println("player 2 died!");
			DeathsComponent deaths = engine.entityManager().getComponent(player2.id(), DeathsComponent.class);
			deaths.deaths++;
			pos1.set(levels.currentLevel().player1Spawn(), 0f);
			pos2.set(levels.currentLevel().player2Spawn(), 0f);
		}

		updateCamera(engine);
	}

	private void handlePlayerMovement(Engine engine, Entity player, float speed, int leftKey, int rightKey, int jumpKey) {
		boolean leftPressed = engine.keyHandler().isKeyHeld(leftKey);
		boolean rightPressed = engine.keyHandler().isKeyHeld(rightKey);
		
		Vector3f position = engine.entityManager().getComponent(player.id(), PositionComponent.class).position();
		Vector3f rotation = engine.entityManager().getComponent(player.id(), RotationComponent.class).rotation();

		if (leftPressed && rightPressed) {
		} else if (leftPressed) {
			position.x -= speed;
			rotation.y = 0f;
		} else if (rightPressed) {
			position.x += speed;
			rotation.y = Rotations.PI;
		}
		
		// jumping
		if (engine.keyHandler().isKeyHeld(jumpKey)) {
			GroundedComponent grounded = engine.entityManager().getComponent(player.id(), GroundedComponent.class);
			if (grounded.isGrounded) {
				Vector3f velocity = engine.entityManager().getComponent(player.id(), VelocityComponent.class).velocity();
				velocity.y = 10f;
			}
		}
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