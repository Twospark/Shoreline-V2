package net.shoreline.client.impl.event.level;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.shoreline.eventbus.Event;

@AllArgsConstructor
@Getter
@Setter
public class BlockCollisionEvent extends Event
{
    private VoxelShape collisionShape;

    private final BlockState state;
    private final BlockPos blockPos;
}