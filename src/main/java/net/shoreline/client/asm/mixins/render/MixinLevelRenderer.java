package net.shoreline.client.asm.mixins.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.impl.render.ClientRenderer;
import net.shoreline.eventbus.EventBus;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "renderLevel", at = @At(value = "RETURN"))
    private void renderLevelHook(GraphicsResourceAllocator resourceAllocator,
                                 DeltaTracker deltaTracker,
                                 boolean renderOutline,
                                 CameraRenderState cameraState,
                                 Matrix4fc modelViewMatrix,
                                 GpuBufferSlice terrainFog,
                                 Vector4f fogColor,
                                 boolean shouldRenderSky,
                                 ChunkSectionsToRender chunkSectionsToRender,
                                 CallbackInfo info)
    {
        PoseStack matrices = new PoseStack();
        matrices.pushPose();
        matrices.mulPose(Axis.XP.rotationDegrees(minecraft.gameRenderer.getMainCamera().xRot()));
        matrices.mulPose(Axis.YP.rotationDegrees(minecraft.gameRenderer.getMainCamera().yRot() + 180f));

        ClientRenderer renderer = new ClientRenderer(minecraft.renderBuffers().bufferSource(), matrices.last());
        RenderWorldEvent event = new RenderWorldEvent(renderer, matrices, cameraState, deltaTracker.getGameTimeDeltaPartialTick(false));
        EventBus.getInstance().post(event);
        renderer.flush();

        matrices.popPose();
    }
}
