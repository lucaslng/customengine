package com.lucaslng.engine.renderer;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_SHORT;

public record VertexBufferElement(int count, int type, boolean normalized) {

	protected int size() {
		switch (type) {
			case GL_FLOAT -> { return Float.BYTES; }
			case GL_UNSIGNED_INT -> { return Integer.BYTES; }
			case GL_UNSIGNED_BYTE -> { return Byte.BYTES; }
			case GL_UNSIGNED_SHORT -> { return Short.BYTES; }
			default -> { throw new IllegalArgumentException("Bad vertex buffer element type!"); }
		}
	}
	
}
