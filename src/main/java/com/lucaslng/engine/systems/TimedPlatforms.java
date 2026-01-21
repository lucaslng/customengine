package com.lucaslng.engine.systems;

import org.joml.Vector3f;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.TimedMoveComponent;
import com.lucaslng.engine.components.VelocityComponent;

public class TimedPlatforms {
	private static final float SNAP_DISTANCE = 0.02f;

	private final EntityManager entityManager;

	public TimedPlatforms(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void update(double dt) {
		if (dt <= 0d) {
			return;
		}

		float dtf = (float) dt;
		for (int platformId : entityManager.getEntitiesWith(TimedMoveComponent.class, PositionComponent.class,
				VelocityComponent.class)) {
			TimedMoveComponent mover = entityManager.getComponent(platformId, TimedMoveComponent.class);
			Vector3f position = entityManager.getComponent(platformId, PositionComponent.class).position();
			Vector3f velocity = entityManager.getComponent(platformId, VelocityComponent.class).velocity();

			if (mover.pauseTimer > 0f) {
				mover.pauseTimer -= dtf;
				velocity.set(0f, 0f, 0f);
				if (mover.pauseTimer <= 0f && mover.arrived) {
					mover.active = !mover.active;
					mover.arrived = false;
				}
				continue;
			}

			Vector3f target = new Vector3f(mover.startPosition);
			if (mover.active) {
				target.add(mover.moveOffset);
			}

			Vector3f toTarget = new Vector3f(target).sub(position);
			float distance = toTarget.length();
			if (distance < SNAP_DISTANCE) {
				position.set(target);
				velocity.set(0f, 0f, 0f);
				if (!mover.arrived) {
					mover.arrived = true;
					if (mover.pauseDuration > 0f) {
						mover.pauseTimer = mover.pauseDuration;
					} else {
						mover.active = !mover.active;
						mover.arrived = false;
					}
				}
				continue;
			}

			mover.arrived = false;
			float maxSpeed = distance / dtf;
			float speed = Math.min(mover.speed, maxSpeed);
			toTarget.normalize().mul(speed);
			velocity.set(toTarget);
			position.add(new Vector3f(velocity).mul(dtf));
		}
	}
}
