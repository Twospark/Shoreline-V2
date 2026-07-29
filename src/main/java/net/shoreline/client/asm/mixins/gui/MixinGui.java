package net.shoreline.client.asm.mixins.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.impl.event.render.HudOverlayEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGui
{
    @Inject(method = "extractRenderState", at = @At(value = "RETURN"))
    private void extractRenderStateHook(GuiGraphicsExtractor graphics,
                                        DeltaTracker deltaTracker,
                                        CallbackInfo info)
    {
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(true);
        HudOverlayEvent.Post event = new HudOverlayEvent.Post(graphics, partialTicks);
        EventBus.getInstance().post(event);
    }
}
