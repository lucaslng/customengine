package com.lucaslng.engine.systems;

import com.lucaslng.engine.systems.Levels.Level;

@FunctionalInterface
public interface KillBothPlayersI {
	abstract void killBothPlayers(Level currentLevel, SetTimerI setTimer);
}