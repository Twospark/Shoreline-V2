#version 330 core

in vec2 v_TexCoord;
in vec2 v_TexelSize;

uniform sampler2D TextureSampler;

layout(std140) uniform OutlineConfig
{
    float u_Width;
    float u_FillOpacity;
    float u_OutlineOpacity;
};

out vec4 fragColor;

void main()
{
    vec4 center = texture(TextureSampler, v_TexCoord);
    if (center.a != 0.0)
    {
        fragColor = vec4(center.rgb, center.a * u_FillOpacity);
        return;
    }

    int iWidth = int(ceil(u_Width));
    for (int x = -iWidth; x <= iWidth; x++)
    {
        for (int y = -iWidth; y <= iWidth; y++)
        {
            if (x == 0 && y == 0)
            {
                continue;
            }

            if (length(vec2(float(x), float(y))) > u_Width)
            {
                continue;
            }

            vec4 offset = texture(TextureSampler, v_TexCoord + v_TexelSize * vec2(x, y));
            if (offset.a != 0 && offset.a > center.a)
            {
                center = offset;
                break;
            }
        }
    }

    if (center.a > 0.0)
    {
        fragColor = vec4(center.rgb, u_OutlineOpacity);
        return;
    }

    discard;
}