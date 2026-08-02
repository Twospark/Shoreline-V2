package net.shoreline.client.impl.mining;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.shoreline.client.impl.network.NetworkHandler;

public enum MiningPackets
{
    NORMAL
    {
        @Override
        public void sendStartPackets(NetworkHandler handler, BlockPos blockPos, Direction direction)
        {
            handler.sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, blockPos, direction));
            handler.sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, blockPos, direction));
            handler.sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
            handler.sendPacket(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
        }

        @Override
        public void sendStopPackets(NetworkHandler handler, BlockPos blockPos, Direction direction)
        {
            handler.sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
        }
    },
    GRIM
    {
        @Override
        public void sendStartPackets(NetworkHandler handler, BlockPos blockPos, Direction direction)
        {
            handler.sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
            handler.sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, blockPos, direction));
            handler.sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
            handler.sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
            handler.sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, blockPos, direction));
            handler.sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
            handler.sendPacket(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
        }

        @Override
        public void sendStopPackets(NetworkHandler handler, BlockPos blockPos, Direction direction)
        {
            handler.sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
            handler.sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, blockPos, direction));
        }
    },
    GRIM_V3
    {
        @Override
        public void sendStartPackets(NetworkHandler handler, BlockPos blockPos, Direction direction)
        {
            handler.sendPacket(new ServerboundPlayerActionPacket(
                    ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
            handler.sendPacket(new ServerboundPlayerActionPacket(
                    ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, blockPos, direction));
            handler.sendPacket(new ServerboundPlayerActionPacket(
                    ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, blockPos, direction));
            handler.sendPacket(new ServerboundPlayerActionPacket(
                    ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));

            handler.sendPacket(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
            handler.sendPacket(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
            handler.sendPacket(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
        }

        @Override
        public void sendStopPackets(NetworkHandler handler, BlockPos blockPos, Direction direction)
        {
            handler.sendPacket(new ServerboundPlayerActionPacket(
                    ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
            handler.sendPacket(new ServerboundPlayerActionPacket(
                    ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, blockPos, direction));
        }
    };

    public abstract void sendStartPackets(NetworkHandler handler, BlockPos blockPos, Direction direction);

    public abstract void sendStopPackets(NetworkHandler handler, BlockPos blockPos, Direction direction);
}