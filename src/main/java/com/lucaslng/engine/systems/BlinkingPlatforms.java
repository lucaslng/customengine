package com.lucaslng.engine.systems;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.BlinkComponent;
import com.lucaslng.engine.components.DisabledComponent;

public class BlinkingPlatforms {
	private final EntityManager entityManager;

	public BlinkingPlatforms(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void update(float dt) {
		if (dt <= 0f) {
			return;
		}
		for (int entityId : entityManager.getEntitiesWith(BlinkComponent.class)) {
			BlinkComponent blink = entityManager.getComponent(entityId, BlinkComponent.class);
			blink.timer -= dt;
			if (blink.timer > 0f) {
				continue;
			}

			blink.active = !blink.active;
			blink.timer = blink.active ? blink.onDuration : blink.offDuration;

			if (blink.active) {
				entityManager.removeComponent(entityId, DisabledComponent.class);
			} else {
				entityManager.addComponent(entityId, new DisabledComponent());
			}
		}
	}
}
