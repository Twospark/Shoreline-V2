package net.shoreline.client.impl.render.animation;

public enum Easing
{
    LINEAR
    {
        @Override
        public double ease(double factor)
        {
            return factor;
        }
    },
    CUBIC_IN
    {
        @Override
        public double ease(double factor)
        {
            return Math.pow(factor, 3);
        }
    },
    CUBIC_OUT
    {
        @Override
        public double ease(double factor)
        {
            return 1 - Math.pow(1 - factor, 3);
        }
    },
    CUBIC_IN_OUT
    {
        @Override
        public double ease(double factor)
        {
            return factor < 0.5 ? 4 * Math.pow(factor, 3) : 1 - Math.pow(-2 * factor + 2, 3) / 2;
        }
    },
    EXPO_IN
    {
        @Override
        public double ease(double factor)
        {
            return factor == 0 ? 0 : Math.pow(2, 10 * factor - 10);
        }
    },
    EXPO_OUT
    {
        @Override
        public double ease(double factor)
        {
            return factor == 1 ? 1 : 1 - Math.pow(2, -10 * factor);
        }
    },
    SMOOTH
    {
        @Override
        public double ease(double factor)
        {
            return factor * factor * (3 - 2 * factor); // standard smoothstep
        }
    };

    public abstract double ease(double factor);
}