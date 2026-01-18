package com.lucaslng.engine.renderer;

import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glBufferSubData;

import java.nio.IntBuffer;

public final class IntegerBuffer extends Buffer {

	public IntegerBuffer(int target, int[] data) {
		super(target);
		glBufferData(target, data, GL_STATIC_DRAW);
	}

	public IntegerBuffer(int target, int[] data, int usage) {
		super(target);
		glBufferData(target, data, usage);
	}
	
	public void updateData(int[] data) {
		glBufferSubData(target(), 0l, data);
	}

	public void updateData(IntBuffer data) {
		glBufferSubData(target(), 0l, data);
	}


}
