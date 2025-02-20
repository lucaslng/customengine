package com.lucaslng.engine.renderer;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_FILL;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11C.GL_LINE;
import static org.lwjgl.opengl.GL11C.GL_POLYGON_MODE;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.GL_ZERO;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL11C.glGetError;
import static org.lwjgl.opengl.GL11C.glGetInteger;
import static org.lwjgl.opengl.GL11C.glPolygonMode;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import org.lwjgl.opengl.awt.AWTGLCanvas;

import com.lucaslng.engine.EngineSettings;
import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.MeshComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.RotationComponent;
import com.lucaslng.engine.components.TextureComponent;
import static com.lucaslng.engine.utils.FileReader.readImage;

public final class Renderer extends AWTGLCanvas {
	private final EngineSettings engineSettings;
	private final EntityManager entityManager;
	private final ShaderProgram[] shaderPrograms = new ShaderProgram[5];
	private Texture catTexture;
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

		shaderPrograms[2] = new ShaderProgram("texture_vertex.glsl", "texture_frag.glsl");
		shaderPrograms[2].compileShader();

		catTexture = new Texture(readImage("freakycat.png"));

		glEnable(GL_DEPTH_TEST);
		glClearColor(0.8f, 0.7f, 0.6f, 1.0f);

		// toggleDebugLines();

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

		for (int entityId : entityManager.getEntitiesWith(MeshComponent.class, PositionComponent.class,
				RotationComponent.class)) {
			MeshComponent component = entityManager.getComponent(entityId, MeshComponent.class);
			Vector3f position = entityManager.getComponent(entityId, PositionComponent.class).position();
			Vector3f rotation = entityManager.getComponent(entityId, RotationComponent.class).rotation();
			Matrix4f model = new Matrix4f().translate(position).rotateXYZ(rotation);

			glActiveTexture(GL_TEXTURE0);

			catTexture.bind();
			
			ShaderProgram shaderProgram;
			

			VertexArray va = new VertexArray();
			BufferLayout layout = new BufferLayout();
			FloatBuffer positionBuffer = new FloatBuffer(GL_ARRAY_BUFFER, component.vertices());
			FloatBuffer texCoordBuffer = null;
			layout.add(GL_FLOAT, 3);
			va.addBuffer(positionBuffer, layout);

			if (entityManager.hasComponent(entityId, TextureComponent.class)) {
				texCoordBuffer = new FloatBuffer(GL_ARRAY_BUFFER, entityManager.getComponent(entityId, TextureComponent.class).textureCoordinates());
				BufferLayout texLayout = new BufferLayout();
				texLayout.add(GL_FLOAT, 2);
				va.addBuffer(texCoordBuffer, texLayout);
				shaderProgram = shaderPrograms[2];
			} else {
				shaderProgram = shaderPrograms[0];
			}
			
			shaderProgram.bind();
			IntBuffer ib = new IntBuffer(GL_ELEMENT_ARRAY_BUFFER, component.indices());

			shaderProgram.setUniformMatrix4v("projection", false,
					projectionMatrix.get(new float[16]));
			shaderProgram.setUniformMatrix4v("view", false, camera.matrix().get(new float[16]));
			shaderProgram.setUniformMatrix4v("model", false, model.get(new float[16]));

			glDrawElements(GL_TRIANGLES, component.indices().length, GL_UNSIGNED_INT, 0);

			va.delete();
			ib.delete();
			positionBuffer.delete();
			if (entityManager.hasComponent(entityId, TextureComponent.class)) {
				texCoordBuffer.delete();
			}
		}

		swapBuffers();
		isRendering = false;
	}

	public void setCamera(Vector3f position, Vector3f rotation) {
		camera.setCamera(position, rotation);
	}

	public void toggleDebugLines() {
		if (isDebugLines()) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		} else {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		}
		
	}

	public boolean isDebugLines() {
		return glGetInteger(GL_POLYGON_MODE) == GL_LINE;
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
