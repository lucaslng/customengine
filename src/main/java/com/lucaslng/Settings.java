package com.lucaslng;

import java.awt.Dimension;

import org.lwjgl.opengl.awt.GLData;

public class Settings {
	public static int samples = 4;
	public static String title = "Custom Engine";
	public static final String resourcesDir = "resources/";
	public static final String shadersDir = resourcesDir + "shaders/";
	public static Dimension windowSize = new Dimension(600, 600);

	public static float FOV = (float) Math.toRadians(45.0f);

	public static float Z_FAR = 100.f;

	public static float sensitivity = 0.01f;

	public static int FPS = 60;

	public static GLData getGLData() {
		GLData data = new GLData();
		data.samples = 4;
		data.swapInterval = 1;
		data.profile = GLData.Profile.CORE;
		data.majorVersion = 4;
		data.minorVersion = 1;
		return data;
	}

}
