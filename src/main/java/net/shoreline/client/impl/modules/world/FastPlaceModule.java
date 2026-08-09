package net.shoreline.client.impl.modules.world;

import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.ItemStack;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.asm.ducks.IMinecraft;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.eventbus.api.Subscribe;

public class FastPlaceModule extends Toggleable
{
    Setting<Integer> delay = new NumberSetting.Builder<Integer>("Delay")
            .setMin(0).setMax(4).setDefaultValue(0)
            .setDescription("The click delay of placements").build();
    Setting<Boolean> ghostFix = new BooleanSetting.Builder("GhostFix")
            .setDescription("Fixes items ghosting on Paper servers")
            .setDefaultValue(false).build();

    public FastPlaceModule()
    {
        super("FastPlace", "Place blocks and items faster", Category.WORLD);
    }

    @Subscribe
    public void onTick(TickEvent event)
    {
        if (checkNull())
        {
            return;
        }

        if (mc.options.keyUse.isDown() && checkItem(mc.player.getMainHandItem())
                && ((IMinecraft) mc).shoreline$getRightClickDelay() > delay.getValue())
        {
            ((IMinecraft) mc).shoreline$setRightClickDelay(delay.getValue());
        }
    }

    @Subscribe
    public void onPacketOutbound(PacketEvent.Send<?> event)
    {
        if (checkNull() || wasSentFromClient(event.getPacket()))
        {
            return;
        }

        if (event.getPacket() instanceof ServerboundUseItemOnPacket packet
                && ghostFix.getValue() && checkItem(mc.player.getItemInHand(packet.getHand())))
        {
            event.setCanceled(true);
        }
    }

    private boolean checkItem(ItemStack stack)
    {
        return stack.getItem() instanceof ExperienceBottleItem;
    }
}
