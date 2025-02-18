package com.lucaslng;

import java.awt.event.KeyEvent;

import org.joml.Vector3f;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameLoop;
import com.lucaslng.engine.components.HeadRotationComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.engine.systems.Positions;
import com.lucaslng.entities.CubeEntityFactory;
import com.lucaslng.entities.PlayerEntityFactory;

public class Game extends GameLoop {

	private Entity player;

	public Game(Engine engine) {
		super(engine);
	}

	@Override
	public void init(Engine engine) {
		player = engine.buildEntity(new PlayerEntityFactory(0.0f, 0.0f, 7.0f));
		engine.buildEntity(new CubeEntityFactory(0.0f, 0.0f, 0.0f, 1.0f));
		engine.setCamera(player);
		engine.linkMouseToRotation(engine.entityManager().getComponent(player.id(), HeadRotationComponent.class).rotation());
	}

	@Override
	public void doLoop(Engine engine) {
		if (engine.isKeyHeld(KeyEvent.VK_A)) {
			Vector3f position = engine.getComponent(player.id(), PositionComponent.class).position();
			Vector3f rotation = engine.getComponent(player.id(), HeadRotationComponent.class).rotation();
			Positions.moveLeft(position, rotation, 0.02f);
		}
		if (engine.isKeyHeld(KeyEvent.VK_E)) {
			Vector3f position = engine.getComponent(player.id(), PositionComponent.class).position();
			Vector3f rotation = engine.getComponent(player.id(), HeadRotationComponent.class).rotation();
			Positions.moveRight(position, rotation, 0.02f);
		}
		if (engine.isKeyHeld(KeyEvent.VK_W)) {
			Vector3f position = engine.getComponent(player.id(), PositionComponent.class).position();
			Vector3f rotation = engine.getComponent(player.id(), HeadRotationComponent.class).rotation();
			Positions.moveForward(position, rotation, 0.02f);
		}
		if (engine.isKeyHeld(KeyEvent.VK_O)) {
			Vector3f position = engine.getComponent(player.id(), PositionComponent.class).position();
			Vector3f rotation = engine.getComponent(player.id(), HeadRotationComponent.class).rotation();
			Positions.moveBackward(position, rotation, 0.02f);
		}
		if (engine.isKeyHeld(KeyEvent.VK_SPACE)) {
			Vector3f position = engine.getComponent(player.id(), PositionComponent.class).position();
			Vector3f rotation = engine.getComponent(player.id(), HeadRotationComponent.class).rotation();
			Positions.moveUp(position, rotation, 0.03f);
		}
		if (engine.isKeyHeld(KeyEvent.VK_SHIFT)) {
			Vector3f position = engine.getComponent(player.id(), PositionComponent.class).position();
			Vector3f rotation = engine.getComponent(player.id(), HeadRotationComponent.class).rotation();
			Positions.moveDown(position, rotation, 0.03f);
		}

		engine.setCamera(player);
	}
}
