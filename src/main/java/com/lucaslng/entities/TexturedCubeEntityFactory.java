package com.lucaslng.entities;

import org.joml.Vector3f;

import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;

public class TexturedCubeEntityFactory implements AbstractEntityFactory {

	private static final int[] indices = new int[36];
	private final float[] vertices;
	private final Vector3f position;
	private final float size;

	private static final float[] uvs = new float[] {
			0.0f, 0.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
			1.0f, 1.0f,
			0.0f, 1.0f,
			0.0f, 0.0f,

			0.0f, 0.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
			1.0f, 1.0f,
			0.0f, 1.0f,
			0.0f, 0.0f,

			1.0f, 0.0f,
			1.0f, 1.0f,
			0.0f, 1.0f,
			0.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 0.0f,

			1.0f, 0.0f,
			1.0f, 1.0f,
			0.0f, 1.0f,
			0.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 0.0f,

			0.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,
			1.0f, 0.0f,
			0.0f, 0.0f,
			0.0f, 1.0f,

			0.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,
			1.0f, 0.0f,
			0.0f, 0.0f,
			0.0f, 1.0f
	};

	static {
		for (int i = 0; i < 36; i++) {
			indices[i] = i;
		}
	}

	public TexturedCubeEntityFactory(float x, float y, float z, float size) {
		position = new Vector3f(x, y, z);
		this.size = size;
		float[] positions = new float[] {
				-size, -size, -size,
				size, -size, -size,
				size, size, -size,
				size, size, -size,
				-size, size, -size,
				-size, -size, -size,

				-size, -size, size,
				size, -size, size,
				size, size, size,
				size, size, size,
				-size, size, size,
				-size, -size, size,

				-size, size, size,
				-size, size, -size,
				-size, -size, -size,
				-size, -size, -size,
				-size, -size, size,
				-size, size, size,

				size, size, size,
				size, size, -size,
				size, -size, -size,
				size, -size, -size,
				size, -size, size,
				size, size, size,

				-size, -size, -size,
				size, -size, -size,
				size, -size, size,
				size, -size, size,
				-size, -size, size,
				-size, -size, -size,

				-size, size, -size,
				size, size, -size,
				size, size, size,
				size, size, size,
				-size, size, size,
				-size, size, -size
		};

		vertices = new float[positions.length + uvs.length];
		int p = 0, u = 0;
		for (int i = 0; i < vertices.length; i += 5) {
			vertices[i] = positions[p];
			vertices[i + 1] = positions[p + 1];
			vertices[i + 2] = positions[p + 2];
			vertices[i + 3] = uvs[u];
			vertices[i + 4] = uvs[u + 1];
			p += 3;
			u += 2;
		}
	}

	@Override
	public Object[] components() {
		return new Object[] { new PositionComponent(position), new RotationComponent(new Vector3f()),
				new RigidBodyComponent(0f, 1f, 1f, 1f), new AABBComponent(new Vector3f(size)),
				new MeshComponent(new SubMesh[] { new SubMesh(vertices, indices, "Cat") }) };
	};
}
