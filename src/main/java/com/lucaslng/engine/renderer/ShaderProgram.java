package com.lucaslng.engine.renderer;

import java.util.HashMap;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11C.GL_FALSE;
import static org.lwjgl.opengl.GL11C.GL_ZERO;
import static org.lwjgl.opengl.GL20C.*;
import static com.lucaslng.engine.utils.FileReader.readShaderFile;

class ShaderProgram{

	private final int id, vertexShader, fragmentShader;
	private boolean isCompiled;
	private final HashMap<String, Integer> uniformLocationCache;

	protected ShaderProgram(String vertexPath, String fragmentPath) {
		uniformLocationCache = new HashMap<>();
		
		vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, readShaderFile(vertexPath));

		fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, readShaderFile(fragmentPath));

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
			throw new RuntimeException("Failed compiling fragment shader.\n" + glGetShaderInfoLog(fragmentShader));
		}
		glLinkProgram(id);
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
		isCompiled = true;
	}

	protected void setUniform1i(CharSequence name, int i) {
		glUniform1i(getUniformLocation(name), i);
	}

	protected void setUniform2f(CharSequence name, float x, float y) {
		glUniform2f(getUniformLocation(name), x, y);
	}

	protected void setUniform2f(CharSequence name, Vector2f v) {
		setUniform2f(name, v.x(), v.y());
	}

	protected void setUniform3f(CharSequence name, float x, float y, float z) {
		glUniform3f(getUniformLocation(name), x, y, z);
	}

	protected void setUniform3f(CharSequence name, Vector3f v) {
		setUniform3f(name, v.x(), v.y(), v.z());
	}

	protected void setUniform4f(CharSequence name, float x, float y, float z, float w) {
		glUniform4f(getUniformLocation(name), x, y, z, w);
	}

	protected void setUniform4f(CharSequence name, Vector4f v) {
		setUniform4f(name, v.x(), v.y(), v.z(), v.w());
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

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof ShaderProgram)) return false;
		ShaderProgram other = (ShaderProgram) o;
		return this.id() == other.id();
	}
	
}
