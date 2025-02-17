package com.lucaslng.engine.components;

public class CubeMeshComponentFactory {
	private static final int[] indices = new int[] {
			0, 1, 2, 2, 3, 0, // Back face
			1, 5, 6, 6, 2, 1, // Right face
			5, 4, 7, 7, 6, 5, // Front face
			4, 0, 3, 3, 7, 4, // Left face
			3, 2, 6, 6, 7, 3, // Top face
			4, 5, 1, 1, 0, 4 // Bottom face
	};

	static MeshComponent buildCubeMeshComponent(float x, float y, float z, float size) {
		size /= 2;
		float[] vertices = new float[] {
				-size + x, -size + y, -size + z,
				size + x, -size + y, -size + z,
				size + x, size + y, -size + z,
				-size + x, size + y, -size + z,
				-size + x, -size + y, size + z,
				size + x, -size + y, size + z,
				size + x, size + y, size + z,
				-size + x, size + y, size + z
		};
		return new MeshComponent(vertices, indices);
	}
}
