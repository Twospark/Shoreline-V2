package net.shoreline.client.asm.mixins.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemStack;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.render.RenderBlockOutlineEvent;
import net.shoreline.client.impl.event.render.RenderFloatingItemEvent;
import net.shoreline.client.impl.event.render.ShaderEvent;
import net.shoreline.client.impl.event.render.TiltViewEvent;
import net.shoreline.client.impl.modules.render.ShaderModule;
import net.shoreline.client.impl.render.shader.ShaderPass;
import net.shoreline.client.impl.render.shader.ShaderPasses;
import net.shoreline.client.impl.render.shader.util.ShaderNodeCollector;
import net.shoreline.eventbus.EventBus;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer
{
    @Shadow
    protected abstract void renderItemInHand(CameraRenderState cameraState, float deltaPartialTick, Matrix4fc modelViewMatrix);

    @Shadow
    @Final
    public ItemInHandRenderer itemInHandRenderer;
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract void bobHurt(CameraRenderState cameraState, PoseStack poseStack);

    @Shadow
    @Final
    private GameRenderState gameRenderState;

    @Shadow
    protected abstract void bobView(CameraRenderState cameraState, PoseStack poseStack);

    @Unique
    public boolean skip;

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;" +
                            "renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;" +
                            "FLorg/joml/Matrix4fc;)V",
                    shift = At.Shift.AFTER))
    private void renderLevelHook_Hand(DeltaTracker deltaTracker, CallbackInfo info)
    {
        ShaderEvent event = new ShaderEvent();
        EventBus.getInstance().post(event);
    }

    @Inject(method = "shouldRenderBlockOutline", at = @At(value = "HEAD"), cancellable = true)
    private void shouldRenderBlockOutlineHook(CallbackInfoReturnable<Boolean> cir)
    {
        RenderBlockOutlineEvent event = new RenderBlockOutlineEvent();
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            cir.cancel();
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "bobHurt", at = @At(value = "HEAD"), cancellable = true)
    private void bobHurtHook(CameraRenderState cameraState, PoseStack poseStack, CallbackInfo info)
    {
        TiltViewEvent event = new TiltViewEvent();
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            info.cancel();
        }
    }

    @Inject(method = "displayItemActivation", at = @At(value = "HEAD"), cancellable = true)
    private void displayItemActivationHook(ItemStack itemStack, CallbackInfo info)
    {
        RenderFloatingItemEvent event = new RenderFloatingItemEvent(itemStack);
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            info.cancel();
        }
    }

    @Inject(method = "renderItemInHand", at = @At(value = "HEAD"))
    private void renderItemInHandHook(CameraRenderState cameraState,
                                      float deltaPartialTick,
                                      Matrix4fc modelViewMatrix,
                                      CallbackInfo info)
    {
        ShaderModule shaderMod = ShaderModule.INSTANCE;
        if (skip || !shaderMod.isEnabled() || !shaderMod.getHands().getValue())
        {
            return;
        }

        skip = true;
        ShaderPass shader = ShaderPasses.HANDS;
        shader.begin();

        OutlineBufferSource bufferSource = new OutlineBufferSource();
        ShaderNodeCollector collector = new ShaderNodeCollector(bufferSource);

        shader.bind();

        try
        {
            PoseStack poseStack = new PoseStack();
            poseStack.pushPose();

            this.bobHurt(cameraState, poseStack);
            if (this.gameRenderState.optionsRenderState.bobView)
            {
                this.bobView(cameraState, poseStack);
            }

            collector.setColor(shaderMod.getColor().getValue().getRGB());
            this.itemInHandRenderer.renderHandsWithItems(deltaPartialTick, poseStack, collector, this.minecraft.player, this.minecraft.getEntityRenderDispatcher().getPackedLightCoords(this.minecraft.player, deltaPartialTick));

            poseStack.popPose();
            collector.flush();
        }
        finally
        {
            skip = false;
            shader.unbind();
        }
    }
}
