package net.shoreline.client.impl.event.network;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.shoreline.eventbus.Event;

@RequiredArgsConstructor
@Getter
public class AttackBlockEvent extends Event
{
    private final BlockPos pos;
    private final Direction direction;

    public BlockState getState()
    {
        return Minecraft.getInstance().level.getBlockState(pos);
    }
}