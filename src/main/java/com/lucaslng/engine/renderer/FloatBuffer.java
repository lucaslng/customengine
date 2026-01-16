package com.lucaslng.engine.renderer;

import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBufferData;

public final class FloatBuffer extends Buffer {

	public FloatBuffer(int target, float[] data) {
		super(target);
		glBufferData(target, data, GL_STATIC_DRAW);
	}

	public FloatBuffer(int target, float[] data, int usage) {
		super(target);
		glBufferData(target, data, usage);
	}

}
