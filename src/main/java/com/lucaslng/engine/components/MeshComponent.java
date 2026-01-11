package com.lucaslng.engine.components;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;

import com.lucaslng.engine.renderer.*;

public final class MeshComponent {
	public final VertexArray vao;
	public final int indexCount;

	public MeshComponent(float[] vertices, int[] indices) {
		this.vao = new VertexArray();
		vao.bind();

		// vertex buffer object
		FloatBuffer vbo = new FloatBuffer(GL_ARRAY_BUFFER, vertices);
		BufferLayout layout = new BufferLayout();
		layout.add(GL_FLOAT, 3, false); // position
		layout.add(GL_FLOAT, 2, true); // uv
		// layout.add(GL_FLOAT, 3, true); // normal
		vao.addBuffer(vbo, layout);

		new IntegerBuffer(GL_ELEMENT_ARRAY_BUFFER, indices); // ebo

		this.indexCount = indices.length;

		vao.unbind();
	}
}
