package com.lucaslng;

public class Cube implements Shape {

	private final float[] vertices;
	private static final int[] indices = new int[] {
		0, 1, 2, 2, 3, 0, // Back face
		1, 5, 6, 6, 2, 1, // Right face
		5, 4, 7, 7, 6, 5, // Front face
		4, 0, 3, 3, 7, 4, // Left face
		3, 2, 6, 6, 7, 3, // Top face
		4, 5, 1, 1, 0, 4 // Bottom face
	};

	public Cube(float x, float y, float z, float size) {
		size /= 2;
		// Define cube vertices (8 unique positions)
		vertices = new float[] {
			// Position (X, Y, Z) // Color (R, G, B)
			-size + x, -size + y, -size + z, 1.0f, 0.0f, 0.0f, // 0: Bottom-left-back (red)
			size + x, -size + y, -size + z, 0.0f, 1.0f, 0.0f, // 1: Bottom-right-back (green)
			size + x, size + y, -size + z, 0.0f, 0.0f, 1.0f, // 2: Top-right-back (blue)
			-size + x, size + y, -size + z, 1.0f, 1.0f, 0.0f, // 3: Top-left-back (yellow)
			-size + x, -size + y, size + z, 1.0f, 0.0f, 1.0f, // 4: Bottom-left-front (magenta)
			size + x, -size + y, size + z, 0.0f, 1.0f, 1.0f, // 5: Bottom-right-front (cyan)
			size + x, size + y, size + z, 1.0f, 1.0f, 1.0f, // 6: Top-right-front (white)
			-size + x, size + y, size + z, 0.0f, 0.0f, 0.0f // 7: Top-left-front (black)
		};

	}

	@Override
	public float[] getVertices() {
		return vertices;
	}

	@Override
	public int[] getIndices() {
		return indices;
	}

}
