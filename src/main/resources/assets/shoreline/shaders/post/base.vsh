#version 330 core

layout (std140) uniform BaseConfig
{
    vec2 u_Resolution;
};

out vec2 v_TexCoord;
out vec2 v_TexelSize;

void main()
{
    vec2 uv = vec2((gl_VertexID << 1) & 2, gl_VertexID & 2);
    gl_Position = vec4(uv * vec2(2.0) + vec2(-1.0), 0.0, 1.0);

    v_TexCoord = uv;
    v_TexelSize = 1.0 / u_Resolution;
}