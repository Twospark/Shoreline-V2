package net.shoreline.client.impl.mining;

import lombok.experimental.UtilityClass;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.shoreline.client.util.item.EnchantUtil;

@UtilityClass
public class MiningUtil
{
    public boolean isUnbreakable(BlockState state)
    {
        return state.getBlock().defaultDestroyTime() == -1.0f;
    }

    public boolean isEmpty(BlockState state)
    {
        return state.isAir() || !state.getFluidState().isEmpty();
    }

    public boolean canMineBlock(BlockState state)
    {
        return !isUnbreakable(state) && !isEmpty(state);
    }

    public boolean canHarvest(ItemStack miningStack, BlockState state)
    {
        return !state.requiresCorrectToolForDrops() || miningStack.isCorrectToolForDrops(state);
    }

    public float getBlockBreakingSpeed(Player player, ItemStack miningStack, BlockState block)
    {
        float f = miningStack.getDestroySpeed(block);
        if (f > 1.0f)
        {
            int lvl = EnchantUtil.getLevel(Enchantments.EFFICIENCY, miningStack);
            f += (float) lvl * lvl;
        }

        if (MobEffectUtil.hasDigSpeed(player))
        {
            f *= 1.0f + (float) (MobEffectUtil.getDigSpeedAmplification(player) + 1) * 0.2f;
        }

        if (player.hasEffect(MobEffects.MINING_FATIGUE))
        {
            float g = switch (player.getEffect(MobEffects.MINING_FATIGUE).getAmplifier())
            {
                case 0 -> 0.3f;
                case 1 -> 0.09f;
                case 2 -> 0.0027f;
                default -> 8.1E-4f;
            };

            f *= g;
        }

        f *= (float) player.getAttributeValue(Attributes.BLOCK_BREAK_SPEED);
        if (!player.onGround())
        {
            f /= 5.0f;
        }

        return f;
    }
}