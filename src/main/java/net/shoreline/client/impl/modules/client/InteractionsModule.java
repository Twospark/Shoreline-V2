package net.shoreline.client.impl.modules.client;

import lombok.Getter;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Concurrent;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;

@Getter
public class InteractionsModule extends Concurrent
{
    public static InteractionsModule INSTANCE;

    Setting<Boolean> multiTask = new BooleanSetting.Builder("Multitask")
            .setDescription("Allow using items while interacting")
            .setDefaultValue(true).build();
    Setting<Boolean> interactRotate = new BooleanSetting.Builder("Rotate")
            .setDescription("Rotates to face before interacting")
            .setDefaultValue(false).build();
    Setting<Boolean> noGlitchBlocks = new BooleanSetting.Builder("NoGlitchBlocks")
            .setDescription("Only spawns blocks when server confirms")
            .setDefaultValue(true).build();
    Setting<Boolean> attackCrystals = new BooleanSetting.Builder("AttackCrystal")
            .setDescription("Attacks crystals blocking placements")
            .setDefaultValue(false).build();
    Setting<Boolean> strictDirection = new BooleanSetting.Builder("StrictDirection")
            .setDescription("Only places on visible faces")
            .setDefaultValue(false).build();
    Setting<Boolean> simulation = new BooleanSetting.Builder("Simulate")
            .setDescription("Simulates a block placement to prevent movement flags")
            .setDefaultValue(false).build();


    Setting<Integer> bpt = new NumberSetting.Builder<Integer>("InteractsPer")
            .setMin(1).setMax(100).setDefaultValue(2)
            .setDescription("The max interactions per interval").build();
    Setting<Boolean> intervalMode = new BooleanSetting.Builder("UseThreshold")
            .setDescription("Limits the placements by interval instead of tick")
            .setDefaultValue(false).build();
    Setting<Integer> interactInterval = new NumberSetting.Builder<Integer>("Interval")
            .setMin(50).setMax(1000).setDefaultValue(100).setFormat("ms")
            .setDescription("The interval between interactions")
            .setVisible(() -> intervalMode.getValue()).build();
    Setting<Integer> interactDelay = new NumberSetting.Builder<Integer>("Delay")
            .setMin(0).setMax(1000).setDefaultValue(100).setFormat("ms")
            .setDescription("The delay between interactions").build();
    Setting<Integer> interactAttempts = new NumberSetting.Builder<Integer>("Limit")
            .setMin(0).setMax(100).setDefaultValue(20)
            .setDescription("Max attempts to interact on blocks").build();

    public InteractionsModule()
    {
        super("Interactions", "Manages Shorelines internal InteractionManager", Category.CLIENT);
        INSTANCE = this;
    }
}
