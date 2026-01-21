package com.lucaslng.engine.systems;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joml.Vector3f;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.ButtonComponent;
import com.lucaslng.engine.components.ButtonMoveComponent;
import com.lucaslng.engine.components.CoinComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.VelocityComponent;

public class ButtonPlatforms {
	private static final float SNAP_DISTANCE = 0.02f;

	private final EntityManager entityManager;

	public ButtonPlatforms(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void update(double dt) {
		Set<Integer> activeButtonFlags = new HashSet<>();
		for (int buttonId : entityManager.getEntitiesWith(ButtonComponent.class)) {
			ButtonComponent button = entityManager.getComponent(buttonId, ButtonComponent.class);
			if (button.isActive) {
				activeButtonFlags.add(button.flag);
			}
		}

		Map<Integer, Integer> collectedCoinCounts = new HashMap<>();
		for (int coinId : entityManager.getEntitiesWith(CoinComponent.class)) {
			CoinComponent coin = entityManager.getComponent(coinId, CoinComponent.class);
			if (coin.collected) {
				collectedCoinCounts.merge(coin.flag, 1, Integer::sum);
			}
		}

		float dtf = (float) dt;
		for (int platformId : entityManager.getEntitiesWith(ButtonMoveComponent.class, PositionComponent.class,
				VelocityComponent.class)) {
			ButtonMoveComponent mover = entityManager.getComponent(platformId, ButtonMoveComponent.class);
			Vector3f position = entityManager.getComponent(platformId, PositionComponent.class).position();
			Vector3f velocity = entityManager.getComponent(platformId, VelocityComponent.class).velocity();

			Vector3f target = new Vector3f(mover.startPosition);
			int collectedForFlag = collectedCoinCounts.getOrDefault(mover.flag, 0);
			if (activeButtonFlags.contains(mover.flag) || collectedForFlag >= mover.requiredCoins) {
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
			position.add(new Vector3f(velocity).mul(dtf));
		}
	}
}
