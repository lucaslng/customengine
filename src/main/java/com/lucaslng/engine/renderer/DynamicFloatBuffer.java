package com.lucaslng.engine.renderer;

import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glBufferSubData;

public final class DynamicFloatBuffer extends Buffer {

	public DynamicFloatBuffer(int target, float[] data) {
		super(target);
		glBufferData(target, data, GL_DYNAMIC_DRAW);
	}

	public void update(float[] data) {
		bind();
		glBufferSubData(target(), 0, data);
	}
}
