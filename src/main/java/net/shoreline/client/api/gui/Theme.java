package net.shoreline.client.api.gui;

import lombok.Getter;

@Getter
public class Theme
{
    private static final Theme INSTANCE = new Theme();
    public static Theme getInstance() { return INSTANCE; }

    private int primary = -1;

    private Theme() {}

    public void update(int primary)
    {
        this.primary = primary;
    }

    public int getColor(float transparency)
    {
        return getColor(primary, transparency);
    }

    public int getColor(int color, float transparency)
    {
        return color; // lazy will fix later :p
    }
}