package com.lucaslng.engine.renderer;

import static org.lwjgl.opengl.GL11C.GL_ZERO;
import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glGenBuffers;

public final class IndexBuffer {

	private final int id;

	public IndexBuffer(int[] data) {
		id = glGenBuffers(); 
		bind();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
	}

	public void bind() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
	}

	public void unbind() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_ZERO);
	}

}
