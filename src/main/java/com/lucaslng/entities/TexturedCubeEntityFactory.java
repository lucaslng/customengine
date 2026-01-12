package com.lucaslng.entities;

import org.joml.Vector3f;

import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;

public class TexturedCubeEntityFactory implements AbstractEntityFactory {

	private static final int[] indices = {
			0, 1, 2, 2, 3, 0, // front
			4, 5, 6, 6, 7, 4, // back
			8, 9, 10, 10, 11, 8, // left
			12, 13, 14, 14, 15, 12, // right
			16, 17, 18, 18, 19, 16, // bottom
			20, 21, 22, 22, 23, 20 // top
	};

	private final float[] vertices;
	private final Vector3f position;
	private final float s;

	public TexturedCubeEntityFactory(float x, float y, float z, float size) {
		position = new Vector3f(x, y, z);
		this.s = size;

		// 24 vertices: position + uv
		vertices = new float[] {
				// Front (+Z)
				-s, -s, s, 0, 0, 1, 0, 0,
				s, -s, s, 0, 0, 1, 1, 0,
				s, s, s, 0, 0, 1, 1, 1,
				-s, s, s, 0, 0, 1, 0, 1,

				// Back (-Z)
				s, -s, -s, 0, 0, -1, 0, 0,
				-s, -s, -s, 0, 0, -1, 1, 0,
				-s, s, -s, 0, 0, -1, 1, 1,
				s, s, -s, 0, 0, -1, 0, 1,

				// Left (-X)
				-s, -s, -s, -1, 0, 0, 0, 0,
				-s, -s, s, -1, 0, 0, 1, 0,
				-s, s, s, -1, 0, 0, 1, 1,
				-s, s, -s, -1, 0, 0, 0, 1,

				// Right (+X)
				s, -s, s, 1, 0, 0, 0, 0,
				s, -s, -s, 1, 0, 0, 1, 0,
				s, s, -s, 1, 0, 0, 1, 1,
				s, s, s, 1, 0, 0, 0, 1,

				// Bottom (-Y)
				-s, -s, -s, 0, -1, 0, 0, 0,
				s, -s, -s, 0, -1, 0, 1, 0,
				s, -s, s, 0, -1, 0, 1, 1,
				-s, -s, s, 0, -1, 0, 0, 1,

				// Top (+Y)
				-s, s, s, 0, 1, 0, 0, 0,
				s, s, s, 0, 1, 0, 1, 0,
				s, s, -s, 0, 1, 0, 1, 1,
				-s, s, -s, 0, 1, 0, 0, 1
		};
	}

	@Override
	public Object[] components() {
		return new Object[] { new PositionComponent(position), new RotationComponent(new Vector3f()),
				new RigidBodyComponent(0f, 1f, 1f, 1f), new AABBComponent(new Vector3f(s)),
				new MeshComponent(new SubMesh[] { new SubMesh(vertices, indices, "Cat") }) };
	};
}
