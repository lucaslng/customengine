package com.lucaslng.engine.renderer;

import java.nio.IntBuffer;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11C.*;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.lucaslng.engine.EngineSettings;
import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.*;
import com.lucaslng.engine.ui.UIElement;
import com.lucaslng.engine.ui.UIManager;
import com.lucaslng.engine.utils.FileReader;

public final class Renderer {
	private final EngineSettings engineSettings;
	private final EntityManager entityManager;
	private final UIManager uiManager;
	private ShaderProgram shader, uiShader;
	private HashMap<String, Material> materials;
	private final Matrix4f projectionMatrix;
	private final Camera camera;
	private TextRenderer textRenderer;
	private final FontAtlas fontAtlas;
	private float aspectRatio;
	private boolean isRendering;
	private long window;
	private int framebufferWidth, framebufferHeight;
	private float fadeAlpha = 0f;
	private final UIElement background;
	private final Texture backgroundTexture;
	private float[] matBuf = new float[16];
	private final static float[] defaultMatrix = new Matrix4f().get(new float[16]);

	private static final float[] ortho = new Matrix4f().setOrtho(0, 1, 0, 1, -1, 1).get(new float[16]);

	public Renderer(EngineSettings engineSettings, EntityManager entityManager, UIManager uiManager, FontAtlas fontAtlas) {
		this.engineSettings = engineSettings;
		this.entityManager = entityManager;
		this.uiManager = uiManager;
		this.fontAtlas = fontAtlas;
		projectionMatrix = new Matrix4f();
		camera = new Camera();
		isRendering = false;
		initWindow();
		background = new UIElement(0f, 0f, 1f, 1f, false, false);
		backgroundTexture = new Texture(FileReader.readImage("background.jpg"));
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
		window = glfwCreateWindow(engineSettings.referenceDimension.width, engineSettings.referenceDimension.height, engineSettings.title,
				NULL, NULL);
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
		uiShader = new ShaderProgram("ui-vertex.glsl", "ui-fragment.glsl");
		uiShader.compileShader();

		materials = Materials.createMaterials();

		textRenderer = new TextRenderer(fontAtlas);

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

		renderBackground();
		renderGame();

		if (fadeAlpha > 0f) {
			renderFadeOverlay();
		}

		renderUI();

		renderText();

		glfwSwapBuffers(window);
		glfwPollEvents();
		isRendering = false;
	}

	private void renderBackground() {
		glDisable(GL_DEPTH_TEST);
		glDepthMask(false);
		uiShader.bind();
		uiShader.setUniformMatrix4v("ortho", false, ortho);
		uiShader.setUniformMatrix4v("model", false, defaultMatrix);
		uiShader.setUniform1i("uTexture", 0);
		uiShader.setUniform1i("uUseTexture", 1);
		backgroundTexture.bind(0);

		background.vao.bind();
		glDrawElements(GL_TRIANGLES, UIElement.indexCount, GL_UNSIGNED_INT, 0);
		background.vao.unbind();
		Texture.unbind();

		uiShader.unbind();
		glDepthMask(true);
	}

	private void renderText() {
		glDisable(GL_DEPTH_TEST);
		float scaleX = (float) framebufferWidth / engineSettings.referenceDimension.width;
		float scaleY = (float) framebufferHeight / engineSettings.referenceDimension.height;
		float uiScale = Math.min(scaleX, scaleY);

		float[] uiOrtho = new Matrix4f().ortho(0f, framebufferWidth, framebufferHeight, 0f, -1f, 1f).get(new float[16]);

		float x = 100f * uiScale;
		float y = 100f * uiScale;

		float fontSize = 10f;

		Matrix4f model = new Matrix4f()
					.translate(x, y, 0)
					.scale(uiScale * fontSize, uiScale * fontSize, 1f);
			model.get(matBuf);

		textRenderer.renderText("Arial", "Hello World", uiOrtho, matBuf, new Vector4f());
	}

	private void renderUI() {
		glDisable(GL_DEPTH_TEST);

		float scaleX = (float) framebufferWidth / engineSettings.referenceDimension.width;
		float scaleY = (float) framebufferHeight / engineSettings.referenceDimension.height;
		float uiScale = Math.min(scaleX, scaleY);

		uiShader.bind();

		float[] uiOrtho = new Matrix4f().ortho(0f, framebufferWidth, framebufferHeight, 0f, -1f, 1f).get(new float[16]);
		uiShader.setUniformMatrix4v("ortho", false, uiOrtho);
		uiShader.setUniform1i("uUseTexture", 0);
		uiShader.setUniform4f("uColor", 1f, 0f, 0f, 1f);

		for (UIElement element : uiManager.elements) {
			float x = element.x * uiScale;
			float y = element.y * uiScale;
			float width = element.width * uiScale;
			float height = element.height * uiScale;
			if (element.xAlignRight)
				x = framebufferWidth - width - x;
			if (element.yAlignBottom)
				y = framebufferHeight - height - y;
			Matrix4f model = new Matrix4f()
					.translate(x, y, 0)
					.scale(width, height, 1f);
			model.get(matBuf);
			uiShader.setUniformMatrix4v("model", false, model.get(matBuf));
			element.vao.bind();
			glDrawElements(GL_TRIANGLES, UIElement.indexCount, GL_UNSIGNED_INT, 0);
			element.vao.unbind();
		}

		uiShader.unbind();
	}

	private void renderGame() {
		glEnable(GL_DEPTH_TEST);
		shader.bind();

		projectionMatrix.get(matBuf);
		shader.setUniformMatrix4v("projection", false,
				matBuf);
		camera.matrix().get(matBuf);
		shader.setUniformMatrix4v("view", false, matBuf);
		if (entityManager.entityExists(camera.entityId())) {
			Vector3f cameraPos = entityManager.getComponent(camera.entityId(), PositionComponent.class).position();
			shader.setUniform3f("lightPos", cameraPos);
		} else {
			shader.setUniform3f("lightPos", new Vector3f());
		}

		for (int entityId : entityManager.getEntitiesWith(MeshComponent.class,
				PositionComponent.class)) {

			if (entityManager.hasComponent(entityId, DisabledComponent.class))
				continue;

			Vector3f position = entityManager.getComponent(entityId, PositionComponent.class).position();
			Matrix4f model = new Matrix4f().translate(position);
			if (entityManager.hasComponent(entityId, RotationComponent.class)) {
				Vector3f rotation = entityManager.getComponent(entityId, RotationComponent.class).rotation();
				model.rotateXYZ(rotation);
			}

			model.get(matBuf);
			shader.setUniformMatrix4v("model", false, matBuf);

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
				Texture.unbind();
			}
		}
		shader.unbind();
	}

	private void renderFadeOverlay() {
		glDisable(GL_DEPTH_TEST);
		uiShader.bind();
		uiShader.setUniformMatrix4v("ortho", false, ortho);
		uiShader.setUniform1i("uUseTexture", 0);
		uiShader.setUniform4f("uColor", 0f, 0f, 0f, fadeAlpha);

		float[] quadVertices = {
				0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f,
				1f, 0f, 0f, 0f, 0f, 1f, 1f, 0f,
				1f, 1f, 0f, 0f, 0f, 1f, 1f, 1f,
				0f, 1f, 0f, 0f, 0f, 1f, 0f, 1f
		};
		int[] quadIndices = { 0, 1, 2, 2, 3, 0 };

		SubMesh overlay = new SubMesh(quadVertices, quadIndices, "Black");
		overlay.vao.bind();
		glDrawElements(GL_TRIANGLES, overlay.indexCount, GL_UNSIGNED_INT, 0);
		overlay.vao.unbind();
		uiShader.unbind();
	}

	public void setFadeAlpha(float alpha) {
		this.fadeAlpha = Math.max(0f, Math.min(1f, alpha));
	}

	public void setCamera(Vector3f position, Vector3f rotation, int entityId) {
		camera.setCamera(position, rotation, entityId);
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
			System.err.println("OpenGL ERROR: " + error);
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
