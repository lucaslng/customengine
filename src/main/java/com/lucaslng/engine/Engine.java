package com.lucaslng.engine;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

import java.awt.Font;
import java.util.HashMap;

import com.lucaslng.GameStates;
import com.lucaslng.engine.renderer.FontAtlas;
import com.lucaslng.engine.renderer.Renderer;
import com.lucaslng.engine.renderer.Window;
import com.lucaslng.engine.systems.Levels;

public final class Engine {

	public final Renderer renderer;
	public final Window window;
	public final EngineSettings settings;
	public final FontAtlas fontAtlas;
	public final InputHandler inputHandler;
	private final HashMap<GameStates, GameState> gameStates;

	public GameStateSwitch gameStateSwitch;

	public Engine(HashMap<GameStates, GameState> gameStates) {
		this.gameStates = gameStates;
		settings = new EngineSettings();

		fontAtlas = new FontAtlas();
		fontAtlas.addFont("Arial", Font.PLAIN);
		fontAtlas.addFontFromTTF("Pixeled", Font.PLAIN);
		fontAtlas.dispose();

		window = new Window(settings);
		inputHandler = new InputHandler(window);
		renderer = new Renderer(settings, window, fontAtlas);
	}

	public void setCamera(Vector3f position, Vector3f rotation, int entityId) {
		renderer.setCamera(position, rotation, entityId);
	}

	private Levels cachedLevels;
	public Levels getLevels() {
		if (cachedLevels == null) {
			cachedLevels = new Levels();
		}
		return cachedLevels;
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
		gameStateSwitch = new GameStateSwitch(startingGameState, null);
		while (true) {
			if (!gameStates.containsKey(gameStateSwitch.gameState())) {
				break;
			}
			gameStates.get(gameStateSwitch.gameState()).init(this, gameStateSwitch.payload());
			gameStateSwitch = gameStates.get(gameStateSwitch.gameState()).loop();
		}
	}

	public void doLoop() {
		if (!renderer.isRendering()) {
			renderer.render(gameStates.get(gameStateSwitch.gameState()).entityManager, gameStates.get(gameStateSwitch.gameState()).uiManager);
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