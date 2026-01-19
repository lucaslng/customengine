package com.lucaslng.engine;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.Dimension;

public class EngineSettings {
	public String title = "Custom Engine";
	public Dimension referenceDimension = new Dimension(1920, 1080);

	public float FOV = (float) Math.toRadians(90.0f);

	public float Z_FAR = 100.f;

	public float sensitivity = 0.01f;

	public boolean showFPS = true;

	public int player1Left = GLFW_KEY_A;
	public int player1Right = GLFW_KEY_D;
	public int player1Jump = GLFW_KEY_SPACE;
	
	public int player2Left = GLFW_KEY_LEFT;
	public int player2Right = GLFW_KEY_RIGHT;
	public int player2Jump = GLFW_KEY_UP;

}
