package net.shoreline.client.impl.modules.movement;

import net.minecraft.world.effect.MobEffects;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.EnumSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.entity.player.JumpEvent;
import net.shoreline.client.impl.event.network.StopSprintingEvent;
import net.shoreline.client.impl.rotation.util.ClientRotationEvent;
import net.shoreline.client.util.Formatter;
import net.shoreline.client.util.input.InputUtil;
import net.shoreline.client.util.level.PhaseUtil;
import net.shoreline.eventbus.api.Subscribe;

public class SprintModule extends Toggleable
{
    Setting<SprintMode> mode = new EnumSetting.Builder<SprintMode>("Mode")
            .setDescription("-Legit: Vanilla sprint that never flags.\n-Rage: Sprints in all directions.")
            .setDefaultValue(SprintMode.LEGIT).build();
    Setting<Boolean> rotate = new BooleanSetting.Builder("Rotate")
            .setDescription("Rotates the player in the sprint direction to avoid flags.")
            .setVisible(() -> mode.getValue() == SprintMode.RAGE)
            .setDefaultValue(false).build();
    Setting<Boolean> jumpFix = new BooleanSetting.Builder("JumpFix")
            .setDescription("Fixes jumping slowdown in Rage sprint")
            .setVisible(() -> mode.getValue() == SprintMode.RAGE)
            .setDefaultValue(false).build();
    Setting<Boolean> yawFix = new BooleanSetting.Builder("YawFix")
            .setDescription("Fixes sprint stopping when yaw changes")
            .setVisible(() -> mode.getValue() == SprintMode.RAGE)
            .setDefaultValue(true).build();

    private float lastSprintYaw = Float.NaN;

    public SprintModule()
    {
        super("Sprint", new String[]{"AutoSprint"}, "Automatically sprints for you", Category.MOVEMENT);
    }

    @Override
    public String getDisplayInfo()
    {
        return Formatter.formatEnum(mode.getValue());
    }

    @Subscribe
    public void onTick(TickEvent event)
    {
        if (checkNull())
        {
            return;
        }

        if (!canSprint() || mc.player.horizontalCollision)
        {
            mc.player.setSprinting(false);
            return;
        }

        switch (mode.getValue())
        {
            case LEGIT ->
            {
                if (mc.player.input.hasForwardImpulse())
                {
                    mc.player.setSprinting(true);
                }
            }
            case RAGE ->
            {
                float sprintYaw = InputUtil.getYawFromInput(mc.player.getYRot());
                if (rotate.getValue() && !Managers.ROTATION.isFacingYaw(sprintYaw))
                {
                    boolean shouldSprint = yawFix.getValue()
                            && !Float.isNaN(lastSprintYaw)
                            && Managers.ROTATION.isFacingYaw(lastSprintYaw);

                    mc.player.setSprinting(shouldSprint);
                    if (shouldSprint)
                    {
                        lastSprintYaw = sprintYaw;
                    }

                    return;
                }

                lastSprintYaw = sprintYaw;
                mc.player.setSprinting(true);
            }
        }
    }

    @Subscribe
    public void onClientRotation(ClientRotationEvent event)
    {
        if (mode.getValue() != SprintMode.RAGE || !rotate.getValue() || !canSprint())
        {
            return;
        }

        if (event.isCanceled())
        {
            return;
        }

        float sprintYaw = InputUtil.getYawFromInput(mc.player.getYRot());
        event.setCanceled(true);
        event.setYaw(sprintYaw);
    }

    @Subscribe
    public void onStopSprinting(StopSprintingEvent event)
    {
        if (canSprint() && !mc.player.horizontalCollision && mode.getValue() == SprintMode.RAGE)
        {
            event.setCanceled(true);
        }
    }

    @Subscribe
    public void onJumpYaw(JumpEvent.Yaw event)
    {
        if (jumpFix.getValue() && mode.getValue() == SprintMode.RAGE)
        {
            float yaw = event.getYaw();
            float forward = Math.signum(mc.player.input.getMoveVector().y);
            float strafe = 90.0f * Math.signum(mc.player.input.getMoveVector().x);
            if (forward != 0.0f)
            {
                strafe *= (forward * 0.5f);
            }

            yaw -= strafe;
            if (forward < 0.0f)
            {
                yaw -= 180.0f;
            }

            event.setCanceled(true);
            event.setYaw(yaw);
        }
    }

    private boolean canSprint()
    {
        return InputUtil.isInputtingMovement()
                && !PhaseUtil.isInsideWeb(mc.player)
                && !mc.player.isShiftKeyDown()
                && mc.player.getVehicle() == null
                && !mc.player.isFallFlying()
                && !mc.player.isInWater()
                && !mc.player.isInLava()
                && !mc.player.onClimbable()
                && !mc.player.hasEffect(MobEffects.BLINDNESS)
                && mc.player.getFoodData().getFoodLevel() > 6.0f;
    }

    private enum SprintMode
    {
        LEGIT,
        RAGE
    }
}