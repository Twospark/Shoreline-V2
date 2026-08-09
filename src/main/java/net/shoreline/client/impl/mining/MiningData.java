package net.shoreline.client.impl.mining;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.shoreline.client.impl.network.NetworkHandler;
import net.shoreline.client.impl.render.BoxRender;
import net.shoreline.client.impl.render.ClientRenderer;
import net.shoreline.client.impl.render.ColorUtil;
import net.shoreline.client.impl.render.animation.Easing;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Getter
@Setter
public class MiningData
{
    @Builder.Default
    private final Player player = Minecraft.getInstance().player;

    @EqualsAndHashCode.Include
    private final BlockPos blockPos;
    private final Direction direction;

    private boolean started;
    private final float maxProgress;
    private final ItemStack miningStack;

    private float blockDamage, lastDamage;

    private int ticksMining;

    public void abort(NetworkHandler handler)
    {
        handler.sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, blockPos, direction));
    }

    public float tickDelta()
    {
        return tickDelta(false);
    }

    public float tickDelta(boolean isMultitasking)
    {
        this.lastDamage = blockDamage;
        if (isDoneMining())
        {
            if (!isMultitasking)
            {
                ticksMining++;
            }

            return blockDamage;
        }

        this.blockDamage += getBlockBreakingDelta();
        return blockDamage;
    }

    public void resetTicksMining()
    {
        ticksMining = 0;
    }

    public void render(ClientRenderer renderer,
                       float partialTicks,
                       BoxRender boxRender,
                       int startColor,
                       int endColor,
                       float alpha)
    {
        render(renderer, partialTicks, boxRender, startColor, endColor, alpha, maxProgress);
    }

    public void render(ClientRenderer renderer,
                       float partialTicks,
                       BoxRender boxRender,
                       int startColor,
                       int endColor,
                       float alpha,
                       float miningSpeed)
    {
        final BlockState state = getBlockState();

        VoxelShape outlineShape = state.getShape(Minecraft.getInstance().level, blockPos);
        AABB boundingBox = outlineShape != null && !outlineShape.isEmpty() ? outlineShape.bounds() : Shapes.block().bounds();
        double scale = isDoneMining() ? 1.0 : Easing.SMOOTH.ease(getLinearScale(miningSpeed, partialTicks));

        int color = ColorUtil.interpolate(endColor, startColor, Math.min(blockDamage / miningSpeed, 1.0f));
        Vec3 center = boundingBox.move(blockPos).getCenter();

        double dx = (boundingBox.maxX - boundingBox.minX) * scale;
        double dy = (boundingBox.maxY - boundingBox.minY) * scale;
        double dz = (boundingBox.maxZ - boundingBox.minZ) * scale;
        AABB scaled = AABB.ofSize(center, dx, dy, dz);

        boxRender.render(renderer, scaled, color, alpha);
    }

    private float getLinearScale(float maxProgress, float partialTicks)
    {
        return Mth.clamp((blockDamage + (blockDamage - lastDamage) * partialTicks) / (float) Math.max(0.001, maxProgress), 0.0f, 1.0f);
    }

    public float getProgress()
    {
        return Mth.clamp(blockDamage / (float) Math.max(0.001, maxProgress), 0.0f, 1.0f);
    }

    public double getSquaredDistanceTo()
    {
        return player.distanceToSqr(blockPos.getCenter());
    }

    public BlockState getBlockState()
    {
        return Minecraft.getInstance().level.getBlockState(blockPos);
    }

    public float getBlockBreakingDelta()
    {
        BlockState state = getBlockState();
        float f = state.getDestroySpeed(Minecraft.getInstance().level, blockPos);
        if (f == -1.0f)
        {
            return 0.0f;
        }

        int i = MiningUtil.canHarvest(miningStack, state) ? 30 : 100;
        return MiningUtil.getBlockBreakingSpeed(player, miningStack, state) / f / (float) i;
    }

    public boolean isDoneMining()
    {
        return blockDamage >= maxProgress;
    }

    public boolean isAlmostDone(int ticks)
    {
        return !isBlockMined() && blockDamage + (ticks * getBlockBreakingDelta()) >= maxProgress;
    }

    public boolean isBlockMined()
    {
        return isDoneMining() && isAir();
    }

    public boolean isAir()
    {
        return !MiningUtil.canMineBlock(getBlockState());
    }

    public boolean hasMinedFor(int ticksMining)
    {
        return this.ticksMining >= ticksMining;
    }

    public MiningData copy(float maxProgress)
    {
        return MiningData.builder()
                .player(this.player)
                .blockPos(this.blockPos)
                .direction(this.direction)
                .maxProgress(maxProgress)
                .miningStack(this.miningStack.copy())
                .blockDamage(this.blockDamage)
                .lastDamage(this.lastDamage)
                .ticksMining(this.ticksMining)
                .build();
    }
}