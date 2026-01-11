package com.lucaslng.entities;

import org.joml.Vector3f;

import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;

public class ExitEntityFactory implements AbstractEntityFactory {

	private static final int[] indices = {
			0, 1, 2, 2, 3, 0, // front
	};

	private final float[] vertices;
	private final Vector3f position;

	public ExitEntityFactory(float x, float y) {
		position = new Vector3f(x, y, -1f);

		float s = 2f;

		// 24 vertices: position + uv
		vertices = new float[] {
				// Front
				-s, -s, 0f, 1f, 1f,
				s, -s, 0f, 0f, 1f,
				s, s, 0f, 0f, 0f,
				-s, s, 0f, 1f, 0f,
		};
	}

	@Override
	public Object[] components() {
		return new Object[] { new PositionComponent(position), new ExitComponent(),
				new MeshComponent(new SubMesh[] { new SubMesh(vertices, indices, "Cat") }) };
	};
}
