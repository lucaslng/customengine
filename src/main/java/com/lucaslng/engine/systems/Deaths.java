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

	private static final float LAVA_CONTACT_MARGIN = 0.05f;

	public Deaths(EntityManager entityManager, Entity player1, Entity player2, Exits exits) {
		this.entityManager = entityManager;
		this.player1 = player1;
		this.player2 = player2;
		this.exits = exits;
	}

	public void checkDeaths(Level currentLevel, SetTimerI setTimer) {
		Vector3f pos1 = entityManager.getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = entityManager.getComponent(player2.id(), PositionComponent.class).position();

		if (!entityManager.hasComponent(player1.id(), DisabledComponent.class) && pos1.y < 0f) {
			killPlayer(true, currentLevel, setTimer);
			return;
		}
		if (!entityManager.hasComponent(player2.id(), DisabledComponent.class) && pos2.y < 0f) {
			killPlayer(false, currentLevel, setTimer);
			return;
		}

		AABBComponent player1Aabb = entityManager.getComponent(player1.id(), AABBComponent.class);
		AABBComponent player2Aabb = entityManager.getComponent(player2.id(), AABBComponent.class);
		for (int lavaId : entityManager.getEntitiesWith(LavaComponent.class, PositionComponent.class,
				AABBComponent.class)) {
			Vector3f lavaPos = entityManager.getComponent(lavaId, PositionComponent.class).position();
			Vector3f lavaExtents = entityManager.getComponent(lavaId, AABBComponent.class).halfExtents();

			if (!entityManager.hasComponent(player1.id(), DisabledComponent.class)
					&& isTouchingLava(pos1, player1Aabb.halfExtents(), lavaPos, lavaExtents)) {
				killPlayer(true, currentLevel, setTimer);
				return;
			}
			if (!entityManager.hasComponent(player2.id(), DisabledComponent.class)
					&& isTouchingLava(pos2, player2Aabb.halfExtents(), lavaPos, lavaExtents)) {
				killPlayer(false, currentLevel, setTimer);
				return;
			}
		}
	}

	public void killBothPlayers(Level currentLevel, SetTimerI setTimer) {
		DeathsComponent deaths1 = entityManager.getComponent(player1.id(), DeathsComponent.class);
		deaths1.died();
		DeathsComponent deaths2 = entityManager.getComponent(player2.id(), DeathsComponent.class);
		deaths2.died();
		resetStates(currentLevel, setTimer);
	}

	private void killPlayer(boolean isPlayer1, Level currentLevel, SetTimerI setTimer) {
		Entity deadPlayer = isPlayer1 ? player1 : player2;
		DeathsComponent deaths = entityManager.getComponent(deadPlayer.id(), DeathsComponent.class);
		deaths.died();
		resetStates(currentLevel, setTimer);
	}

	private void resetStates(Level currentLevel, SetTimerI setTimer) {
		Vector3f pos1 = entityManager.getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = entityManager.getComponent(player2.id(), PositionComponent.class).position();
		pos1.set(currentLevel.player1Spawn(), 0f);
		pos2.set(currentLevel.player2Spawn(), 0f);
		Vector3f vel1 = entityManager.getComponent(player1.id(), VelocityComponent.class).velocity();
		vel1.set(0f, 0f, 0f);
		Vector3f vel2 = entityManager.getComponent(player2.id(), VelocityComponent.class).velocity();
		vel2.set(0f, 0f, 0f);
		entityManager.removeComponent(player1.id(), DisabledComponent.class);
		entityManager.removeComponent(player2.id(), DisabledComponent.class);
		exits.player1Exited = false;
		exits.player2Exited = false;
		setTimer.setTimer(currentLevel.timer());
	}

	

	private boolean isTouchingLava(Vector3f playerPos, Vector3f playerExt, Vector3f lavaPos, Vector3f lavaExt) {
		boolean overlapXZ = Math.abs(playerPos.x - lavaPos.x) <= (playerExt.x + lavaExt.x)
				&& Math.abs(playerPos.z - lavaPos.z) <= (playerExt.z + lavaExt.z);

		if (!overlapXZ) {
			return false;
		}

		float playerBottom = playerPos.y - playerExt.y;
		float playerTop = playerPos.y + playerExt.y;
		float lavaBottom = lavaPos.y - lavaExt.y;
		float lavaTop = lavaPos.y + lavaExt.y;

		return playerBottom <= lavaTop + LAVA_CONTACT_MARGIN && playerTop >= lavaBottom - LAVA_CONTACT_MARGIN;
	}
}
