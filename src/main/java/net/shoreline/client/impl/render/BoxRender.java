package net.shoreline.client.impl.render;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public enum BoxRender
{
    FILL
    {
        @Override
        public void render(ClientRenderer renderer, AABB boundingBox, int color, float transparency)
        {
            renderer.renderBoundingBox(boundingBox, ColorUtil.withTransparency(color, transparency).getRGB());
            renderer.renderBox(boundingBox, ColorUtil.withTransparency(color, 0.3f * transparency).getRGB());
        }
    },
    OUTLINE
    {
        @Override
        public void render(ClientRenderer renderer, AABB boundingBox, int color, float transparency)
        {
            renderer.renderBoundingBox(boundingBox, ColorUtil.withTransparency(color, transparency).getRGB());
        }
    };

    public void render(ClientRenderer renderer, BlockPos blockPos, int color)
    {
        render(renderer, new AABB(blockPos), color);
    }

    public void render(ClientRenderer renderer, BlockPos blockPos, int color, float transparency)
    {
        render(renderer, new AABB(blockPos), color, transparency);
    }

    public void render(ClientRenderer renderer, AABB boundingBox, int color)
    {
        render(renderer, boundingBox, color, 1.0f);
    }

    public abstract void render(ClientRenderer renderer, AABB boundingBox, int color, float transparency);
}
