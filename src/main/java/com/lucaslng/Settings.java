package com.lucaslng;

import java.awt.Dimension;

import org.lwjgl.opengl.awt.GLData;

public class Settings {
	public static int samples = 4;
	public static String title = "Custom Engine";
	public static Dimension windowSize = new Dimension(600, 600);

	public static GLData getGLData() {
		GLData data = new GLData();
		data.samples = 4;
		data.swapInterval = 0;
		return data;
	}

}
