package com.lucaslng.engine.renderer;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class ObjParser {

	public static void parse(List<String> lines) { // should not return void, just temporary

		List<Vector3f> vertices = new ArrayList<>();
		List<Vector2f> textures = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		// List<Face> faces = new ArrayList<>();

		for (String line : lines) {
			String[] tokens = line.split("\\s+");
			switch (tokens[0]) {
				case "v" -> { // vertex
					vertices.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
				}
				case "vt" -> { // texture coordinate
					textures.add(new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
				}
				case "vn" -> { // vertex normal
					normals.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
				}
				case "f" -> { // face

				}
				default -> {
					break;
				}
			}
		}
	}

	private static void parseFace() {
		
	}

}
