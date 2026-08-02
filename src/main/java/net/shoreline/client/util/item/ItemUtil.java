package net.shoreline.client.util.item;

import lombok.experimental.UtilityClass;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

@UtilityClass
public class ItemUtil
{
    public boolean isTool(ItemStack stack)
    {
        return stack.is(ItemTags.SHOVELS)
                || stack.is(ItemTags.AXES)
                || stack.is(ItemTags.PICKAXES);
    }

    public int getDurability(ItemStack stack)
    {
        return stack.getMaxDamage() - stack.getDamageValue();
    }
}
