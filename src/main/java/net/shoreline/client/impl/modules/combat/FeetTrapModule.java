package net.shoreline.client.impl.modules.combat;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.api.setting.impl.SettingGroup;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.connection.PacketEvent;
import net.shoreline.client.impl.event.network.PlayerUpdateEvent;
import net.shoreline.client.impl.modules.combat.trap.TrapLayer;
import net.shoreline.client.impl.modules.combat.trap.TrapModule;
import net.shoreline.client.impl.modules.combat.trap.TrapSpec;
import net.shoreline.eventbus.api.Subscribe;

import java.util.EnumSet;

public class FeetTrapModule extends TrapModule
{
    Setting<Float> placeRange = new NumberSetting.Builder<Float>("Range")
            .setMin(1.0f).setMax(6.0f).setDefaultValue(4.0f).setFormat("m")
            .setDescription("Range to place blocks").build();
    Setting<Boolean> extend = new BooleanSetting.Builder("Extend")
            .setDescription("Extends feet trap when being mined")
            .setDefaultValue(false).build();
    Setting<Boolean> head = new BooleanSetting.Builder("CoverHead")
            .setDescription("Traps player head")
            .setDefaultValue(false).build();
    Setting<Boolean> floor = new BooleanSetting.Builder("Floor")
            .setDescription("Places blocks under you")
            .setDefaultValue(false).build();

    Setting<Boolean> instantReplace = new BooleanSetting.Builder("Instant")
            .setDescription("Replaces instantly after mined")
            .setDefaultValue(false).build();
    Setting<Boolean> sequentialReplace = new BooleanSetting.Builder("Sequential")
            .setDescription("Replaces instantly after explosions")
            .setDefaultValue(false).build();
    Setting<Boolean> attackSequential = new BooleanSetting.Builder("Attack")
            .setDescription("Attacks crystals when they spawn")
            .setVisible(() -> sequentialReplace.getValue())
            .setDefaultValue(false).build();
    Setting<Void> replaceConfig = new SettingGroup.Builder("Replace")
            .addAll(instantReplace, sequentialReplace, attackSequential).build();

    Setting<Boolean> autoDisable = new BooleanSetting.Builder("AutoDisable")
            .setDescription("Disables when player y-level changes")
            .setDefaultValue(false).build();

    private double prevY;

    public FeetTrapModule()
    {
        super("FeetTrap", new String[]{"Surround"}, "Surrounds feet in obsidian", Category.COMBAT);
    }

    @Override
    public void onEnable()
    {
        if (!checkNull())
        {
            prevY = mc.player.getY();
        }
    }

    @Subscribe
    public void onPlayerUpdate(PlayerUpdateEvent.Pre event)
    {
        if (checkNull())
        {
            return;
        }

        double dy = mc.player.getY() - prevY;
        if (autoDisable.getValue() && (dy > 0.5 || dy < -1.5))
        {
            disable();
            return;
        }

        int obbySlot = findBestObbySlot();
        if (obbySlot == -1)
        {
            return;
        }

        final AABB playerBox = mc.player.getBoundingBox();
        AABB boundingBox = playerBox.setMinY(Math.round(playerBox.minY)).contract(0.01, 0.1, 0.01);
        TrapSpec trapSpec = TrapSpec.builder()
                .layers(getLayers())
                .extendFeet(extend.getValue())
                .build();

        trapPos.calcTrap(boundingBox, trapSpec);

        createPlacementsFromPositions(getCurrentObbyBlock(), trapPos.getTrapPositions(), placeRange.getValue());
        if (placements.isEmpty() || !Managers.INTERACTION.startPlacement(obbySlot))
        {
            return;
        }

        Shoreline.info(placements.size() + "");
        for (BlockPos placement : placements)
        {
            placeObby(placement);
        }

        Managers.INTERACTION.endPlacement();
    }

    @Subscribe
    public void onPacketInbound(PacketEvent.Receive<?> event)
    {
        if (checkNull())
        {
            return;
        }

        if (event.getPacket() instanceof ClientboundBlockUpdatePacket packet
                && packet.getBlockState().isAir() && instantReplace.getValue())
        {
            BlockPos blockPos = packet.getPos();
            if (trapPos.getTrapPositions().contains(blockPos))
            {
                runSingleObbyPlacement(blockPos);
            }
        }

        else if (event.getPacket() instanceof ClientboundAddEntityPacket packet
                && packet.getType() == EntityType.END_CRYSTAL && sequentialReplace.getValue())
        {
            BlockPos blockPos = BlockPos.containing(packet.getX(), packet.getY(), packet.getZ());
            if (!trapPos.getTrapPositions().contains(blockPos))
            {
                return;
            }

            if (attackSequential.getValue())
            {
                sendAttackPacketsInternal(packet.getId(), false, InteractionHand.MAIN_HAND);
            }

            runSingleObbyPlacement(blockPos);
        }
    }

    @Override
    public EnumSet<TrapLayer> getLayers()
    {
        EnumSet<TrapLayer> layers = EnumSet.of(TrapLayer.FEET);
        if (head.getValue())
        {
            layers.add(TrapLayer.CEILING);
        }

        if (floor.getValue())
        {
            layers.add(TrapLayer.FLOOR);
        }

        return layers;
    }
}
