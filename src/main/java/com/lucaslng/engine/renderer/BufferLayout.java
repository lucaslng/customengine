package com.lucaslng.engine.renderer;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_SHORT;

class BufferLayout {

	private final ArrayList<VertexBufferElement> elements;
	private int stride;

	public BufferLayout() {
		elements = new ArrayList<>();
	}

	public void add(int type, int count, boolean normalized) {
		assert type == GL_FLOAT || type == GL_UNSIGNED_INT || type == GL_UNSIGNED_BYTE || type == GL_UNSIGNED_SHORT;
		VertexBufferElement element = new VertexBufferElement(count, type, normalized);
		stride += element.size() * element.count();
		elements.add(element);
	}

	public ArrayList<VertexBufferElement> elements() {
		return elements;
	}

	public int stride() {
		return stride;
	}
	
}
