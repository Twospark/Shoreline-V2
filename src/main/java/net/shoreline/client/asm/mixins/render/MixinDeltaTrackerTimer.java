package net.shoreline.client.asm.mixins.render;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.client.DeltaTracker;
import net.shoreline.client.impl.event.render.RenderTickCounterEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DeltaTracker.Timer.class)
public class MixinDeltaTrackerTimer
{
    @Shadow
    private float deltaTicks;

    @Shadow
    private long lastMs;

    @Shadow
    @Final
    private FloatUnaryOperator targetMsptProvider;

    @Shadow
    @Final
    private float msPerTick;

    @Shadow
    private float deltaTickResidual;

    @Inject(method = "advanceGameTime", at = @At(value = "HEAD"), cancellable = true)
    private void advanceGameTimeHook(long currentMs, CallbackInfoReturnable<Integer> cir)
    {
        RenderTickCounterEvent event = new RenderTickCounterEvent();
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            deltaTicks = ((currentMs - this.lastMs) / this.targetMsptProvider.apply(this.msPerTick)) * event.getTicks();
            this.lastMs = currentMs;
            this.deltaTickResidual += this.deltaTicks;
            int ticks = (int) this.deltaTickResidual;
            this.deltaTickResidual -= (float)ticks;
            cir.setReturnValue(ticks);
        }
    }
}
