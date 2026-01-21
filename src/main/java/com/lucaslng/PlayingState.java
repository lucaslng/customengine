package com.lucaslng;

import java.awt.Color;
import java.util.Set;

import org.joml.Vector3f;
import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameState;
import com.lucaslng.engine.GameStateSwitch;
import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.engine.systems.*;
import com.lucaslng.engine.systems.Levels.Level;
import com.lucaslng.engine.ui.*;
import com.lucaslng.entities.CameraEntityFactory;
import com.lucaslng.entities.PlayerEntityFactory;

class PlayingState extends GameState {
	private static final float ROPE_MAX_DISTANCE = 10f;
	private static final float ROPE_STIFFNESS = 27f;
	private static final float ROPE_DAMPING = 2.5f;

	private final Entity player1, player2, camera;
	private final Physics physics;
	private final Levels levels;
	private final Exits exits;
	private final Deaths deaths;
	private final Timers timers;
	private final LevelTransition transition;
	private final Buttons buttons;
	private final ButtonPlatforms buttonPlatforms;
	private final BlinkingPlatforms blinkingPlatforms;

	public PlayingState(Engine engine) {
		super(engine);

		Button homeButton = new Button(40f, 40f, 250f, 80f, XAlignment.RIGHT, YAlignment.TOP, ColorList.RED);
		homeButton.addOperation(() -> gameStateSwitch = new GameStateSwitch(GameStates.MAIN_MENU, null));
		uiManager.elements.add(homeButton);
		uiManager.elements.add(new Text(260f, 20f, 0f, 0f, XAlignment.RIGHT, YAlignment.TOP, "Home",
				new TextStyle("Pixeled", 8f, Color.BLACK)));

		levels = new Levels();
		Level level = levels.currentLevel();

		Text timerText = new Text(-120f, 10f, 0f, 0f, XAlignment.CENTER, YAlignment.TOP, level.timer() + "", new TextStyle("Pixeled", 10f, Color.BLACK));
		timerText.visible = level.hasTimer();
		uiManager.elements.add(timerText);

		timers = new Timers(timerText);
		timers.setTimer(level.timer());

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
		buttons = new Buttons(entityManager, player1, player2);
		buttonPlatforms = new ButtonPlatforms(entityManager);
		blinkingPlatforms = new BlinkingPlatforms(entityManager);
	}

	@Override
	public void init(Engine engine, Object payload) {
		super.init(engine, payload);
		if (payload != null) {
			int level = (int) payload;
			performLevelChange(engine, level);
		}
		if (engine.settings.refreshLevels)
			levels.refreshLevels();
	}

	@Override
	public void doLoop(double dt) {
		float baseSpeed = 8f;
		float speed = baseSpeed * (float) dt;

		transition.update((float) dt);

		if (!transition.isTransitioning()) {
			handlePlayerMovement(engine, player1, speed, engine.settings.player1Left.key, engine.settings.player1Right.key,
					engine.settings.player1Jump.key);
			handlePlayerMovement(engine, player2, speed, engine.settings.player2Left.key, engine.settings.player2Right.key,
					engine.settings.player2Jump.key);
			applyRopeTension(player1, player2, (float) dt);

			buttons.update();
			buttonPlatforms.update(dt);
			blinkingPlatforms.update((float) dt);
			physics.step(dt);
			deaths.checkDeaths(levels.currentLevel(), (levelTimer) -> timers.setTimer(levelTimer));
			exits.handleExits(player1, player2, dt);

			if (exits.player1Exited && exits.player2Exited) {
				transition.startTransition(new Runnable() {
					@Override
					public void run() {
						performLevelChange(engine, levels.currentLevelIndex + 1);
					}
				});
			}

			timers.step(dt, levels.currentLevel(), (currentLevel, setTimer) -> deaths.killBothPlayers(currentLevel, setTimer));
		}

		updateCamera(engine);
		updateRopeRender();
		engine.renderer.setFadeAlpha(transition.getFadeAlpha());
	}

	@Override
	public void dispose() {
		engine.renderer.setRopeEnabled(false);
	}

	private void performLevelChange(Engine engine, int newLevel) {
		levels.currentLevelIndex = newLevel;

		if (levels.currentLevelIndex > Levels.LEVEL_COUNT) {
			System.out.println("You won!");
			System.exit(0);
		}

		Level level = levels.currentLevel();
		timers.setTimer(level.timer());

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
				velocity.y = 23;
			}
		}
	}

	private boolean isDisabled(Engine engine, Entity entity) {
		return entityManager.hasComponent(entity.id(), DisabledComponent.class);
	}

	private void updateRopeRender() {
		if (!levels.currentLevel().ropeEnabled()) {
			engine.renderer.setRopeEnabled(false);
			return;
		}

		boolean enabled = !isDisabled(engine, player1) && !isDisabled(engine, player2);
		engine.renderer.setRopeEnabled(enabled);

		if (!enabled) {
			return;
		}

		Vector3f pos1 = entityManager.getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = entityManager.getComponent(player2.id(), PositionComponent.class).position();
		engine.renderer.setRopeEndpoints(pos1, pos2);

		float distance = pos1.distance(pos2);
		float stretch = Math.max(0f, distance - ROPE_MAX_DISTANCE);
		float tension = Math.min(stretch / ROPE_MAX_DISTANCE, 1f);
		engine.renderer.setRopeTension(tension);
	}

	private void applyRopeTension(Entity a, Entity b, float dt) {
		if (!levels.currentLevel().ropeEnabled()) {
			return;
		}

		if (isDisabled(engine, a) || isDisabled(engine, b)) {
			return;
		}

		Vector3f posA = entityManager.getComponent(a.id(), PositionComponent.class).position();
		Vector3f posB = entityManager.getComponent(b.id(), PositionComponent.class).position();
		Vector3f delta = new Vector3f(posB).sub(posA);
		float distance = delta.length();

		// avoid division by zero
		if (distance < 0.0001f) {
			return;
		}

		Vector3f dir = delta.div(distance);

		Vector3f velA = entityManager.getComponent(a.id(), VelocityComponent.class).velocity();
		Vector3f velB = entityManager.getComponent(b.id(), VelocityComponent.class).velocity();

		float relVelAlongRope = new Vector3f(velB).sub(velA).dot(dir);
		float stretch = distance - ROPE_MAX_DISTANCE;
		float SOFT_STOP_BUFFER = 1.5f;

		float massA = entityManager.getComponent(a.id(), RigidBodyComponent.class).mass();
		float massB = entityManager.getComponent(b.id(), RigidBodyComponent.class).mass();
		float invMassA = 1f / massA;
		float invMassB = 1f / massB;
		float invMassSum = invMassA + invMassB;

		if (stretch <= 0f) {
			Vector3f horizontalDir = new Vector3f(dir.x, 0f, dir.z);
			float horizontalLength = horizontalDir.length();

			if (horizontalLength < 0.0001f) {
				return;
			}

			horizontalDir.div(horizontalLength);

			float velAAlongRope = velA.dot(horizontalDir);
			float velBAlongRope = velB.dot(horizontalDir);
			float relVelAlongRopeHorizontal = velBAlongRope - velAAlongRope;

			float horizontalDistance = horizontalLength;

			if (relVelAlongRopeHorizontal > 0f) {
				float projectedDistance = horizontalDistance + relVelAlongRopeHorizontal * dt;
				if (projectedDistance > ROPE_MAX_DISTANCE) {
					float maxAllowedRelVel = (ROPE_MAX_DISTANCE - horizontalDistance) / dt;
					float excessVel = relVelAlongRopeHorizontal - maxAllowedRelVel;
					float correctionImpulse = excessVel / invMassSum;

					velA.add(new Vector3f(horizontalDir).mul(correctionImpulse * invMassA));
					velB.sub(new Vector3f(horizontalDir).mul(correctionImpulse * invMassB));
				}
			}

			if (velAAlongRope > 0f) {
				velA.sub(new Vector3f(horizontalDir).mul(velAAlongRope));
			}
			if (velBAlongRope < 0f) {
				velB.sub(new Vector3f(horizontalDir).mul(velBAlongRope));
			}

			return;
		}

		float stretchRatio = Math.min(stretch / ROPE_MAX_DISTANCE, 1f);
		float softFactor = stretchRatio * stretchRatio;
		float springForce = ROPE_STIFFNESS * stretch * softFactor;

		float dampingForce = ROPE_DAMPING * relVelAlongRope;
		float totalForce = springForce + dampingForce;

		if (totalForce < 0f) {
			totalForce = 0f;
		}

		if (stretch < SOFT_STOP_BUFFER && relVelAlongRope < 0f) {
			float softStopRatio = stretch / SOFT_STOP_BUFFER;
			totalForce *= softStopRatio;
		}

		if (relVelAlongRope < 0f && dt > 0f) {
			float maxClosingSpeed = stretch / dt;
			float maxAllowedRelVel = -maxClosingSpeed;

			if (relVelAlongRope < maxAllowedRelVel) {
				totalForce = 0f;
			} else {
				float maxForce = (relVelAlongRope - maxAllowedRelVel) / (dt * invMassSum);
				if (totalForce > maxForce) {
					totalForce = maxForce;
				}
			}
		}

		// hehehe F = ma
		float accelA = totalForce * invMassA;
		float accelB = totalForce * invMassB;

		Vector3f impulseA = new Vector3f(dir).mul(accelA * dt);
		Vector3f impulseB = new Vector3f(dir).mul(accelB * dt);

		velA.add(impulseA);
		velB.sub(impulseB);
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

	private void setCamera() {
		engine.setCamera(entityManager.getComponent(camera.id(), PositionComponent.class).position(),
				entityManager.getComponent(camera.id(), HeadRotationComponent.class).rotation(), camera.id());
	}

}
