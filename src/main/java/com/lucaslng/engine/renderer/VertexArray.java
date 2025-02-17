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

	public VertexArray() {
		id = glGenVertexArrays();
	}

	public void addBuffer(VertexBuffer vb, VertexBufferLayout layout) {
		bind();
		vb.bind();
		ArrayList<VertexBufferElement> elements = layout.elements();
		int offset = 0;
		for (int i=0;i<elements.size();i++) {
			VertexBufferElement element = elements.get(i);
			glEnableVertexAttribArray(i);
			glVertexAttribPointer(i, element.count(), element.type(), element.normalized(), layout.stride(), offset);
			offset += element.count() * element.size();
		}
		
	}

	public void bind() {
		glBindVertexArray(id);
	}

	public void unbind() {
		glBindVertexArray(GL_ZERO);
	}

	public void delete() {
		unbind();
		glDeleteVertexArrays(id);
	}
	
}
