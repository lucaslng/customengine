package com.lucaslng;

import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

public abstract class AWTGLCanvasExplicitDispose extends AWTGLCanvas {
	
	public AWTGLCanvasExplicitDispose(GLData data) {
		super(data);
	}

	@Override
	public void disposeCanvas() {
	}

	public void doDisposeCanvas() {
		super.disposeCanvas();
	}
}