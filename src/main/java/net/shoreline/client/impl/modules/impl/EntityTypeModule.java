package net.shoreline.client.impl.modules.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;

public class EntityTypeModule extends Toggleable
{
    public Setting<Boolean> targetPlayers = new BooleanSetting.Builder("Players")
            .setDescription("Target Players").setDefaultValue(true).build();
    public Setting<Boolean> targetHostiles = new BooleanSetting.Builder("Hostiles")
            .setDescription("Target Hostiles").setDefaultValue(true).build();
    public Setting<Boolean> targetPassives = new BooleanSetting.Builder("Passives")
            .setDescription("Target Passives").setDefaultValue(false).build();

    public EntityTypeModule(String name, String description, Category category)
    {
        super(name, description, category);
    }

    public EntityTypeModule(String name, String[] nameAliases, String description, Category category)
    {
        super(name, nameAliases, description, category);
    }

    public boolean isValid(Entity entity)
    {
        if (entity == null || entity == mc.player)
        {
            return false;
        }

        return switch (entity)
        {
            case Player player when targetPlayers.getValue() -> true;
            case Monster monster when targetHostiles.getValue() -> true;
            default -> entity instanceof Animal && targetPassives.getValue();
        };
    }
}
