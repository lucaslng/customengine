package com.lucaslng.engine;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import org.joml.Vector3f;

public class MouseMotionToRotationListener implements MouseMotionListener {

	private final Robot robot;
	private final Engine engine;
	private Vector3f rotation;

	public MouseMotionToRotationListener(Engine engine) {
		this.engine = engine;
		rotation = null;
		robot = createRobot();
		robot.mouseMove(engine.width() / 2, engine.height() / 2);
	}

	private Robot createRobot() {
		try {
			return new Robot();
		} catch (AWTException e) {
			throw new RuntimeException("Error creating awt robot: " + e.getMessage());
		}
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public boolean isActive() {
		return rotation != null;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (isActive()) {
			rotation.add(new Vector3f(
					-(e.getYOnScreen() - engine.height() / 2),
					e.getXOnScreen() - engine.width() / 2,
					0.0f));
			robot.mouseMove(engine.width() / 2, engine.height() / 2);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

}
