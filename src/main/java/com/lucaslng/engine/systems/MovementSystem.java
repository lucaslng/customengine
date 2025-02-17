package com.lucaslng.engine.systems;

import org.joml.Vector3f;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.*;

public class MovementSystem extends System {

	public MovementSystem(EntityManager entityManager) {
		super(entityManager);
	}

  @Override
	public void update(float deltaTime) {
		for (int entityId : entityManager.getEntitiesWith(PositionComponent.class, VelocityComponent.class)) {
			PositionComponent positionComponent = entityManager.getComponent(entityId, PositionComponent.class);
			VelocityComponent velocityComponent = entityManager.getComponent(entityId, VelocityComponent.class);

			Vector3f deltaVelocity = null;
			velocityComponent.velocity().mul(deltaTime, deltaVelocity);
			positionComponent.position().add(deltaVelocity);
		}
	}

}
