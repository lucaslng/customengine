package com.lucaslng.engine.systems;

import com.lucaslng.engine.EntityManager;

public abstract class System {

	protected final EntityManager entityManager;
	
	public System(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	abstract public void update(float deltaTime);

}
