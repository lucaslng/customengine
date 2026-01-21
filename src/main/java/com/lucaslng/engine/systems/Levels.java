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
import com.lucaslng.entities.CoinEntityFactory;
import com.lucaslng.entities.ExitEntityFactory;
import com.lucaslng.entities.MovingPlatformEntityFactory;

public class Levels {

	public static final int LEVEL_COUNT = 8;
	private static final float PLATFORM_LENGTH = 3f;
	private static final float MOVING_PLATFORM_DEFAULT_OFFSET_X = 0f;
	private static final float MOVING_PLATFORM_DEFAULT_OFFSET_Y = 0f;
	private static final float MOVING_PLATFORM_DEFAULT_SPEED = 0f;
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

					entities[i] = new BoxEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]), 0f,
							parseFloat(tokens[3]), parseFloat(tokens[4]), PLATFORM_LENGTH, "Grass", extras);
				}
				case "lava" -> {
					entities[i] = new BoxEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]), 0f,
							parseFloat(tokens[3]), parseFloat(tokens[4]), PLATFORM_LENGTH, "Lava", new LavaComponent());
				}
				case "exit" -> {
					entities[i] = new ExitEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]));
				}
				case "wall" -> {
					entities[i] = new BoxEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]), PLATFORM_LENGTH / 2f + 0.5f, parseFloat(tokens[3]), parseFloat(tokens[4]), 0.4f, "Wall");
				}
				case "button" -> {
					boolean latch = false;
					boolean toggle = false;
					int flag = 0;
					for (int t = 3; t < tokens.length; t++) {
						if ("latch".equalsIgnoreCase(tokens[t])) {
							latch = true;
						} else if ("toggle".equalsIgnoreCase(tokens[t])) {
							toggle = true;
						} else if ("flag".equalsIgnoreCase(tokens[t]) && t + 1 < tokens.length) {
							flag = Integer.parseInt(tokens[t + 1]);
							t++;
						} else if (tokens[t].startsWith("flag=")) {
							flag = Integer.parseInt(tokens[t].substring(5));
						}
					}
					entities[i] = new ButtonEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]), latch, toggle, flag);
				}
				case "moving_platform" -> {
					float x = parseFloat(tokens[1]);
					float y = parseFloat(tokens[2]);
					float width = parseFloat(tokens[3]);
					float height = parseFloat(tokens[4]);
					int idx = 5;
					float moveX = MOVING_PLATFORM_DEFAULT_OFFSET_X;
					float moveY = MOVING_PLATFORM_DEFAULT_OFFSET_Y;
					float speed = MOVING_PLATFORM_DEFAULT_SPEED;
					if (idx < tokens.length && isFloat(tokens[idx])) {
						moveX = parseFloat(tokens[idx++]);
					}
					if (idx < tokens.length && isFloat(tokens[idx])) {
						moveY = parseFloat(tokens[idx++]);
					}
					if (idx < tokens.length && isFloat(tokens[idx])) {
						speed = parseFloat(tokens[idx++]);
					}
					int flag = 0;
					int requiredCoins = 1;
					for (int t = idx; t < tokens.length; t++) {
						if ("flag".equalsIgnoreCase(tokens[t]) && t + 1 < tokens.length) {
							flag = Integer.parseInt(tokens[t + 1]);
							t++;
						} else if (tokens[t].startsWith("flag=")) {
							flag = Integer.parseInt(tokens[t].substring(5));
						} else if (("coins".equalsIgnoreCase(tokens[t]) || "count".equalsIgnoreCase(tokens[t]))
								&& t + 1 < tokens.length) {
							requiredCoins = Integer.parseInt(tokens[t + 1]);
							t++;
						} else if (tokens[t].startsWith("coins=")) {
							requiredCoins = Integer.parseInt(tokens[t].substring(6));
						} else if (tokens[t].startsWith("count=")) {
							requiredCoins = Integer.parseInt(tokens[t].substring(6));
						}
					}
					entities[i] = new MovingPlatformEntityFactory(x, y, 0, width, height, PLATFORM_LENGTH,
							new Vector3f(moveX, moveY, 0f), speed, "Brown", true, flag, requiredCoins);
				}
				case "moving_lava" -> {
					float x = parseFloat(tokens[1]);
					float y = parseFloat(tokens[2]);
					float width = parseFloat(tokens[3]);
					float height = parseFloat(tokens[4]);
					int idx = 5;
					float moveX = MOVING_PLATFORM_DEFAULT_OFFSET_X;
					float moveY = MOVING_PLATFORM_DEFAULT_OFFSET_Y;
					float speed = MOVING_PLATFORM_DEFAULT_SPEED;
					float pauseDuration = MOVING_LAVA_DEFAULT_PAUSE_DURATION;
					if (idx < tokens.length && isFloat(tokens[idx])) {
						moveX = parseFloat(tokens[idx++]);
					}
					if (idx < tokens.length && isFloat(tokens[idx])) {
						moveY = parseFloat(tokens[idx++]);
					}
					if (idx < tokens.length && isFloat(tokens[idx])) {
						speed = parseFloat(tokens[idx++]);
					}
					if (idx < tokens.length && isFloat(tokens[idx])) {
						pauseDuration = parseFloat(tokens[idx++]);
					}
					entities[i] = new MovingPlatformEntityFactory(x, y, 0, width, height, PLATFORM_LENGTH,
							new Vector3f(moveX, moveY, 0f), speed, "Lava", false, 0, 1,
							new TimedMoveComponent(new Vector3f(x, y, 0), new Vector3f(moveX, moveY, 0f), speed,
									pauseDuration, false),
							new LavaComponent());
				}
				case "coin" -> {
					int flag = 0;
					for (int t = 3; t < tokens.length; t++) {
						if ("flag".equalsIgnoreCase(tokens[t]) && t + 1 < tokens.length) {
							flag = Integer.parseInt(tokens[t + 1]);
							t++;
						} else if (tokens[t].startsWith("flag=")) {
							flag = Integer.parseInt(tokens[t].substring(5));
						}
					}
					entities[i] = new CoinEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]), flag);
				}
				default -> {}
			}
		}
		in.close();
		return new Level(player1Spawn, player2Spawn, ropeEnabled, entities, timer);
	}

	public Level currentLevel() {
		return levels[currentLevelIndex - 1];
	}

	private static boolean isFloat(String value) {
		try {
			parseFloat(value);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public record Level(Vector2f player1Spawn, Vector2f player2Spawn, boolean ropeEnabled,
			AbstractEntityFactory[] entityFactories, int timer) {
		public boolean hasTimer() {
			return timer != 0;
		}
	}
}
