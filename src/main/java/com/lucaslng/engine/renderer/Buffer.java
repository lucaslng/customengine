package com.lucaslng.engine.renderer;

import static org.lwjgl.opengl.GL11C.GL_ZERO;
import static org.lwjgl.opengl.GL15C.*;

abstract class Buffer {

	private final int id, target;

	public Buffer(int target) {
		id = glGenBuffers();
		this.target = target;
		bind();
	}

	public final void bind() {
		glBindBuffer(target, id);
	}

	public final void unbind() {
		glBindBuffer(target, GL_ZERO);
	}

	public final void delete() {
		glDeleteBuffers(id);
	}

	public final int id() {
		return id;
	}

	protected final int target() {
		return target;
	}
}
