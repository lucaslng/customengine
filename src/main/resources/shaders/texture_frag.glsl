#version 410 core

in vec2 TexCoord;
out vec4 FragColor;
uniform sampler2D u_Texture;


void main() {
    // FragColor = vec4(vertexColor, 1.0);
		// FragColor = mix(texture(u_Texture, TexCoord), vec4(1.0, 0.0, 0.0, 1.0), 0.2);
		FragColor = texture(u_Texture, TexCoord);
}
