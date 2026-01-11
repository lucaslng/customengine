package com.lucaslng.entities;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.renderer.ParsedObject;
import com.lucaslng.engine.renderer.ObjParser;
import com.lucaslng.engine.utils.FileReader;

public class PlayerEntityFactory implements AbstractEntityFactory {

	private final Vector3f position;
	private static final ParsedObject parsedObject = ObjParser
			.parse(FileReader.readLines("src/main/resources/models/model.obj"));

	public PlayerEntityFactory(float x, float y, float z) {
		position = new Vector3f(x, y, z);
		System.out.println(parsedObject.halfExtents());
	}

	@Override
	public Object[] components() {
		PositionComponent positionComponent = new PositionComponent(position);
		VelocityComponent velocityComponent = new VelocityComponent(new Vector3f());
		HeadRotationComponent headRotationComponent = new HeadRotationComponent(new Vector3f(0.0f, -90.0f, 0.0f));
		RigidBodyComponent rigidBodyComponent = new RigidBodyComponent(20f, 0.9f, 0.99f, 1f);
		AABBComponent aabbComponent = new AABBComponent(parsedObject.halfExtents());
		RotationComponent rotationComponent = new RotationComponent(new Vector3f());
		MeshComponent meshComponent = new MeshComponent(parsedObject.vertices(), parsedObject.indices());
		MaterialComponent materialComponent = new MaterialComponent(new Vector4f(0.1f, 0.1f, 0.1f, 1f));
		GroundedComponent groundedComponent = new GroundedComponent();

		return new Object[] { positionComponent, velocityComponent, headRotationComponent,
				rotationComponent, meshComponent, materialComponent, rigidBodyComponent,
				aabbComponent, groundedComponent };
	}

}
