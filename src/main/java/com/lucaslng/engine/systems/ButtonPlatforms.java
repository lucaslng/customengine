package com.lucaslng.engine.systems;

import org.joml.Vector3f;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.ButtonComponent;
import com.lucaslng.engine.components.ButtonMoveComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.VelocityComponent;

public class ButtonPlatforms {
	private static final float SNAP_DISTANCE = 0.02f;

	private final EntityManager entityManager;

	public ButtonPlatforms(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void update(double dt) {
		boolean anyButtonPressed = false;
		for (int buttonId : entityManager.getEntitiesWith(ButtonComponent.class)) {
			if (entityManager.getComponent(buttonId, ButtonComponent.class).isPressed) {
				anyButtonPressed = true;
				break;
			}
		}

		float dtf = (float) dt;
		for (int platformId : entityManager.getEntitiesWith(ButtonMoveComponent.class, PositionComponent.class,
				VelocityComponent.class)) {
			ButtonMoveComponent mover = entityManager.getComponent(platformId, ButtonMoveComponent.class);
			Vector3f position = entityManager.getComponent(platformId, PositionComponent.class).position();
			Vector3f velocity = entityManager.getComponent(platformId, VelocityComponent.class).velocity();

			Vector3f target = new Vector3f(mover.startPosition);
			if (anyButtonPressed) {
				target.add(mover.pressedOffset);
			}

			Vector3f toTarget = new Vector3f(target).sub(position);
			float distance = toTarget.length();
			if (distance < SNAP_DISTANCE || dtf <= 0f) {
				position.set(target);
				velocity.set(0f, 0f, 0f);
				continue;
			}

			float maxSpeed = distance / dtf;
			float speed = Math.min(mover.speed, maxSpeed);
			toTarget.normalize().mul(speed);
			velocity.set(toTarget);
		}
	}
}
