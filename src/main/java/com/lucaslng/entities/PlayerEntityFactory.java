package com.lucaslng.entities;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.renderer.*;
import com.lucaslng.engine.utils.FileReader;

public class PlayerEntityFactory implements AbstractEntityFactory {

	private final Vector3f position;
	private static final parsedObj parsedObject = ModelParser
			.parseObj(FileReader.readLines("src/main/resources/models/model.obj"));
	private final MeshComponent meshComponent;

	public PlayerEntityFactory(float x, float y, float z) {
		position = new Vector3f(x, y, z);

		SubMesh[] subMeshes = new SubMesh[parsedObject.subParsedObjects().length];
		for (int i=0;i<subMeshes.length;i++) {
			subParsedObj subParsedObject = parsedObject.subParsedObjects()[i];
			subMeshes[i] = new SubMesh(subParsedObject.vertices(), subParsedObject.indices(), subParsedObject.materialName());
		}
		meshComponent = new MeshComponent(subMeshes);
	}

	@Override
	public Object[] components() {
		PositionComponent positionComponent = new PositionComponent(position);
		VelocityComponent velocityComponent = new VelocityComponent(new Vector3f());
		HeadRotationComponent headRotationComponent = new HeadRotationComponent(new Vector3f(0.0f, -90.0f, 0.0f));
		RigidBodyComponent rigidBodyComponent = new RigidBodyComponent(20f, 0.9f, 0.99f, 1f);
		AABBComponent aabbComponent = new AABBComponent(parsedObject.halfExtents());
		RotationComponent rotationComponent = new RotationComponent(new Vector3f());
		GroundedComponent groundedComponent = new GroundedComponent();

		return new Object[] { positionComponent, velocityComponent, headRotationComponent,
				rotationComponent, meshComponent, rigidBodyComponent,
				aabbComponent, groundedComponent };
	}

}
