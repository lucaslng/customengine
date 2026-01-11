package com.lucaslng.engine.renderer;

import org.joml.Vector4f;

public class Material {

	public Texture texture;
	public Vector4f color;
	public final boolean textured;

	public Material(Texture texture) {
		this.texture = texture;
		textured = true;
	}

	public Material(Vector4f color) {
		this.color = color;
		textured = false;
	}
	
}
