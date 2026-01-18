package com.lucaslng.engine;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

import java.awt.Font;
import java.util.HashMap;

import com.lucaslng.GameStates;
import com.lucaslng.engine.renderer.FontAtlas;
import com.lucaslng.engine.renderer.Renderer;
import com.lucaslng.engine.renderer.Window;

public final class Engine {

	public final Renderer renderer;
	public final Window window;
	public final EngineSettings settings;
	public final FontAtlas fontAtlas;
	public final InputHandler inputHandler;
	private final HashMap<GameStates, GameState> gameStates;

	public GameStates gameState;

	public Engine(HashMap<GameStates, GameState> gameStates) {
		System.out.println("Initializing engine...");
		this.gameStates = gameStates;
		settings = new EngineSettings();

		fontAtlas = new FontAtlas();
		fontAtlas.addFont(new Font("Arial", Font.PLAIN, 50));
		fontAtlas.dispose();

		window = new Window(settings);
		inputHandler = new InputHandler(window);
		renderer = new Renderer(settings, window, fontAtlas);

		System.out.println("Engine initialized.");
	}

	public void setCamera(Vector3f position, Vector3f rotation, int entityId) {
		renderer.setCamera(position, rotation, entityId);
	}

	public void linkMouseToRotation(Vector3f rotation) {
		MouseMotionToRotationListener mouseHandler = new MouseMotionToRotationListener(this);
		mouseHandler.setRotation(rotation);

		// Setup mouse callback
		glfwSetCursorPosCallback(window.window, (window, xpos, ypos) -> {
			mouseHandler.mouseMoved(xpos, ypos);
		});
	}

	public void start(GameStates startingGameState) {
		gameState = startingGameState;
		while (true) {
			if (!gameStates.containsKey(gameState))
				break;
			gameStates.get(gameState).init();
			gameState = gameStates.get(gameState).loop();
			System.out.println(gameState);
		}
	}

	public void doLoop() {
		if (!renderer.isRendering()) {
			renderer.render(gameStates.get(gameState).entityManager, gameStates.get(gameState).uiManager);
		}
	}

	public int w() {
		return window.w();
	}

	public int h() {
		return window.h();
	}

	public boolean shouldClose() {
		return window.shouldClose();
	}
}