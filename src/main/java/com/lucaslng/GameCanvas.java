package com.lucaslng;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL41C.*;
import org.lwjgl.opengl.awt.GLData;

public class GameCanvas extends AWTGLCanvasExplicitDispose {
	private int vao, vbo, ebo;
	private int shaderProgram;
	private Matrix4f projectionMatrix, viewMatrix, modelMatrix;
	private int projectionLoc, viewLoc, modelLoc;
	private int angleCube;
	private float aspectRatio;
	private Cube cube;
	private Camera player = new Camera();
	private float lastMouseX, lastMouseY;
	private long lastFrame = System.nanoTime();
	private long targetFrameTime = 1000000000 / Settings.FPS;

	public GameCanvas(GLData data) {
		super(data);
		lastMouseX = (float) getFramebufferWidth() / 2;
		lastMouseY = (float) getFramebufferHeight() / 2;
		setAspectRatio();
		addComponentListener(new CanvasListener());
		addKeyListener(new CanvasListener());
		addMouseMotionListener(new CanvasListener());
		projectionMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();
		modelMatrix = new Matrix4f().identity();
		cube = new Cube(0.0f, 0.0f, 0.0f, 1.0f);
	}

	@Override
	public void initGL() {
		createCapabilities();

		// Load shaders
		shaderProgram = ShaderUtils.createShaderProgram("vertex.glsl", "fragment.glsl");

		// Generate VAO, VBO, EBO
		vao = glGenVertexArrays();
		vbo = glGenBuffers();
		ebo = glGenBuffers();

		glBindVertexArray(vao);

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, cube.getVertices(), GL_DYNAMIC_DRAW);

		// Upload index data
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, cube.getIndices(), GL_DYNAMIC_DRAW);

		// Define vertex attributes
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
		glEnableVertexAttribArray(1);

		// Unbind
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);

		// Get shader uniform locations
		projectionLoc = glGetUniformLocation(shaderProgram, "projection");
		viewLoc = glGetUniformLocation(shaderProgram, "view");
		modelLoc = glGetUniformLocation(shaderProgram, "model");

		// Enable depth testing
		glEnable(GL_DEPTH_TEST);
	}

	@Override
	public void paintGL() {
		glViewport(0, 0, getFramebufferWidth(), getFramebufferHeight());
		long currentTime = System.nanoTime();
		long deltaTime = currentTime - lastFrame;
		if (deltaTime >= targetFrameTime) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			glUseProgram(shaderProgram);
			glBindVertexArray(vao);

			// Set transformations
			projectionMatrix.setPerspective(Settings.FOV, aspectRatio, 0.1f, Settings.Z_FAR);
			viewMatrix.rotationXYZ(-player.pitch, -player.yaw, 0.0f)
				.translate(player.getPosition().mul(-1.0f, new Vector3f()));
			System.out.println(deltaTime + " - " + targetFrameTime);
			System.out.println(player.getPosition());
			System.out.println(player.pitch + " - " + player.yaw);

			// System.out.println(player.getPosition());
			// viewMatrix.identity().lookAt();
			modelMatrix.rotationX((float) Math.toRadians(angleCube))
				.rotateZ((float) Math.toRadians(angleCube));

			glUniformMatrix4fv(projectionLoc, false, projectionMatrix.get(new float[16]));
			glUniformMatrix4fv(viewLoc, false, viewMatrix.get(new float[16]));
			glUniformMatrix4fv(modelLoc, false, modelMatrix.get(new float[16]));

			glDrawElements(GL_TRIANGLES, cube.getIndices().length, GL_UNSIGNED_INT, 0);
			swapBuffers();
			angleCube -= 1.0f;
			angleCube %= 360;
			lastFrame = currentTime;
		}
	}

	private void setAspectRatio() {
		aspectRatio = (float) getFramebufferWidth() / getFramebufferHeight();
	}

	private class CanvasListener implements ComponentListener, KeyListener, MouseMotionListener {

		@Override
		public void componentResized(ComponentEvent e) {
			setAspectRatio();
			
		}

		@Override
		public void componentMoved(ComponentEvent e) {

		}

		@Override
		public void componentShown(ComponentEvent e) {

		}

		@Override
		public void componentHidden(ComponentEvent e) {

		}

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {
			System.out.println("Key - " + e.getKeyCode() + " - pressed.");
			switch (e.getKeyCode()) {
				case KeyEvent.VK_A -> player.moveX(1.0f);
				case KeyEvent.VK_E -> player.moveX(-1.0f);
				case KeyEvent.VK_W -> player.moveZ(1.0f);
				case KeyEvent.VK_O -> player.moveZ(-1.0f);
				case KeyEvent.VK_SPACE -> player.moveY(-1.0f);
				case KeyEvent.VK_SHIFT -> player.moveY(1.0f);
				default -> {
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {

		}

		@Override
		public void mouseDragged(MouseEvent e) {

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			player.yaw += (e.getX() - lastMouseX) * Settings.sensitivity;
			player.pitch -= (e.getY() - lastMouseY) * Settings.sensitivity;
			lastMouseX = e.getX();
			lastMouseY = e.getY();
		}

	}
}
