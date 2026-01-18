#version 410 core

in vec2 vUV;
uniform sampler2D uTexture;
uniform vec4 uColor;
uniform bool uUseTexture;
out vec4 FragColor;

void main() {
  FragColor = vec4(uColor.rgb, texture(uTexture, vUV).r * uColor.a);
}