package com.lucaslng.engine.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.lucaslng.engine.EngineSettings;
import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.*;

public final class Renderer {
	private final EngineSettings engineSettings;
	private final EntityManager entityManager;
	private ShaderProgram shader;
	private HashMap<String, Material> materials;
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
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_SAMPLES, 4);

		// create window
		window = glfwCreateWindow(width, height, engineSettings.title, NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}
		System.out.println("GLFW window created successfully");

		glfwSetFramebufferSizeCallback(window, (window, w, h) -> {
			framebufferWidth = w;
			framebufferHeight = h;
			setAspectRatio();
		});

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1); // vsync
		glfwShowWindow(window); // makes window visible
		initGL();
	}

	public void initGL() {
		System.out.println("Initializing OpenGL...");
		GL.createCapabilities();

		String vendor = glGetString(GL_VENDOR);
		String renderer = glGetString(GL_RENDERER);
		String version = glGetString(GL_VERSION);

		System.out.println("OpenGL Vendor  : " + vendor);
		System.out.println("OpenGL Renderer: " + renderer);
		System.out.println("OpenGL Version : " + version);

		// get initial framebuffer size
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			glfwGetFramebufferSize(window, w, h);
			framebufferWidth = w.get(0);
			framebufferHeight = h.get(0);
		}

		shader = new ShaderProgram("vertex.glsl", "fragment.glsl");
		shader.compileShader();

		materials = Materials.createMaterials();

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

		for (int entityId : entityManager.getEntitiesWith(MeshComponent.class,
				PositionComponent.class,
				RotationComponent.class)) {

			Vector3f position = entityManager.getComponent(entityId, PositionComponent.class).position();
			Vector3f rotation = entityManager.getComponent(entityId, RotationComponent.class).rotation();
			Matrix4f model = new Matrix4f().translate(position).rotateXYZ(rotation);

			shader.bind();

			shader.setUniformMatrix4v("projection", false,
					projectionMatrix.get(new float[16]));
			shader.setUniformMatrix4v("view", false, camera.matrix().get(new float[16]));
			shader.setUniformMatrix4v("model", false, model.get(new float[16]));

			MeshComponent meshComponent = entityManager.getComponent(entityId, MeshComponent.class);
			for (SubMesh subMesh : meshComponent.subMeshes()) {
				// System.out.println(subMesh.materialName);
				Material material = materials.getOrDefault(subMesh.materialName, materials.get("Black"));
				if (material.textured) {
					material.texture.bind(0);
					shader.setUniform1i("uUseTexture", 1);
				} else {
					shader.setUniform1i("uUseTexture", 0);
					shader.setUniform4f("uColor", material.color);
				}

				subMesh.vao.bind();
				glDrawElements(GL_TRIANGLES, subMesh.indexCount, GL_UNSIGNED_INT, 0);
				subMesh.vao.unbind();
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
}