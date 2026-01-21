package com.lucaslng.entities;

import org.joml.Vector3f;

import com.lucaslng.engine.components.HeadRotationComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.VelocityComponent;
import com.lucaslng.engine.entities.AbstractEntityFactory;

public class CameraEntityFactory implements AbstractEntityFactory {

	private final Vector3f position;

	public CameraEntityFactory() {
		position = new Vector3f();
	}

	public CameraEntityFactory(float x, float y, float z) {
		position = new Vector3f(x, y, z);
	}

	@Override
	public Object[] components() {
		PositionComponent positionComponent = new PositionComponent(position);
		VelocityComponent velocityComponent = new VelocityComponent(new Vector3f());
		HeadRotationComponent headRotationComponent = new HeadRotationComponent(new Vector3f(0.0f, -90.0f, 0.0f));
		return new Object[] { positionComponent, velocityComponent, headRotationComponent };
	}

}
