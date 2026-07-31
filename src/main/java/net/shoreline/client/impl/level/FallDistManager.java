package net.shoreline.client.impl.level;

import net.shoreline.client.api.common.Feature;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.eventbus.EventBus;
import net.shoreline.eventbus.api.Subscribe;

public class FallDistManager extends Feature
{
    private double lastY = Double.NaN;
    private float accumulated = 0.0f;

    public FallDistManager()
    {
        super("FallDistance");
        EventBus.getInstance().subscribe(this);
    }

    @Subscribe
    public void onTick(TickEvent.Post event)
    {
        if (checkNull())
        {
            reset();
            return;
        }

        if (mc.player.onGround()
                || mc.player.isInWater()
                || mc.player.isUnderWater()
                || mc.player.onClimbable()
                || mc.player.isFallFlying()
                || mc.player.getAbilities().flying)
        {
            lastY = mc.player.getY();
            accumulated = 0.0f;
            return;
        }

        if (Double.isNaN(lastY))
        {
            lastY = mc.player.getY();
            accumulated = 0.0f;
            return;
        }

        double dy = mc.player.getY() - lastY;
        if (dy < 0.0)
        {
            accumulated += (float) -dy;
        }

        lastY = mc.player.getY();
    }

    public float getFallDistance()
    {
        return accumulated;
    }

    public void reset()
    {
        lastY = Double.NaN;
        accumulated = 0.0f;
    }
}
