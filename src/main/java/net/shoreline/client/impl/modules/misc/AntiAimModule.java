package net.shoreline.client.impl.modules.misc;

import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.EnumSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.impl.rotation.util.ClientRotationEvent;
import net.shoreline.client.impl.rotation.util.Rotation;
import net.shoreline.eventbus.api.Subscribe;

public class AntiAimModule extends Toggleable
{
    Setting<YawMode> yawConfig = new EnumSetting.Builder<YawMode>("Yaw")
            .setDescription("The rotation spin mode for yaw")
            .setDefaultValue(YawMode.SPIN).build();
    Setting<PitchMode> pitchConfig = new EnumSetting.Builder<PitchMode>("Pitch")
            .setDescription("The rotation spin mode for pitch")
            .setDefaultValue(PitchMode.NONE).build();
    Setting<Float> spinSpeedConfig = new NumberSetting.Builder<Float>("SpinSpeed")
            .setMin(1.0f).setMax(50.0f).setDefaultValue(15.0f)
            .setDescription("The rotation spin speed")
            .setVisible(() -> yawConfig.getValue() == YawMode.SPIN).build();
    Setting<Integer> yawAngleConfig = new NumberSetting.Builder<Integer>("YawAngle")
            .setMin(-180).setMax(180).setDefaultValue(0).setFormat("deg")
            .setDescription("The rotation spin yaw angle")
            .setVisible(() -> yawConfig.getValue() == YawMode.STATIC || yawConfig.getValue() == YawMode.JITTER).build();
    Setting<Integer> pitchAngleConfig = new NumberSetting.Builder<Integer>("PitchAngle")
            .setMin(-90).setMax(90).setDefaultValue(0).setFormat("deg")
            .setDescription("The rotation spin pitch angle")
            .setVisible(() -> pitchConfig.getValue() == PitchMode.STATIC || pitchConfig.getValue() == PitchMode.JITTER).build();
    Setting<Integer> jitterTicksConfig = new NumberSetting.Builder<Integer>("JitterTicks")
            .setMin(1).setMax(20).setDefaultValue(2)
            .setDescription("The ticks between each random rotation")
            .setVisible(() -> yawConfig.getValue() == YawMode.JITTER).build();

    private Rotation current;
    private Rotation playerRotation;

    public AntiAimModule()
    {
        super("AntiAim", new String[] {"SpinBot"}, "Makes it harder for enemies to hit headshots", Category.MISCELLANEOUS);
    }

    @Override
    public void onEnable()
    {
        if (!checkNull())
        {
            playerRotation = new Rotation(mc.player);
            current = playerRotation;
        }
    }

    @Subscribe(priority = Integer.MIN_VALUE)
    public void onRotation(ClientRotationEvent event)
    {
        if (event.isCanceled())
        {
            return;
        }

        if (mc.options.keyAttack.isDown() || mc.options.keyUse.isDown())
        {
            return;
        }

        current = new Rotation(getYaw(), getPitch());
        event.setCanceled(true);
        event.setRotation(current);
    }

    public float getYaw()
    {
        return switch (yawConfig.getValue())
        {
            case NONE -> mc.player.getYRot();
            case STATIC -> mc.player.getYRot() + yawAngleConfig.getValue();
            case ZERO -> playerRotation == null ? 0.0f : playerRotation.getYaw();
            case SPIN ->
            {
                if (current == null)
                {
                    yield 0.0f;
                }

                float spin = current.getYaw() + spinSpeedConfig.getValue();
                if (spin > 360.0f)
                {
                    yield spin - 360.0f;
                }

                yield spin;
            }
            case JITTER -> mc.player.getYRot() + ((mc.player.tickCount % jitterTicksConfig.getValue() == 0) ?
                    yawAngleConfig.getValue() : -yawAngleConfig.getValue());
        };
    }

    public float getPitch()
    {
        return switch (pitchConfig.getValue())
        {
            case NONE -> mc.player.getXRot();
            case STATIC -> pitchAngleConfig.getValue();
            case ZERO -> playerRotation == null ? 0.0f : playerRotation.getPitch();
            case UP -> -90.0f;
            case DOWN -> 90.0f;
            case JITTER ->
            {
                if (current == null)
                {
                    yield 0.0f;
                }

                float jitter = current.getPitch() + 30.0f;
                if (jitter > 90.0f)
                {
                    yield -90.0f;
                }

                if (jitter < -90.0f)
                {
                    yield 90.0f;
                }

                yield jitter;
            }
        };
    }

    public enum YawMode
    {
        NONE, STATIC, ZERO, SPIN, JITTER
    }

    public enum PitchMode
    {
        NONE, STATIC, ZERO, UP, DOWN, JITTER
    }
}