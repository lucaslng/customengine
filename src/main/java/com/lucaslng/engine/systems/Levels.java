package com.lucaslng.engine.systems;

import static java.lang.Float.parseFloat;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.components.LavaComponent;
import com.lucaslng.entities.BoxEntityFactory;
import com.lucaslng.entities.ButtonEntityFactory;
import com.lucaslng.entities.ExitEntityFactory;
import com.lucaslng.entities.MovingPlatformEntityFactory;

public class Levels {

	public static final int LEVEL_COUNT = 2;
	private static final float MOVING_PLATFORM_DEFAULT_OFFSET_X = 0f;
	private static final float MOVING_PLATFORM_DEFAULT_OFFSET_Y = 6f;
	private static final float MOVING_PLATFORM_DEFAULT_SPEED = 6f;
	private final Level[] levels;
	public int currentLevelIndex;

	public Levels() {
		levels = readLevels();
		currentLevelIndex = 1;
	}

	private static Level[] readLevels() {
		Level[] levels = new Level[LEVEL_COUNT];
		for (int i = 1; i <= LEVEL_COUNT; i++) {
			levels[i - 1] = parseLevel(i);
		}
		return levels;
	}

	private static Level parseLevel(int level) {
		Scanner in;
		try {
			in = new Scanner(new File("assets/levels/" + level));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to read level file.");
		}
		String[] tokens = in.nextLine().split(" ");
		assert tokens.length == 4;
		Vector2f player1Spawn = new Vector2f(parseFloat(tokens[0]), parseFloat(tokens[1]));
		Vector2f player2Spawn = new Vector2f(parseFloat(tokens[2]), parseFloat(tokens[3]));

		boolean ropeEnabled = Boolean.parseBoolean(in.nextLine().trim());
		AbstractEntityFactory[] entities = new AbstractEntityFactory[Integer.parseInt(in.nextLine())];

		for (int i = 0; i < entities.length; i++) {
			tokens = in.nextLine().split(" ");
			switch (tokens[0]) {
				case "platform" -> {
					entities[i] = new BoxEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]), -1.1f,
							parseFloat(tokens[3]), parseFloat(tokens[4]), 2.2f, "Grass");
				}
				case "lava" -> {
					entities[i] = new BoxEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]), -1.1f,
							parseFloat(tokens[3]), parseFloat(tokens[4]), 2.2f, "Lava", new LavaComponent());
				}
				case "exit" -> {
					entities[i] = new ExitEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]));
				}
				case "button" -> {
					boolean latch = false;
					boolean toggle = false;
					for (int t = 3; t < tokens.length; t++) {
						if ("latch".equalsIgnoreCase(tokens[t])) {
							latch = true;
						} else if ("toggle".equalsIgnoreCase(tokens[t])) {
							toggle = true;
						}
					}
					entities[i] = new ButtonEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]), latch, toggle);
				}
				case "moving_platform" -> {
					float x = parseFloat(tokens[1]);
					float y = parseFloat(tokens[2]);
					float width = parseFloat(tokens[3]);
					float height = parseFloat(tokens[4]);
					float moveX = tokens.length > 5 ? parseFloat(tokens[5]) : MOVING_PLATFORM_DEFAULT_OFFSET_X;
					float moveY = tokens.length > 6 ? parseFloat(tokens[6]) : MOVING_PLATFORM_DEFAULT_OFFSET_Y;
					float speed = tokens.length > 7 ? parseFloat(tokens[7]) : MOVING_PLATFORM_DEFAULT_SPEED;
					entities[i] = new MovingPlatformEntityFactory(x, y, -1.1f, width, height, 2.2f,
							new Vector3f(moveX, moveY, 0f), speed);
				}
				default -> {
				}
			}
		}
		in.close();
		return new Level(player1Spawn, player2Spawn, ropeEnabled, entities);
	}

	public Level currentLevel() {
		return levels[currentLevelIndex - 1];
	}

	public record Level(Vector2f player1Spawn, Vector2f player2Spawn, boolean ropeEnabled,
			AbstractEntityFactory[] entityFactories) {
	}
}
