package com.lucaslng.engine.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;

public class FileReader {

	private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

	public static final InputStream getStream(String name) {
		return CLASS_LOADER.getResourceAsStream(name);
	}

	public static final URL getResource(String name) {
		return CLASS_LOADER.getResource(name);
	}

	private static final InputStream getShader(String fileName) {
		return getStream("shaders/" + fileName);
	}

	private static final InputStream getTexture(String fileName) {
		return getStream("textures/" + fileName);
	}

	public static String readShaderFile(String path) {
		try {
			return new String(getShader(path).readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load shader file: " + path + "\n" + e.getMessage());
		}
	}

	public static BufferedImage readImage(String path) {
		try {
			return ImageIO.read(getTexture(path));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load image file: " + path, e);
		}
	}

}
