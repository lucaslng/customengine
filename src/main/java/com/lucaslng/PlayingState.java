package com.lucaslng;

import java.util.Set;

import org.joml.Vector3f;
import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameState;
import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.engine.systems.Deaths;
import com.lucaslng.engine.systems.Exits;
import com.lucaslng.engine.systems.LevelTransition;
import com.lucaslng.engine.systems.Levels;
import com.lucaslng.engine.systems.Levels.Level;
import com.lucaslng.engine.systems.Physics;
import com.lucaslng.engine.systems.Rotations;
import com.lucaslng.entities.CameraEntityFactory;
import com.lucaslng.entities.PlayerEntityFactory;

class PlayingState extends GameState {
	private final Entity player1, player2, camera;
	private final Physics physics;
	private final Levels levels;
	private final Exits exits;
	private final Deaths deaths;
	private final LevelTransition transition;

	public PlayingState(Engine engine) {
		super(engine);

		levels = new Levels();
		Level level = levels.currentLevel();

		player1 = entityManager.buildEntity(new PlayerEntityFactory(level.player1Spawn()));
		player2 = entityManager.buildEntity(new PlayerEntityFactory(level.player2Spawn()));

		for (AbstractEntityFactory entityFactory : level.entityFactories())
			entityManager.buildEntity(entityFactory);

		camera = entityManager.buildEntity(new CameraEntityFactory());
		setCamera();
		physics = new Physics(entityManager);
		exits = new Exits(entityManager);
		deaths = new Deaths(entityManager, player1, player2, exits);
		transition = new LevelTransition();
	}

	@Override
	public void init() {
		
	}

	@Override
	public GameStates doLoop(double dt) {
		float baseSpeed = 6f;
		float speed = baseSpeed * (float) dt;

		transition.update((float) dt);

		if (!transition.isTransitioning()) {
			handlePlayerMovement(engine, player1, speed, engine.settings.player1Left, engine.settings.player1Right, engine.settings.player1Jump);
			handlePlayerMovement(engine, player2, speed, engine.settings.player2Left, engine.settings.player2Right, engine.settings.player2Jump);

			physics.step(dt);
			deaths.checkDeaths(levels.currentLevel());
			exits.handleExits(player1, player2);

			if (exits.player1Exited && exits.player2Exited) {
				transition.startTransition(new Runnable() {
					@Override
					public void run() {
						performLevelChange(engine);
					}
				});
			}
		}

		updateCamera(engine);
		engine.renderer.setFadeAlpha(transition.getFadeAlpha());

		return GameStates.PLAYING;
	}

	private void performLevelChange(Engine engine) {
		levels.currentLevelIndex++;

		if (levels.currentLevelIndex > Levels.LEVEL_COUNT) {
			System.out.println("You won!");
			System.exit(0);
		}

		Level level = levels.currentLevel();

		Vector3f pos1 = entityManager.getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = entityManager.getComponent(player2.id(), PositionComponent.class).position();
		pos1.set(levels.currentLevel().player1Spawn(), 0f);
		pos2.set(levels.currentLevel().player2Spawn(), 0f);

		DeathsComponent deaths1 = entityManager.getComponent(player1.id(), DeathsComponent.class);
		DeathsComponent deaths2 = entityManager.getComponent(player2.id(), DeathsComponent.class);
		deaths1.nextLevel();
		deaths2.nextLevel();

		Set<Integer> entityIds = entityManager.entities;
		for (Integer entityId : entityIds) {
			if (entityId == player1.id() || entityId == player2.id() || entityId == camera.id())
				continue;
			entityManager.removeEntity(entityId);
		}

		for (AbstractEntityFactory entityFactory : level.entityFactories())
			entityManager.buildEntity(entityFactory);

		entityManager.removeComponent(player1.id(), DisabledComponent.class);
		entityManager.removeComponent(player2.id(), DisabledComponent.class);
		exits.refresh();
	}

	private void handlePlayerMovement(Engine engine, Entity player, float speed, int leftKey, int rightKey, int jumpKey) {
		if (isDisabled(engine, player))
			return;

		boolean leftPressed = engine.inputHandler.isKeyHeld(leftKey);
		boolean rightPressed = engine.inputHandler.isKeyHeld(rightKey);

		Vector3f position = entityManager.getComponent(player.id(), PositionComponent.class).position();
		Vector3f rotation = entityManager.getComponent(player.id(), RotationComponent.class).rotation();

		if (leftPressed && rightPressed) {
		} else if (leftPressed) {
			position.x -= speed;
			rotation.y = 0f;
		} else if (rightPressed) {
			position.x += speed;
			rotation.y = Rotations.PI;
		}

		// jumping
		if (engine.inputHandler.isKeyHeld(jumpKey)) {
			GroundedComponent grounded = entityManager.getComponent(player.id(), GroundedComponent.class);
			if (grounded.isGrounded) {
				Vector3f velocity = entityManager.getComponent(player.id(), VelocityComponent.class).velocity();
				velocity.y = 10f;
			}
		}
	}

	private boolean isDisabled(Engine engine, Entity entity) {
		return entityManager.hasComponent(entity.id(), DisabledComponent.class);
	}

	private void updateCamera(Engine engine) {
		Vector3f pos1 = entityManager.getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = entityManager.getComponent(player2.id(), PositionComponent.class).position();

		// calculate midpoint between players
		Vector3f midpoint = new Vector3f((pos1.x + pos2.x) / 2f, (pos1.y + pos2.y) / 2f, (pos1.z + pos2.z) / 2f);

		// calculate distance between players
		float distance = pos1.distance(pos2);

		// calculate camera distance based on player separation
		float baseDistance = 15f;
		float cameraDistance = Math.max(baseDistance, distance * 0.8f + 3f);

		// position camera behind and above the midpoint
		Vector3f cameraPos = entityManager.getComponent(camera.id(), PositionComponent.class).position();
		cameraPos.set(midpoint.x, midpoint.y + 2f, midpoint.z + cameraDistance);

		setCamera();
	}

	@Override
	public void dispose() {

	}

	private void setCamera() {
		engine.setCamera(entityManager.getComponent(camera.id(), PositionComponent.class).position(),
				entityManager.getComponent(camera.id(), HeadRotationComponent.class).rotation(), camera.id());
	}

}