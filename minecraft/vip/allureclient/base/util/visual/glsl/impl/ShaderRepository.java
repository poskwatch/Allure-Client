package vip.allureclient.base.util.visual.glsl.impl;

import vip.allureclient.base.util.visual.glsl.GLShader;

public class ShaderRepository {

    // Class with GL Shaders stored for usage.

    // Vertex shader used for most shaders

    public static final String VERTEX_SHADER =
            "#version 120 \n" +
                    "\n" +
                    "void main() {\n" +
                    "    gl_TexCoord[0] = gl_MultiTexCoord0;\n" +
                    "    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" +
                    "}";

    // Rounded quad shader

    private static final String ROUNDED_QUAD_FRAG_SHADER =
            "#version 120\n" +
                    "uniform float width;\n" +
                    "uniform float height;\n" +
                    "uniform float radius;\n" +
                    "uniform vec4 colour;\n" +
                    "\n" +
                    "float SDRoundedRect(vec2 p, vec2 b, float r) {\n" +
                    "    vec2 q = abs(p) - b + r;\n" +
                    "    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;\n" +
                    "}\n" +
                    "\n" +
                    "void main() {\n" +
                    "    vec2 size = vec2(width, height);\n" +
                    "    vec2 pixel = gl_TexCoord[0].st * size;\n" +
                    "    vec2 centre = 0.5 * size;\n" +
                    "    float b = SDRoundedRect(pixel - centre, centre, radius);\n" +
                    "    float a = 1.0 - smoothstep(0, 1.0, b);\n" +
                    "    gl_FragColor = vec4(colour.rgb, colour.a * a);\n" +
                    "}";

    public static final GLShader ROUNDED_QUAD_SHADER = new GLShader(VERTEX_SHADER, ROUNDED_QUAD_FRAG_SHADER) {
        @Override
        public void setupUniforms() {
            this.setupUniform("width");
            this.setupUniform("height");
            this.setupUniform("colour");
            this.setupUniform("radius");
        }
    };

}
