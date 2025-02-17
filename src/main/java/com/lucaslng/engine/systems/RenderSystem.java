package com.lucaslng.engine.systems;

import com.lucaslng.engine.components.MeshComponent;
import com.lucaslng.engine.EntityManager;

public class RenderSystem extends System {

	public RenderSystem(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	public void update(float deltaTime) {
		for (int entityId : entityManager.getEntitiesWith(MeshComponent.class)) {
			MeshComponent meshComponent = entityManager.getComponent(entityId, MeshComponent.class);
			
		}
	}

}
