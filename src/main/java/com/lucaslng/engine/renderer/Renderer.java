package com.lucaslng.engine.renderer;

import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import com.lucaslng.engine.EngineSettings;
import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.*;
import com.lucaslng.engine.ui.*;
import com.lucaslng.engine.utils.ColorUtils;
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
	private final RectElement background;
	private final Texture backgroundTexture;

	private final TextRenderer textRenderer;

	private boolean isRendering;
	private float fadeAlpha = 0f;
	private float[] matBuf = new float[16];
	private boolean ropeEnabled = false;
	private Vector3f ropeStart = new Vector3f();
	private Vector3f ropeEnd = new Vector3f();
	private float ropeTension = 0f;
	private VertexArray ropeVao;
	private FloatBuffer ropeVbo;
	private EntityManager activeEntityManager;

	public Renderer(EngineSettings engineSettings, Window window, FontAtlas fontAtlas) {
		projectionMatrix = new Matrix4f();
		this.engineSettings = engineSettings;
		this.window = window;
		window.addFramebufferSizeCallback((_window, w, h) -> {
			frameBufferSizeCallback(_window, w, h);
		});

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
		background = new RectElement(0f, 0f, 1f, 1f, XAlignment.LEFT, YAlignment.TOP);
		backgroundTexture = new Texture(FileReader.readImage("background.jpg"));
		shader = new ShaderProgram("vertex.glsl", "fragment.glsl");
		shader.compileShader();
		uiShader = new ShaderProgram("ui-vertex.glsl", "ui-fragment.glsl");
		uiShader.compileShader();
		materials = Materials.createMaterials();
		textRenderer = new TextRenderer(fontAtlas);
		initRope();
	}

	public void render(EntityManager entityManager, UIManager uiManager) {
		isRendering = true;
		activeEntityManager = entityManager;
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, window.w(), window.h());

		renderBackground();
		renderGame(entityManager);

		if (fadeAlpha > 0f)
			renderFadeOverlay();

		renderUI(uiManager);

		renderText(uiManager);

		window.swapBuffers();
		glfwPollEvents();
		isRendering = false;
		activeEntityManager = null;
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
		glDrawElements(GL_TRIANGLES, RectElement.indexCount, GL_UNSIGNED_INT, 0);
		background.vao.unbind();
		Texture.unbind();

		uiShader.unbind();
		glDepthMask(true);
	}

	private void renderText(UIManager uiManager) {
		glDisable(GL_DEPTH_TEST);

		float[] uiOrtho = new Matrix4f().ortho(0f, window.w(), window.h(), 0f, -1f, 1f).get(new float[16]);

		for (UIElement element : uiManager.elements) {
			if (element instanceof Text text && element.visible) {
				float x = text.x * window.uiScale();
				float y = text.y * window.uiScale();
				// float width = text.width * window.uiScale();
				// float height = text.height * window.uiScale();
				if (text.xAlignment == XAlignment.CENTER)
					x += window.w() / 2; // - width / 2f;
				else if (text.xAlignment == XAlignment.RIGHT)
					x = window.w() - x; // - width
				if (text.yAlignment == YAlignment.CENTER)
					y += window.h() / 2; // - height / 2f;
				else if (text.yAlignment == YAlignment.BOTTOM)
					y = window.h() - y; // - height

				Matrix4f model = new Matrix4f()
						.translate(x, y, 0)
						.scale(window.uiScale() * text.textStyle.fontSize, window.uiScale() * text.textStyle.fontSize, 1f);
				model.get(matBuf);

				textRenderer.renderText(text.textStyle.family, text.text, uiOrtho, matBuf, new Vector4f());
			}
		}

	}

	private void renderUI(UIManager uiManager) {
		glDisable(GL_DEPTH_TEST);

		uiShader.bind();

		float[] uiOrtho = new Matrix4f().ortho(0f, window.w(), window.h(), 0f, -1f, 1f).get(new float[16]);
		uiShader.setUniformMatrix4v("ortho", false, uiOrtho);
		uiShader.setUniform1i("uUseTexture", 0);
		System.out.println("---");
		for (UIElement element : uiManager.elements) {
			if (element.visible && element instanceof RectElement rectElement) {
				System.out.println("DRAW " + element.getClass().getSimpleName());
				float x = rectElement.x * window.uiScale();
				float y = rectElement.y * window.uiScale();
				float width = rectElement.width * window.uiScale();
				float height = rectElement.height * window.uiScale();
				if (rectElement.xAlignment == XAlignment.CENTER)
					x += window.w() / 2 - width / 2f;
				else if (rectElement.xAlignment == XAlignment.RIGHT)
					x = window.w() - width - x;
				if (rectElement.yAlignment == YAlignment.CENTER)
					y += window.h() / 2 - height / 2f;
				else if (rectElement.yAlignment == YAlignment.BOTTOM)
					y = window.h() - height - y;
				Matrix4f model = new Matrix4f()
						.translate(x, y, 0)
						.scale(width, height, 1f);
				model.get(matBuf);
				uiShader.setUniformMatrix4v("model", false, model.get(matBuf));
				if (rectElement instanceof ColoredRectElement coloredRectElement)
					uiShader.setUniform4f("uColor", new Vector4f(ColorUtils.color2Vec(coloredRectElement.color), 1.0f));
				else
					uiShader.setUniform4f("uColor", 1f, 0f, 0f, 1f);

				rectElement.vao.bind();
				glDrawElements(GL_TRIANGLES, RectElement.indexCount, GL_UNSIGNED_INT, 0);
				rectElement.vao.unbind();
			}
		}

		uiShader.unbind();
	}

	private void renderGame(EntityManager entityManager) {
		glEnable(GL_DEPTH_TEST);
		shader.bind();
		shader.setUniform1i("uDebugNormals", 0);
		shader.setUniform1i("uUnlit", 0);

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
		renderRope();
		shader.unbind();
	}

	private void initRope() {
		ropeVao = new VertexArray();
		ropeVao.bind();
		ropeVbo = new FloatBuffer(GL_ARRAY_BUFFER, buildRopeVertices());

		BufferLayout layout = new BufferLayout();

		layout.add(GL_FLOAT, 3, false);
		layout.add(GL_FLOAT, 3, true);
		layout.add(GL_FLOAT, 2, true);

		ropeVao.addBuffer(ropeVbo, layout);
		ropeVao.unbind();
	}

	private void renderRope() {
		if (!ropeEnabled) {
			return;
		}

		ropeVbo.bind();
		ropeVbo.updateData(buildRopeVertices());
		ropeVbo.unbind();
		shader.setUniformMatrix4v("projection", false, projectionMatrix.get(new float[16]));
		shader.setUniformMatrix4v("view", false, camera.matrix().get(new float[16]));
		shader.setUniformMatrix4v("model", false, new Matrix4f().get(new float[16]));
		shader.setUniform1i("uUseTexture", 0);
		Vector3f ropeColor = ropeColorFromTension();
		shader.setUniform4f("uColor", ropeColor.x(), ropeColor.y(), ropeColor.z(), 1.0f);
		shader.setUniform1i("uUnlit", 1);

		ropeVao.bind();
		glDrawArrays(GL_TRIANGLES, 0, 6);
		ropeVao.unbind();

		shader.setUniform1i("uUnlit", 0);
	}

	private float[] buildRopeVertices() {
		Vector3f viewDir = new Vector3f(0f, 0f, 1f);
		Vector3f cameraPos = null;

		if (activeEntityManager != null && activeEntityManager.entityExists(camera.entityId())) {
			cameraPos = activeEntityManager.getComponent(camera.entityId(), PositionComponent.class).position();
			Vector3f midpoint = new Vector3f(ropeStart).add(ropeEnd).mul(0.5f);
			viewDir = new Vector3f(cameraPos).sub(midpoint);
			if (viewDir.lengthSquared() > 0.0001f) {
				viewDir.normalize();
			} else {
				viewDir.set(0f, 0f, 1f);
			}
		}

		Vector3f ropeDir = new Vector3f(ropeEnd).sub(ropeStart);
		if (ropeDir.lengthSquared() < 0.0001f) {
			ropeDir.set(1f, 0f, 0f);
		} else {
			ropeDir.normalize();
		}

		Vector3f sideStart = new Vector3f(viewDir).sub(new Vector3f(ropeDir).mul(viewDir.dot(ropeDir)));
		if (sideStart.lengthSquared() < 0.0001f) {
			Vector3f up = new Vector3f(0f, 1f, 0f);
			sideStart.set(up).sub(new Vector3f(ropeDir).mul(up.dot(ropeDir)));
			if (sideStart.lengthSquared() < 0.0001f) {
				sideStart.set(1f, 0f, 0f);
			}
		}
		sideStart.normalize();
		Vector3f sideEnd = new Vector3f(sideStart);

		float halfWidth = 0.33f;
		sideStart.mul(halfWidth);
		sideEnd.mul(halfWidth);

		Vector3f startLeft = new Vector3f(ropeStart).sub(sideStart);
		Vector3f startRight = new Vector3f(ropeStart).add(sideStart);
		Vector3f endLeft = new Vector3f(ropeEnd).sub(sideEnd);
		Vector3f endRight = new Vector3f(ropeEnd).add(sideEnd);

		return new float[] {
				startLeft.x(), startLeft.y(), startLeft.z(), 0f, 0f, 1f, 0f, 0f,
				endLeft.x(), endLeft.y(), endLeft.z(), 0f, 0f, 1f, 0f, 1f,
				endRight.x(), endRight.y(), endRight.z(), 0f, 0f, 1f, 1f, 1f,

				startLeft.x(), startLeft.y(), startLeft.z(), 0f, 0f, 1f, 0f, 0f,
				endRight.x(), endRight.y(), endRight.z(), 0f, 0f, 1f, 1f, 1f,
				startRight.x(), startRight.y(), startRight.z(), 0f, 0f, 1f, 1f, 0f
		};
	}

	private Vector3f ropeColorFromTension() {
		Vector3f green = new Vector3f(0.2f, 0.85f, 0.2f);
		Vector3f yellow = new Vector3f(0.95f, 0.85f, 0.2f);
		Vector3f red = new Vector3f(0.9f, 0.2f, 0.2f);

		if (ropeTension <= 0f) {
			return green;
		}
		if (ropeTension >= 1f) {
			return red;
		}
		
		return yellow;
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

	public void setRopeEnabled(boolean ropeEnabled) {
		this.ropeEnabled = ropeEnabled;
	}

	public void setRopeEndpoints(Vector3f start, Vector3f end) {
		this.ropeStart.set(start);
		this.ropeEnd.set(end);
	}

	public void setRopeTension(float tension) {
		if (tension < 0f) {
			ropeTension = 0f;
		} else if (tension > 1f) {
			ropeTension = 1f;
		} else {
			ropeTension = tension;
		}
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
