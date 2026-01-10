package com.lucaslng.entities;

import org.joml.Vector3f;

import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;

public class CubeEntityFactory implements AbstractEntityFactory {

	private static final int[] indices = new int[] {
			0, 1, 2, 2, 3, 0, // Back face
			1, 5, 6, 6, 2, 1, // Right face
			5, 4, 7, 7, 6, 5, // Front face
			4, 0, 3, 3, 7, 4, // Left face
			3, 2, 6, 6, 7, 3, // Top face
			4, 5, 1, 1, 0, 4 // Bottom face
	};
	private final float[] vertices;
	private final Vector3f position;
	private final float size;

	public CubeEntityFactory(float x, float y, float z, float size) {
		position = new Vector3f(x, y, z);
		size /= 2;
		this.size = size;
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
		return new Record[] { new PositionComponent(position), new RotationComponent(new Vector3f()),
				new MeshComponent(vertices, indices), new RigidBodyComponent(0f, 1f, 1f, 1f), new AABBComponent(new Vector3f(size)) };
	}
}
