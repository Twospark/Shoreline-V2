package net.shoreline.client.util.math;

public class Timer
{
    private long time;

    public boolean passed(double ms)
    {
        return System.currentTimeMillis() - time >= ms;
    }

    public boolean passed(long ms)
    {
        return System.currentTimeMillis() - time >= ms;
    }

    public Timer reset()
    {
        time = System.currentTimeMillis();
        return this;
    }

    public long getTime()
    {
        return System.currentTimeMillis() - time;
    }

    public void setTime(long ns)
    {
        time = ns;
    }
}
