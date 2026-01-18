package com.lucaslng.engine.renderer;

import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11C.*;
import com.lucaslng.engine.EngineSettings;
import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.*;
import com.lucaslng.engine.ui.UIElement;
import com.lucaslng.engine.ui.UIManager;
import com.lucaslng.engine.utils.FileReader;

public final class Renderer {
	
	private final static float[] defaultMatrix = new Matrix4f().get(new float[16]);
	private static final float[] ortho = new Matrix4f().setOrtho(0, 1, 0, 1, -1, 1).get(new float[16]);

	private final EngineSettings engineSettings;
	private final Window window;

	private final Matrix4f projectionMatrix;
	private final Camera camera;

	private final ShaderProgram shader, uiShader;
	private final HashMap<String, Material> materials;
	private final UIElement background;
	private final Texture backgroundTexture;
	
	private final TextRenderer textRenderer;

	private boolean isRendering;
	private float fadeAlpha = 0f;
	private float[] matBuf = new float[16];
	

	public Renderer(EngineSettings engineSettings, Window window, FontAtlas fontAtlas) {
		projectionMatrix = new Matrix4f();
		this.engineSettings = engineSettings;
		this.window = window;
		window.addFramebufferSizeCallback((_window, w,  h) -> {
				frameBufferSizeCallback(_window, w, h);
			}
		);
		
		camera = new Camera();
		isRendering = false;

		System.out.println("Initializing OpenGL...");
		GL.createCapabilities();

		System.out.println("OpenGL Vendor  : " + glGetString(GL_VENDOR));
		System.out.println("OpenGL Renderer: " + glGetString(GL_RENDERER));
		System.out.println("OpenGL Version : " + glGetString(GL_VERSION));

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.8f, 0.7f, 0.6f, 1.0f);

		checkErrors();
		System.out.println("OpenGL initialized successfully.");

		projectionMatrix.setPerspective(engineSettings.FOV, (float) window.w() / window.h(), 0.1f, engineSettings.Z_FAR);
		background = new UIElement(0f, 0f, 1f, 1f, false, false);
		backgroundTexture = new Texture(FileReader.readImage("background.jpg"));
		shader = new ShaderProgram("vertex.glsl", "fragment.glsl");
		shader.compileShader();
		uiShader = new ShaderProgram("ui-vertex.glsl", "ui-fragment.glsl");
		uiShader.compileShader();
		materials = Materials.createMaterials();
		textRenderer = new TextRenderer(fontAtlas);
	}

	public void render(EntityManager entityManager, UIManager uiManager) {
		isRendering = true;
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, window.w(), window.h());

		renderBackground();
		renderGame(entityManager);

		if (fadeAlpha > 0f)
			renderFadeOverlay();

		renderUI(uiManager);

		renderText();

		window.swapBuffers();
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

		float[] uiOrtho = new Matrix4f().ortho(0f, window.w(), window.h(), 0f, -1f, 1f).get(new float[16]);

		float x = 100f * window.uiScale();
		float y = 100f * window.uiScale();

		float fontSize = 8f;

		Matrix4f model = new Matrix4f()
					.translate(x, y, 0)
					.scale(window.uiScale() * fontSize, window.uiScale() * fontSize, 1f);
			model.get(matBuf);

		textRenderer.renderText("Arial", "fgreathIIJq", uiOrtho, matBuf, new Vector4f());
	}

	private void renderUI(UIManager uiManager) {
		glDisable(GL_DEPTH_TEST);

		uiShader.bind();

		float[] uiOrtho = new Matrix4f().ortho(0f, window.w(), window.h(), 0f, -1f, 1f).get(new float[16]);
		uiShader.setUniformMatrix4v("ortho", false, uiOrtho);
		uiShader.setUniform1i("uUseTexture", 0);
		uiShader.setUniform4f("uColor", 1f, 0f, 0f, 1f);

		for (UIElement element : uiManager.elements) {
			float x = element.x * window.uiScale();
			float y = element.y * window.uiScale();
			float width = element.width * window.uiScale();
			float height = element.height * window.uiScale();
			if (element.xAlignRight)
				x = window.w() - width - x;
			if (element.yAlignBottom)
				y = window.h() - height - y;
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

	private void renderGame(EntityManager entityManager) {
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

	public void frameBufferSizeCallback(long window, int w, int h) {
		projectionMatrix.setPerspective(engineSettings.FOV, (float) w / h, 0.1f, engineSettings.Z_FAR);
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
}
