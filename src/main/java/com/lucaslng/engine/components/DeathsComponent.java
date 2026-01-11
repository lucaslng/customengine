package com.lucaslng.engine.components;

public class DeathsComponent {
	public int levelDeaths, totalDeaths;

	public DeathsComponent() {
		levelDeaths = 0;
		totalDeaths = 0;
	}

	public void died() {
		levelDeaths++;
		totalDeaths++;
	}

	public void nextLevel() {
		levelDeaths = 0;
	}
}
