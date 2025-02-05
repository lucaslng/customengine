package com.lucaslng;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glFrustum;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glViewport;
import org.lwjgl.opengl.awt.GLData;

public class GameCanvas extends AWTGLCanvasExplicitDispose {

	private float angleCube;

	public GameCanvas(GLData data) {
		super(data);
		angleCube = 0.0f;
	}

	private static void gluPerspective(float fovy, float aspect, float near, float far) {
			float bottom = -near * (float) Math.tan(fovy / 2);
			float top = -bottom;
			float left = aspect * bottom;
			float right = -left;
			glFrustum(left, right, bottom, top, near, far);
		}

	@Override
	public void initGL() {
		createCapabilities();
		glClearColor(0.3f, 0.4f, 0.5f, 1);
		glClearDepth(1.0f);
		glEnable(GL_DEPTH_TEST); // enable z buffer culling
		glDepthFunc(GL_LEQUAL); // set type of depth test
		glShadeModel(GL_SMOOTH);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // Nice perspective corrections

		glMatrixMode(GL_PROJECTION); // To operate on the Projection matrix
		glLoadIdentity(); // Reset
		// Enable perspective projection with fovy, aspect, zNear and zFar
		int w = getFramebufferWidth();
		int h = getFramebufferHeight();
		float aspect = (float) w / h;
		gluPerspective(45.0f, aspect, 0.1f, 100.0f);
	}

	@Override
	public void paintGL() {
		int w = getFramebufferWidth();
		int h = getFramebufferHeight();
		// float aspect = (float) w / h;
		// double now = System.currentTimeMillis() * 0.001;
		// float width = (float) Math.abs(Math.sin(now * 0.3));
		glViewport(0, 0, w, h);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glMatrixMode(GL_MODELVIEW);

		glLoadIdentity();
		glTranslatef(1.5f, 0.0f, -7.0f);
		glRotatef(angleCube, 1.0f, 1.0f, 1.0f);

		glBegin(GL_QUADS); // Begin drawing the color cube with 6 quads
		// Top face (y = 1.0f)
		// Define vertices in counter-clockwise (CCW) order with normal pointing out
		glColor3f(0.0f, 1.0f, 0.0f); // Green
		glVertex3f(1.0f, 1.0f, -1.0f);
		glVertex3f(-1.0f, 1.0f, -1.0f);
		glVertex3f(-1.0f, 1.0f, 1.0f);
		glVertex3f(1.0f, 1.0f, 1.0f);

		// Bottom face (y = -1.0f)
		glColor3f(1.0f, 0.5f, 0.0f); // Orange
		glVertex3f(1.0f, -1.0f, 1.0f);
		glVertex3f(-1.0f, -1.0f, 1.0f);
		glVertex3f(-1.0f, -1.0f, -1.0f);
		glVertex3f(1.0f, -1.0f, -1.0f);

		// Front face (z = 1.0f)
		glColor3f(1.0f, 0.0f, 0.0f); // Red
		glVertex3f(1.0f, 1.0f, 1.0f);
		glVertex3f(-1.0f, 1.0f, 1.0f);
		glVertex3f(-1.0f, -1.0f, 1.0f);
		glVertex3f(1.0f, -1.0f, 1.0f);

		// Back face (z = -1.0f)
		glColor3f(1.0f, 1.0f, 0.0f); // Yellow
		glVertex3f(1.0f, -1.0f, -1.0f);
		glVertex3f(-1.0f, -1.0f, -1.0f);
		glVertex3f(-1.0f, 1.0f, -1.0f);
		glVertex3f(1.0f, 1.0f, -1.0f);

		// Left face (x = -1.0f)
		glColor3f(0.0f, 0.0f, 1.0f); // Blue
		glVertex3f(-1.0f, 1.0f, 1.0f);
		glVertex3f(-1.0f, 1.0f, -1.0f);
		glVertex3f(-1.0f, -1.0f, -1.0f);
		glVertex3f(-1.0f, -1.0f, 1.0f);

		// Right face (x = 1.0f)
		glColor3f(1.0f, 0.0f, 1.0f); // Magenta
		glVertex3f(1.0f, 1.0f, -1.0f);
		glVertex3f(1.0f, 1.0f, 1.0f);
		glVertex3f(1.0f, -1.0f, 1.0f);
		glVertex3f(1.0f, -1.0f, -1.0f);
		glEnd(); // End of drawing color-cube
		swapBuffers();
		angleCube -= 0.25f;
	}
}