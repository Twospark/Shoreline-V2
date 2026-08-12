package net.shoreline.client.impl.modules.hud;

import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.shoreline.client.api.element.dynamic.DynamicElement;
import net.shoreline.client.api.element.dynamic.DynamicEntry;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;

public class CoordinatesElement extends DynamicElement
{
    Setting<Boolean> netherCoordinates = new BooleanSetting.Builder("Nether")
            .setDescription("Show nether coordinates")
            .setDefaultValue(true).build();

    public CoordinatesElement()
    {
        super("Coordinates", "Displays the player coordinates", 200, 400);
    }

    @Override
    public void loadEntries()
    {
        getEntries().add(new DynamicEntry(this, this::getText, () -> true));
    }

    public String getText()
    {
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        boolean nether = mc.level.dimension() == Level.NETHER;
        double nX = nether ? x * 8 : x / 8;
        double nZ = nether ? z * 8 : z / 8;

        return String.format("XYZ " + ChatFormatting.WHITE + "%s, %s, %s " + (netherCoordinates.getValue()
                ? ChatFormatting.RESET + "[" + ChatFormatting.WHITE + "%s, %s" + ChatFormatting.RESET + "]" : ""),
                format(x), format(y), format(z), format(nX), format(nZ));
    }

    public int format(double n)
    {
        return (int) n;
    }
}
