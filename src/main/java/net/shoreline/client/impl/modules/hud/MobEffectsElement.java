package net.shoreline.client.impl.modules.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.shoreline.client.api.element.dynamic.DynamicElement;
import net.shoreline.client.api.element.dynamic.DynamicEntry;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.EnumSetting;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.modules.client.ThemeModule;
import net.shoreline.client.impl.render.ColorUtil;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MobEffectsElement extends DynamicElement
{
    Setting<PotionColors> potionColors = new EnumSetting.Builder<PotionColors>("Color")
            .setDescription("The potion colors in hud")
            .setDefaultValue(PotionColors.DEFAULT).build();
    Setting<Boolean> potionIcons = new BooleanSetting.Builder("Icons")
            .setDescription("Shows the potion icon")
            .setDefaultValue(false).build();

    private final Map<MobEffect, String> nameMap = new HashMap<>();

    public MobEffectsElement()
    {
        super("MobEffects", "Displays the mob effects the local player has", 80, 80);
    }

    @Override
    public void loadEntries()
    {
        for (MobEffect effect : BuiltInRegistries.MOB_EFFECT)
        {
            Holder<MobEffect> entry = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
            getEntries().add(new DynamicPotionEntry(
                    this,
                    Gui.getMobEffectSprite(entry),
                    () -> decorate(entry, effect),
                    () -> mc.player != null && mc.player.hasEffect(entry),
                    () -> getPotionColor(entry, effect)));
        }
    }

    public String decorate(Holder<MobEffect> entry, MobEffect effect)
    {
        if (mc.player.getEffect(entry) != null)
        {
            MobEffectInstance instance = mc.player.getEffect(entry);
            String decorated = effect.getDisplayName().getString() + (instance.getAmplifier() > 0
                    ? " " + (instance.getAmplifier() + 1)
                    : "") + " " + ChatFormatting.WHITE + getPotionDuration(instance);
            nameMap.put(effect, decorated);
            return decorated;
        }

        String str = nameMap.get(effect);
        return str == null ? effect.getDisplayName().getString() : str;
    }

    private String getPotionDuration(MobEffectInstance instance)
    {
        if (instance.isInfiniteDuration())
        {
            return "*:*";
        }
        else
        {
            int duration = instance.getDuration();
            int mins = duration / 1200;
            int sec = (duration % 1200) / 20;
            return mins + ":" + (sec < 10 ? "0" + sec : sec);
        }
    }

    public int getPotionColor(Holder<MobEffect> entry, MobEffect effect)
    {
        if (potionColors.getValue() == PotionColors.DEFAULT)
        {
            return effect.getColor();
        }
        else if (potionColors.getValue() == PotionColors.THEME)
        {
            return ThemeModule.INSTANCE.getPrimary();
        }

        String id = entry.getRegisteredName();
        return switch (id.replace("minecraft:", ""))
        {
            case "speed" -> 8171462;
            case "slowness" -> 5926017;
            case "haste" -> 14270531;
            case "mining_fatigue" -> 4866583;
            case "strength" -> 9643043;
            case "instant_health" -> 16262179;
            case "instant_damage" -> 4393481;
            case "jump_boost" -> 2293580;
            case "nausea" -> 5578058;
            case "regeneration" -> 13458603;
            case "resistance" -> 10044730;
            case "fire_resistance" -> 14981690;
            case "water_breathing" -> 3035801;
            case "invisibility" -> 8356754;
            case "blindness" -> 2039587;
            case "night_vision" -> 2039713;
            case "hunger" -> 5797459;
            case "weakness" -> 4738376;
            case "poison" -> 5149489;
            case "wither" -> 3484199;
            case "health_boost" -> 16284963;
            case "absorption" -> 2445989;
            case "saturation" -> 16262179;
            case "glowing" -> 9740385;
            case "levitation" -> 13565951;
            case "luck" -> 3381504;
            case "unluck" -> 12624973;
            default -> effect.getColor();
        };
    }

    private class DynamicPotionEntry extends DynamicEntry
    {
        private final Identifier sprite;
        private final Supplier<Integer> color;

        public DynamicPotionEntry(DynamicElement mod, Identifier sprite, Supplier<String> text, Supplier<Boolean> drawing, Supplier<Integer> color)
        {
            super(mod, text, drawing);
            this.sprite = sprite;
            this.color = color;
        }

        @Override
        public void drawText(GuiGraphicsExtractor graphics, String string, float x, float y)
        {
            if (sprite != null && potionIcons.getValue())
            {
                float xOffset = isLeft() ? Managers.TEXT.getWidth(string) : -12.5f;
                Matrix3x2fStack matrices = graphics.pose();
                matrices.pushMatrix();
                matrices.translate(x + xOffset, y - 2.5f);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, 0, 0, 11, 11);
                matrices.popMatrix();
            }

            int c = ColorUtil.withTransparency(color.get(), 1.0f).getRGB();
            getElement().drawTextTransparency(graphics, string, x, y, c, (float) yAnimation.getFactor());
        }
    }

    private enum PotionColors
    {
        DEFAULT, OLD, THEME
    }
}
