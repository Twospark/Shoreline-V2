package net.shoreline.client.impl.modules.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.api.setting.impl.RegistrySetting;

import java.util.Collection;

public class ScaffoldModule extends Toggleable
{
    Setting<Float> placeRange = new NumberSetting.Builder<Float>("Range")
            .setMin(1.0f).setMax(6.0f).setDefaultValue(4.0f).setFormat("m")
            .setDescription("Range to place blocks").build();
    Setting<Boolean> keepYConfig = new BooleanSetting.Builder("KeepY")
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
}
