package com.lucaslng.ecs.systems;

import com.lucaslng.ecs.EntityManager;
import com.lucaslng.ecs.components.*;

public class MovementSystem {
	
	private final EntityManager entityManager;

	public MovementSystem(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void update(float deltaTime) {
		for (int entityId : entityManager.getEntitiesWith(PositionComponent.class, VelocityComponent.class)) {
			PositionComponent positionComponent = entityManager.getComponent(entityId, PositionComponent.class);
      VelocityComponent velocityComponent = entityManager.getComponent(entityId, VelocityComponent.class);
			positionComponent.position().x += velocityComponent.x() * deltaTime;
			positionComponent.position().y += velocityComponent.y() * deltaTime;
		}
	}
	
}
