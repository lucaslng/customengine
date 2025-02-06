package com.lucaslng;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import static java.nio.file.Paths.get;

import static org.lwjgl.opengl.GL41.*;

public class ShaderUtils {
	public static int createShaderProgram(String vertexPath, String fragmentPath) {
			int vertexShader = glCreateShader(GL_VERTEX_SHADER);
			glShaderSource(vertexShader, readFile(vertexPath));
			glCompileShader(vertexShader);
			checkCompileErrors(vertexShader, "VERTEX");

			int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
			glShaderSource(fragmentShader, readFile(fragmentPath));
			glCompileShader(fragmentShader);
			checkCompileErrors(fragmentShader, "FRAGMENT");

			// Link shaders into a shader program
			int shaderProgram = glCreateProgram();
			glAttachShader(shaderProgram, vertexShader);
			glAttachShader(shaderProgram, fragmentShader);
			glLinkProgram(shaderProgram);

			// Cleanup: We can delete shaders after linking
			glDeleteShader(vertexShader);
			glDeleteShader(fragmentShader);

			return shaderProgram;
	}

	private static String readFile(String path) {
			try {
					return new String(Files.readAllBytes(get(Settings.shadersDir + path)), StandardCharsets.UTF_8);
			} catch (IOException e) {
					throw new RuntimeException("Failed to load shader file: " + path, e);
			}
	}

	private static void checkCompileErrors(int shader, String type) {
			int success = glGetShaderi(shader, GL_COMPILE_STATUS);
			if (success == GL_FALSE) {
					String infoLog = glGetShaderInfoLog(shader);
					throw new RuntimeException("ERROR: " + type + " SHADER COMPILATION FAILED\n" + infoLog);
			}
	}
}
