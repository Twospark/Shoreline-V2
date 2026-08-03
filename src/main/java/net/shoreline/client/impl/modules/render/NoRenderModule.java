package net.shoreline.client.impl.modules.render;

import lombok.Getter;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.api.setting.impl.SettingGroup;

public class NoRenderModule extends Toggleable
{
    Setting<Boolean> hurtCam = new BooleanSetting.Builder("HurtCam")
            .setDescription("Cancels the camera shake when taking damage")
            .setDefaultValue(true).build();
    Setting<Boolean> armor = new BooleanSetting.Builder("Armor")
            .setDescription("Removes armor rendering")
            .setDefaultValue(false).build();
    Setting<Boolean> fireOverlay = new BooleanSetting.Builder("Fire")
            .setDescription("Cancels the burning screen overlay")
            .setDefaultValue(true).build();
    Setting<Boolean> waterOverlay = new BooleanSetting.Builder("Water")
            .setDescription("Cancels the water screen overlay")
            .setDefaultValue(true).build();
    Setting<Boolean> frostbiteOverlay = new BooleanSetting.Builder("Frostbite")
            .setDescription("Cancels the frostbite screen overlay")
            .setDefaultValue(true).build();
    Setting<Boolean> blockOverlay = new BooleanSetting.Builder("Blocks")
            .setDescription("Cancels the block screen overlay")
            .setDefaultValue(true).build();
    Setting<Boolean> spyglassOverlay = new BooleanSetting.Builder("Spyglass")
            .setDescription("Cancels the spyglass overlay")
            .setDefaultValue(false).build();
    Setting<Boolean> bossBarOverlay = new BooleanSetting.Builder("BossBar")
            .setDescription("Cancels the boss bar overlay")
            .setDefaultValue(false).build();
    Setting<Boolean> portalOverlay = new BooleanSetting.Builder("Portal")
            .setDescription("Cancels the portal screen overlay")
            .setDefaultValue(false).build();
    Setting<Void> overlayGroup = new SettingGroup.Builder("Overlays")
            .addAll(fireOverlay, waterOverlay, frostbiteOverlay, blockOverlay,
                    spyglassOverlay, bossBarOverlay, portalOverlay).build();

    Setting<Boolean> totemEffects = new BooleanSetting.Builder("TotemEffects")
            .setDescription("Cancels the totem effects when a player pops")
            .setDefaultValue(false).build();
    Setting<Integer> totemParticles = new NumberSetting.Builder<Integer>("TotemParticles")
            .setMin(3).setDefaultValue(16).setMax(16)
            .setVisible(() -> !totemEffects.getValue())
            .setDescription("The number of particles for the totem effect").build();
    Setting<Boolean> fireEffect = new BooleanSetting.Builder("EntityFire")
            .setDescription("Cancels the fire effect on entities")
            .setDefaultValue(false).build();
    Setting<Boolean> hurt = new BooleanSetting.Builder("Hurt")
            .setDescription("Cancels the red effect when you hurt a entity")
            .setDefaultValue(false).build();
    Setting<Void> effectsGroup = new SettingGroup.Builder("Effects")
            .addAll(totemEffects, fireEffect, hurt).build();

    Setting<Boolean> explosionsConfig = new BooleanSetting.Builder("Explosion")
            .setDescription("Cancels the explosion particles")
            .setDefaultValue(false).build();
    Setting<Boolean> statusEffectsConfig = new BooleanSetting.Builder("StatusEffect")
            .setDescription("Cancels the potion effect particles")
            .setDefaultValue(false).build();
    Setting<Boolean> fireworkConfig = new BooleanSetting.Builder("Firework")
            .setDescription("Cancels the firework particles")
            .setDefaultValue(false).build();
    Setting<Boolean> splashConfig = new BooleanSetting.Builder("BottleSplash")
            .setDescription("Cancels the bottle splash particles")
            .setDefaultValue(false).build();
    Setting<Boolean> portalConfig = new BooleanSetting.Builder("Portal")
            .setDescription("Cancels the portal particles")
            .setDefaultValue(false).build();
    Setting<Boolean> drippingBlocksConfig = new BooleanSetting.Builder("DrippingBlocks")
            .setDescription("Cancels the block dripping particles")
            .setDefaultValue(false).build();
    Setting<Boolean> walkingConfig = new BooleanSetting.Builder("Walking")
            .setDescription("Cancels the walking particles")
            .setDefaultValue(false).build();
    Setting<Boolean> eatingConfig = new BooleanSetting.Builder("Eating")
            .setDescription("Cancels the eating particles")
            .setDefaultValue(false).build();
    Setting<Boolean> breakingConfig = new BooleanSetting.Builder("Breaking")
            .setDescription("Cancels the block breaking particles")
            .setDefaultValue(false).build();
    Setting<Void> particlesConfig = new SettingGroup.Builder("Particles")
            .addAll(explosionsConfig, statusEffectsConfig, fireworkConfig, splashConfig, portalConfig,
                    drippingBlocksConfig, walkingConfig, eatingConfig, breakingConfig).build();

    @Getter
    Setting<Boolean> potionsHud = new BooleanSetting.Builder("PotionEffects")
            .setDescription("Cancels the status effects hud element")
            .setDefaultValue(false).build();
    Setting<Boolean> itemName = new BooleanSetting.Builder("ItemName")
            .setDescription("Cancels the item name hud element")
            .setDefaultValue(false).build();
    Setting<Boolean> toastConfig = new BooleanSetting.Builder("Toast")
            .setDescription("Cancels the toast hud element")
            .setDefaultValue(true).build();
    Setting<Boolean> textShadow = new BooleanSetting.Builder("TextShadow")
            .setDescription("Reduces the vanilla text shadow")
            .setDefaultValue(false).build();
    Setting<Void> hudConfig = new SettingGroup.Builder("HUD")
            .addAll(potionsHud, itemName, toastConfig, textShadow).build();

    Setting<Boolean> nauseaConfig = new BooleanSetting.Builder("Nausea")
            .setDescription("Cancels the nausea effect")
            .setDefaultValue(false).build();
    Setting<Boolean> blindnessConfig = new BooleanSetting.Builder("Blindness")
            .setDescription("Cancels the blindness effect")
            .setDefaultValue(false).build();
    Setting<Boolean> totemConfig = new BooleanSetting.Builder("Totem")
            .setDescription("Cancels the totem pop animation")
            .setDefaultValue(false).build();

    public NoRenderModule()
    {
        super("NoRender", "Removes annoying overlays", Category.RENDER);
    }
}
