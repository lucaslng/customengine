package com.lucaslng.entities;


import org.joml.Vector3f;

import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;

public class PlayerEntityFactory implements AbstractEntityFactory {

	private static final int[] indices = new int[] {
			0, 1, 2, 2, 3, 0, // Back face
			1, 5, 6, 6, 2, 1, // Right face
			5, 4, 7, 7, 6, 5, // Front face
			4, 0, 3, 3, 7, 4, // Left face
			3, 2, 6, 6, 7, 3, // Top face
			4, 5, 1, 1, 0, 4 // Bottom face
	};
	private final Vector3f position;
	private final float[] vertices;

	public PlayerEntityFactory(float x, float y, float z) {
		position = new Vector3f(x, y, z);
		float size = 0.5f;
		vertices = new float[] {
				-size, -size, -size,
				size, -size, -size,
				size, size, -size,
				-size, size, -size,
				-size, -size, size,
				size, -size, size,
				size, size, size,
				-size, size, size
		};
	}

	@Override
	public Record[] components() {
		PositionComponent positionComponent = new PositionComponent(position);
		VelocityComponent velocityComponent = new VelocityComponent(new Vector3f());
		HeadRotationComponent headRotationComponent = new HeadRotationComponent(new Vector3f(0.0f, -90.0f, 0.0f));
		return new Record[] { positionComponent, velocityComponent, headRotationComponent, new RotationComponent(new Vector3f()), new MeshComponent(vertices, indices) };
	}

}
