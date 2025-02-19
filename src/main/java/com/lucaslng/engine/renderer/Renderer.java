package com.lucaslng.engine.renderer;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.GL_ZERO;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL11C.glGetError;
import static org.lwjgl.opengl.GL11C.glViewport;
import org.lwjgl.opengl.awt.AWTGLCanvas;

import com.lucaslng.engine.EngineSettings;
import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.MeshComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.RotationComponent;

public final class Renderer extends AWTGLCanvas {
	private final EngineSettings engineSettings;
	private final EntityManager entityManager;
	private final ShaderProgram[] shaderPrograms = new ShaderProgram[2];
	private final Matrix4f projectionMatrix;
	private final Camera camera;
	private float aspectRatio;
	private boolean isRendering;

	public Renderer(EngineSettings engineSettings, EntityManager entityManager) {
		super(engineSettings.getGLData());
		this.engineSettings = engineSettings;
		this.entityManager = entityManager;
		projectionMatrix = new Matrix4f();
		camera = new Camera();
		setAspectRatio();
		addComponentListener(new AspectRatioListener());
		isRendering = false;
	}

	@Override
	public void initGL() {
		System.out.println("Initializing OpenGL...");
		createCapabilities();

		shaderPrograms[0] = new ShaderProgram("vertex.glsl", "fragment.glsl");
		shaderPrograms[0].compileShader();

		shaderPrograms[1] = new ShaderProgram("vertex2.glsl", "fragment.glsl");
		shaderPrograms[1].compileShader();

		glEnable(GL_DEPTH_TEST);
		glClearColor(0.8f, 0.7f, 0.6f, 1.0f);
		int error = glGetError();
		if (error == GL_ZERO) {
			System.out.println("OpenGL initialized.");
		} else {
			System.out.println("OpenGL failed initialization with error: " + error + ".");
		}
	}

	@Override
	public void paintGL() {
		isRendering = true;
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, getFramebufferWidth(), getFramebufferHeight());

		for (int entityId : entityManager.getEntitiesWith(MeshComponent.class, PositionComponent.class, RotationComponent.class)) {
			MeshComponent component = entityManager.getComponent(entityId, MeshComponent.class);
			Vector3f position = entityManager.getComponent(entityId, PositionComponent.class).position();
			Vector3f rotation = entityManager.getComponent(entityId, RotationComponent.class).rotation();
			Matrix4f model = new Matrix4f().translate(position).rotateXYZ(rotation);
			shaderPrograms[component.shaderProgramId()].bind();
			VertexArray va = new VertexArray();
			VertexBuffer vb = new VertexBuffer(component.vertices());
			VertexBufferLayout layout = new VertexBufferLayout();
			layout.add(GL_FLOAT, 3);
			va.addBuffer(vb, layout);
			IndexBuffer ib = new IndexBuffer(component.indices());
			shaderPrograms[component.shaderProgramId()].setUniformMatrix4v("projection", false,
					projectionMatrix.get(new float[16]));
			shaderPrograms[component.shaderProgramId()].setUniformMatrix4v("view", false, camera.matrix().get(new float[16]));
			shaderPrograms[component.shaderProgramId()].setUniformMatrix4v("model", false, model.get(new float[16]));
			glDrawElements(GL_TRIANGLES, component.indices().length, GL_UNSIGNED_INT, 0);
			va.delete();
			ib.delete();
			vb.delete();
		}

		swapBuffers();
		isRendering = false;
	}

	public void setCamera(Vector3f position, Vector3f rotation) {
		camera.setCamera(position, rotation);
	}

	private void setAspectRatio() {
		aspectRatio = (float) getFramebufferWidth() / getFramebufferHeight();
		projectionMatrix.setPerspective(engineSettings.FOV, aspectRatio, 0.1f, engineSettings.Z_FAR);
	}

	private class AspectRatioListener implements ComponentListener {

		@Override
		public void componentResized(ComponentEvent e) {
			System.err.println("Window resized.");
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
	}

	public boolean isRendering() {
		return isRendering;
	}

}
