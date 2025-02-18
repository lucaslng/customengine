package com.lucaslng.engine;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.joml.Vector3f;

public class MouseHandler implements MouseListener, MouseMotionListener {

	private Robot robot;
	private final Engine engine;
	private int turnX, turnY;
	private Vector3f rotation;

	public MouseHandler(Engine engine) {
		this.engine = engine;
		rotation = null;
		turnX = 0;
		turnY = 0;
		try {
			robot = new Robot();
			robot.mouseMove(engine.width() / 2, engine.height() / 2);
		} catch (AWTException e) {
			System.out.println("Error with mouse handler Robot initialization: " + e.getMessage());
			System.exit(1);
		}
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public boolean isActive() {
		return rotation != null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
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

}
