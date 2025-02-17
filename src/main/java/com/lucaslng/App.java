package com.lucaslng;

import java.awt.event.KeyEvent;

import org.joml.Vector3f;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.components.HeadRotationComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.entities.Entity;
import com.lucaslng.entities.PlayerEntityFactory;

public class App {
	public static void main(String[] args) {

		Engine engine = new Engine();

		Entity player = engine.buildEntity(new PlayerEntityFactory(0.0f, 0.0f, -7.0f));

		while (true) {
			if (engine.isKeyPressed(KeyEvent.VK_A)) {
				Vector3f position = engine.getComponent(player.id(), PositionComponent.class).position();
				HeadRotationComponent rotation = engine.getComponent(player.id(), HeadRotationComponent.class);
				position.x -= Math.cos(rotation.yaw()) * -0.02f;
				position.z += Math.sin(rotation.yaw()) * -0.02f;
			}
			if (engine.isKeyPressed(KeyEvent.VK_E)) {
				Vector3f position = engine.getComponent(player.id(), PositionComponent.class).position();
				HeadRotationComponent rotation = engine.getComponent(player.id(), HeadRotationComponent.class);
				position.x -= Math.cos(rotation.yaw()) * 0.02f;
				position.z += Math.sin(rotation.yaw()) * 0.02f;
			}
			if (engine.isKeyPressed(KeyEvent.VK_W)) {
				Vector3f position = engine.getComponent(player.id(), PositionComponent.class).position();
				HeadRotationComponent rotation = engine.getComponent(player.id(), HeadRotationComponent.class);
				position.x += Math.sin(rotation.yaw()) * 0.02f;
				position.z += Math.cos(rotation.yaw()) * 0.02f;
			}
			if (engine.isKeyPressed(KeyEvent.VK_O)) {
				Vector3f position = engine.getComponent(player.id(), PositionComponent.class).position();
				HeadRotationComponent rotation = engine.getComponent(player.id(), HeadRotationComponent.class);
				position.x += Math.sin(rotation.yaw()) * -0.02f;
				position.z += Math.cos(rotation.yaw()) * -0.02f;
			}
			
			engine.setCamera(player);
			engine.doLoop();
		}

	}
}
