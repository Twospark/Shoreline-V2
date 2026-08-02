package net.shoreline.client.impl.modules.world;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.asm.ducks.connection.IMultiPlayerGameMode;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.inventory.ItemSlot;
import net.shoreline.client.util.entity.PlayerUtil;
import net.shoreline.client.util.item.EnchantUtil;
import net.shoreline.client.util.item.ItemUtil;
import net.shoreline.eventbus.api.Subscribe;

public class AutoToolModule extends Toggleable
{
    public static AutoToolModule INSTANCE;

    Setting<Boolean> swapBack = new BooleanSetting.Builder("SwapBack")
            .setDescription("Swaps back to your previously held item")
            .setDefaultValue(false).build();

    private int prevSlot = -1;

    public AutoToolModule()
    {
        super("AutoTool", "Automatically switches to a tool before mining", Category.WORLD);
        INSTANCE = this;
    }

    @Subscribe
    public void onTickPre(TickEvent event)
    {
        if (checkNull() || mc.gameMode == null || !PlayerUtil.isInSurvival(mc.player))
        {
            return;
        }

        if (mc.gameMode.isDestroying())
        {
            ItemSlot blockSlot = getBestTool(((IMultiPlayerGameMode) mc.gameMode).shoreline$getDestroyBlockPos());
            int holding = mc.player.getInventory().getSelectedSlot();
            if (blockSlot != null && blockSlot.getSlot() != holding)
            {
                prevSlot = holding;
                mc.player.getInventory().setSelectedSlot(blockSlot.getSlot());
            }
        }
        else if (swapBack.getValue() && prevSlot != -1)
        {
            mc.player.getInventory().setSelectedSlot(prevSlot);
            prevSlot = -1;
        }
    }

    public ItemSlot getBestTool(BlockPos breakingPos)
    {
        final BlockState state = mc.level.getBlockState(breakingPos);
        if (state.is(Blocks.COBWEB))
        {
            for (int i = 0; i < 9; i++)
            {
                final ItemStack stack = mc.player.getInventory().getItem(i);
                if (stack.isEmpty() || !stack.is(ItemTags.SWORDS))
                {
                    continue;
                }

                return new ItemSlot(i, stack);
            }
        }

        int slot = -1;
        ItemStack toolStack = null;

        float bestTool = 0.0f;
        for (int i = 0; i < 9; i++)
        {
            final ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.isEmpty() || !ItemUtil.isTool(stack))
            {
                continue;
            }

            float speed = stack.getDestroySpeed(state);
            final int efficiency = EnchantUtil.getLevel(Enchantments.EFFICIENCY, stack);
            if (efficiency > 0)
            {
                speed += efficiency * efficiency + 1.0f;
            }

            if (speed > bestTool)
            {
                bestTool = speed;
                toolStack = stack.copy();
                slot = i;
            }
        }

        if (slot == -1 || toolStack == null)
        {
            return null;
        }

        return new ItemSlot(slot, toolStack);
    }
}
