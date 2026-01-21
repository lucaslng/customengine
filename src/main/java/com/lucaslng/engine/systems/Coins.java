package com.lucaslng.engine.systems;

import java.util.Set;

import org.joml.Vector3f;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.SoundHandler;
import com.lucaslng.engine.components.AABBComponent;
import com.lucaslng.engine.components.CoinComponent;
import com.lucaslng.engine.components.DisabledComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.entities.Entity;

public class Coins {
	private final EntityManager entityManager;
	private int collectedCount;

	public Coins(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void update(Entity player1, Entity player2) {
		Set<Integer> coins = entityManager.getEntitiesWith(CoinComponent.class, PositionComponent.class,
				AABBComponent.class);
		if (coins.isEmpty()) {
			return;
		}

		Vector3f pos1 = entityManager.getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = entityManager.getComponent(player2.id(), PositionComponent.class).position();
		Vector3f ext1 = entityManager.getComponent(player1.id(), AABBComponent.class).halfExtents();
		Vector3f ext2 = entityManager.getComponent(player2.id(), AABBComponent.class).halfExtents();

		for (int coinId : coins) {
			CoinComponent coin = entityManager.getComponent(coinId, CoinComponent.class);
			if (coin.collected) {
				continue;
			}
			Vector3f coinPos = entityManager.getComponent(coinId, PositionComponent.class).position();
			Vector3f coinExt = entityManager.getComponent(coinId, AABBComponent.class).halfExtents();

			if (isOverlapping(pos1, ext1, coinPos, coinExt) || isOverlapping(pos2, ext2, coinPos, coinExt)) {
				SoundHandler.play("coin");
				coin.collected = true;
				entityManager.addComponent(coinId, new DisabledComponent());
				collectedCount++;
			}
		}
	}

	public int collectedCount() {
		return collectedCount;
	}

	public void resetCount() {
		collectedCount = 0;
	}

	public void resetAll() {
		collectedCount = 0;
		for (int coinId : entityManager.getEntitiesWith(CoinComponent.class)) {
			CoinComponent coin = entityManager.getComponent(coinId, CoinComponent.class);
			coin.collected = false;
			entityManager.removeComponent(coinId, DisabledComponent.class);
		}
	}

	private boolean isOverlapping(Vector3f posA, Vector3f extA, Vector3f posB, Vector3f extB) {
		return Math.abs(posA.x - posB.x) <= (extA.x + extB.x)
				&& Math.abs(posA.y - posB.y) <= (extA.y + extB.y)
				&& Math.abs(posA.z - posB.z) <= (extA.z + extB.z);
	}
}
