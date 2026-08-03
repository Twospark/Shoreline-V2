package net.shoreline.client.impl.modules.client;

import lombok.Getter;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.Identifier;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.EnumSetting;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.eventbus.api.Subscribe;

import java.util.IdentityHashMap;
import java.util.Map;

public class LatencyModule extends Toggleable
{
    Setting<Latency> mode = new EnumSetting.Builder<Latency>("Mode")
            .setDescription("The mode to get the latency")
            .setDefaultValue(Latency.PING).build();

    @Getter
    private int currentLatency;
    private final Map<Integer, Long> trackedLatency = new IdentityHashMap<>();

    private int categoryIndex;
    private final Identifier[] categories = new Identifier[] {
            Identifier.parse("minecraft:story/root"),
            Identifier.parse("minecraft:recipes/root"),
            Identifier.parse("minecraft:nether/root"),
            Identifier.parse("minecraft:adventure/root"),
            Identifier.parse("minecraft:end/root"),
            Identifier.parse("minecraft:husbandry/root")
    };

    public LatencyModule()
    {
        super("Latency", "Resolves the client latency", Category.CLIENT);
    }

    @Override
    public String getDisplayInfo()
    {
        return currentLatency + "ms";
    }

    @Subscribe
    public void onTick(TickEvent event)
    {
        if (checkNull())
        {
            return;
        }

        if (mc.player.tickCount % 2 == 0 && !(mc.screen instanceof AdvancementsScreen))
        {
            sendPacket(new ServerboundSeenAdvancementsPacket(ServerboundSeenAdvancementsPacket.Action.OPENED_TAB, categories[categoryIndex]));
            trackedLatency.put(categoryIndex, System.currentTimeMillis());
            categoryIndex++;
            if (categoryIndex >= 6)
            {
                categoryIndex = 0;
            }
        }
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive<?> event)
    {
        if (checkNull())
        {
            return;
        }

        if (event.getPacket() instanceof ClientboundSelectAdvancementsTabPacket p && p.getTab() != null)
        {
            for (int i = 0; i < 6; i++)
            {
                if (!categories[i].equals(p.getTab()))
                {
                    continue;
                }

                currentLatency = (int) (System.currentTimeMillis() - trackedLatency.getOrDefault(i, System.currentTimeMillis()));
                break;
            }
        }
    }

    private enum Latency
    {
        TAB,
        PING
    }
}
