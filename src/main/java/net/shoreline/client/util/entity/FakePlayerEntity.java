package net.shoreline.client.util.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class FakePlayerEntity extends RemotePlayer
{
    public static final UUID FAKE_UUID = UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7");

    public static final AtomicInteger CURRENT_ID = new AtomicInteger(1000000);

    private final Player player;

    public FakePlayerEntity(Player player, String name)
    {
        super(Minecraft.getInstance().level, new GameProfile(FAKE_UUID, name));
        this.player = player;
        this.tickCount = 100;

        copyPosition(player);
        this.yHeadRot = player.yHeadRot;
        this.yBodyRot = player.yBodyRot;
        this.walkAnimation.setSpeed(player.walkAnimation.speed());

        Byte playerModel = player.getEntityData().get(Player.DATA_PLAYER_MODE_CUSTOMISATION);
        getEntityData().set(Player.DATA_PLAYER_MODE_CUSTOMISATION, playerModel);
        getAttributes().assignAllValues(player.getAttributes());
        setShiftKeyDown(player.isShiftKeyDown());
        setSwimming(player.isSwimming());
        setPose(player.getPose());
        setHealth(player.getHealth());

        getInventory().replaceWith(player.getInventory());
        setId(CURRENT_ID.incrementAndGet());
    }

    public FakePlayerEntity(Player player)
    {
        this(player, player.getName().getString());
    }

    @Override
    public boolean isAlive()
    {
        return true;
    }

    @Override
    public boolean isDeadOrDying()
    {
        return false;
    }

    public void spawnPlayer()
    {
        unsetRemoved();
        Minecraft.getInstance().level.addEntity(this);
    }

    public void despawnPlayer()
    {
        Minecraft.getInstance().level.removeEntity(getId(), RemovalReason.DISCARDED);
        setRemoved(RemovalReason.DISCARDED);
    }
}
