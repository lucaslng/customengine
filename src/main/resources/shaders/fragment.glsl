#version 410 core

in vec2 vUV;
uniform sampler2D uTexture;
uniform vec4 uColor;
uniform bool uUseTexture;
out vec4 fragColor;

void main() {
    if (uUseTexture) {
        fragColor = texture(uTexture, vUV);
    } else {
        fragColor = uColor;
    }
}
