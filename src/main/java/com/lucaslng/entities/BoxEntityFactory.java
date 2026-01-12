package com.lucaslng.entities;

import org.joml.Vector3f;

import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;

public class BoxEntityFactory implements AbstractEntityFactory {

	private static final int[] indices = new int[] {
			0, 1, 2, 2, 3, 0, // back
			4, 5, 6, 6, 7, 4, // right
			8, 9, 10, 10, 11, 8, // front
			12, 13, 14, 14, 15, 12, // left
			16, 17, 18, 18, 19, 16, // top
			20, 21, 22, 22, 23, 20 // bottom
	};
	private final float[] vertices;
	private final Vector3f position;
	private final float hx, hy, hz;
	private final String materialName;

	public BoxEntityFactory(float x, float y, float z, float width, float height, float length, String materialName) {
		position = new Vector3f(x, y, z);
		this.materialName = materialName;
		hx = width / 2f;
		hy = height / 2f;
		hz = length / 2f;
		vertices = new float[] {
				// BACK (-Z)
				-hx, -hy, -hz, 0, 0, -1, 0, 0,
				hx, -hy, -hz, 0, 0, -1, 1, 0,
				hx, hy, -hz, 0, 0, -1, 1, 1,
				-hx, hy, -hz, 0, 0, -1, 0, 1,

				// RIGHT (+X)
				hx, -hy, -hz, 1, 0, 0, 0, 0,
				hx, -hy, hz, 1, 0, 0, 1, 0,
				hx, hy, hz, 1, 0, 0, 1, 1,
				hx, hy, -hz, 1, 0, 0, 0, 1,

				// FRONT (+Z)
				hx, -hy, hz, 0, 0, 1, 0, 0,
				-hx, -hy, hz, 0, 0, 1, 1, 0,
				-hx, hy, hz, 0, 0, 1, 1, 1,
				hx, hy, hz, 0, 0, 1, 0, 1,

				// LEFT (-X)
				-hx, -hy, hz, -1, 0, 0, 0, 0,
				-hx, -hy, -hz, -1, 0, 0, 1, 0,
				-hx, hy, -hz, -1, 0, 0, 1, 1,
				-hx, hy, hz, -1, 0, 0, 0, 1,

				// TOP (+Y)
				-hx, hy, -hz, 0, 1, 0, 0, 0,
				hx, hy, -hz, 0, 1, 0, 1, 0,
				hx, hy, hz, 0, 1, 0, 1, 1,
				-hx, hy, hz, 0, 1, 0, 0, 1,

				// BOTTOM (-Y)
				-hx, -hy, hz, 0, -1, 0, 0, 0,
				hx, -hy, hz, 0, -1, 0, 1, 0,
				hx, -hy, -hz, 0, -1, 0, 1, 1,
				-hx, -hy, -hz, 0, -1, 0, 0, 1
		};

	}

	@Override
	public Object[] components() {
		return new Object[] { new PositionComponent(position), new RotationComponent(new Vector3f()),
				new MeshComponent(new SubMesh[] { new SubMesh(vertices, indices, materialName) }),
				new RigidBodyComponent(0f, 1f, 1f, 1f), new AABBComponent(new Vector3f(hx, hy, hz)) };
	}
}
