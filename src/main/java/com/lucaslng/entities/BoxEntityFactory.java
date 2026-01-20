package com.lucaslng.entities;

import java.util.Arrays;

import org.joml.Vector3f;

import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.renderer.SubMesh;

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
	private final Object[] extraComponents;

	public BoxEntityFactory(float x, float y, float z, float width, float height, float length, String materialName, Object... extraComponents) {
		position = new Vector3f(x, y, z);
		this.materialName = materialName;
		this.extraComponents = extraComponents == null ? new Object[0] : extraComponents;
		hx = width / 2f;
		hy = height / 2f;
		hz = length / 2f;
		float tx = width / 10f;
		float ty = height / 10f;
		float tz = length / 10f;
		vertices = new float[] {
				// BACK (-Z)
				-hx, -hy, -hz, 0, 0, -1, 0, 0,
				hx, -hy, -hz, 0, 0, -1, tx, 0,
				hx, hy, -hz, 0, 0, -1, tx, ty,
				-hx, hy, -hz, 0, 0, -1, 0, ty,

				// RIGHT (+X)
				hx, -hy, -hz, 1, 0, 0, 0, 0,
				hx, -hy, hz, 1, 0, 0, 0, tz,
				hx, hy, hz, 1, 0, 0, ty, tz,
				hx, hy, -hz, 1, 0, 0, ty, 0,

				// FRONT (+Z)
				hx, -hy, hz, 0, 0, 1, tx, 0,
				-hx, -hy, hz, 0, 0, 1, 0, 0,
				-hx, hy, hz, 0, 0, 1, 0, ty,
				hx, hy, hz, 0, 0, 1, tx, ty,

				// LEFT (-X)
				-hx, -hy, hz, -1, 0, 0, 0, tz,
				-hx, -hy, -hz, -1, 0, 0, 0, 0,
				-hx, hy, -hz, -1, 0, 0, ty, 0,
				-hx, hy, hz, -1, 0, 0, ty, tz,

				// TOP (+Y)
				-hx, hy, -hz, 0, 1, 0, 0, 0,
				hx, hy, -hz, 0, 1, 0, tx, 0,
				hx, hy, hz, 0, 1, 0, tx, tz,
				-hx, hy, hz, 0, 1, 0, 0, tz,

				// BOTTOM (-Y)
				-hx, -hy, hz, 0, -1, 0, 0, tz,
				hx, -hy, hz, 0, -1, 0, tx, tz,
				hx, -hy, -hz, 0, -1, 0, tx, 0,
				-hx, -hy, -hz, 0, -1, 0, 0, 0
		};

	}

	@Override
	public Object[] components() {
		Object[] baseComponents = new Object[] { new PositionComponent(position), new RotationComponent(new Vector3f()),
				new MeshComponent(new SubMesh[] { new SubMesh(vertices, indices, materialName) }),
				new RigidBodyComponent(0f, 1f, 1f, 1f), new AABBComponent(new Vector3f(hx, hy, hz)) };

		if (extraComponents.length == 0) {
			return baseComponents;
		}
		
		Object[] components = Arrays.copyOf(baseComponents, baseComponents.length + extraComponents.length);
		System.arraycopy(extraComponents, 0, components, baseComponents.length, extraComponents.length);
		return components;
	}
}
