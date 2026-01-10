package com.lucaslng.engine.components;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;

import com.lucaslng.engine.renderer.*;

public final class MeshComponent {
	public final VertexArray vao;
	public final int indexCount;

	public MeshComponent(float[] vertices, int[] indices, float[] uvs) {
		this.vao = new VertexArray();
		vao.bind();

		// position VBO
		FloatBuffer posVbo = new FloatBuffer(GL_ARRAY_BUFFER, vertices);
		BufferLayout posLayout = new BufferLayout();
		posLayout.add(GL_FLOAT, 3, false);
		vao.addBuffer(posVbo, posLayout);

		// uv VBO
		FloatBuffer uvVbo = new FloatBuffer(GL_ARRAY_BUFFER, uvs);
		BufferLayout uvLayout = new BufferLayout();
		uvLayout.add(GL_FLOAT, 2, true);
		vao.addBuffer(uvVbo, uvLayout);

		IntegerBuffer ebo = new IntegerBuffer(GL_ELEMENT_ARRAY_BUFFER, indices);

		this.indexCount = indices.length;

		vao.unbind();
	}
}
