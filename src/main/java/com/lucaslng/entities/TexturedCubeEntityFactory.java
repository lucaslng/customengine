package com.lucaslng.entities;

import org.joml.Vector3f;

import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.renderer.Texture;
import com.lucaslng.engine.utils.FileReader;

public class TexturedCubeEntityFactory implements AbstractEntityFactory {

	private static final int[] indices = new int[36];
	private final float[] vertices, textureCoordinates;
	private final Vector3f position;
	private final float size;

	static {
		for (int i = 0; i < 36; i++) {
			indices[i] = i;
		}
	}

	public TexturedCubeEntityFactory(float x, float y, float z, float size) {
		position = new Vector3f(x, y, z);
		this.size = size;
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
	public Object[] components() {
		return new Object[] { new PositionComponent(position), new RotationComponent(new Vector3f()),
				new RigidBodyComponent(0f, 1f, 1f, 1f), new AABBComponent(new Vector3f(size / 2f)),
				new MeshComponent(vertices, indices, textureCoordinates),
				new MaterialComponent(new Texture(FileReader.readImage("freakycat.png")))
		};
	}
}
