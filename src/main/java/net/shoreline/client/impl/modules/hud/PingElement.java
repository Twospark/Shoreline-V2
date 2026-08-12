package net.shoreline.client.impl.modules.hud;

import net.minecraft.ChatFormatting;
import net.shoreline.client.api.element.dynamic.DynamicElement;
import net.shoreline.client.api.element.dynamic.DynamicEntry;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.modules.client.LatencyModule;

public class PingElement extends DynamicElement
{
    public PingElement()
    {
        super("Ping", "Displays current server latency", 200, 250);
    }

    @Override
    public void loadEntries()
    {
        getEntries().add(new DynamicEntry(this, this::getLatencyText, () -> true));
    }

    public String getLatencyText()
    {
        int latency = LatencyModule.INSTANCE.isEnabled() ? LatencyModule.INSTANCE.getCurrentLatency() : Managers.NETWORK.getClientLatency();
        return String.format("Ping " + ChatFormatting.WHITE + "%dms", latency);
    }
}
