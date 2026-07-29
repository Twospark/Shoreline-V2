package net.shoreline.client.api.gui.handler;

import net.minecraft.util.Mth;
import net.shoreline.client.util.math.MathUtil;

import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class NumberHandler<N extends Number>
{
    protected final DoubleFunction<N> conversion;
    protected final Supplier<N> input;
    protected final Consumer<N> output;
    protected final int roundingPlaces;
    private final double min;
    private final double max;

    public NumberHandler(Supplier<N> input, Consumer<N> output, DoubleFunction<N> conversion)
    {
        this(input, output, conversion, 2, 0, 1);
    }

    public NumberHandler(Supplier<N> input,
                         Consumer<N> output,
                         DoubleFunction<N> conversion,
                         int roundingPlaces,
                         double min,
                         double max)
    {
        this.input = input;
        this.output = output;
        this.conversion = conversion;
        this.roundingPlaces = roundingPlaces;
        this.min = min;
        this.max = max;
    }

    public Number getValue()
    {
        return input.get();
    }

    public float getPercent()
    {
        return percent(getValue());
    }

    public N fromPercent(float percent)
    {
        double value = MathUtil.round(Mth.lerp(percent, min, max), roundingPlaces);
        double clamped = Math.clamp(value, min, max);
        return conversion.apply(clamped);
    }

    public N fromText(String text)
    {
        double value = Math.clamp(Double.parseDouble(text), min, max);
        return conversion.apply(value);
    }

    public float percent(Number value)
    {
        return (float) Math.max(0f, Math.min(1f, (value.doubleValue() - min) / (max - min)));
    }

    public void update(float percent)
    {
        output.accept(fromPercent(percent));
    }

    public void updateText(String text)
    {
        try
        {
            output.accept(fromText(text));
        }
        catch (NumberFormatException ignored)
        {
        }
    }
}
