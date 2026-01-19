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

	public KeyBind player1Left = new KeyBind(GLFW_KEY_A);
	public KeyBind player1Right = new KeyBind(GLFW_KEY_D);
	public KeyBind player1Jump = new KeyBind(GLFW_KEY_W);
	
	public KeyBind player2Left = new KeyBind(GLFW_KEY_LEFT);
	public KeyBind player2Right = new KeyBind(GLFW_KEY_RIGHT);
	public KeyBind player2Jump = new KeyBind(GLFW_KEY_UP);

}
