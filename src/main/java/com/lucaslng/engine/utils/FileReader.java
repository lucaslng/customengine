package com.lucaslng.engine.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.Paths.get;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import com.lucaslng.engine.Constants;

public class FileReader {

	public static String readShaderFile(String path) {
		try {
			return new String(Constants.getShader(path).readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load shader file: " + path + "\n" + e.getMessage());
		}
	}

	public static BufferedImage readImage(String path) {
		try {
			return ImageIO.read(Constants.getTexture(path));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load image file: " + path, e);
		}
	}

	public static List<String> readLines(String path) {
		List<String> lines = Collections.emptyList();

		try {
			lines = Files.readAllLines(
					Paths.get(path),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load image file: " + path, e);
		}

		return lines;
	}

}
