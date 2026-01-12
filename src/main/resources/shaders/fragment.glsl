#version 410 core

in vec3 FragPos;
in vec3 Normal;
in vec2 vUV;
uniform sampler2D uTexture;
uniform vec4 uColor;
uniform bool uUseTexture;
uniform vec3 lightPos;
out vec4 FragColor;

void main() {
    vec4 objectColor;
    if (uUseTexture) {
        objectColor = texture(uTexture, vUV);
    } else {
        objectColor = uColor;
    }

    // lighting
    vec3 lightColor = vec3(1.0);
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    vec3 ambient = 1.0 * lightColor; // ambient strength

    vec3 result = (ambient + diffuse) * objectColor.xyz;
    FragColor = vec4(result, objectColor.w);
}