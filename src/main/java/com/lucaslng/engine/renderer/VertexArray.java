package com.lucaslng.engine.renderer;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11C.GL_ZERO;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

public class VertexArray {

	private final int id;
	private int i, length;

	protected VertexArray() {
		id = glGenVertexArrays();
		i = 0;
		length = -1;
	}

	protected void addBuffer(Buffer vb, BufferLayout layout) {
		bind();
		vb.bind();
		ArrayList<VertexBufferElement> elements = layout.elements();
		int offset = 0;
		for (int j=i;j<i+elements.size();j++) {
			VertexBufferElement element = elements.get(j-i);
			glEnableVertexAttribArray(j);
			glVertexAttribPointer(j, element.count(), element.type(), element.normalized(), layout.stride(), offset);
			offset += element.count() * element.size();
		}
		i += elements.size();
	}

	protected void addBuffer(Buffer vb, VertexBufferElement element) {
		bind();
		vb.bind();
		glEnableVertexAttribArray(i);
		glVertexAttribPointer(i, element.count(), element.type(), element.normalized(), element.size() * element.count(), 0);
		i++;
	}

	protected void bind() {
		glBindVertexArray(id);
	}

	protected void unbind() {
		glBindVertexArray(GL_ZERO);
	}

	protected void delete() {
		glDeleteVertexArrays(id);
	}
	
}
