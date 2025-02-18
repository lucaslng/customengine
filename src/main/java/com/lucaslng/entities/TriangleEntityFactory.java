package com.lucaslng.entities;

import com.lucaslng.engine.components.DrawableComponent;
import com.lucaslng.engine.entities.AbstractEntityFactory;

public class TriangleEntityFactory implements AbstractEntityFactory {

	@Override
	public Record[] components() {
		
		return new Record[] { new DrawableComponent(new float[] {
				0.5f, 0.5f, 0.0f,
				1.0f, 1.0f, 0.0f,
				1.0f, 0.5f, 0.0f,
		}, new int[] { 0, 1, 2 }, 0) };
	}

}
