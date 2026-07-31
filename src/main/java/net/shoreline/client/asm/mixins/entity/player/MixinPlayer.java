package net.shoreline.client.asm.mixins.entity.player;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.impl.event.entity.player.TravelEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class MixinPlayer
{
    @Inject(method = "travel", at = @At(value = "HEAD"), cancellable = true)
    private void travelHook(Vec3 input, CallbackInfo info)
    {
        // noinspection ConstantConditions
        if (LocalPlayer.class.isInstance(this))
        {
            TravelEvent.Pre event = new TravelEvent.Pre(input);
            EventBus.getInstance().post(event);
            if (event.isCanceled())
            {
                info.cancel();
            }
        }
    }

    @Inject(method = "travel", at = @At(value = "TAIL"))
    private void travelHook_Tail(Vec3 input, CallbackInfo info)
    {
        // noinspection ConstantConditions
        if (LocalPlayer.class.isInstance(this))
        {
            TravelEvent.Post event = new TravelEvent.Post(input);
            EventBus.getInstance().post(event);
        }
    }
}
