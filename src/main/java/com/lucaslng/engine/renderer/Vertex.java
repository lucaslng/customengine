package com.lucaslng.engine.renderer;

import org.joml.Vector2f;
import org.joml.Vector3f;

public record Vertex(Vector3f position, Vector2f normal, Vector2f texCoords) {}