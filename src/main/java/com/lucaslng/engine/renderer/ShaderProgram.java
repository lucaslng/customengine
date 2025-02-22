package com.lucaslng.engine.renderer;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11C.GL_FALSE;
import static org.lwjgl.opengl.GL11C.GL_ZERO;
import static org.lwjgl.opengl.GL20C.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20C.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20C.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20C.glAttachShader;
import static org.lwjgl.opengl.GL20C.glCompileShader;
import static org.lwjgl.opengl.GL20C.glCreateProgram;
import static org.lwjgl.opengl.GL20C.glCreateShader;
import static org.lwjgl.opengl.GL20C.glDeleteProgram;
import static org.lwjgl.opengl.GL20C.glDeleteShader;
import static org.lwjgl.opengl.GL20C.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20C.glGetShaderi;
import static org.lwjgl.opengl.GL20C.glGetUniformLocation;
import static org.lwjgl.opengl.GL20C.glLinkProgram;
import static org.lwjgl.opengl.GL20C.glShaderSource;
import static org.lwjgl.opengl.GL20C.glUniform4f;
import static org.lwjgl.opengl.GL20C.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20C.glUseProgram;

import static com.lucaslng.engine.utils.FileReader.readShaderFile;

public class ShaderProgram {

	private final int id, vertexShader, fragmentShader;
	private boolean isCompiled;
	private final HashMap<String, Integer> uniformLocationCache;

	protected ShaderProgram(String vertexPath, String fragmentPath) {
		uniformLocationCache = new HashMap<>();
		
		vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, readShaderFile(vertexPath, this.getClass()));

		fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, readShaderFile(fragmentPath, this.getClass()));

		id = glCreateProgram();
		glAttachShader(id, vertexShader);
		glAttachShader(id, fragmentShader);
	}

	protected void compileShader() {
		glCompileShader(vertexShader);
		if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
			throw new RuntimeException("Failed compiling vertex shader.\n" + glGetShaderInfoLog(vertexShader));
		}
		glCompileShader(fragmentShader);
		if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
			throw new RuntimeException("Failed compiling vertex shader.\n" + glGetShaderInfoLog(fragmentShader));
		}
		glLinkProgram(id);
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
		isCompiled = true;
	}

	protected void setUniform4f(CharSequence name, float x, float y, float z, float w) {
		glUniform4f(getUniformLocation(name), x, y, z, w);
	}

	protected void setUniformMatrix4v(CharSequence name, boolean transpose, float[] value) {
		glUniformMatrix4fv(getUniformLocation(name), transpose, value);
	}

	protected int getUniformLocation(CharSequence name) {
		assert isCompiled;

		String nameString = name.toString();
		if (uniformLocationCache.containsKey(nameString)) {
			return uniformLocationCache.get(nameString);
		}

		int location = glGetUniformLocation(id, name);
		if (location == -1) {
			System.out.println("WARNING: shader uniform " + name + " location is -1.");
		}
		uniformLocationCache.put(nameString, location);
		return location;
	}

	protected void bind() {
		assert isCompiled;
		glUseProgram(id);
	}

	protected void unbind() {
		glUseProgram(GL_ZERO);
	}

	protected void delete() {
		unbind();
		glDeleteProgram(id);
	}

	protected int id() {
		return id;
	}
	
}
