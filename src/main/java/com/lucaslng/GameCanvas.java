package com.lucaslng;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glViewport;
import org.lwjgl.opengl.awt.GLData;

public class GameCanvas extends AWTGLCanvasExplicitDispose {

	public GameCanvas(GLData data) {
		super(data);

	}

	@Override
	public void initGL() {
		createCapabilities();
		glClearColor(0.3f, 0.4f, 0.5f, 1);
	}

	@Override
	public void paintGL() {
		int w = getFramebufferWidth();
		int h = getFramebufferHeight();
		float aspect = (float) w / h;
		double now = System.currentTimeMillis() * 0.001;
		float width = (float) Math.abs(Math.sin(now * 0.3));
		glClear(GL_COLOR_BUFFER_BIT);
		glViewport(0, 0, w, h);
		glBegin(GL_QUADS);
		glColor3f(0.4f, 0.6f, 0.8f);
		glVertex2f(-0.75f * width / aspect, 0.0f);
		glVertex2f(0, -0.75f);
		glVertex2f(+0.75f * width / aspect, 0);
		glVertex2f(0, +0.75f);
		glEnd();
		swapBuffers();
	}
}