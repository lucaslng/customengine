package com.lucaslng.engine.systems;

import org.joml.Vector3f;

import com.lucaslng.engine.EntityManager;
import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.Entity;

public class Buttons {

	private final EntityManager entityManager;
	private final Entity player1, player2;
	
	private static final float BUTTON_PRESS_DEPTH = 0.35f;
	private static final float ACTIVATION_MARGIN = 0.5f;

	public Buttons(EntityManager entityManager, Entity player1, Entity player2) {
		this.entityManager = entityManager;
		this.player1 = player1;
		this.player2 = player2;
	}

	public void update() {
		Vector3f pos1 = entityManager.getComponent(player1.id(), PositionComponent.class).position();
		Vector3f pos2 = entityManager.getComponent(player2.id(), PositionComponent.class).position();
		AABBComponent player1Aabb = entityManager.getComponent(player1.id(), AABBComponent.class);
		AABBComponent player2Aabb = entityManager.getComponent(player2.id(), AABBComponent.class);

		// Check all buttons
		for (int buttonId : entityManager.getEntitiesWith(ButtonComponent.class, PositionComponent.class, AABBComponent.class, MeshComponent.class)) {
			ButtonComponent button = entityManager.getComponent(buttonId, ButtonComponent.class);
			Vector3f buttonPos = entityManager.getComponent(buttonId, PositionComponent.class).position();
			Vector3f buttonExtents = entityManager.getComponent(buttonId, AABBComponent.class).halfExtents();
			MeshComponent mesh = entityManager.getComponent(buttonId, MeshComponent.class);

			boolean player1OnButton = !entityManager.hasComponent(player1.id(), DisabledComponent.class) 
					&& isPlayerOnButton(pos1, player1Aabb.halfExtents(), buttonPos, buttonExtents);
			boolean player2OnButton = !entityManager.hasComponent(player2.id(), DisabledComponent.class) 
					&& isPlayerOnButton(pos2, player2Aabb.halfExtents(), buttonPos, buttonExtents);

			boolean wasVisualPressed = button.isActive;
			boolean isPressedNow = player1OnButton || player2OnButton;

			if (button.isToggle) {
				if (isPressedNow && !button.isPressed) {
					button.isActive = !button.isActive;
				}
			} else if (button.isLatch) {
				if (isPressedNow && !button.isPressed) {
					button.isActive = true;
				}
			} else {
				button.isActive = isPressedNow;
			}

			button.isPressed = isPressedNow;

			// Update button position and material when state changes
			if (button.isActive && !wasVisualPressed) {
				// Press down
				buttonPos.y -= BUTTON_PRESS_DEPTH;
				// Change material to blue
				if (mesh.subMeshes().length > 0) {
					mesh.subMeshes()[0] = new com.lucaslng.engine.renderer.SubMesh(
						getVertices(buttonExtents),
						getIndices(),
						"ButtonBlue"
					);
				}
			} else if (!button.isActive && wasVisualPressed) {
				// Release up
				buttonPos.y += BUTTON_PRESS_DEPTH;
				// Change material back to yellow
				if (mesh.subMeshes().length > 0) {
					mesh.subMeshes()[0] = new com.lucaslng.engine.renderer.SubMesh(
						getVertices(buttonExtents),
						getIndices(),
						"ButtonYellow"
					);
				}
			}
		}
	}

	private boolean isPlayerOnButton(Vector3f playerPos, Vector3f playerExt, Vector3f buttonPos, Vector3f buttonExt) {
		// Check if player is above button (in XZ plane)
		boolean overlapXZ = Math.abs(playerPos.x - buttonPos.x) <= (playerExt.x + buttonExt.x + ACTIVATION_MARGIN)
				&& Math.abs(playerPos.z - buttonPos.z) <= (playerExt.z + buttonExt.z + ACTIVATION_MARGIN);

		if (!overlapXZ) {
			return false;
		}

		// Check if player is standing on top of button
		float playerBottom = playerPos.y - playerExt.y;
		float buttonTop = buttonPos.y + buttonExt.y;

		// Player must be standing on or very close to the button top
		return playerBottom >= buttonTop - 0.5f && playerBottom <= buttonTop + 0.5f;
	}

	private static final int[] getIndices() {
		return new int[] {
			0, 1, 2, 2, 3, 0, // back
			4, 5, 6, 6, 7, 4, // right
			8, 9, 10, 10, 11, 8, // front
			12, 13, 14, 14, 15, 12, // left
			16, 17, 18, 18, 19, 16, // top
			20, 21, 22, 22, 23, 20 // bottom
		};
	}

	private static float[] getVertices(Vector3f extents) {
		float hx = extents.x;
		float hy = extents.y;
		float hz = extents.z;
		float tx = hx / 2f;
		float ty = hy / 2f;
		float tz = hz / 2f;
		
		return new float[] {
			// BACK (-Z)
			-hx, -hy, -hz, 0, 0, -1, 0, 0,
			hx, -hy, -hz, 0, 0, -1, tx, 0,
			hx, hy, -hz, 0, 0, -1, tx, ty,
			-hx, hy, -hz, 0, 0, -1, 0, ty,

			// RIGHT (+X)
			hx, -hy, -hz, 1, 0, 0, 0, 0,
			hx, -hy, hz, 1, 0, 0, 0, tz,
			hx, hy, hz, 1, 0, 0, ty, tz,
			hx, hy, -hz, 1, 0, 0, ty, 0,

			// FRONT (+Z)
			hx, -hy, hz, 0, 0, 1, tx, 0,
			-hx, -hy, hz, 0, 0, 1, 0, 0,
			-hx, hy, hz, 0, 0, 1, 0, ty,
			hx, hy, hz, 0, 0, 1, tx, ty,

			// LEFT (-X)
			-hx, -hy, hz, -1, 0, 0, 0, tz,
			-hx, -hy, -hz, -1, 0, 0, 0, 0,
			-hx, hy, -hz, -1, 0, 0, ty, 0,
			-hx, hy, hz, -1, 0, 0, ty, tz,

			// TOP (+Y)
			-hx, hy, -hz, 0, 1, 0, 0, 0,
			hx, hy, -hz, 0, 1, 0, tx, 0,
			hx, hy, hz, 0, 1, 0, tx, tz,
			-hx, hy, hz, 0, 1, 0, 0, tz,

			// BOTTOM (-Y)
			-hx, -hy, hz, 0, -1, 0, 0, tz,
			hx, -hy, hz, 0, -1, 0, tx, tz,
			hx, -hy, -hz, 0, -1, 0, tx, 0,
			-hx, -hy, -hz, 0, -1, 0, 0, 0
		};
	}
}
