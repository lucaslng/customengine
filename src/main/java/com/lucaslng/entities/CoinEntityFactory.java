package com.lucaslng.entities;

import org.joml.Vector3f;

import com.lucaslng.engine.components.AABBComponent;
import com.lucaslng.engine.components.CoinComponent;
import com.lucaslng.engine.components.MeshComponent;
import com.lucaslng.engine.components.PositionComponent;
import com.lucaslng.engine.components.RotationComponent;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.renderer.SubMesh;

public class CoinEntityFactory implements AbstractEntityFactory {

	private static final int[] INDICES = {
			0, 1, 2, 2, 3, 0, // front face only
	};

	public static final float HALF_SIZE = 0.7f;
	private static final float HALF_DEPTH = 0.6f;
	private static final float[] VERTICES;
	static {
		float s = HALF_SIZE;
		VERTICES = new float[] {
				-s, -s, 0f, 0f, 0f, 1f, 1f, 1f,
				s, -s, 0f, 0f, 0f, 1f, 0f, 1f,
				s, s, 0f, 0f, 0f, 1f, 0f, 0f,
				-s, s, 0f, 0f, 0f, 1f, 1f, 0f,
		};
	}

	private final Vector3f position;

	public CoinEntityFactory(float x, float y) {
		position = new Vector3f(x, y, 0f);
	}

	@Override
	public Object[] components() {
		return new Object[] {
				new PositionComponent(position),
				new RotationComponent(new Vector3f()),
				new MeshComponent(new SubMesh[] { new SubMesh(VERTICES, INDICES, "CoinYellow") }),
				new AABBComponent(new Vector3f(HALF_SIZE, HALF_SIZE, HALF_DEPTH)),
				new CoinComponent()
		};
	}
}
