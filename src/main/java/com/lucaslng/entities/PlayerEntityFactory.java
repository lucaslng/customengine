package com.lucaslng.entities;


import org.joml.Vector3f;

import com.lucaslng.engine.components.HeadRotationComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.VelocityComponent;
import com.lucaslng.engine.entities.AbstractEntityFactory;

public class PlayerEntityFactory implements AbstractEntityFactory {

	private final Vector3f position;

	public PlayerEntityFactory() {
		position = new Vector3f();
	}

	public PlayerEntityFactory(float x, float y, float z) {
		position = new Vector3f(x, y, z);
	}

	@Override
	public Record[] components() {
		PositionComponent positionComponent = new PositionComponent(position);
		VelocityComponent velocityComponent = new VelocityComponent(new Vector3f());
		HeadRotationComponent headRotationComponent = new HeadRotationComponent(new Vector3f());
		return new Record[] { positionComponent, velocityComponent, headRotationComponent };
	}

}
