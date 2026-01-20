package com.lucaslng.entities;

import org.joml.Vector3f;

import com.lucaslng.engine.components.*;
import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.renderer.SubMesh;

public class ButtonEntityFactory implements AbstractEntityFactory {

	private static final int[] indices = new int[] {
			0, 1, 2, 2, 3, 0, // back
			4, 5, 6, 6, 7, 4, // right
			8, 9, 10, 10, 11, 8, // front
			12, 13, 14, 14, 15, 12, // left
			16, 17, 18, 18, 19, 16, // top
			20, 21, 22, 22, 23, 20 // bottom
	};
	
	private final float[] vertices;
	private final Vector3f position;
	private final float hx, hy, hz;

	public ButtonEntityFactory(float x, float y) {
		// Position the button slightly below the platform surface
		position = new Vector3f(x, y - 0.5f, -1f);
		
		// Make it a flat button shape
		hx = 1.5f; // width
		hy = 0.25f; // height (thin)
		hz = 1.5f; // depth
		
		float tx = hx / 2f;
		float ty = hy / 2f;
		float tz = hz / 2f;
		
		vertices = new float[] {
				// BACK (-Z)
				-hx, -hy, -hz, 0, 0, -1, 0, 0,
				hx, -hy, -hz, 0, 0, -1, tx, 0,
				hx, hy, -hz, 0, 0, -1, tx, ty,
				-hx, hy, -hz, 0, 0, -1, 0, ty,

				// RIGHT (+X)
				hx, -hy, -hz, 1, 0, 0, 0, 0,
				hx, -hy, hz, 1, 0, 0, 0, tz,
				hx, hy, hz, 1, 0, 0, ty, tz,
				hx, hy, -hz, 1, 0, 0, ty, 0,

				// FRONT (+Z)
				hx, -hy, hz, 0, 0, 1, tx, 0,
				-hx, -hy, hz, 0, 0, 1, 0, 0,
				-hx, hy, hz, 0, 0, 1, 0, ty,
				hx, hy, hz, 0, 0, 1, tx, ty,

				// LEFT (-X)
				-hx, -hy, hz, -1, 0, 0, 0, tz,
				-hx, -hy, -hz, -1, 0, 0, 0, 0,
				-hx, hy, -hz, -1, 0, 0, ty, 0,
				-hx, hy, hz, -1, 0, 0, ty, tz,

				// TOP (+Y)
				-hx, hy, -hz, 0, 1, 0, 0, 0,
				hx, hy, -hz, 0, 1, 0, tx, 0,
				hx, hy, hz, 0, 1, 0, tx, tz,
				-hx, hy, hz, 0, 1, 0, 0, tz,

				// BOTTOM (-Y)
				-hx, -hy, hz, 0, -1, 0, 0, tz,
				hx, -hy, hz, 0, -1, 0, tx, tz,
				hx, -hy, -hz, 0, -1, 0, tx, 0,
				-hx, -hy, -hz, 0, -1, 0, 0, 0
		};
	}

	@Override
	public Object[] components() {
		return new Object[] { 
			new PositionComponent(position), 
			new RotationComponent(new Vector3f()),
			new MeshComponent(new SubMesh[] { new SubMesh(vertices, indices, "ButtonYellow") }),
			new RigidBodyComponent(0f, 1f, 1f, 1f), 
			new AABBComponent(new Vector3f(hx, hy, hz)),
			new ButtonComponent()
		};
	}
}