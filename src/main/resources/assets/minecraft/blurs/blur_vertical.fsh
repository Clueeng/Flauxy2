uniform sampler2D Texture;
uniform float Radius;
uniform vec2 TexelSize;

void main() {
    vec2 uv = gl_FragCoord.xy * TexelSize;
    vec4 color = vec4(0.0);
    float weightSum = 0.0;

    for (float i = -Radius; i <= Radius; i++) {
        float weight = exp(-i * i / (2.0 * Radius * Radius));
        color += texture2D(Texture, uv + vec2(0.0, i * TexelSize.y)) * weight;
        weightSum += weight;
    }

    gl_FragColor = color / weightSum;
}