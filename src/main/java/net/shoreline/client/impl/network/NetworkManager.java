package net.shoreline.client.impl.network;

import net.minecraft.network.protocol.Packet;
import net.shoreline.client.api.common.Feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class NetworkManager extends Feature
{
    private final List<NetworkHandler> handlers = new ArrayList<>();
    private final Map<NetworkHandler, LongAdder> sentCount = new ConcurrentHashMap<>();
    private final Map<Packet<?>, SentPacketData> sentFromClient =
            Collections.synchronizedMap(new ConcurrentHashMap<>());

    public NetworkManager()
    {
        super("Network");
    }

    public void registerHandler(NetworkHandler h)
    {
        handlers.add(h);
    }

    private record SentPacketData(NetworkHandler handler, Long timestamp) {}
}
