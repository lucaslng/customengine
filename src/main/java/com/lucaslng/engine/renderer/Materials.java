package com.lucaslng.engine.renderer;

import java.util.HashMap;

import org.joml.Vector4f;

import com.lucaslng.engine.utils.FileReader;

public class Materials {
	public static HashMap<String, Material> createMaterials() {
		HashMap<String, Material> materials = new HashMap<>();
		materials.put("Cat", new Material(new Texture(FileReader.readImage("freakycat.png"))));
		materials.put("Black", new Material(new Vector4f(0f, 0f, 0f, 1f)));
		materials.put("Platform", new Material(new Vector4f(0.9f, 0.9f, 0.9f, 1f)));
		materials.put("Lava", new Material(new Vector4f(1f, 0f, 0f, 1f)));
		materials.putAll(ModelParser.parseMtl("model"));
		return materials;
	}
}
