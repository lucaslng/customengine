package com.lucaslng.engine.renderer;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glGetUniformLocation;
import static org.lwjgl.opengl.GL20C.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;
import org.lwjgl.opengl.awt.AWTGLCanvas;

import com.lucaslng.engine.EngineSettings;

public class Renderer extends AWTGLCanvas {
	private final EngineSettings engineSettings;
	private int vao;
	private int shaderProgram;
	private final Matrix4f projectionMatrix, viewMatrix, modelMatrix;
	private int projectionLoc, viewLoc, modelLoc;
	private int angleCube;
	private float aspectRatio;

	public Renderer(EngineSettings engineSettings) {
		super(engineSettings.getGLData());
		this.engineSettings = engineSettings;
		setAspectRatio();
		addComponentListener(new CanvasListener());
		addKeyListener(new CanvasListener());
		addMouseMotionListener(new CanvasListener());
		projectionMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();
		modelMatrix = new Matrix4f().identity();
	}

	@Override
	public void initGL() {
		createCapabilities();

		// Load shaders
		shaderProgram = ShaderUtils.createShaderProgram("vertex.glsl", "fragment.glsl");

		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		// Define vertex attributes
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);

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
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glUseProgram(shaderProgram);
		glBindVertexArray(vao);

		// Set transformations
		projectionMatrix.setPerspective(engineSettings.FOV, aspectRatio, 0.1f, engineSettings.Z_FAR);
		// viewMatrix.rotationXYZ(-player.pitch, -player.yaw, 0.0f)
		// 		.translate(player.getPosition().mul(-1.0f, new Vector3f()));
	
		// System.out.println(player.getPosition());
		// viewMatrix.identity().lookAt();
		modelMatrix.rotationX((float) Math.toRadians(angleCube))
				.rotateZ((float) Math.toRadians(angleCube));

		glUniformMatrix4fv(projectionLoc, false, projectionMatrix.get(new float[16]));
		glUniformMatrix4fv(viewLoc, false, viewMatrix.get(new float[16]));
		glUniformMatrix4fv(modelLoc, false, modelMatrix.get(new float[16]));

		// glDrawElements(GL_TRIANGLES, cube.getIndices().length, GL_UNSIGNED_INT, 0);
		swapBuffers();
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
			// switch (e.getKeyCode()) {
			// 	case KeyEvent.VK_A -> player.moveX(1.0f);
			// 	case KeyEvent.VK_E -> player.moveX(-1.0f);
			// 	case KeyEvent.VK_W -> player.moveZ(1.0f);
			// 	case KeyEvent.VK_O -> player.moveZ(-1.0f);
			// 	case KeyEvent.VK_SPACE -> player.moveY(-1.0f);
			// 	case KeyEvent.VK_SHIFT -> player.moveY(1.0f);
			// 	default -> {
			// 	}
			// }
		}

		@Override
		public void keyReleased(KeyEvent e) {

		}

		@Override
		public void mouseDragged(MouseEvent e) {

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// player.yaw += (e.getX() - lastMouseX) * EngineSettings.sensitivity;
			// player.pitch -= (e.getY() - lastMouseY) * EngineSettings.sensitivity;
			// lastMouseX = e.getX();
			// lastMouseY = e.getY();
		}

	}
}
