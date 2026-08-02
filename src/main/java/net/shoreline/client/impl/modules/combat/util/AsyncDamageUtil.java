package net.shoreline.client.impl.modules.combat.util;

import lombok.experimental.UtilityClass;
import net.shoreline.client.impl.level.entity.state.LivingEntityState;

@UtilityClass
public class AsyncDamageUtil
{
    private static final float ASSUMED_ARMOR_REDUCTION = 0.11f;

    public float getAssumedDamage(float baseDamage, LivingEntityState state)
    {
        return state.getArmor() > 0 ? baseDamage * ASSUMED_ARMOR_REDUCTION : baseDamage;
    }
}