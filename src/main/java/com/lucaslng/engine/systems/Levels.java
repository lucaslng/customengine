package com.lucaslng.engine.systems;

import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.Float.parseFloat;
import java.util.Scanner;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.lucaslng.engine.components.BlinkComponent;
import com.lucaslng.engine.components.LavaComponent;
import com.lucaslng.engine.components.TimedMoveComponent;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.entities.BoxEntityFactory;
import com.lucaslng.entities.ButtonEntityFactory;
import com.lucaslng.entities.ExitEntityFactory;
import com.lucaslng.entities.MovingPlatformEntityFactory;

public class Levels {

	public static final int LEVEL_COUNT = 4;
	private static final float MOVING_PLATFORM_DEFAULT_OFFSET_X = 0f;
	private static final float MOVING_PLATFORM_DEFAULT_OFFSET_Y = 0f;
	private static final float MOVING_PLATFORM_DEFAULT_SPEED = 0f;
	private static final float MOVING_LAVA_DEFAULT_ON_DURATION = 2f;
	private static final float MOVING_LAVA_DEFAULT_OFF_DURATION = 2f;
	private static final float MOVING_LAVA_DEFAULT_PAUSE_DURATION = 0f;
	private Level[] levels;
	public int currentLevelIndex;

	public Levels() {
		levels = readLevels();
		currentLevelIndex = 1;
	}

	public void refreshLevels() {
		levels = readLevels();
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
		
		int timer = Integer.parseInt(in.nextLine());
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
					Float blinkOn = null;
					Float blinkOff = null;
					for (int t = 5; t < tokens.length; t++) {
						if ("blink".equalsIgnoreCase(tokens[t]) && t + 2 < tokens.length) {
							blinkOn = parseFloat(tokens[t + 1]);
							blinkOff = parseFloat(tokens[t + 2]);
							t += 2;
						}
					}

					Object[] extras = new Object[0];
					if (blinkOn != null && blinkOff != null) {
						extras = new Object[] { new BlinkComponent(blinkOn, blinkOff, false) };
					}

					entities[i] = new BoxEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]), -1.1f,
							parseFloat(tokens[3]), parseFloat(tokens[4]), 2.2f, "Grass", extras);
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
				case "moving_lava" -> {
					float x = parseFloat(tokens[1]);
					float y = parseFloat(tokens[2]);
					float width = parseFloat(tokens[3]);
					float height = parseFloat(tokens[4]);
					float moveX = tokens.length > 5 ? parseFloat(tokens[5]) : MOVING_PLATFORM_DEFAULT_OFFSET_X;
					float moveY = tokens.length > 6 ? parseFloat(tokens[6]) : MOVING_PLATFORM_DEFAULT_OFFSET_Y;
					float speed = tokens.length > 7 ? parseFloat(tokens[7]) : MOVING_PLATFORM_DEFAULT_SPEED;
					float onDuration = tokens.length > 8 ? parseFloat(tokens[8]) : MOVING_LAVA_DEFAULT_ON_DURATION;
					float offDuration = tokens.length > 9 ? parseFloat(tokens[9]) : MOVING_LAVA_DEFAULT_OFF_DURATION;
					float pauseDuration = tokens.length > 10 ? parseFloat(tokens[10]) : MOVING_LAVA_DEFAULT_PAUSE_DURATION;
					entities[i] = new MovingPlatformEntityFactory(x, y, -1.1f, width, height, 2.2f,
							new Vector3f(moveX, moveY, 0f), speed, "Lava", false,
							new TimedMoveComponent(new Vector3f(x, y, -1.1f), new Vector3f(moveX, moveY, 0f), speed,
									onDuration, offDuration, pauseDuration, false),
							new LavaComponent());
				}
				default -> {
				}
			}
		}
		in.close();
		return new Level(player1Spawn, player2Spawn, ropeEnabled, entities, timer);
	}

	public Level currentLevel() {
		return levels[currentLevelIndex - 1];
	}

	public record Level(Vector2f player1Spawn, Vector2f player2Spawn, boolean ropeEnabled,
			AbstractEntityFactory[] entityFactories, int timer) {
		public boolean hasTimer() {
			return timer != 0;
		}
	}
}
