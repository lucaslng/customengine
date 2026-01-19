package com.lucaslng.engine.renderer;

import org.lwjgl.glfw.*;
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
	private final ArrayList<GLFWMouseButtonCallbackI> mouseButtonCallbacks;
	private final ArrayList<GLFWCursorPosCallbackI> cursorPosCallbacks;
	private final ArrayList<GLFWKeyCallbackI> keyCallbacks;

	public Window(EngineSettings engineSettings) {
		framebufferSizeCallbacks = new ArrayList<>();
		mouseButtonCallbacks = new ArrayList<>();
		cursorPosCallbacks = new ArrayList<>();
		keyCallbacks = new ArrayList<>();

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
		glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);

		// windowed fullscreen
		long primaryMonitor = glfwGetPrimaryMonitor();
		GLFWVidMode videoMode = primaryMonitor != NULL ? glfwGetVideoMode(primaryMonitor) : null;
		int windowWidth = engineSettings.referenceDimension.width;
		int windowHeight = engineSettings.referenceDimension.height;
		if (videoMode != null) {
			windowWidth = videoMode.width();
			windowHeight = videoMode.height();
		}

		// create window
		window = glfwCreateWindow(windowWidth, windowHeight, engineSettings.title, NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}
		System.out.println("GLFW window created successfully");
		if (videoMode != null && glfwGetPlatform() != GLFW_PLATFORM_WAYLAND) {
			try (MemoryStack stack = stackPush()) {
				IntBuffer monitorX = stack.mallocInt(1);
				IntBuffer monitorY = stack.mallocInt(1);
				glfwGetMonitorPos(primaryMonitor, monitorX, monitorY);
				glfwSetWindowPos(window, monitorX.get(0), monitorY.get(0));
			}
		}

		glfwSetFramebufferSizeCallback(window, (window, w, h) -> {
			this.w = w;
			this.h = h;
			for (GLFWFramebufferSizeCallbackI callback : framebufferSizeCallbacks)
				callback.invoke(window, w, h);
			scaleX = (float) w / engineSettings.referenceDimension.width;
			scaleY = (float) h / engineSettings.referenceDimension.height;
			uiScale = Math.min(scaleX, scaleY);
		});

		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			for (GLFWMouseButtonCallbackI callback : mouseButtonCallbacks)
				callback.invoke(window, button, action, mods);
		});

		glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
			for (GLFWCursorPosCallbackI callback : cursorPosCallbacks)
				callback.invoke(window, xpos, ypos);
		});

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			for (GLFWKeyCallbackI callback : keyCallbacks)
				callback.invoke(window, key, scancode, action, mods);
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

	public void addMouseButtonCallback(GLFWMouseButtonCallbackI mouseButtonCallback) {
		mouseButtonCallbacks.add(mouseButtonCallback);
	}

	public void addCursorPosCallback(GLFWCursorPosCallbackI cursorPosCallback) {
		cursorPosCallbacks.add(cursorPosCallback);
	}

	public void addKeyCallback(GLFWKeyCallbackI keyCallback) {
		keyCallbacks.add(keyCallback);
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
