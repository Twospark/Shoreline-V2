package net.shoreline.client.util.item;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.shoreline.client.api.interfaces.Globals;

@UtilityClass
public class EnchantUtil implements Globals
{
    public int getLevel(ResourceKey<Enchantment> key, ItemStack stack)
    {
        for (Object2IntMap.Entry<Holder<Enchantment>> ench :
                stack.getEnchantments().entrySet())
        {
            if (ench.getKey().is(key))
            {
                return ench.getIntValue();
            }
        }

        return 0;
    }
}
