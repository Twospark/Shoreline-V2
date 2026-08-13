package net.shoreline.client.impl.render.shader;

public class ShaderPasses
{
    public static ShaderPass ENTITIES;
    public static ShaderPass HANDS;

    public static void init()
    {
        ENTITIES = new ShaderPass("entities");
        HANDS    = new ShaderPass("hands");
    }
}
