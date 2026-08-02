package net.shoreline.client.impl.level.entity.state;

import lombok.Getter;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.shoreline.client.util.entity.DamageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class LivingEntityState extends EntityState
{
    private final float health;
    private final Item heldItem;
    private final float armor;
    private final List<ItemStack> armorItems;

    public LivingEntityState(LivingEntity entity)
    {
        super(entity);
        health = entity.getHealth() + entity.getAbsorptionAmount();
        heldItem = entity.getMainHandItem().getItem();
        armor = DamageUtil.getArmor(entity);
        armorItems = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlotGroup.ARMOR)
        {
            armorItems.add(entity.getItemBySlot(slot));
        }
    }
}
