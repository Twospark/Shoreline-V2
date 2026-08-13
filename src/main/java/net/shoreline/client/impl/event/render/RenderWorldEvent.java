package net.shoreline.client.impl.event.render;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.shoreline.client.impl.render.ClientRenderer;

@RequiredArgsConstructor
@Getter
public class RenderWorldEvent
{
    private final ClientRenderer renderer;
    private final PoseStack poseStack;
    private final CameraRenderState camera;
    private final float partialTicks;
}
