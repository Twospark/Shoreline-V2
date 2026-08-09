package net.shoreline.client.impl.modules.world;

import lombok.Getter;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.EnumSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.impl.event.LevelEvent;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.render.RenderTickCounterEvent;
import net.shoreline.client.util.math.MathUtil;
import net.shoreline.eventbus.api.Subscribe;

public class TimerModule extends Toggleable
{
    Setting<TickMode> modeConfig = new EnumSetting.Builder<TickMode>("Mode")
            .setDescription("The mode to speed up ticks")
            .setDefaultValue(TickMode.ALWAYS).build();
    Setting<Float> ticksConfig = new NumberSetting.Builder<Float>("Ticks")
            .setMin(0.1f).setMax(50.0f).setDefaultValue(1.5f)
            .setDescription("The game ticks speed").build();
    Setting<Integer> boostConfig = new NumberSetting.Builder<Integer>("Boost")
            .setMin(5).setMax(60).setDefaultValue(20).setFormat(" ticks")
            .setVisible(() -> modeConfig.getValue() == TickMode.PULSE)
            .setDescription("The max ticks to boost").build();

    private int boostTicks;

    @Getter
    private float timerTicks = 1.0f;
    private float prevTimerTicks = 1.0f;

    public TimerModule()
    {
        super("Timer", "Change the game tick speed", Category.WORLD);
    }

    @Override
    public String getDisplayInfo()
    {
        String ticks = String.valueOf(MathUtil.round(timerTicks, 2));
        return modeConfig.getValue() == TickMode.PULSE ? boostTicks + ", " + ticks : ticks;
    }

    @Override
    public void onEnable()
    {
        if (modeConfig.getValue() == TickMode.ALWAYS)
        {
            timerTicks = ticksConfig.getValue();
        }
    }

    @Override
    public void onDisable()
    {
        timerTicks = 1.0f;
        prevTimerTicks = 1.0f;
        boostTicks = 0;
    }

    @Subscribe
    public void onJoin(LevelEvent.Join event)
    {
        timerTicks = 1.0f;
    }

    @Subscribe
    public void onTickPost(TickEvent.Post event)
    {
        if (checkNull())
        {
            return;
        }

        if (modeConfig.getValue() == TickMode.PULSE)
        {
            if (mc.player.getDeltaMovement().horizontalDistanceSqr() > 1.0e-7 || !mc.player.onGround())
            {
                float ticksBoosted = Math.max(ticksConfig.getValue(), 2.0f) - 1.0f;
                if (boostTicks > 0)
                {
                    boostTicks = Math.max(boostTicks - (int) ticksBoosted, 0);
                    if (timerTicks < ticksConfig.getValue())
                    {
                        prevTimerTicks = timerTicks;
                        timerTicks = ticksConfig.getValue();
                    }

                }
                else
                {
                    timerTicks = prevTimerTicks;
                }
            }

            else
            {
                if (boostTicks < boostConfig.getValue())
                {
                    ++boostTicks;
                }

                timerTicks = prevTimerTicks;
            }
        }

        else
        {
            timerTicks = ticksConfig.getValue();
        }
    }

    @Subscribe
    public void onTickCounter(RenderTickCounterEvent event)
    {
        event.setCanceled(true);
        event.setTicks(timerTicks);
    }

    public void setTimerTicks(float timerTicks)
    {
        this.timerTicks = timerTicks;
        this.prevTimerTicks = timerTicks;
    }

    public enum TickMode
    {
        ALWAYS,
        PULSE
    }
}
