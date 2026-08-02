package net.shoreline.client.asm.mixins.level;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.shoreline.client.impl.event.level.BlockCollisionEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockCollisions.class)
public class MixinBlockCollisions
{
    @Redirect(
            method = "computeNext",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/shapes/CollisionContext;" +
                            "getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;" +
                            "Lnet/minecraft/world/level/CollisionGetter;Lnet/minecraft/core/BlockPos;" +
                            ")Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape computeNextHook_CollisionShape(CollisionContext instance,
                                                      BlockState blockState,
                                                      CollisionGetter collisionGetter,
                                                      BlockPos blockPos)
    {
        VoxelShape voxelShape = instance.getCollisionShape(blockState, collisionGetter, blockPos);
        if (collisionGetter != Minecraft.getInstance().level)
        {
            return voxelShape;
        }

        BlockCollisionEvent blockCollisionEvent = new BlockCollisionEvent(voxelShape, blockState, blockPos);
        EventBus.getInstance().post(blockCollisionEvent);
        if (blockCollisionEvent.isCanceled())
        {
            return blockCollisionEvent.getCollisionShape();
        }

        return voxelShape;
    }
}
