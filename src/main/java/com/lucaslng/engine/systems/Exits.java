package com.lucaslng.engine.systems;

import java.util.Set;

import org.joml.Vector3f;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.DisabledComponent;
import com.lucaslng.engine.components.ExitComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.entities.ExitEntityFactory;

public class Exits {
	public boolean player1Exited, player2Exited;
	private final EntityManager entityManager;
	private Set<Integer> exits;

	public Exits(EntityManager entityManager) {
		this.entityManager = entityManager;
		refresh();
	}

	public void handleExits(Entity player1, Entity player2) {
		Vector3f pos1 = entityManager.getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = entityManager.getComponent(player2.id(), PositionComponent.class).position();
		for (int exit : exits) {
			Vector3f exitPos = entityManager.getComponent(exit, PositionComponent.class).position();
			float s = ExitEntityFactory.halfSize;
			if (!player1Exited && pos1.x() > exitPos.x() - s && pos1.x() < exitPos.x() + s && pos1.y() > exitPos.y() - s && pos1.y() < exitPos.y() + s) {
				System.out.println("player 1 exit");
				entityManager.addComponent(player1.id(), new DisabledComponent());
				player1Exited = true;
			}
			if (!player2Exited && pos2.x() > exitPos.x() - s && pos2.x() < exitPos.x() + s && pos2.y() > exitPos.y() - s && pos2.y() < exitPos.y() + s) {
				System.out.println("player 2 exit");
				entityManager.addComponent(player2.id(), new DisabledComponent());
				player2Exited = true;
			}
		}
	}

	public void refresh() {
		player1Exited = false;
		player2Exited = false;
		exits = entityManager.getEntitiesWith(ExitComponent.class);
	}
}
