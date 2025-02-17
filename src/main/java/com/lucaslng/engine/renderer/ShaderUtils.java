package com.lucaslng.engine.renderer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import static java.nio.file.Paths.get;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;

import com.lucaslng.engine.Constants;

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
					return new String(Files.readAllBytes(get(Constants.shadersDir + path)), StandardCharsets.UTF_8);
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
