package com.lucaslng.engine.renderer;

import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBufferData;

public final class IntegerBuffer extends Buffer {

	public IntegerBuffer(int target, int[] data) {
		super(target);
		glBufferData(target, data, GL_STATIC_DRAW);
	}
}
