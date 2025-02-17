package com.lucaslng.engine.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import static java.nio.file.Paths.get;

import com.lucaslng.engine.Constants;

public class FileReader {

	public static String readShaderFile(String path) {
		try {
			return new String(Files.readAllBytes(get(Constants.SHADERS_DIR + path)), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load shader file: " + path, e);
		}
	}

}
