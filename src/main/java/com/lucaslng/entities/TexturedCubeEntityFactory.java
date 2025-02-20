package com.lucaslng.entities;

import org.joml.Vector3f;

import com.lucaslng.engine.components.MeshComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.RotationComponent;
import com.lucaslng.engine.components.TextureComponent;
import com.lucaslng.engine.entities.AbstractEntityFactory;

public class TexturedCubeEntityFactory implements AbstractEntityFactory {

	private static final int[] indices = new int[36];
	private final float[] vertices, textureCoordinates;
	private final Vector3f position;

	static {
		for (int i = 0; i < 36; i++) {
			indices[i] = i;
		}
	}

	public TexturedCubeEntityFactory(float x, float y, float z, float size) {
		position = new Vector3f(x, y, z);
		size /= 2;
		vertices = new float[] {
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

		textureCoordinates = new float[] {
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
	}

	@Override
	public Record[] components() {
		return new Record[] { new PositionComponent(position), new RotationComponent(new Vector3f()),
				new MeshComponent(vertices, indices),
				new TextureComponent(textureCoordinates)
		};
	}
}
