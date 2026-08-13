package net.shoreline.client.impl.modules.render;

import lombok.Getter;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.*;
import net.shoreline.client.impl.event.render.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
public class NoRenderModule extends Toggleable
{

    public static NoRenderModule INSTANCE;
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
    Setting<Integer> totemTicks = new NumberSetting.Builder<Integer>("TotemTicks")
            .setMin(5).setDefaultValue(30).setMax(30)
            .setVisible(() -> !totemEffects.getValue())
            .setDescription("The time in ticks that the totem effect will last").build();
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

    Setting<Collection<Block>> blockBlackListConfig = new RegistrySetting.Builder<Block>("Blacklist")
            .setValues(Blocks.CAVE_VINES, Blocks.CAVE_VINES_PLANT)
            .setRegistry(BuiltInRegistries.BLOCK)
            .setDescription("List of blocks that you dont want to render")
            .build();
    Setting<Boolean> blocksConfig = new ToggleableSettingGroup.Builder("Blocks")
            .add(blockBlackListConfig)
            .setDefaultValue(false).build();
    Setting<Boolean> blindnessConfig = new BooleanSetting.Builder("Blindness")
            .setDescription("Cancels the blindness effect")
            .setDefaultValue(false).build();
    Setting<Boolean> totemConfig = new BooleanSetting.Builder("Totem")
            .setDescription("Cancels the totem pop animation")
            .setDefaultValue(false).build();

    private final Set<ParticleType<?>> drippingParticles = new HashSet<>(Set.of
    (
        ParticleTypes.FALLING_OBSIDIAN_TEAR,
        ParticleTypes.DRIPPING_OBSIDIAN_TEAR,
        ParticleTypes.LANDING_OBSIDIAN_TEAR,
        ParticleTypes.FALLING_DRIPSTONE_WATER,
        ParticleTypes.DRIPPING_DRIPSTONE_WATER,
        ParticleTypes.FALLING_DRIPSTONE_LAVA,
        ParticleTypes.DRIPPING_DRIPSTONE_LAVA,
        ParticleTypes.FALLING_LAVA,
        ParticleTypes.DRIPPING_LAVA,
        ParticleTypes.FALLING_WATER,
        ParticleTypes.DRIPPING_WATER,
        ParticleTypes.FALLING_HONEY,
        ParticleTypes.DRIPPING_HONEY,
        ParticleTypes.FALLING_NECTAR
    ));

    public NoRenderModule()
    {
        super("NoRender", "Removes annoying overlays", Category.RENDER);
        INSTANCE = this;
    }

    public boolean shouldCancelParticle(ParticleType<?> type)
    {
        return type == ParticleTypes.ENTITY_EFFECT && statusEffectsConfig.getValue()
                || (type == ParticleTypes.EXPLOSION || type == ParticleTypes.EXPLOSION_EMITTER) && explosionsConfig.getValue()
                || type == ParticleTypes.FIREWORK && fireworkConfig.getValue()
                || (type == ParticleTypes.EFFECT || type == ParticleTypes.INSTANT_EFFECT) && splashConfig.getValue()
                || (type == ParticleTypes.PORTAL || type == ParticleTypes.REVERSE_PORTAL) && portalConfig.getValue()
                || type == ParticleTypes.BLOCK && walkingConfig.getValue()
                || type == ParticleTypes.ITEM && eatingConfig.getValue()
                || drippingParticles.contains(type) && drippingBlocksConfig.getValue();
    }
}
