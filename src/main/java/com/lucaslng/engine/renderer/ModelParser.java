package com.lucaslng.engine.renderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
public class ModelParser {

	private static final int STRIDE = 8;

	public static ParsedObj parseObj(String fileName) {

		ArrayList<Vector3f> positions = new ArrayList<>();
		ArrayList<Vector2f> uvs = new ArrayList<>();
		ArrayList<Vector3f> normals = new ArrayList<>();
		HashMap<String, HashMap<String, Integer>> vertexMap = new HashMap<>();
		HashMap<String, ArrayList<Float>> vertexData = new HashMap<>();
		HashMap<String, ArrayList<Integer>> indices = new HashMap<>();
		String material = "Fallback";

		
		Scanner in;
		try {
			in = new Scanner(new File("assets/models/" + fileName + ".obj"));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to read model file.");
		}
		while (in.hasNextLine()) {
			String line = in.nextLine();
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
				case "usemtl" -> {
					material = tokens[1];
					vertexMap.putIfAbsent(material, new HashMap<String, Integer>());
					vertexData.putIfAbsent(material, new ArrayList<Float>());
					indices.putIfAbsent(material, new ArrayList<Integer>());
				}
				case "f" -> { // face
					for (int t = 1; t < tokens.length; t++) {
						Integer index = vertexMap.get(material).get(tokens[t]);
						if (index == null) {
							String[] parts = tokens[t].split("/");
							Vector3f p = positions.get(Integer.parseInt(parts[0]) - 1);
							Vector2f uv = uvs.get(Integer.parseInt(parts[1]) - 1);
							Vector3f n = normals.get(Integer.parseInt(parts[2]) - 1);

							vertexData.get(material).add(p.x);
							vertexData.get(material).add(p.y);
							vertexData.get(material).add(p.z);
							vertexData.get(material).add(n.x);
							vertexData.get(material).add(n.y);
							vertexData.get(material).add(n.z);
							vertexData.get(material).add(uv.x);
							vertexData.get(material).add(uv.y);

							index = (vertexData.get(material).size() / STRIDE) - 1; // change to / 8 after adding back normals
							vertexMap.get(material).put(tokens[t], index);
						}
						indices.get(material).add(index);
					}
				}
				default -> {
				}
			}
		}
		in.close();

		Vector3f min = new Vector3f(Float.POSITIVE_INFINITY);
		Vector3f max = new Vector3f(Float.NEGATIVE_INFINITY);

		SubParsedObj[] subParsedObjects = new SubParsedObj[vertexData.size()];

		int s = 0;
		for (String m : vertexData.keySet()) {
			float[] verticesArray = new float[vertexData.get(m).size()];
			for (int i = 0; i < verticesArray.length; i++)
				verticesArray[i] = vertexData.get(m).get(i);

			int[] indicesArray = new int[indices.get(m).size()];
			for (int i = 0; i < indicesArray.length; i++)
				indicesArray[i] = indices.get(m).get(i);

			subParsedObjects[s] = new SubParsedObj(verticesArray, indicesArray, m);

			for (int i = 0; i < verticesArray.length; i += STRIDE) {
				min.min(new Vector3f(verticesArray[i], verticesArray[i + 1], verticesArray[i + 2]));
				max.max(new Vector3f(verticesArray[i], verticesArray[i + 1], verticesArray[i + 2]));
			}
			s++;
		}

		// calculate model center and half extents based on model
		Vector3f center = new Vector3f();
		min.add(max, center);
		center.mul(0.5f);

		Vector3f halfExtents = new Vector3f();
		max.sub(min, halfExtents);
		halfExtents.mul(0.5f);

		// adjust vertices based on center
		for (SubParsedObj subParsedObject : subParsedObjects) {
			for (int i = 0; i < subParsedObject.vertices().length; i += STRIDE) {
				subParsedObject.vertices()[i] -= center.x();
				subParsedObject.vertices()[i + 1] -= center.y();
				subParsedObject.vertices()[i + 2] -= center.z();
			}
		}
		return new ParsedObj(subParsedObjects, halfExtents);

	}

	public static HashMap<String, Material> parseMtl(String fileName) {
		HashMap<String, Material> materials = new HashMap<>();
		String material = "";
		Scanner in;
		try {
			in = new Scanner(new File("assets/materials/" + fileName + ".mtl"));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to read material file.");
		}
		while (in.hasNextLine()) {
			String line = in.nextLine();
			String[] tokens = line.split(" ");
			switch (tokens[0]) {
				case "newmtl" -> {
					material = tokens[1];
				}
				case "Kd" -> {
					materials.put(material, new Material(
							new Vector4f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]), 1f)));
				}
				default -> {
				}
			}
		}
		in.close();
		return materials;
	}
}