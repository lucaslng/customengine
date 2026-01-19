package com.lucaslng.engine.ui;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;

import com.lucaslng.engine.renderer.*;

public class RectElement extends UIElement {

	private static final int[] indices = {
			0, 1, 2, 2, 3, 0, // front face only
	};
	private static final float[] vertices = new float[] {
			0f, 0f, 1f, 1f,
			1f, 0f, 0f, 1f,
			1f, 1f, 0f, 0f,
			0f, 1f, 1f, 0f,
	};
	public static final int indexCount = indices.length;

	public final VertexArray vao;

	public RectElement(float x, float y, float width, float height, XAlignment xAlignment, YAlignment yAlignment) {
		super(x, y, width, height, xAlignment, yAlignment);
		this.vao = new VertexArray();
		vao.bind();

		// vertex buffer object
		FloatBuffer vbo = new FloatBuffer(GL_ARRAY_BUFFER, vertices);
		vbo.bind();
		BufferLayout layout = new BufferLayout();
		layout.add(GL_FLOAT, 2, false); // position, x and y only
		layout.add(GL_FLOAT, 2, true); // uv
		vao.addBuffer(vbo, layout);

		new IntegerBuffer(GL_ELEMENT_ARRAY_BUFFER, indices); // ebo

		vao.unbind();
		vbo.unbind();
	}

}
