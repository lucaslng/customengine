package com.lucaslng.engine;

import java.io.InputStream;

public class Constants {

	public static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

	public static final InputStream getShader(String fileName) {
		return CLASS_LOADER.getResourceAsStream("shaders/" + fileName);
	}

	public static final InputStream getTexture(String fileName) {
		return CLASS_LOADER.getResourceAsStream("textures/" + fileName);
	}

}
