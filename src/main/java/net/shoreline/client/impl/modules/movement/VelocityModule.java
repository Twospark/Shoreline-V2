package net.shoreline.client.impl.modules.movement;

import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.EnumSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.api.setting.impl.SettingGroup;
import net.shoreline.client.asm.ducks.connection.packet.server.IClientboundExplodePacket;
import net.shoreline.client.asm.ducks.connection.packet.server.IClientboundSetEntityMotionPacket;
import net.shoreline.client.impl.event.network.ExplosionEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.util.Formatter;
import net.shoreline.client.util.level.PhaseUtil;
import net.shoreline.client.util.math.MathUtil;
import net.shoreline.eventbus.api.Subscribe;

import java.util.Optional;

public class VelocityModule extends Toggleable
{
    Setting<Boolean> cancelKnockback = new BooleanSetting.Builder("Knockback")
            .setDescription("Cancels player knockback")
            .setDefaultValue(true).build();
    Setting<Boolean> cancelExplosion = new BooleanSetting.Builder("Explosion")
            .setDescription("Cancels explosion knockback")
            .setDefaultValue(true).build();
    Setting<VelocityMode> modeConfig = new EnumSetting.Builder<VelocityMode>("Mode")
            .setDescription("The bypass mode for anti knockback")
            .setDefaultValue(VelocityMode.NORMAL).build();
    Setting<Boolean> noPushEntitiesConfig = new BooleanSetting.Builder("Entities")
            .setDescription("Prevents getting pushed by other entities")
            .setDefaultValue(false).build();
    Setting<Boolean> noPushBlocksConfig = new BooleanSetting.Builder("Blocks")
            .setDescription("Prevents being pushed out of blocks")
            .setDefaultValue(false).build();
    Setting<Boolean> noPushLiquidsConfig = new BooleanSetting.Builder("Liquid")
            .setDescription("Prevents getting pushed by liquids")
            .setDefaultValue(false).build();
    Setting<Void> noPushGroup = new SettingGroup.Builder("NoPush")
            .addAll(noPushEntitiesConfig, noPushBlocksConfig, noPushLiquidsConfig).build();
    Setting<Boolean> fishhookConfig = new BooleanSetting.Builder("NoFishhook")
            .setDescription("Prevents getting knocked back by fishing hooks")
            .setDefaultValue(false).build();
    Setting<Integer> horizontalConfig = new NumberSetting.Builder<Integer>("Horizontal")
            .setDefaultValue(0).setMin(0).setMax(100).setFormat("%")
            .setVisible(() -> modeConfig.getValue() == VelocityMode.NORMAL)
            .setDescription("The horizontal velocity reduction").build();
    Setting<Integer> verticalConfig = new NumberSetting.Builder<Integer>("Vertical")
            .setDefaultValue(0).setMin(0).setMax(100).setFormat("%")
            .setVisible(() -> modeConfig.getValue() == VelocityMode.NORMAL)
            .setDescription("The vertical velocity reduction").build();
    Setting<Boolean> groundOnlyConfig = new BooleanSetting.Builder("GroundOnly")
            .setDescription("Only applies wall velocity when grounded.")
            .setVisible(() -> modeConfig.getValue().equals(VelocityMode.WALLS))
            .setDefaultValue(false).build();
    Setting<Boolean> concealConfig = new BooleanSetting.Builder("Conceal")
            .setDescription("Prevents excessive lagbacks on servers with strict movement anticheats")
            .setDefaultValue(false).build();

    private boolean concealVelocity;

    public VelocityModule()
    {
        super("Velocity", new String[]{"AntiKB"}, "Prevents player knockback", Category.MOVEMENT);
    }

    @Override
    public String getDisplayInfo()
    {
        if (modeConfig.getValue() == VelocityMode.NORMAL)
        {
            return String.format("H:%s%%, V:%s%%",
                    MathUtil.round(horizontalConfig.getValue(), 0),
                    MathUtil.round(verticalConfig.getValue(), 0));
        }

        return Formatter.formatEnum(modeConfig.getValue());
    }

    @Override
    public void onDisable()
    {
        concealVelocity = false;
    }

    @Subscribe
    public void onPacketInbound(PacketEvent.Receive<?> event)
    {
        if (checkNull())
        {
            return;
        }

        if (event.getPacket() instanceof ClientboundSetEntityMotionPacket packet && packet.id() == mc.player.getId())
        {
            Vec3 velocity = packet.movement();
            if (concealVelocity && velocity.x == 0.0 && velocity.y == 0.0 && velocity.z == 0.0)
            {
                concealVelocity = false;
                return;
            }

            if (!cancelKnockback.getValue())
            {
                return;
            }

            if (shouldCancelKnockback())
            {
                event.setCanceled(true);
            }
            else if (modeConfig.getValue() == VelocityMode.NORMAL)
            {
                double x = velocity.x * (horizontalConfig.getValue() / 100.0f);
                double y = velocity.y * (verticalConfig.getValue() / 100.0f);
                double z = velocity.z * (horizontalConfig.getValue() / 100.0f);
                ((IClientboundSetEntityMotionPacket) (Object) packet).setMovement(
                        new Vec3(x * 8000, y  * 8000, z  * 80000)
                );
            }
        }

        if (event.getPacket() instanceof ClientboundExplodePacket packet && cancelExplosion.getValue())
        {
            if (shouldCancelExplosions())
            {
                event.setCanceled(true);
            }
            else if (modeConfig.getValue() == VelocityMode.NORMAL)
            {
                Vec3 velocity = packet.playerKnockback().orElse(Vec3.ZERO);
                ((IClientboundExplodePacket) event.getPacket()).setPlayerKnockback(Optional.of(new Vec3(
                        velocity.x * (horizontalConfig.getValue() / 100f),
                        velocity.y * (verticalConfig.getValue() / 100f),
                        velocity.z * (horizontalConfig.getValue() / 100f))));
            }
        }

        if (event.getPacket() instanceof ClientboundPlayerRotationPacket && concealConfig.getValue())
        {
            concealVelocity = true;
        }

        if (event.getPacket() instanceof ClientboundEntityEventPacket packet
                && packet.getEventId() == EntityEvent.FISHING_ROD_REEL_IN && fishhookConfig.getValue())
        {
            Entity entity = packet.getEntity(mc.level);
            if (entity instanceof FishingHook hook && hook.getHookedIn() == mc.player)
            {
                event.setCanceled(true);
            }
        }
    }

    @Subscribe
    public void onExplosion(ExplosionEvent event)
    {
        if (!cancelExplosion.getValue())
        {
            return;
        }

        if (shouldCancelExplosions())
        {
            event.setCanceled(true);
            if (mc.isSingleplayer())
            {
                event.setVelocity(Vec3.ZERO);
            }
        }
        else if (modeConfig.getValue() == VelocityMode.NORMAL)
        {
            Vec3 knockback = event.getVelocity();
            double x = knockback.x * (horizontalConfig.getValue() / 100.0f);
            double y = knockback.y * (verticalConfig.getValue() / 100.0f);
            double z = knockback.z * (horizontalConfig.getValue() / 100.0f);
            event.setCanceled(true);
            event.setVelocity(new Vec3(x, y, z));
        }
    }

    private boolean shouldCancelKnockback()
    {
        return switch (modeConfig.getValue())
        {
            case WALLS -> PhaseUtil.isInsideBlock(mc.player) && (!groundOnlyConfig.getValue() || mc.player.onGround());
            case NORMAL -> horizontalConfig.getValue() == 0 && verticalConfig.getValue() == 0;
            default -> true;
        };
    }

    private boolean shouldCancelExplosions()
    {
        return switch (modeConfig.getValue())
        {
            case WALLS -> PhaseUtil.isInsideBlock(mc.player);
            case NORMAL -> horizontalConfig.getValue() == 0 && verticalConfig.getValue() == 0;
            default -> true;
        };
    }

    private enum VelocityMode
    {
        NORMAL,
        WALLS,
        GRIM_V2,
        JUMP
    }
}
