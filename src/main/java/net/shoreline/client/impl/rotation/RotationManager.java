package net.shoreline.client.impl.rotation;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.shoreline.client.impl.event.connection.PacketEvent;
import net.shoreline.client.impl.network.NetworkHandler;
import net.shoreline.client.impl.rotation.handler.MovementCorrectionHandler;
import net.shoreline.client.impl.rotation.handler.RotationHandler;
import net.shoreline.client.impl.rotation.util.Rotation;
import net.shoreline.eventbus.EventBus;
import net.shoreline.eventbus.listener.LambdaListener;

@Getter
@Setter
public class RotationManager extends NetworkHandler
{
    private final RotationHandler handler;
    private final MovementCorrectionHandler correction;
    private final Rotation serverRotation;

    private Rotation clientRotation;
    private Rotation preJumpRotation;

    public RotationManager()
    {
        super("Rotations");
        this.handler = new RotationHandler();
        this.correction = new MovementCorrectionHandler();
        this.serverRotation = new Rotation(0.0f, 0.0f);
        EventBus.getInstance().subscribe(this);
        EventBus.getInstance().register(this, new LambdaListener<>
                (PacketEvent.Send.class, ServerboundMovePlayerPacket.class, this::onMovePlayer));
    }

    public void onMovePlayer(PacketEvent.Send<ServerboundMovePlayerPacket> event)
    {
        if (checkNull())
        {
            return;
        }

        ServerboundMovePlayerPacket p = event.getPacket();
        if (p.hasRotation())
        {
            serverRotation.setYaw(p.getYRot(0.0f));
            serverRotation.setPitch(p.getXRot(0.0f));
        }
    }
}