package com.lucaslng.engine.systems;

import org.joml.Vector3f;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.DeathsComponent;
import com.lucaslng.engine.components.DisabledComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.VelocityComponent;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.engine.systems.Levels.Level;

public class Deaths {

	private final EntityManager entityManager;
	private final Entity player1, player2;
	private final Exits exits;

	public Deaths(EntityManager entityManager, Entity player1, Entity player2, Exits exits) {
		this.entityManager = entityManager;
		this.player1 = player1;
		this.player2 = player2;
		this.exits = exits;
	}

	public void checkDeaths(Level currentLevel) {
		Vector3f pos1 = entityManager.getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = entityManager.getComponent(player2.id(), PositionComponent.class).position();

		if (!entityManager.hasComponent(player1.id(), DisabledComponent.class) && pos1.y < 0f) {
			System.out.println("player 1 died!");
			DeathsComponent deaths = entityManager.getComponent(player1.id(), DeathsComponent.class);
			deaths.died();
			pos1.set(currentLevel.player1Spawn(), 0f);
			Vector3f vel1 = entityManager.getComponent(player1.id(), VelocityComponent.class).velocity();
			vel1.set(0f, 0f, 0f);
			pos2.set(currentLevel.player2Spawn(), 0f);
			Vector3f vel2 = entityManager.getComponent(player2.id(), VelocityComponent.class).velocity();
			vel2.set(0f, 0f, 0f);
			entityManager.removeComponent(player2.id(), DisabledComponent.class);
			exits.player2Exited = false;
		}

		if (!entityManager.hasComponent(player2.id(), DisabledComponent.class) && pos2.y < 0f) {
			System.out.println("player 2 died!");
			DeathsComponent deaths = entityManager.getComponent(player2.id(), DeathsComponent.class);
			deaths.died();
			pos2.set(currentLevel.player2Spawn(), 0f);
			Vector3f vel2 = entityManager.getComponent(player2.id(), VelocityComponent.class).velocity();
			vel2.set(0f, 0f, 0f);
			pos1.set(currentLevel.player1Spawn(), 0f);
			Vector3f vel1 = entityManager.getComponent(player1.id(), VelocityComponent.class).velocity();
			vel1.set(0f, 0f, 0f);
			entityManager.removeComponent(player1.id(), DisabledComponent.class);
			exits.player1Exited = false;
		}
	}
}