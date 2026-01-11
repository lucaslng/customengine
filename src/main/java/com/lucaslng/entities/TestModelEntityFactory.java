package com.lucaslng.entities;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.renderer.ObjParser;
import com.lucaslng.engine.utils.FileReader;

public class TestModelEntityFactory implements AbstractEntityFactory {

	private final Vector3f position;
	private final MeshComponent meshComponent;

	public TestModelEntityFactory(float x, float y, float z, String modelName) {
		position = new Vector3f(x, y, z);
		meshComponent = ObjParser.parse(FileReader.readLines("src/main/resources/models/" + modelName + ".obj"));
	}

	@Override
	public Object[] components() {
		return new Object[] { new PositionComponent(position), new RotationComponent(new Vector3f()),
				meshComponent, new MaterialComponent(new Vector4f(0.1f, 0.1f, 0.1f, 1f)) };
	}
}
