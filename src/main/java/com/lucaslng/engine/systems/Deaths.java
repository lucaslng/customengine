package com.lucaslng.engine.systems;

import org.joml.Vector3f;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.DeathsComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.engine.systems.Levels.Level;

public class Deaths {

	private final EntityManager entityManager;
	private final Entity player1, player2;

	public Deaths(EntityManager entityManager, Entity player1, Entity player2) {
		this.entityManager = entityManager;
		this.player1 = player1;
		this.player2 = player2;
	}

	public void checkDeaths(Level currentLevel) {
		Vector3f pos1 = entityManager.getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = entityManager.getComponent(player2.id(), PositionComponent.class).position();

		// Deaths
		if (pos1.y < 0f) {
			System.out.println("player 1 died!");
			DeathsComponent deaths = entityManager.getComponent(player1.id(), DeathsComponent.class);
			deaths.died();
			pos1.set(currentLevel.player1Spawn(), 0f);
			pos2.set(currentLevel.player2Spawn(), 0f);
		}
		if (pos2.y < 0f) {
			System.out.println("player 2 died!");
			DeathsComponent deaths = entityManager.getComponent(player2.id(), DeathsComponent.class);
			deaths.died();
			pos1.set(currentLevel.player1Spawn(), 0f);
			pos2.set(currentLevel.player2Spawn(), 0f);
		}
	}
}
