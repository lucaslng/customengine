package com.lucaslng.engine;

import java.net.URL;

public class Constants {

	public static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

	public static final String shadersDir(Class<?> c) {
		return CLASS_LOADER.getResource("shaders").getPath();
	}

	public static final String texturesDir(Class<?> c) {
		return CLASS_LOADER.getResource("textures").getPath() + "/";
	}

}
