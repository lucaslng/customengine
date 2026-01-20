package com.lucaslng.engine.systems;

import org.joml.Vector3f;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.AABBComponent;
import com.lucaslng.engine.components.DeathsComponent;
import com.lucaslng.engine.components.DisabledComponent;
import com.lucaslng.engine.components.LavaComponent;
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
			killPlayer(true, currentLevel);
			return;
		}
		if (!entityManager.hasComponent(player2.id(), DisabledComponent.class) && pos2.y < 0f) {
			killPlayer(false, currentLevel);
			return;
		}

		AABBComponent player1Aabb = entityManager.getComponent(player1.id(), AABBComponent.class);
		AABBComponent player2Aabb = entityManager.getComponent(player2.id(), AABBComponent.class);
		for (int lavaId : entityManager.getEntitiesWith(LavaComponent.class, PositionComponent.class, AABBComponent.class)) {
			Vector3f lavaPos = entityManager.getComponent(lavaId, PositionComponent.class).position();
			Vector3f lavaExtents = entityManager.getComponent(lavaId, AABBComponent.class).halfExtents();

			if (!entityManager.hasComponent(player1.id(), DisabledComponent.class)
					&& isOverlapping(pos1, player1Aabb.halfExtents(), lavaPos, lavaExtents)) {
				killPlayer(true, currentLevel);
				return;
			}
			if (!entityManager.hasComponent(player2.id(), DisabledComponent.class)
					&& isOverlapping(pos2, player2Aabb.halfExtents(), lavaPos, lavaExtents)) {
				killPlayer(false, currentLevel);
				return;
			}
		}
	}

	private void killPlayer(boolean isPlayer1, Level currentLevel) {
		Entity deadPlayer = isPlayer1 ? player1 : player2;
		Entity otherPlayer = isPlayer1 ? player2 : player1;
		
		DeathsComponent deaths = entityManager.getComponent(deadPlayer.id(), DeathsComponent.class);
		deaths.died();

		Vector3f deadPos = entityManager.getComponent(deadPlayer.id(), PositionComponent.class).position();
		Vector3f otherPos = entityManager.getComponent(otherPlayer.id(), PositionComponent.class).position();
		if (isPlayer1) {
			deadPos.set(currentLevel.player1Spawn(), 0f);
			otherPos.set(currentLevel.player2Spawn(), 0f);
		} else {
			deadPos.set(currentLevel.player2Spawn(), 0f);
			otherPos.set(currentLevel.player1Spawn(), 0f);
		}
		Vector3f deadVel = entityManager.getComponent(deadPlayer.id(), VelocityComponent.class).velocity();
		deadVel.set(0f, 0f, 0f);
		Vector3f otherVel = entityManager.getComponent(otherPlayer.id(), VelocityComponent.class).velocity();
		otherVel.set(0f, 0f, 0f);

		entityManager.removeComponent(otherPlayer.id(), DisabledComponent.class);
		if (isPlayer1) {
			exits.player2Exited = false;
		} else {
			exits.player1Exited = false;
		}
	}

	private boolean isOverlapping(Vector3f posA, Vector3f extA, Vector3f posB, Vector3f extB) {
		return Math.abs(posA.x - posB.x) <= (extA.x + extB.x)
				&& Math.abs(posA.y - posB.y) <= (extA.y + extB.y)
				&& Math.abs(posA.z - posB.z) <= (extA.z + extB.z);
	}
}
