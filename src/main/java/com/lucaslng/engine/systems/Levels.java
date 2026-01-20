package com.lucaslng.engine.systems;

import static java.lang.Float.parseFloat;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.joml.Vector2f;

import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.components.LavaComponent;
import com.lucaslng.entities.BoxEntityFactory;
import com.lucaslng.entities.ButtonEntityFactory;
import com.lucaslng.entities.ExitEntityFactory;

public class Levels {

	public static final int LEVEL_COUNT = 2;
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
					entities[i] = new ButtonEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]));
				}
				default -> {
				}
			}
		}
		in.close();
		return new Level(player1Spawn, player2Spawn, entities);
	}

	public Level currentLevel() {
		return levels[currentLevelIndex - 1];
	}

	public record Level(Vector2f player1Spawn, Vector2f player2Spawn, AbstractEntityFactory[] entityFactories) {
	}
}