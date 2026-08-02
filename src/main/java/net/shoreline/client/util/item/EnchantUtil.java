package net.shoreline.client.util.item;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.shoreline.client.api.interfaces.Globals;

import java.util.Set;

@UtilityClass
public class EnchantUtil implements Globals
{
    public Holder<Enchantment> getEntry(ResourceKey<Enchantment> key)
    {
        RegistryAccess.Frozen registryAccess = Minecraft.getInstance().getConnection().registryAccess();
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).get(key).orElse(null);
    }

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

    public boolean isEnchantsObfuscated(ItemStack itemStack)
    {
        Set<Object2IntMap.Entry<Holder<Enchantment>>> enchants =
                itemStack.getEnchantments().entrySet();

        if (enchants.size() > 1)
        {
            return false;
        }

        for (Object2IntMap.Entry<Holder<Enchantment>> e : enchants)
        {
            Holder<Enchantment> enchantment = e.getKey();
            int lvl = e.getIntValue();
            if (lvl == 0 && enchantment.unwrapKey().isPresent()
                    && enchantment.unwrapKey().get() == Enchantments.PROTECTION)
            {
                return true;
            }
        }
        return false;
    }
}