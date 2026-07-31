package net.shoreline.client.asm.mixins.connection;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.shoreline.client.impl.event.item.ItemUseEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiPlayerGameMode.class)
public class MixinMultiPlayerGameMode
{
    @Redirect(
            method = "performUseItemOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;" +
                            "getItemInHand(Lnet/minecraft/world/InteractionHand;" +
                            ")Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack getItemInHandHook(LocalPlayer instance, InteractionHand hand)
    {
        if (hand.equals(InteractionHand.OFF_HAND))
        {
            return instance.getItemInHand(hand);
        }

        ItemUseEvent.Block event = new ItemUseEvent.Block();
        EventBus.getInstance().post(event);
        return event.isCanceled() ? event.getItemStack() : instance.getItemInHand(InteractionHand.MAIN_HAND);
    }
}
