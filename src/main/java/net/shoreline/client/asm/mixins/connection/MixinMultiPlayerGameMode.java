package net.shoreline.client.asm.mixins.connection;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.shoreline.client.asm.ducks.connection.IMultiPlayerGameMode;
import net.shoreline.client.impl.event.item.ItemUseEvent;
import net.shoreline.client.impl.event.network.AttackBlockEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MixinMultiPlayerGameMode implements IMultiPlayerGameMode
{
    @Override
    @Accessor(value = "destroyBlockPos")
    public abstract BlockPos shoreline$getDestroyBlockPos();

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

    @Inject(method = "startDestroyBlock", at = @At(value = "HEAD"), cancellable = true)
    private void startDestroyBlockHook(BlockPos pos,
                                       Direction direction,
                                       CallbackInfoReturnable<Boolean> cir)
    {
        AttackBlockEvent event = new AttackBlockEvent(pos, direction);
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            cir.cancel();
            cir.setReturnValue(false);
        }
    }
}
