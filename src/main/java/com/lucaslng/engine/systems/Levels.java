package com.lucaslng.engine.systems;

import static java.lang.Float.parseFloat;

import java.util.List;

import org.joml.Vector2f;

import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.utils.FileReader;
import com.lucaslng.entities.BoxEntityFactory;
import com.lucaslng.entities.ExitEntityFactory;

public class Levels {

	private static final int LEVEL_COUNT = 1;
	private final Level[] levels;
	public int currentLevelIndex;

	public Levels() {
		levels = readLevels();
		currentLevelIndex = 1;
	}

	private static Level[] readLevels() {
		Level[] levels = new Level[LEVEL_COUNT];
		for (int i = 1; i <= LEVEL_COUNT; i++) {
			levels[i - 1] = parseLevel(FileReader.readLines("levels/" + i));
		}
		return levels;
	}

	private static Level parseLevel(List<String> lines) {
		assert lines.size() > 0;
		AbstractEntityFactory[] entities = new AbstractEntityFactory[lines.size() - 1]; // ignore first line
		String[] tokens;
		for (int i = 0; i < entities.length; i++) {
			tokens = lines.get(i + 1).split(" ");
			switch (tokens[0]) {
				case "platform" -> {
					entities[i] = new BoxEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]), -1.1f,
							parseFloat(tokens[3]), parseFloat(tokens[4]), 2.2f, "Platform");
				}
				case "exit" -> {
					entities[i] = new ExitEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]));
				}
				default -> {
				}
			}
		}
		tokens = lines.get(0).split(" ");
		assert tokens.length == 4;
		return new Level(new Vector2f(parseFloat(tokens[0]), parseFloat(tokens[1])),
				new Vector2f(parseFloat(tokens[2]), parseFloat(tokens[3])), entities);
	}

	public Level currentLevel() {
		return levels[currentLevelIndex - 1];
	}

	public record Level(Vector2f player1Spawn, Vector2f player2Spawn, AbstractEntityFactory[] entityFactories) {
	}
}
