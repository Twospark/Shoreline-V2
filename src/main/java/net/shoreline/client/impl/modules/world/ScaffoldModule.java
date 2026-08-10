package net.shoreline.client.impl.modules.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.api.setting.impl.RegistrySetting;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.network.PlayerUpdateEvent;
import net.shoreline.client.impl.interact.InteractDirection;
import net.shoreline.client.impl.modules.impl.PlacerModule;
import net.shoreline.client.util.input.InputUtil;
import net.shoreline.eventbus.api.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScaffoldModule extends PlacerModule
{
    Setting<Float> placeRange = new NumberSetting.Builder<Float>("Range")
            .setMin(1.0f).setMax(6.0f).setDefaultValue(4.0f).setFormat("m")
            .setDescription("Range to place blocks").build();
    Setting<Boolean> keepY = new BooleanSetting.Builder("KeepY")
            .setDescription("Maintains the player's y height")
            .setDefaultValue(false).build();
    Setting<Collection<Block>> blockList = new RegistrySetting.Builder<Block>("Blocks")
            .setRegistry(BuiltInRegistries.BLOCK)
            .setValues(Blocks.OBSIDIAN, Blocks.DIRT)
            .setDescription("The blocks to use for scaffolding").build();

    private int groundPosY = Integer.MIN_VALUE;

    private BlockPos lastPlacement;
    private Block currentScaffoldBlock;

    public ScaffoldModule()
    {
        super("Scaffold", new String[] {"BlockFly"}, "Places blocks under the player", Category.WORLD);
    }

    @Override
    public void onDisable()
    {
        groundPosY = Integer.MIN_VALUE;
        lastPlacement = null;
    }

    @Subscribe
    public void onUpdate(PlayerUpdateEvent.Pre event)
    {
        if (checkNull())
        {
            return;
        }

        int slot = findValidBlockSlot();
        if (slot == -1)
        {
            return;
        }

        int posY = (int) Math.round(mc.player.getY());
        if (keepY.getValue() && InputUtil.isInputtingMovement())
        {
            if (mc.player.onGround() || groundPosY < mc.level.getMinY())
            {
                groundPosY = posY;
            }

            posY = groundPosY;
        }

        BlockPos pos = new BlockPos(mc.player.getBlockX(), posY, mc.player.getBlockZ());
        createPlacementsFromPositions(currentScaffoldBlock, getScaffoldPlacements(pos.below()), 4.0);
        if (placements.isEmpty() || !Managers.INTERACTION.startPlacement(slot))
        {
            return;
        }

        for (BlockPos blockPos : placements)
        {
            placeBlock(blockPos, currentScaffoldBlock, true, false);
            lastPlacement = blockPos;
        }

        Managers.INTERACTION.endPlacement();
    }

    private int findValidBlockSlot()
    {
        for (int i = 0; i < Inventory.SELECTION_SIZE; i++)
        {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.getItem() instanceof BlockItem blockItem && isValidBlock(blockItem.getBlock()))
            {
                Block block = blockItem.getBlock();
                if (!((RegistrySetting<Block>) blockList).contains(block))
                {
                    continue;
                }

                currentScaffoldBlock = block;
                return i;
            }
        }

        return -1;
    }

    private boolean isValidBlock(Block block)
    {
        return !block.defaultBlockState().canBeReplaced();
    }

    private List<BlockPos> getScaffoldPlacements(BlockPos playerPos)
    {
        List<BlockPos> placements = new ArrayList<>();
        placements.add(playerPos);

        if (lastPlacement == null || AirPlaceModule.INSTANCE.isEnabled())
        {
            return placements;
        }

        int x0 = lastPlacement.getX();
        int y0 = lastPlacement.getY();
        int z0 = lastPlacement.getZ();
        int x1 = playerPos.getX();
        int y1 = playerPos.getY();
        int z1 = playerPos.getZ();

        int dx = x1 - x0;
        int dy = y1 - y0;
        int dz = z1 - z0;
        int sx = Integer.compare(dx, 0);
        int sy = Integer.compare(dy, 0);
        int sz = Integer.compare(dz, 0);

        dx = Math.abs(dx);
        dy = Math.abs(dy);
        dz = Math.abs(dz);

        int ax = dx << 1;
        int ay = dy << 1;
        int az = dz << 1;

        int steps = 0;
        if (dx >= dy && dx >= dz)
        {
            int yd = ay - dx;
            int zd = az - dx;
            while (true)
            {
                if (!ensurePlaceableWithSupport(new BlockPos(x0, y0, z0), placements))
                {
                    break;
                }

                if (++steps > 8 || (x0 == x1 && y0 == y1 && z0 == z1))
                {
                    break;
                }

                if (yd >= 0)
                {
                    y0 += sy;
                    yd -= ax;
                }

                if (zd >= 0)
                {
                    z0 += sz;
                    zd -= ax;
                }

                x0 += sx;
                yd += ay;
                zd += az;
            }
        }
        else if (dy >= dx && dy >= dz)
        {
            int xd = ax - dy;
            int zd = az - dy;
            while (true)
            {
                if (!ensurePlaceableWithSupport(new BlockPos(x0, y0, z0), placements))
                {
                    break;
                }

                if (++steps > 8 || (x0 == x1 && y0 == y1 && z0 == z1))
                {
                    break;
                }

                if (xd >= 0)
                {
                    x0 += sx;
                    xd -= ay;
                }

                if (zd >= 0)
                {
                    z0 += sz;
                    zd -= ay;
                }

                y0 += sy;
                xd += ax;
                zd += az;
            }
        }
        else
        {
            int xd = ax - dz;
            int yd = ay - dz;
            while (true)
            {
                if (!ensurePlaceableWithSupport(new BlockPos(x0, y0, z0), placements))
                {
                    break;
                }

                if (++steps > 8 || (x0 == x1 && y0 == y1 && z0 == z1))
                {
                    break;
                }

                if (xd >= 0)
                {
                    x0 += sx;
                    xd -= az;
                }

                if (yd >= 0)
                {
                    y0 += sy;
                    yd -= az;
                }

                z0 += sz;
                xd += ax;
                yd += ay;
            }
        }

        return placements;
    }

    private boolean ensurePlaceableWithSupport(BlockPos pos, List<BlockPos> out)
    {
        if (!mc.level.getBlockState(pos).canBeReplaced())
        {
            return false;
        }

        Direction face = InteractDirection.getInteractDirection(pos);
        if (face != null)
        {
            return false;
        }

        BlockPos support = getSupportingBlock(pos);
        if (support != null)
        {
            double dist = mc.player.distanceToSqr(support.getCenter());
            if (!out.contains(support) && dist <= placeRange.getValue() * placeRange.getValue())
            {
                out.add(support);
            }
        }
        else
        {
            BlockPos down = pos.below();
            int depth = 0;
            while (depth++ < 3 && mc.level.getBlockState(down).canBeReplaced())
            {
                double dist = mc.player.distanceToSqr(down.getCenter());
                if (!out.contains(down) && dist <= placeRange.getValue() * placeRange.getValue())
                {
                    out.add(down);
                }

                down = down.below();
            }
        }

        double dist = mc.player.distanceToSqr(pos.getCenter());
        if (!out.contains(pos) && dist <= placeRange.getValue() * placeRange.getValue())
        {
            out.add(pos);
        }

        return true;
    }

    private BlockPos getSupportingBlock(BlockPos pos)
    {
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            BlockPos blockPos = pos.relative(dir);
            if (InteractDirection.getInteractDirection(blockPos) != null)
            {
                return blockPos;
            }
        }

        return null;
    }
}
