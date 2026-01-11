package com.lucaslng.entities;

import org.joml.Vector3f;
import org.joml.Vector4f;

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
		float size = 1f;
		vertices = new float[] {
				-size, -size, -size, 0f, 0f,
				size, -size, -size, 0f, 0f,
				size, size, -size, 0f, 0f,
				-size, size, -size, 0f, 0f,
				-size, -size, size, 0f, 0f,
				size, -size, size, 0f, 0f,
				size, size, size, 0f, 0f,
				-size, size, size, 0f, 0f
		};
	}

	@Override
	public Object[] components() {
		PositionComponent positionComponent = new PositionComponent(position);
		VelocityComponent velocityComponent = new VelocityComponent(new Vector3f());
		HeadRotationComponent headRotationComponent = new HeadRotationComponent(new Vector3f(0.0f, -90.0f, 0.0f));
		RigidBodyComponent rigidBodyComponent = new RigidBodyComponent(20f, 0.9f, 0.99f, 1f);
		AABBComponent aabbComponent = new AABBComponent(new Vector3f(2f));
		RotationComponent rotationComponent = new RotationComponent(new Vector3f());
		MeshComponent meshComponent = new MeshComponent(vertices, indices);
		MaterialComponent materialComponent = new MaterialComponent(new Vector4f(0f, 1f, 1f, 1f));
		GroundedComponent groundedComponent = new GroundedComponent();
		return new Object[] { positionComponent, velocityComponent, headRotationComponent,
				rotationComponent, meshComponent, materialComponent, rigidBodyComponent,
				aabbComponent, groundedComponent };
	}

}
