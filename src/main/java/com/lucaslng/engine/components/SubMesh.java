package com.lucaslng.engine.components;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;

import com.lucaslng.engine.renderer.*;

public final class SubMesh {
	public final VertexArray vao;
	public final int indexCount;
	public final String materialName;

	public SubMesh(float[] vertices, int[] indices, String materialName) {
		this.materialName = materialName;
		this.vao = new VertexArray();
		vao.bind();

		// vertex buffer object
		FloatBuffer vbo = new FloatBuffer(GL_ARRAY_BUFFER, vertices);
		BufferLayout layout = new BufferLayout();
		layout.add(GL_FLOAT, 3, false); // position
		layout.add(GL_FLOAT, 3, true); // normal
		layout.add(GL_FLOAT, 2, true); // uv
		vao.addBuffer(vbo, layout);

		new IntegerBuffer(GL_ELEMENT_ARRAY_BUFFER, indices); // ebo

		this.indexCount = indices.length;

		vao.unbind();
	}
}
