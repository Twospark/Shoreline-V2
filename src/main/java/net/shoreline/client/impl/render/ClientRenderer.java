package net.shoreline.client.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClientRenderer
{
    private final MultiBufferSource.BufferSource bufferSource;
    private final PoseStack.Pose pose;
    private final double cameraX, cameraY, cameraZ;
    private final List<RenderType> types;

    public ClientRenderer(MultiBufferSource.BufferSource bufferSource, PoseStack.Pose pose)
    {
        this.bufferSource = bufferSource;
        this.pose = pose;

        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().position();
        cameraX = camera.x;
        cameraY = camera.y;
        cameraZ = camera.z;
        types = new ArrayList<>();
    }

    public void flush()
    {
        for (RenderType type : types)
        {
            bufferSource.endBatch(type);
        }
    }

    public void render(RenderType type, Consumer<VertexConsumer> consumer)
    {
        VertexConsumer buffer = getVertexBuffer(type);
        consumer.accept(buffer);
    }

    public void renderBox(AABB boundingBox, int color)
    {
        VertexConsumer buffer = getVertexBuffer(ClientRenderTypes.QUADS);
        float minX = (float) (boundingBox.minX - cameraX);
        float minY = (float) (boundingBox.minY - cameraY);
        float minZ = (float) (boundingBox.minZ - cameraZ);
        float maxX = (float) (boundingBox.maxX - cameraX);
        float maxY = (float) (boundingBox.maxY - cameraY);
        float maxZ = (float) (boundingBox.maxZ - cameraZ);
        
        buffer.addVertex(pose, minX, minY, minZ).setColor(color);
        buffer.addVertex(pose, maxX, minY, minZ).setColor(color);
        buffer.addVertex(pose, maxX, minY, maxZ).setColor(color);
        buffer.addVertex(pose, minX, minY, maxZ).setColor(color);

        buffer.addVertex(pose, minX, maxY, minZ).setColor(color);
        buffer.addVertex(pose, minX, maxY, maxZ).setColor(color);
        buffer.addVertex(pose, maxX, maxY, maxZ).setColor(color);
        buffer.addVertex(pose, maxX, maxY, minZ).setColor(color);

        buffer.addVertex(pose, minX, minY, minZ).setColor(color);
        buffer.addVertex(pose, minX, maxY, minZ).setColor(color);
        buffer.addVertex(pose, maxX, maxY, minZ).setColor(color);
        buffer.addVertex(pose, maxX, minY, minZ).setColor(color);

        buffer.addVertex(pose, maxX, minY, minZ).setColor(color);
        buffer.addVertex(pose, maxX, maxY, minZ).setColor(color);
        buffer.addVertex(pose, maxX, maxY, maxZ).setColor(color);
        buffer.addVertex(pose, maxX, minY, maxZ).setColor(color);

        buffer.addVertex(pose, minX, minY, maxZ).setColor(color);
        buffer.addVertex(pose, maxX, minY, maxZ).setColor(color);
        buffer.addVertex(pose, maxX, maxY, maxZ).setColor(color);
        buffer.addVertex(pose, minX, maxY, maxZ).setColor(color);

        buffer.addVertex(pose, minX, minY, minZ).setColor(color);
        buffer.addVertex(pose, minX, minY, maxZ).setColor(color);
        buffer.addVertex(pose, minX, maxY, maxZ).setColor(color);
        buffer.addVertex(pose, minX, maxY, minZ).setColor(color);
    }

    public void renderBoundingBox(AABB boundingBox, int color)
    {
        VertexConsumer buffer = getVertexBuffer(ClientRenderTypes.DEBUG_LINES);
        float minX = (float) (boundingBox.minX - cameraX);
        float minY = (float) (boundingBox.minY - cameraY);
        float minZ = (float) (boundingBox.minZ - cameraZ);
        float maxX = (float) (boundingBox.maxX - cameraX);
        float maxY = (float) (boundingBox.maxY - cameraY);
        float maxZ = (float) (boundingBox.maxZ - cameraZ);

        buffer.addVertex(pose, minX, minY, minZ).setColor(color);
        buffer.addVertex(pose, minX, minY, maxZ).setColor(color);
        buffer.addVertex(pose, minX, minY, maxZ).setColor(color);
        buffer.addVertex(pose, maxX, minY, maxZ).setColor(color);

        buffer.addVertex(pose, maxX, minY, maxZ).setColor(color);
        buffer.addVertex(pose, maxX, minY, minZ).setColor(color);
        buffer.addVertex(pose, maxX, minY, minZ).setColor(color);
        buffer.addVertex(pose, minX, minY, minZ).setColor(color);

        buffer.addVertex(pose, minX, maxY, minZ).setColor(color);
        buffer.addVertex(pose, minX, maxY, maxZ).setColor(color);
        buffer.addVertex(pose, minX, maxY, maxZ).setColor(color);
        buffer.addVertex(pose, maxX, maxY, maxZ).setColor(color);

        buffer.addVertex(pose, maxX, maxY, maxZ).setColor(color);
        buffer.addVertex(pose, maxX, maxY, minZ).setColor(color);
        buffer.addVertex(pose, maxX, maxY, minZ).setColor(color);
        buffer.addVertex(pose, minX, maxY, minZ).setColor(color);

        buffer.addVertex(pose, minX, minY, minZ).setColor(color);
        buffer.addVertex(pose, minX, maxY, minZ).setColor(color);
        buffer.addVertex(pose, maxX, minY, minZ).setColor(color);
        buffer.addVertex(pose, maxX, maxY, minZ).setColor(color);

        buffer.addVertex(pose, maxX, minY, maxZ).setColor(color);
        buffer.addVertex(pose, maxX, maxY, maxZ).setColor(color);
        buffer.addVertex(pose, minX, minY, maxZ).setColor(color);
        buffer.addVertex(pose, minX, maxY, maxZ).setColor(color);
    }

    public VertexConsumer getVertexBuffer(RenderType type)
    {
        types.add(type);
        return bufferSource.getBuffer(type);
    }
}