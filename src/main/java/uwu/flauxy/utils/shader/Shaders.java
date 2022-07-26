package uwu.flauxy.utils.shader;

public final class Shaders {

    private Shaders() {
    }

    public static final String BACKGROUND_FRAG_SHADER =
        "#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n" +
                "\n" +
                "// glslsandbox uniforms\n" +
                "uniform float time;\n" +
                "uniform vec2 resolution;\n" +
                "\n" +
                "// shadertoy emulation\n" +
                "#define iTime time\n" +
                "#define iResolution vec3(resolution,1.)\n" +
                "\n" +
                "// --------[ Original ShaderToy begins here ]---------- //\n" +
                "// 240 chars by Xor (with aspect ratio fix)\n" +
                "void mainImage(out vec4 O, vec2 I)\n" +
                "{\n" +
                "    vec3 p=iResolution,d = -.5*vec3(I+I-p.xy,p)/p.x,c = d-d, i=c;\n" +
                "    for(int x=0;x<25;++x) {\n" +
                "        if (i.x>=1.) break;\n" +
                "        p = c,\n" +
                "        p.z -= iTime+(i.x+=.01),\n" +
                "        p.xy *= mat2(sin((p.z*=.1)+vec4(0,11,33,0)));\n" +
                "        c += length(sin(p.yx)+cos(p.xz+iTime))*d;\n" +
                "    }\n" +
                "    O = vec4(1,0,2.5,9)/length(c);\n" +
                "}\n" +
                "\n" +
                "// ORIGINAL: 258 chars\n" +
                "/*void mainImage(out vec4 O, in vec2 I) {\n" +
                "    vec3 d = .5-vec3(I,1)/iResolution, p, c;\n" +
                "    for(float i=0.;i<99.;i++) {\n" +
                "        p = c;\n" +
                "        p.z -= iTime+i*.01;\n" +
                "        p.z *= .1;\n" +
                "        p.xy *= mat2(sin(p.z),-cos(p.z),cos(p.z),sin(p.z));\n" +
                "        c += length(sin(p.yx)+cos(p.xz+iTime))*d;\n" +
                "    }\n" +
                "    O.rgb = vec3(5./length(c))*vec3(2.,.0,.5);\n" +
                "}*/\n" +
                "// --------[ Original ShaderToy ends here ]---------- //\n" +
                "\n" +
                "void main(void)\n" +
                "{\n" +
                "    mainImage(gl_FragColor, gl_FragCoord.xy);\n" +
                "    gl_FragColor.a = 1.;\n" +
                "}";

    public static final String BACKGROUND_CZ_FRAG_SHADER =
        "//#  Greetz from DK.... thank you ;\n" +
        "#ifdef GL_ES\n" +
        "precision mediump float;\n" +
        "#endif\n" +
        "\n" +
        "#extension GL_OES_standard_derivatives : enable\n" +
        "\n" +
        "uniform float time;\n" +
        "uniform vec2 mouse;\n" +
        "uniform vec2 resolution;\n" +
        "\n" +
        "const vec3 red = vec3(0.874, 0.049, 0.077);\n" +
        "const vec3 blue = vec3(0.040,0.332,0.6291);\n" +
        "const vec3 white = vec3(1, 1, 1);\n" +
        "vec3 col1;\n" +
        "const float PI = 3.1415926535;\n" +
        "void main( void ) {\n" +
        "\n" +
        "\tvec2 p = 2.0*( gl_FragCoord.xy / resolution.xy ) -1.0; \n" +
        "\tp.x *= resolution.x/resolution.y; \n" +
        "\t\n" +
        "\tp.x += sin(p.y+time*2.0)*.05;\n" +
        "\tp.y += sin(p.x*2.0-time*2.0)*.2;\n" +
        "\t\n" +
        "\t\n" +
        "\t\n" +
        "\tvec2 uv = (gl_FragCoord.xy*2.-resolution.xy)/resolution.y+1.1;\n" +
        "\t\n" +
        "\tfloat w = sin((uv.x + uv.y - time * .5 + sin(1.5 * uv.x + 4.5 * uv.y) * PI * .3) * PI * .6); // fake waviness factor\n" +
        " \n" +
        "\t\tcol1 = vec3(0.80,0.80,0.0);\n" +
        "\t\tcol1 = mix(col1, red, smoothstep(.01, .025, uv.y+w*.02));\n" +
        "\t\t \n" +
        "\t\tcol1 += w * .2;\n" +
        "\t\n" +
        "\tvec3 col = col1; \n" +
        "\t\n" +
        "\tif (p.y > 0.0) col = white+w*0.3;\n" +
        "\t\n" +
        "\tfloat tw = (1.0 - abs(p.y * resolution.x/resolution.y)) * 0.5;\n" +
        "\t\n" +
        "\tif (p.x < tw) col = blue+w*0.3;\n" +
        "\t\n" +
        "\t//if(abs(p.x) > 1.60) col = col1;\n" +
        "\t//if(abs(p.y) > 1.0) col = col1;\n" +
        "\t\n" +
        "\tgl_FragColor = vec4(col , 1.0); \n" +
        "}";

    public static final String VERTEX_SHADER =
        "#version 120 \n" +
            "\n" +
            "void main() {\n" +
            "    gl_Position = gl_Vertex;\n" +
            "}";
}
