package net.shoreline.client.impl.modules.misc;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Items;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.asm.ducks.IMinecraft;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.eventbus.api.Subscribe;

public class AutoFishModule extends Toggleable
{
    Setting<Boolean> inventoryConfig = new BooleanSetting.Builder("Inventory")
            .setDescription("Allows you to fish while in the inventory")
            .setDefaultValue(true).build();
    Setting<Integer> castingDelay = new NumberSetting.Builder<Integer>("CastDelay")
            .setMin(10).setMax(25).setDefaultValue(15)
            .setDescription("The delay between fishing rod cast").build();
    Setting<Float> maxSoundRange = new NumberSetting.Builder<Float>("MaxSoundDist")
            .setMin(0.1f).setMax(5.0f).setDefaultValue(2.0f)
            .setDescription("The maximum distance from the splash sound").build();

    private boolean autoReel;
    private int autoReelTicks;
    private int autoCastTicks;

    public AutoFishModule()
    {
        super("AutoFish", "Automatically reels in your fishing rod", Category.MISCELLANEOUS);
    }

    @Subscribe
    public void onPacketInbound(PacketEvent.Receive<?> event)
    {
        if (checkNull() || mc.player.getMainHandItem().getItem() != Items.FISHING_ROD)
        {
            return;
        }

        if (event.getPacket() instanceof ClientboundSoundPacket packet
                && packet.getSound().value() == SoundEvents.FISHING_BOBBER_SPLASH)
        {
            FishingHook fishHook = mc.player.fishing;
            if (fishHook == null || fishHook.getPlayerOwner() != mc.player)
            {
                return;
            }

            double dist = fishHook.distanceToSqr(packet.getX(), packet.getY(), packet.getZ());
            if (dist <= maxSoundRange.getValue())
            {
                autoReel = true;
                autoReelTicks = 4;
            }
        }
    }

    @Subscribe
    public void onTick(TickEvent event)
    {
        if (checkNull() || mc.player.getMainHandItem().getItem() != Items.FISHING_ROD)
        {
            return;
        }

        if (mc.screen != null && !(mc.screen instanceof ChatScreen) && !inventoryConfig.getValue())
        {
            return;
        }

        FishingHook fishHook = mc.player.fishing;
        if ((fishHook == null || fishHook.getHookedIn() != null) && autoCastTicks <= 0)
        {
            ((IMinecraft) mc).shoreline$startUseItem();
            autoCastTicks = castingDelay.getValue();
            return;
        }

        if (autoReel)
        {
            if (autoReelTicks <= 0)
            {
                ((IMinecraft) mc).shoreline$startUseItem();
                autoReel = false;
                return;
            }

            autoReelTicks--;
        }

        autoCastTicks--;
    }
}