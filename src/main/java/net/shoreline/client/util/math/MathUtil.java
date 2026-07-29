package net.shoreline.client.util.math;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class MathUtil
{
    public double round(double value, int places)
    {
        return places < 0 ? value : (new BigDecimal(value)).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    public double getValueFromPercent(float percent, double min, double max)
    {
        return Math.min(max, Math.max(min, min + (max - min) * percent));
    }
}
