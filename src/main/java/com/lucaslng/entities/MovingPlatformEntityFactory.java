package com.lucaslng.entities;

import org.joml.Vector3f;

import java.util.Arrays;

import com.lucaslng.engine.components.AABBComponent;
import com.lucaslng.engine.components.ButtonMoveComponent;
import com.lucaslng.engine.components.MeshComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.RigidBodyComponent;
import com.lucaslng.engine.components.RotationComponent;
import com.lucaslng.engine.components.VelocityComponent;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.renderer.SubMesh;

public class MovingPlatformEntityFactory implements AbstractEntityFactory {

	private static final int[] INDICES = new int[] {
			0, 1, 2, 2, 3, 0, // back
			4, 5, 6, 6, 7, 4, // right
			8, 9, 10, 10, 11, 8, // front
			12, 13, 14, 14, 15, 12, // left
			16, 17, 18, 18, 19, 16, // top
			20, 21, 22, 22, 23, 20 // bottom
	};

	private final Vector3f position;
	private final Vector3f moveOffset;
	private final float speed;
	private final float[] vertices;
	private final float hx, hy, hz;
	private final String materialName;
	private final Object[] extraComponents;
	private final boolean buttonControlled;

	public MovingPlatformEntityFactory(float x, float y, float z, float width, float height, float length,
			Vector3f moveOffset, float speed) {
		this(x, y, z, width, height, length, moveOffset, speed, "Brown", true);
	}

	public MovingPlatformEntityFactory(float x, float y, float z, float width, float height, float length,
			Vector3f moveOffset, float speed, String materialName) {
		this(x, y, z, width, height, length, moveOffset, speed, materialName, true);
	}

	public MovingPlatformEntityFactory(float x, float y, float z, float width, float height, float length,
			Vector3f moveOffset, float speed, String materialName, boolean buttonControlled, Object... extraComponents) {
		position = new Vector3f(x, y, z);
		this.moveOffset = new Vector3f(moveOffset);
		this.speed = speed;
		this.materialName = materialName;
		this.extraComponents = extraComponents == null ? new Object[0] : extraComponents;
		this.buttonControlled = buttonControlled;
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
		Object[] baseComponents;
		if (buttonControlled) {
			baseComponents = new Object[] { new PositionComponent(position), new RotationComponent(new Vector3f()),
					new MeshComponent(new SubMesh[] { new SubMesh(vertices, INDICES, materialName) }),
					new VelocityComponent(new Vector3f()), new RigidBodyComponent(200f, 0.1f, 0.9f, 0f),
					new AABBComponent(new Vector3f(hx, hy, hz)),
					new ButtonMoveComponent(position, moveOffset, speed) };
		} else {
			baseComponents = new Object[] { new PositionComponent(position), new RotationComponent(new Vector3f()),
					new MeshComponent(new SubMesh[] { new SubMesh(vertices, INDICES, materialName) }),
					new VelocityComponent(new Vector3f()), new RigidBodyComponent(200f, 0.1f, 0.9f, 0f),
					new AABBComponent(new Vector3f(hx, hy, hz)) };
		}

		if (extraComponents.length == 0) {
			return baseComponents;
		}

		Object[] components = Arrays.copyOf(baseComponents, baseComponents.length + extraComponents.length);
		System.arraycopy(extraComponents, 0, components, baseComponents.length, extraComponents.length);
		return components;
	}
}
