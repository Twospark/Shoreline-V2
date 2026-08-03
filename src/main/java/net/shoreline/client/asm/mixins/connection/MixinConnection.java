package net.shoreline.client.asm.mixins.connection;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.shoreline.client.asm.ducks.connection.IConnection;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.eventbus.EventBus;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.List;

@Mixin(Connection.class)
public abstract class MixinConnection implements IConnection
{
    @Override
    @Invoker(value = "doSendPacket")
    public abstract void shoreline$doSendPacket(Packet<?> packet, ChannelFutureListener listener, boolean flush);

    @Shadow
    private volatile @Nullable PacketListener packetListener;

    @Shadow
    private static <T extends PacketListener> void genericsFtw(Packet<T> packet, PacketListener listener) {}

    @Inject(
            method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;" +
                    "Lnet/minecraft/network/protocol/Packet;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/Connection;" +
                            "genericsFtw(Lnet/minecraft/network/protocol/Packet;" +
                            "Lnet/minecraft/network/PacketListener;)V",
                    shift = At.Shift.BEFORE),
            cancellable = true)
    private void channelRead0Hook(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo info)
    {
        PacketListener listener = packetListener;
        if (packet == null)
        {
            return;
        }

        if (packet instanceof ClientboundBundlePacket bundle)
        {
            List<Packet<? super ClientGamePacketListener>> packets = new LinkedList<>();
            for (Packet<? super ClientGamePacketListener> p : bundle.subPackets())
            {
                PacketEvent.Receive<Packet<?>> event = new PacketEvent.Receive<>(p, (Connection) (Object) this);
                EventBus.getInstance().post(event, p.getClass());
                if (!event.isCanceled())
                {
                    packets.add(p);
                }
            }

            info.cancel();
            if (!packets.isEmpty())
            {
                genericsFtw(new ClientboundBundlePacket(packets), listener);
            }

            return;
        }

        PacketEvent.Receive<?> event = new PacketEvent.Receive<>(packet, (Connection) (Object) this);
        EventBus.getInstance().post(event, packet.getClass());
        if (event.isCanceled())
        {
            info.cancel();
        }
    }

    @Inject(
            method = "send(Lnet/minecraft/network/protocol/Packet;" +
                    "Lio/netty/channel/ChannelFutureListener;Z)V",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void sendHook_Head(Packet<?> packet,
                               ChannelFutureListener listener,
                               boolean flush,
                               CallbackInfo info)
    {
        PacketEvent.Send<?> event = new PacketEvent.Send<>(packet);
        EventBus.getInstance().post(event, packet.getClass());
        if (event.isCanceled())
        {
            info.cancel();
        }
    }

    @Inject(
            method = "send(Lnet/minecraft/network/protocol/Packet;" +
                    "Lio/netty/channel/ChannelFutureListener;Z)V",
            at = @At(value = "RETURN"))
    private void sendHook_Return(Packet<?> packet,
                                 ChannelFutureListener listener,
                                 boolean flush,
                                 CallbackInfo info)
    {

        PacketEvent.Post<?> event = new PacketEvent.Post<>(packet);
        EventBus.getInstance().post(event, packet.getClass());
    }
}

