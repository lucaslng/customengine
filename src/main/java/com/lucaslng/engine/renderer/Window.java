package com.lucaslng.engine.renderer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;

import com.lucaslng.engine.EngineSettings;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.ArrayList;

public class Window {

	public final long window;
	private int w, h;
	private boolean focused;
	private float scaleX, scaleY, uiScale;
	private final ArrayList<GLFWFramebufferSizeCallbackI> framebufferSizeCallbacks;

	public Window(EngineSettings engineSettings) {
		framebufferSizeCallbacks = new ArrayList<>();

		if (System.getProperty("os.name").startsWith("Mac")) {
			Configuration.GLFW_LIBRARY_NAME.set("glfw_async");
		}

		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_SAMPLES, 4);

		// create window
		window = glfwCreateWindow(engineSettings.referenceDimension.width, engineSettings.referenceDimension.height,
				engineSettings.title,
				NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}
		System.out.println("GLFW window created successfully");

		glfwSetFramebufferSizeCallback(window, (window, w, h) -> {
			this.w = w;
			this.h = h;
			for (GLFWFramebufferSizeCallbackI callback : framebufferSizeCallbacks) {
				callback.invoke(window, w, h);
			}
			scaleX = (float) w / engineSettings.referenceDimension.width;
			scaleY = (float) h / engineSettings.referenceDimension.height;
			uiScale = Math.min(scaleX, scaleY);
		});

		glfwSetWindowFocusCallback(window, (window, focused) -> {
			this.focused = focused;
		});

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1); // vsync
		glfwShowWindow(window); // makes window visible

		// get initial framebuffer size
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			glfwGetFramebufferSize(window, w, h);
			this.w = w.get(0);
			this.h = h.get(0);
		}
		scaleX = (float) w / engineSettings.referenceDimension.width;
		scaleY = (float) h / engineSettings.referenceDimension.height;
		uiScale = Math.min(scaleX, scaleY);

	}

	public void addFramebufferSizeCallback(GLFWFramebufferSizeCallbackI framebufferSizeCallback) {
		framebufferSizeCallbacks.add(framebufferSizeCallback);
	}

	public void swapBuffers() {
		glfwSwapBuffers(window);
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}

	public int w() {
		return w;
	}

	public int h() {
		return h;
	}

	public boolean focused() {
		return focused;
	}

	public float scaleX() {
		return scaleX;
	}

	public float scaleY() {
		return scaleY;
	}

	public float uiScale() {
		return uiScale;
	}
}
