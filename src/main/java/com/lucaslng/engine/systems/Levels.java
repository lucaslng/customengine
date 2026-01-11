package com.lucaslng.engine.systems;

import static java.lang.Float.parseFloat;

import java.util.List;

import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.utils.FileReader;
import com.lucaslng.entities.BoxEntityFactory;
import com.lucaslng.entities.PlayerEntityFactory;

public class Levels {

	private static final int LEVEL_COUNT = 1;
	private final AbstractEntityFactory[][] levels;

	public Levels() {
		levels = readLevels();
	}

	private static AbstractEntityFactory[][] readLevels() {
		AbstractEntityFactory[][] levels = new AbstractEntityFactory[LEVEL_COUNT][];
		for (int i = 1; i <= LEVEL_COUNT; i++) {
			levels[i - 1] = parseLevel(FileReader.readLines("levels/" + i));
		}
		return levels;
	}

	private static AbstractEntityFactory[] parseLevel(List<String> lines) {
		assert lines.size() > 0;
		AbstractEntityFactory[] entities = new AbstractEntityFactory[lines.size() + 1]; // first line contains 2 entities:
																																										// player 1 and player 2
		String[] tokens = lines.get(0).split(" ");
		assert tokens.length == 4;
		entities[0] = new PlayerEntityFactory(parseFloat(tokens[0]), parseFloat(tokens[1]), 0);
		entities[1] = new PlayerEntityFactory(parseFloat(tokens[2]), parseFloat(tokens[3]), 0);
		for (int i = 1; i < lines.size(); i++) {
			tokens = lines.get(i).split(" ");
			switch (tokens[0]) {
				case "platform" -> {
					entities[i + 1] = new BoxEntityFactory(parseFloat(tokens[1]), parseFloat(tokens[2]), -1.1f, parseFloat(tokens[3]), parseFloat(tokens[4]), 2.2f, "Platform");
				}
				default -> {
				}
			}
		}
		return entities;
	}

	public AbstractEntityFactory[] getLevelEntities(int level) {
		return levels[level - 1];
	}
}
