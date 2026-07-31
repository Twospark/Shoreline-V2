package net.shoreline.client.impl.network;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.shoreline.client.api.common.Feature;
import net.shoreline.client.asm.ducks.connection.IClientPacketListener;
import net.shoreline.client.impl.event.connection.PacketEvent;
import net.shoreline.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

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

    public void send(NetworkHandler handler, Packet<?> packet)
    {
        applyIfPresent(packetListener ->
        {
            packetListener.send(packet);
            logPacket(handler, packet);
        });
    }

    public void sendQuietly(NetworkHandler handler, Packet<?> packet)
    {
        applyIfPresent(packetListener ->
        {
            ((IClientPacketListener) packetListener).shoreline$sendQuietPacket(packet);
            logPacket(handler, packet);
        });
    }

    public void recieve(Packet<ClientGamePacketListener> packet)
    {
        applyIfPresent(packetListener ->
        {
            PacketEvent.Receive<?> event = new PacketEvent.Receive<>(packet, mc.getConnection().getConnection());
            EventBus.getInstance().post(event, packet.getClass());
            if (event.isCanceled())
            {
                return;
            }

            runOnThread(() -> packet.handle(packetListener));
        });
    }

    private void logPacket(NetworkHandler handler, Packet<?> packet)
    {
        sentFromClient.put(packet, new SentPacketData(handler, System.currentTimeMillis()));
        sentCount.computeIfAbsent(handler, k -> new LongAdder()).increment();
    }

    public void applyIfPresent(Consumer<ClientPacketListener> consumer)
    {
        ClientPacketListener listener = mc.getConnection();
        if (mc.level != null && listener != null)
        {
            consumer.accept(listener);
        }
    }

    private record SentPacketData(NetworkHandler handler, Long timestamp) {}
}
