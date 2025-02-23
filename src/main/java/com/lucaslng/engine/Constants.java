package com.lucaslng.engine;

import java.io.InputStream;

public final class Constants {

	private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

	public static final InputStream getResource(String path) {
		return CLASS_LOADER.getResourceAsStream(path);
	}

	public static final InputStream getShader(String fileName) {
		return getResource("shaders/" + fileName);
	}

	public static final InputStream getTexture(String fileName) {
		return getResource("textures/" + fileName);
	}

}
