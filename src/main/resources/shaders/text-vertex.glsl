#version 410 core

layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aUV;

uniform mat4 ortho;
uniform mat4 model;

out vec2 vUV;

void main() {
  gl_Position = ortho * model * vec4(aPos, 0.0, 1.0);
	vUV = aUV / vec2(2000, 200);
}