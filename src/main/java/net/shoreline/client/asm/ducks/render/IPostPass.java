package net.shoreline.client.asm.ducks.render;

import com.mojang.blaze3d.buffers.GpuBuffer;

import java.util.Map;

public interface IPostPass
{
    Map<String, GpuBuffer> shoreline$getUniformBuffers();
}
