package net.shoreline.client.asm.mixins.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.shoreline.client.impl.event.render.OverlayEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class MixinScreenEffectRenderer
{
    @Inject(method = "renderFire", at = @At(value = "HEAD"), cancellable = true)
    private static void renderFireHook(PoseStack poseStack,
                                       MultiBufferSource bufferSource,
                                       TextureAtlasSprite sprite,
                                       CallbackInfo info)
    {
        OverlayEvent.Fire event = new OverlayEvent.Fire();
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            info.cancel();
        }
    }

    @Inject(method = "renderWater", at = @At(value = "HEAD"), cancellable = true)
    private static void renderWaterHook(Minecraft minecraft,
                                        PoseStack poseStack,
                                        MultiBufferSource bufferSource,
                                        CallbackInfo info)
    {
        OverlayEvent.Water event = new OverlayEvent.Water();
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            info.cancel();
        }
    }

    @Inject(method = "renderTex", at = @At(value = "HEAD"), cancellable = true)
    private static void renderTexHook(TextureAtlasSprite sprite,
                                      PoseStack poseStack,
                                      MultiBufferSource bufferSource,
                                      CallbackInfo info)
    {
        OverlayEvent.Blocks event = new OverlayEvent.Blocks();
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            info.cancel();
        }
    }

    @Inject(method = "displayItemActivation", at = @At(value = "HEAD"), cancellable = true)
    private void displayItemActivationHook(ItemStack itemStack,
                                           RandomSource random,
                                           CallbackInfo info)
    {

    }
}
