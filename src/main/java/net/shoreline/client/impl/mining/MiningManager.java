package net.shoreline.client.impl.mining;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.shoreline.client.api.common.Feature;
import net.shoreline.client.impl.event.LevelEvent;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.connection.PacketEvent;
import net.shoreline.client.util.item.EnchantUtil;
import net.shoreline.eventbus.EventBus;
import net.shoreline.eventbus.api.Subscribe;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MiningManager extends Feature
{
    private final ConcurrentMap<BlockPos, MiningData> miningBlocks = new ConcurrentHashMap<>();

    private ItemStack maxPickaxeStack;

    public MiningManager()
    {
        super("Mining");
        EventBus.getInstance().subscribe(this);
    }

    @Subscribe
    public void onWorldDisconnect(LevelEvent.Disconnect event)
    {
        miningBlocks.clear();
    }

    @Subscribe
    public void onTick(TickEvent event)
    {
        if (checkNull())
        {
            return;
        }

        if (maxPickaxeStack == null)
        {
            this.maxPickaxeStack = new ItemStack(Items.NETHERITE_PICKAXE);
        }

        maxPickaxeStack.enchant(EnchantUtil.getEntry(Enchantments.EFFICIENCY), 5);
        for (MiningData data : miningBlocks.values())
        {
            if (data.getSquaredDistanceTo() > 36.0f || data.isBlockMined() || data.hasMinedFor(40))
            {
                miningBlocks.remove(data.getBlockPos());
                continue;
            }

            data.tickDelta();
        }
    }

    @Subscribe
    public void onPacketInbound(PacketEvent.Receive<?> event)
    {
        if (checkNull())
        {
            return;
        }

        if (event.getPacket() instanceof ClientboundBlockDestructionPacket packet)
        {
            if (miningBlocks.containsKey(packet.getPos()))
            {
                return;
            }

            Entity entity = mc.level.getEntity(packet.getId());
            if (!(entity instanceof Player playerEntity))
            {
                return;
            }

            MiningData data = MiningData.builder()
                    .blockPos(packet.getPos())
                    .direction(Direction.UP)
                    .maxProgress(1.0f)
                    .player(playerEntity)
                    .miningStack(maxPickaxeStack)
                    .build();

            if (mc.player.distanceToSqr(data.getBlockPos().getCenter()) > 144.0f)
            {
                return;
            }

            long count = getMiningCount(playerEntity);
            if (count >= 2)
            {
                for (var entry : miningBlocks.entrySet())
                {
                    if (entry.getValue().getPlayer().equals(playerEntity))
                    {
                        miningBlocks.remove(entry.getKey());
                        break;
                    }
                }
            }

            miningBlocks.put(packet.getPos(), data);
        }
    }

    public MiningData getData(BlockPos pos)
    {
        return miningBlocks.get(pos);
    }

    public int getMiningCount(Player playerEntity)
    {
        int count = 0;
        for (MiningData data : miningBlocks.values())
        {
            if (data.getPlayer().equals(playerEntity) && ++count >= 2)
            {
                break;
            }
        }
        return count;
    }

    public float getMiningDamage(BlockPos blockPos)
    {
        MiningData data = miningBlocks.get(blockPos);
        return data != null ? data.getBlockDamage() : 0.0f;
    }

    public float getMiningProgress(BlockPos blockPos)
    {
        MiningData data = miningBlocks.get(blockPos);
        return data != null ? data.getBlockDamage() / data.getMaxProgress() : 0.0f;
    }

    public Collection<MiningData> getMiningBlocks()
    {
        return miningBlocks.values();
    }
}