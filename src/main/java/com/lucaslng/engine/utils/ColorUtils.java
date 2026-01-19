package com.lucaslng.engine.utils;

import java.awt.Color;

import org.joml.Vector3f;

public class ColorUtils {
	public static Vector3f color2Vec(Color color) {
		return new Vector3f(
				color.getRed() / 255f,
				color.getGreen() / 255f,
				color.getBlue() / 255f);
	}
}
