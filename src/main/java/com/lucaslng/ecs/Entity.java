package com.lucaslng.ecs;

import java.util.concurrent.atomic.AtomicInteger;

public class Entity {

	private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
	private final int id;

	public Entity() {
		this.id = ID_GENERATOR.incrementAndGet();
	}

	public int id() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Entity entity) {
			return id == entity.id;
		}
		return false;
	}

}
