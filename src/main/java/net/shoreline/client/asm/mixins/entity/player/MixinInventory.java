package net.shoreline.client.asm.mixins.entity.player;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.shoreline.client.impl.event.entity.player.AddItemEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class MixinInventory
{
    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "HEAD"), cancellable = true)
    private void setItemHook(int slot, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir)
    {
        AddItemEvent event = new AddItemEvent(slot, itemStack);
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }
}
