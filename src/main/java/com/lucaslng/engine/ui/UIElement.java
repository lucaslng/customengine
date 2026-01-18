package com.lucaslng.engine.ui;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;

import java.util.concurrent.atomic.AtomicInteger;

import com.lucaslng.engine.renderer.*;

public class UIElement {

	private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
	public float x, y, width, height;
	public final XAlignment xAlignment;
	public final YAlignment yAlignment;
	public final VertexArray vao;
	private final int id;

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

	// all parameters should be between 0f and 1f because they are percentages
	public UIElement(float x, float y, float width, float height, XAlignment xAlignment, YAlignment yAlignment) {
		this.id = ID_GENERATOR.getAndIncrement();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.xAlignment = xAlignment;
		this.yAlignment = yAlignment;

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

	public int id() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof UIElement uiElement) {
			return id == uiElement.id;
		}
		return false;
	}
}
