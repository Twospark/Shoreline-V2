package net.shoreline.client.util.level;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PositionUtil
{
    public static double distanceSq(double x, double y, double z, double x1, double y1, double z1)
    {
        double xDist = x - x1;
        double yDist = y - y1;
        double zDist = z - z1;
        return xDist * xDist + yDist * yDist + zDist * zDist;
    }
}
