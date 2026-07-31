package net.shoreline.client.impl.rotation;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.player.ClientInput;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Input;
import net.shoreline.client.impl.event.connection.PacketEvent;
import net.shoreline.client.impl.event.entity.player.TravelEvent;
import net.shoreline.client.impl.event.input.PlayerInputEvent;
import net.shoreline.client.impl.event.network.MovementPacketsEvent;
import net.shoreline.client.impl.event.network.PlayerUpdateEvent;
import net.shoreline.client.impl.event.network.RotationUpdateEvent;
import net.shoreline.client.impl.modules.client.RotationsModule;
import net.shoreline.client.impl.network.NetworkHandler;
import net.shoreline.client.impl.rotation.handler.CorrectionHandler;
import net.shoreline.client.impl.rotation.handler.RotationHandler;
import net.shoreline.client.impl.rotation.util.ClientRotationEvent;
import net.shoreline.client.impl.rotation.util.Rotation;
import net.shoreline.eventbus.EventBus;
import net.shoreline.eventbus.api.Subscribe;
import net.shoreline.eventbus.listener.LambdaListener;

@Getter
@Setter
public class RotationManager extends NetworkHandler
{
    private final RotationsModule rotationsConfig = RotationsModule.INSTANCE;

    private final CorrectionHandler correction;
    private final RotationHandler handler;
    private final Rotation serverRotation;

    private Rotation clientRotation;
    private Rotation preJumpRotation;

    public RotationManager()
    {
        super("Rotations");
        this.handler = new RotationHandler();
        this.correction = new CorrectionHandler();
        this.serverRotation = new Rotation(0.0f, 0.0f);
        EventBus.getInstance().subscribe(this);
        EventBus.getInstance().register(this, new LambdaListener<>
                (PacketEvent.Send.class, ServerboundMovePlayerPacket.class, this::onMovePlayer));
    }

    @Subscribe
    public void onRotationUpdate(RotationUpdateEvent event)
    {
        Rotation rotationUpdate = new Rotation(event.getYaw(), event.getPitch());
        if (!rotationsConfig.getNoServerRotate().getValue())
        {
            setClientRotation(rotationUpdate);
        }

        serverRotation.setYaw(rotationUpdate.getYaw());
        serverRotation.setPitch(rotationUpdate.getPitch());
    }

    @Subscribe(priority = Integer.MIN_VALUE)
    public void onUpdatePre(PlayerUpdateEvent.PrePacket event)
    {
        if (hasClientRotation())
        {
            handler.applyRotations(mc.player);
        }
    }

    @Subscribe(priority = Integer.MAX_VALUE)
    public void onUpdatePost(PlayerUpdateEvent.Post event)
    {
        handler.revertRotations(mc.player);
    }

    @Subscribe(priority = Integer.MIN_VALUE)
    public void onUpdatePre(PlayerUpdateEvent.Pre event)
    {
        Rotation playerRotation = new Rotation(mc.player);
        ClientRotationEvent rotationEvent = new ClientRotationEvent(playerRotation);
        EventBus.getInstance().post(rotationEvent);
        if (rotationEvent.isCanceled())
        {
            setClientRotation(rotationEvent.getRotation());
        }
        else if (hasClientRotation())
        {
            handler.resetRotations(playerRotation, 1.0f);
        }
    }

    @Subscribe
    public void onPlayerInput(PlayerInputEvent event)
    {
        if (!checkNull()
                && rotationsConfig.getMoveFix().getValue() != RotationsModule.MoveFix.NONE
                && hasClientRotation())
        {
            float deltaYaw = Mth.wrapDegrees(mc.player.getYRot() - clientRotation.getYaw());
            correction.correctInput(event.getInput(), deltaYaw);
        }
    }

    @Subscribe
    public void onMovementPackets(MovementPacketsEvent.Update event)
    {
        if (rotationsConfig.getTickSync().getValue())
        {
            event.setCanceled(true);
        }
    }

    @Subscribe
    public void onMovementPackets(MovementPacketsEvent.Send event)
    {
        if (rotationsConfig.getLookSync().getValue())
        {
            if (event.getPacket() instanceof ServerboundMovePlayerPacket.Pos posGround)
            {
                event.setCanceled(true);
                event.setPacket(new ServerboundMovePlayerPacket.PosRot(posGround.getX(0.0),
                        posGround.getY(0.0),
                        posGround.getZ(0.0),
                        mc.player.getYRot(),
                        mc.player.getXRot(),
                        mc.player.onGround(),
                        mc.player.horizontalCollision));
            }
            else if (event.getPacket() instanceof ServerboundMovePlayerPacket.Rot lookGround)
            {
                event.setCanceled(true);
                event.setPacket(new ServerboundMovePlayerPacket.PosRot(
                        mc.player.getX(),
                        mc.player.getY(),
                        mc.player.getZ(),
                        lookGround.getYRot(0.0f),
                        lookGround.getXRot(0.0f),
                        mc.player.onGround(),
                        mc.player.horizontalCollision));
            }
        }
    }

    @Subscribe
    public void onTravelPre(TravelEvent.Pre event)
    {
        if (rotationsConfig.getFixTravel().getValue() && hasClientRotation())
        {
            handler.applyRotations(mc.player);
        }
    }

    @Subscribe
    public void onTravelPost(TravelEvent.Post event)
    {
        if (rotationsConfig.getFixTravel().getValue())
        {
            handler.revertRotations(mc.player);
        }
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

    /**
     * Should instantly update server rotations.
     *
     * @param rotation the rotation to apply.
     */
    public void setSilentRotation(Rotation rotation)
    {
        if (serverRotation.getYaw() == rotation.getYaw()
                && serverRotation.getPitch() == rotation.getPitch())
        {
            return;
        }

        sendPacket(new ServerboundMovePlayerPacket.PosRot(
                mc.player.getX(),
                mc.player.getY(),
                mc.player.getZ(),
                rotation.getYaw(),
                rotation.getPitch(),
                mc.player.onGround(),
                mc.player.horizontalCollision));
    }

    public void resetSilentRotation()
    {
        Rotation playerRotation = hasClientRotation() ? clientRotation : new Rotation(mc.player);
        setSilentRotation(playerRotation);
    }

    public boolean isFacingYaw(float yaw)
    {
        float dyaw = Mth.wrapDegrees(serverRotation.getYaw() - yaw);
        return Math.abs(dyaw) <= 0.1f;
    }

    public boolean isFacingPitch(float pitch)
    {
        float p2 = Mth.clamp(pitch, -90.0f, 90.0f);
        return Math.abs(serverRotation.getPitch() - p2) <= 0.1f;
    }

    public boolean isFacing(float yaw, float pitch)
    {
        return isFacingYaw(yaw) && isFacingPitch(pitch);
    }

    public void clearClientRotation()
    {
        this.clientRotation = null;
    }

    public boolean hasClientRotation()
    {
        return clientRotation != null;
    }
}