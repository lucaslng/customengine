package com.lucaslng.engine.renderer;

import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_FILL;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11C.GL_LINE;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_POLYGON_MODE;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.GL_ZERO;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL11C.glGetError;
import static org.lwjgl.opengl.GL11C.glGetInteger;
import static org.lwjgl.opengl.GL11C.glPolygonMode;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.lucaslng.engine.EngineSettings;
import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.MeshComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.RotationComponent;
import com.lucaslng.engine.components.TextureComponent;
import static com.lucaslng.engine.utils.FileReader.readImage;

public final class Renderer {
	private final EngineSettings engineSettings;
	private final EntityManager entityManager;
	private final ShaderProgram[] shaderPrograms = new ShaderProgram[5];
	private Texture catTexture;
	private final Matrix4f projectionMatrix;
	private final Camera camera;
	private float aspectRatio;
	private boolean isRendering;
	private long window;
	private int width, height;
	private int framebufferWidth, framebufferHeight;

	public Renderer(EngineSettings engineSettings, EntityManager entityManager) {
		this.engineSettings = engineSettings;
		this.entityManager = entityManager;
		this.width = engineSettings.windowSize.width;
		this.height = engineSettings.windowSize.height;
		projectionMatrix = new Matrix4f();
		camera = new Camera();
		isRendering = false;
		initWindow();
	}

	private void initWindow() {
		// Setup error callback
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_SAMPLES, 4);

		// Create window
		window = glfwCreateWindow(width, height, engineSettings.title, NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}
		System.out.println("GLFW window created successfully");

		// Setup resize callback
		glfwSetFramebufferSizeCallback(window, (window, w, h) -> {
			framebufferWidth = w;
			framebufferHeight = h;
			setAspectRatio();
			System.err.println("Window resized.");
		});

		// Note: Window centering is skipped as Wayland doesn't support it
		// The compositor will position the window

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
		System.out.println("Window shown, should be visible now");

		// Initialize OpenGL
		initGL();
	}

	public void initGL() {
		System.out.println("Initializing OpenGL...");
		GL.createCapabilities();

		// Get initial framebuffer size
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			glfwGetFramebufferSize(window, w, h);
			framebufferWidth = w.get(0);
			framebufferHeight = h.get(0);
			System.out.println("Framebuffer size: " + framebufferWidth + "x" + framebufferHeight);
		}

		shaderPrograms[0] = new ShaderProgram("vertex.glsl", "fragment.glsl");
		shaderPrograms[0].compileShader();

		shaderPrograms[1] = new ShaderProgram("vertex2.glsl", "fragment.glsl");
		shaderPrograms[1].compileShader();

		shaderPrograms[2] = new ShaderProgram("texture_vertex.glsl", "texture_frag.glsl");
		shaderPrograms[2].compileShader();

		catTexture = new Texture(readImage("freakycat.png"));

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.8f, 0.7f, 0.6f, 1.0f);

		setAspectRatio();
		checkErrors();
		System.out.println("OpenGL initialized successfully.");
	}

	public void render() {
		isRendering = true;
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, framebufferWidth, framebufferHeight);

		for (int entityId : entityManager.getEntitiesWith(MeshComponent.class, PositionComponent.class,
				RotationComponent.class)) {
			MeshComponent component = entityManager.getComponent(entityId, MeshComponent.class);
			Vector3f position = entityManager.getComponent(entityId, PositionComponent.class).position();
			Vector3f rotation = entityManager.getComponent(entityId, RotationComponent.class).rotation();
			Matrix4f model = new Matrix4f().translate(position).rotateXYZ(rotation);

			catTexture.bind(0);

			ShaderProgram shaderProgram;

			VertexArray va = new VertexArray();
			BufferLayout layout = new BufferLayout();
			FloatBuffer positionBuffer = new FloatBuffer(GL_ARRAY_BUFFER, component.vertices());
			FloatBuffer texCoordBuffer = null;
			layout.add(GL_FLOAT, 3, false);
			va.addBuffer(positionBuffer, layout);

			if (entityManager.hasComponent(entityId, TextureComponent.class)) {
				texCoordBuffer = new FloatBuffer(GL_ARRAY_BUFFER,
						entityManager.getComponent(entityId, TextureComponent.class).textureCoordinates());
				va.addBuffer(texCoordBuffer, new VertexBufferElement(2, GL_FLOAT, true));
				shaderProgram = shaderPrograms[2];
			} else {
				shaderProgram = shaderPrograms[0];
			}

			shaderProgram.bind();
			com.lucaslng.engine.renderer.IntBuffer ib = new com.lucaslng.engine.renderer.IntBuffer(GL_ELEMENT_ARRAY_BUFFER, component.indices());

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

		glfwSwapBuffers(window);
		glfwPollEvents();
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
		aspectRatio = (float) framebufferWidth / framebufferHeight;
		projectionMatrix.setPerspective(engineSettings.FOV, aspectRatio, 0.1f, engineSettings.Z_FAR);
	}

	protected static void checkErrors() {
		int error;
		boolean errored = false;
		while ((error = glGetError()) != GL_ZERO) {
			System.err.println("OpenGL error: " + error);
			errored = true;
		}
		assert !errored;
	}

	public boolean isRendering() {
		return isRendering;
	}

	public long getWindow() {
		return window;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getFramebufferWidth() {
		return framebufferWidth;
	}

	public int getFramebufferHeight() {
		return framebufferHeight;
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}

	private void cleanup() {
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
}