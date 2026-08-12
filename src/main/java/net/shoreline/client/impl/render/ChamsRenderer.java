package net.shoreline.client.impl.render;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public enum ChamsRenderer
{
    NONE,
    CHAMS,
    WIREFRAME,
    BOTH;

    private static ChamsRenderer chams;
    private static ClientRenderer renderer;
    private static Vec3 position;
    private static int color;
    private static float factor;

    public static void render(ClientRenderer renderer, ChamsRenderer chams, Entity entity, float partialTicks, int color, float transparency)
    {

    }
}
