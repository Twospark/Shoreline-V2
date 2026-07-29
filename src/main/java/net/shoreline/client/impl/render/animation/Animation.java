package net.shoreline.client.impl.render.animation;

import lombok.Getter;
import lombok.Setter;
import net.shoreline.client.api.interfaces.Globals;

@Getter
@Setter
public class Animation implements Globals
{
    private Easing easing;
    private double start;
    private double target;
    private double length;

    private long lastMillis = 0L;
    private boolean state;

    public Animation(double speed, Easing easing)
    {
        this(false, 0, 1, speed, easing);
    }

    public Animation(double start, double target, double speed, Easing easing)
    {
        this(false, start, target, speed, easing);
    }

    public Animation(boolean state, double start, double target, double length, Easing easing)
    {
        this.state = state;
        this.start = start;
        this.target = target;
        this.length = length;
        this.easing = easing;
    }

    public boolean getState()
    {
        return state;
    }

    public void setState(boolean state)
    {
        lastMillis = (long) (!state ? System.currentTimeMillis() - ((1 - getLinearFactor()) * length) : System.currentTimeMillis() - (getLinearFactor() * length));
        this.state = state;
    }

    public void setStateHard(boolean state)
    {
        this.state = state;
        if (state)
        {
            this.lastMillis = System.currentTimeMillis() - (long) (getLinearFactor() * length);
        }
        else
        {
            this.lastMillis = (long) (System.currentTimeMillis() - ((1 - getLinearFactor()) * length));
        }
    }

    public double getFactor()
    {
        return easing.ease(getLinearFactor());
    }

    public void setFactor(double factor)
    {
        long currentTime = System.currentTimeMillis();
        lastMillis = currentTime - (long) (factor * length);
    }

    public double getCurrent()
    {
        return start + ((target - start)) * getFactor();
    }

    public double getLinearFactor()
    {
        return state
                ? clamp(((System.currentTimeMillis() - lastMillis) / length))
                : clamp((1 - (System.currentTimeMillis() - lastMillis) / length));
    }

    private double clamp(double in)
    {
        return in < 0 ? 0 : Math.min(in, 1);
    }

    public boolean isFinished()
    {
        return !getState() && getFactor() == 0.0 || getState() && getFactor() == 1.0;
    }
}