package net.shoreline.client.impl.interact;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.modules.world.AirPlaceModule;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class PlaceInteraction extends Interaction<BlockPos>
{
    public static final AtomicInteger GLOBAL_COUNT = new AtomicInteger();

    private final Block block;
    private final boolean airPlace;

    private Direction direction;

    public PlaceInteraction(BlockPos pos,
                            Block block,
                            Direction direction,
                            InteractionHand hand,
                            boolean clientInteract,
                            boolean airPlace)
    {
        super("BlockPlaceInteraction", pos, hand, clientInteract);
        this.block = block;
        this.direction = direction;
        this.airPlace = airPlace;
    }

    public PlaceInteraction(BlockPos pos,
                            Block block,
                            Direction direction,
                            InteractionHand hand)
    {
        this(pos, block, direction, hand, false, AirPlaceModule.INSTANCE.isForceAirPlace());
    }

    public static PlaceInteraction.Builder builder()
    {
        return new PlaceInteraction.Builder();
    }

    public BlockPos getPos()
    {
        return getInteract();
    }

    public BlockPos getInteractPos()
    {
        return airPlace ? interact : interact.relative(direction.getOpposite());
    }

    public Vec3 getInteractVec()
    {
        return getInteractPos().getCenter().add(new Vec3(direction.getUnitVec3f()).scale(0.5));
    }

    public BlockState getState()
    {
        return Minecraft.getInstance().level.getBlockState(getPos());
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof PlaceInteraction i && i.getInteract().equals(getInteract());
    }

    @Override
    public InteractionResult applyInteraction()
    {
        AABB box = new AABB(getPos());
        BlockHitResult result = new BlockHitResult(
                getInteractVec(),
                direction,
                getInteractPos(),
                box.contains(mc.player.getEyePosition()));

        if (!clientInteract || !mc.isSameThread())
        {
            sendSequencedPacket(id -> new ServerboundUseItemOnPacket(hand, result, id));
            Managers.INTERACTION.playBlockPlaceSound(interact, getState());
            return InteractionResult.SUCCESS;
        }
        else
        {
            return mc.gameMode.useItemOn(mc.player, hand, result);
        }
    }

    public static class Builder
    {
        private BlockPos pos;
        private Block block;
        private Direction direction;
        private InteractionHand hand;
        private boolean clientInteract;
        private boolean airPlace;

        public Builder pos(BlockPos pos)
        {
            this.pos = pos;
            return this;
        }

        public Builder block(Block block)
        {
            this.block = block;
            return this;
        }

        public Builder direction(Direction direction)
        {
            this.direction = direction;
            return this;
        }

        public Builder hand(InteractionHand hand)
        {
            this.hand = hand;
            return this;
        }

        public Builder clientInteract(boolean clientInteract)
        {
            this.clientInteract = clientInteract;
            return this;
        }

        public Builder airPlace(boolean airPlace)
        {
            this.airPlace = airPlace;
            return this;
        }

        public PlaceInteraction build()
        {
            return new PlaceInteraction(
                    pos,
                    block,
                    direction,
                    hand,
                    clientInteract,
                    airPlace
            );
        }
    }
}