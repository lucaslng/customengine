package com.lucaslng.engine.renderer;

import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBufferData;

final class IntBuffer extends Buffer {

	protected IntBuffer(int target, int[] data) {
		super(target);
		glBufferData(target, data, GL_STATIC_DRAW);
	}
	
}
