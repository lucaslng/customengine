package com.lucaslng.engine.renderer;

import java.util.*;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.lucaslng.engine.components.MeshComponent;

public class ObjParser {

	public static MeshComponent parse(List<String> lines) {

		ArrayList<Vector3f> positions = new ArrayList<>();
		ArrayList<Vector2f> uvs = new ArrayList<>();
		ArrayList<Vector3f> normals = new ArrayList<>();
		HashMap<String, Integer> vertexMap = new HashMap<>();
		ArrayList<Float> vertexData = new ArrayList<>();
		ArrayList<Integer> indices = new ArrayList<>();

		for (String line : lines) {
			String[] tokens = line.split(" ");
			switch (tokens[0]) {
				case "v" -> { // position
					positions
							.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
				}
				case "vt" -> { // texture coordinate
					uvs.add(new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
				}
				case "vn" -> { // vertex normal
					normals
							.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
				}
				case "f" -> { // face
					for (int t = 1; t < tokens.length; t++) {
						Integer index = vertexMap.get(tokens[t]);
						if (index == null) {
							String[] parts = tokens[t].split("/");
							Vector3f p = positions.get(Integer.parseInt(parts[0]) - 1);
							Vector2f uv = uvs.get(Integer.parseInt(parts[1]) - 1);
							Vector3f n = normals.get(Integer.parseInt(parts[2]) - 1);

							vertexData.add(p.x);
							vertexData.add(p.y);
							vertexData.add(p.z);
							vertexData.add(uv.x);
							vertexData.add(uv.y);
							// vertexData.add(n.x);
							// vertexData.add(n.y);
							// vertexData.add(n.z);

							index = (vertexData.size() / 5) - 1;
							vertexMap.put(tokens[t], index);
						}
						indices.add(index);
					}
				}
				default -> {
				}
			}
		}
		float[] verticesArray = new float[vertexData.size()];
		for (int i = 0; i < verticesArray.length; i++)
			verticesArray[i] = vertexData.get(i);

		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++)
			indicesArray[i] = indices.get(i);
		return new MeshComponent(verticesArray, indicesArray);
	}

}
