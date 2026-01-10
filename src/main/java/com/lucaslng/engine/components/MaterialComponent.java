package com.lucaslng.engine.components;

import org.joml.Vector4f;

import com.lucaslng.engine.renderer.Texture;

public class MaterialComponent {

	public Texture texture;
	public Vector4f color;

	public MaterialComponent(Texture texture) {
		this.texture = texture;
	}

	public MaterialComponent(Vector4f color) {
		this.color = color;
	}
	
}
