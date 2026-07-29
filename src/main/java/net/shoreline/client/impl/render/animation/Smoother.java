package net.shoreline.client.impl.render.animation;

public class Smoother
{
    private double smoothedValue;

    public double smooth(double original, double smoother, double partialTicks)
    {
        double alpha = 1.0 - Math.exp(-smoother * partialTicks);
        smoothedValue += (original - smoothedValue) * alpha;
        return smoothedValue;
    }

    public void clear()
    {
        smoothedValue = 0.0;
    }
}