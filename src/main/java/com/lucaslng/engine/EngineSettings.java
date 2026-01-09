package com.lucaslng.engine;

import java.awt.Dimension;

import org.lwjgl.opengl.awt.GLData;

public class EngineSettings {
	public String title = "Custom Engine";
	public Dimension windowSize = new Dimension(800, 600);

	public float FOV = (float) Math.toRadians(90.0f);

	public float Z_FAR = 100.f;

	public float sensitivity = 0.01f;

	public GLData getGLData() {
		GLData data = new GLData();
		data.samples = 4;
		data.swapInterval = 1;
		data.profile = GLData.Profile.CORE;
		data.majorVersion = 4;
		data.minorVersion = 1;
		data.forwardCompatible = true;
		return data;
	}

}
