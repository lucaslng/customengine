package com.lucaslng.engine.systems;

import com.lucaslng.engine.systems.Levels.Level;
import com.lucaslng.engine.ui.Text;

public class Timers {

	private double timer;
	private final Text text;

	public Timers(Text timerText) {
		text = timerText;
	}

	public void setTimer(int levelTimer) {
		timer = levelTimer;
		text.visible = levelTimer != 0;
	}

	public void step(double dt, Level currentLevel, KillBothPlayersI killBothPlayers) {
		if (currentLevel.timer() != 0) {
				if (timer < 0d) {
					killBothPlayers.killBothPlayers(currentLevel, (levelTimer) -> setTimer(levelTimer));
					timer = currentLevel.timer();
				} else
					timer -= dt;
			}
			text.text = Math.round(timer * 100) / 100f + "";
	}
}
