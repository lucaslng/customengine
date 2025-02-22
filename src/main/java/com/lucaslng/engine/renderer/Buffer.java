package com.lucaslng.engine.renderer;

import static org.lwjgl.opengl.GL11C.GL_ZERO;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL15C.glGenBuffers;

abstract public class Buffer {

	private final int id, target;

	protected Buffer(int target) {
		id = glGenBuffers();
		this.target = target;
		bind();
	}

	protected final void bind() {
		glBindBuffer(target, id);
	}

	protected final void unbind() {
		glBindBuffer(target, GL_ZERO);
	}

	protected final void delete() {
		glDeleteBuffers(id);
	}
	
}
