package com.lucaslng;

import java.util.Set;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameLoop;
import com.lucaslng.engine.components.DeathsComponent;
import com.lucaslng.engine.components.DisabledComponent;
import com.lucaslng.engine.components.GroundedComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.RotationComponent;
import com.lucaslng.engine.components.VelocityComponent;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.engine.systems.Deaths;
import com.lucaslng.engine.systems.Exits;
import com.lucaslng.engine.systems.LevelTransition;
import com.lucaslng.engine.systems.Levels;
import com.lucaslng.engine.systems.Levels.Level;
import com.lucaslng.engine.ui.UIElement;
import com.lucaslng.engine.systems.Physics;
import com.lucaslng.engine.systems.Rotations;
import com.lucaslng.entities.CameraEntityFactory;
import com.lucaslng.entities.PlayerEntityFactory;

class Game extends GameLoop {
	private Entity player1, player2, camera;
	private Physics physics;
	private Levels levels;
	private Exits exits;
	private Deaths deaths;
	private LevelTransition transition;

	public Game(Engine engine) {
		super(engine);
	}

	@Override
	public void init(Engine engine) {
		levels = new Levels();
		Level level = levels.currentLevel();
		player1 = engine.entityManager.buildEntity(new PlayerEntityFactory(level.player1Spawn()));
		player2 = engine.entityManager.buildEntity(new PlayerEntityFactory(level.player2Spawn()));
		for (AbstractEntityFactory entityFactory : level.entityFactories())
			engine.entityManager.buildEntity(entityFactory);
		camera = engine.entityManager.buildEntity(new CameraEntityFactory());
		engine.setCamera(camera);
		physics = new Physics(engine.entityManager);
		exits = new Exits(engine.entityManager);
		deaths = new Deaths(engine.entityManager, player1, player2, exits);
		transition = new LevelTransition();

		engine.uiManager.elements.add(new UIElement(10f, 10f, 200f, 300f, true, true));
	}

	@Override
	public void doLoop(Engine engine, double dt) {
		float baseSpeed = 6f;
		float speed = baseSpeed * (float) dt;

		transition.update((float) dt);

		if (!transition.isTransitioning()) {
			handlePlayerMovement(engine, player1, speed, GLFW_KEY_A, GLFW_KEY_D, GLFW_KEY_SPACE);
			handlePlayerMovement(engine, player2, speed, GLFW_KEY_LEFT, GLFW_KEY_RIGHT, GLFW_KEY_UP);

			physics.step(dt);
			deaths.checkDeaths(levels.currentLevel());
			exits.handleExits(player1, player2);

			if (exits.player1Exited && exits.player2Exited) {
				transition.startTransition(() -> performLevelChange(engine));
			}
		}

		updateCamera(engine);
		engine.renderer.setFadeAlpha(transition.getFadeAlpha());
	}

	private void performLevelChange (Engine engine) {
		levels.currentLevelIndex++;
		if (levels.currentLevelIndex > Levels.LEVEL_COUNT) {
			System.out.println("You won!");
			System.exit(0);
		}
		Level level = levels.currentLevel();
		Vector3f pos1 = engine.entityManager.getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = engine.entityManager.getComponent(player2.id(), PositionComponent.class).position();
		pos1.set(levels.currentLevel().player1Spawn(), 0f);
		pos2.set(levels.currentLevel().player2Spawn(), 0f);
		DeathsComponent deaths1 = engine.entityManager.getComponent(player1.id(), DeathsComponent.class);
		DeathsComponent deaths2 = engine.entityManager.getComponent(player2.id(), DeathsComponent.class);
		deaths1.nextLevel();
		deaths2.nextLevel();
		Set<Integer> entityIds = engine.entityManager.entities;
		for (Integer entityId : entityIds) {
			if (entityId == player1.id() || entityId == player2.id() || entityId == camera.id())
				continue;
			engine.entityManager.removeEntity(entityId);
		}
		for (AbstractEntityFactory entityFactory : level.entityFactories())
			engine.entityManager.buildEntity(entityFactory);
		engine.entityManager.removeComponent(player1.id(), DisabledComponent.class);
		engine.entityManager.removeComponent(player2.id(), DisabledComponent.class);
		exits.refresh();
	}

	private void handlePlayerMovement(Engine engine, Entity player, float speed, int leftKey, int rightKey, int jumpKey) {
		if (isDisabled(engine, player))
			return;
		boolean leftPressed = engine.keyHandler.isKeyHeld(leftKey);
		boolean rightPressed = engine.keyHandler.isKeyHeld(rightKey);

		Vector3f position = engine.entityManager.getComponent(player.id(), PositionComponent.class).position();
		Vector3f rotation = engine.entityManager.getComponent(player.id(), RotationComponent.class).rotation();

		if (leftPressed && rightPressed) {
		} else if (leftPressed) {
			position.x -= speed;
			rotation.y = 0f;
		} else if (rightPressed) {
			position.x += speed;
			rotation.y = Rotations.PI;
		}

		// jumping
		if (engine.keyHandler.isKeyHeld(jumpKey)) {
			GroundedComponent grounded = engine.entityManager.getComponent(player.id(), GroundedComponent.class);
			if (grounded.isGrounded) {
				Vector3f velocity = engine.entityManager.getComponent(player.id(), VelocityComponent.class).velocity();
				velocity.y = 10f;
			}
		}
	}

	private boolean isDisabled(Engine engine, Entity entity) {
		return engine.entityManager.hasComponent(entity.id(), DisabledComponent.class);
	}

	private void updateCamera(Engine engine) {
		Vector3f pos1 = engine.entityManager.getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = engine.entityManager.getComponent(player2.id(), PositionComponent.class).position();

		// calculate midpoint between players
		Vector3f midpoint = new Vector3f((pos1.x + pos2.x) / 2f, (pos1.y + pos2.y) / 2f, (pos1.z + pos2.z) / 2f);

		// calculate distance between players
		float distance = pos1.distance(pos2);

		// calculate camera distance based on player separation
		float baseDistance = 15f;
		float cameraDistance = Math.max(baseDistance, distance * 0.8f + 3f);

		// position camera behind and above the midpoint
		Vector3f cameraPos = engine.entityManager.getComponent(camera.id(), PositionComponent.class).position();
		cameraPos.set(midpoint.x, midpoint.y + 2f, midpoint.z + cameraDistance);

		engine.setCamera(camera);
	}

}