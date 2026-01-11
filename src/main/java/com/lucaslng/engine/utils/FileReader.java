package com.lucaslng.engine.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

public class FileReader {

	private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

	public static final InputStream getResource(String path) {
		return CLASS_LOADER.getResourceAsStream(path);
	}

	private static final InputStream getShader(String fileName) {
		return getResource("shaders/" + fileName);
	}

	private static final InputStream getTexture(String fileName) {
		return getResource("textures/" + fileName);
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

	public static List<String> readLines(String path) {
		List<String> lines = Collections.emptyList();

		try {
			lines = Files.readAllLines(
					Paths.get("src/main/resources/" + path),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load file: " + path, e);
		}

		return lines;
	}

}
