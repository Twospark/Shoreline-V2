#version 330 core

uniform sampler2D TextureSampler;

out vec2 v_TexCoord;
out vec2 v_TexelSize;

void main()
{
    vec2 uv = vec2((gl_VertexID << 1) & 2, gl_VertexID & 2);

    gl_Position = vec4(uv * 2.0 - 1.0, 0.0, 1.0);

    v_TexCoord = uv;
    v_TexelSize = 1.0 / vec2(textureSize(TextureSampler, 0));
}