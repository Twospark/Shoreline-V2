package net.shoreline.client.impl.modules.combat;

import lombok.Getter;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.*;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.LevelEvent;
import net.shoreline.client.impl.inventory.InventoryUtil;
import net.shoreline.client.impl.inventory.ItemSlot;
import net.shoreline.client.impl.inventory.SwapHandler;
import net.shoreline.client.impl.modules.impl.CombatModule;
import net.shoreline.client.impl.modules.impl.Priorities;
import net.shoreline.client.impl.render.animation.Animation;
import net.shoreline.client.impl.rotation.RotationUtil;
import net.shoreline.client.impl.rotation.util.ClientRotationEvent;
import net.shoreline.client.impl.rotation.util.RotateMode;
import net.shoreline.client.impl.rotation.util.Rotation;
import net.shoreline.client.util.item.EnchantUtil;
import net.shoreline.eventbus.api.Subscribe;

public class AuraModule extends CombatModule
{
    Setting<Float> attackRange = new NumberSetting.Builder<Float>("Range")
            .setDefaultValue(4.0f).setMin(0.5f).setMax(6.0f).setFormat("m")
            .setDescription("The range to attack entities").build();
    Setting<Float> delay = new NumberSetting.Builder<Float>("Delay")
            .setDefaultValue(1.0f).setMin(0.0f).setMax(1.0f).setRoundingScale(2)
            .setDescription("The delay between hits").build();
    Setting<Boolean> multitask = new BooleanSetting.Builder("Multitask")
            .setDescription("Allows you to use items while attacking")
            .setDefaultValue(true).build();
    Setting<Boolean> requireWeapon = new BooleanSetting.Builder("RequireWeapon")
            .setDescription("Must be holding a weapon to attack")
            .setDefaultValue(false).build();
    Setting<Boolean> awaitCrits = new BooleanSetting.Builder("AwaitCrits")
            .setDescription("Waits for a crit before attacking in the air")
            .setDefaultValue(true).build();
    Setting<Boolean> swing = new BooleanSetting.Builder("Swing")
            .setDescription("Swings the hand when attacking")
            .setDefaultValue(false).build();
    Setting<RotateMode> rotateMode = new EnumSetting.Builder<RotateMode>("Rotate")
            .setDescription("Rotates to the entity before attacking")
            .setDefaultValue(RotateMode.NONE).build();

    Setting<Void> targetGroup = new SettingGroup.Builder("Target")
            .addAll(targetPlayers, targetHostiles, targetPassives).build();

    Setting<Float> minBonusDamage = new NumberSetting.Builder<Float>("MinDamage")
            .setMin(1.0f).setMax(36.0f).setDefaultValue(4.0f)
            .setDescription("The minimum fall bonus damage before attacking").build();
    Setting<Boolean> maceBreach = new BooleanSetting.Builder("BreachSwap")
            .setDescription("Swaps to mace before attacking to apply breach effect")
            .setDefaultValue(false).build();
    Setting<Boolean> maceAura = new ToggleableSettingGroup.Builder("Mace")
            .addAll(minBonusDamage, maceBreach)
            .setDefaultValue(false)
            .setDescription("Automatically attacks with a mace")
            .build();

    Setting<Boolean> autoSwap = new BooleanSetting.Builder("AutoSwap")
            .setDescription("Automatically swaps to a weapon before attacking")
            .setDefaultValue(false).build();
    Setting<Boolean> silentSwap = new BooleanSetting.Builder("SilentSwap")
            .setVisibilityDependant(true)
            .setDescription("Swaps to a weapon silently")
            .setVisible(() -> autoSwap.getValue())
            .setDefaultValue(false).build();
    Setting<Boolean> swapBack = new BooleanSetting.Builder("SwapBack")
            .setVisibilityDependant(true)
            .setDescription("Swaps back after done")
            .setVisible(() -> autoSwap.getValue() && !silentSwap.getValue())
            .setDefaultValue(false).build();
    Setting<Void> swapGroup = new SettingGroup.Builder("Swap")
            .addAll(autoSwap, silentSwap, swapBack).build();

    private final SwapHandler swapHandler = new SwapHandler();
    private final Animation fade = new Animation(true, 300);

    @Getter
    private boolean running;
    private Entity auraTarget;
    private AABB targetBox;

    public AuraModule()
    {
        super("Aura", new String[]{"KillAura"}, "Automaticly attacks entities around you", Category.COMBAT);
    }

    @Override
    public void onDisable()
    {
        auraTarget = null;
        running = false;
    }

    @Subscribe
    public void onDisconnect(LevelEvent.Disconnect event)
    {
        disable();
    }

    @Subscribe(priority = Priorities.AURA)
    public void onClientRotation(ClientRotationEvent event)
    {
        running = false;
        if (checkNull() || event.isCanceled() || mc.player.isSpectator())
        {
            return;
        }

        if (mc.player.isUsingItem() && !multitask.getValue())
        {
            return;
        }

        auraTarget = getAuraTarget();
        if (auraTarget == null)
        {
            return;
        }

        ItemSlot weaponSlot = getAuraWeaponSlot();
        if (requireWeapon.getValue() && !Managers.INVENTORY.isHolding(weaponSlot.getItem(), InteractionHand.MAIN_HAND))
        {
            return;
        }

        float[] rotations = RotationUtil.getRotationsTo(mc.player.getEyePosition(), auraTarget.getEyePosition());
        Rotation rotation = new Rotation(rotations[0], rotations[1]);
        if (rotateMode.getValue() == RotateMode.NORMAL)
        {
            event.setCanceled(true);
            event.setYaw(rotation.getYaw());
            event.setPitch(rotation.getPitch());
        }
        else if (rotateMode.getValue() == RotateMode.SILENT)
        {
            Managers.ROTATION.setSilentRotation(rotation);
        }

        runAttack(auraTarget, weaponSlot);

        if (rotateMode.getValue() == RotateMode.SILENT)
        {
            Managers.ROTATION.resetSilentRotation();
        }
    }

    private void runAttack(final Entity entity, ItemSlot weaponSlot)
    {
        if (weaponSlot.getSlot() != -1)
        {
            if (silentSwap.getValue())
            {
                if (!Managers.INVENTORY.startSwap(weaponSlot.getSlot()))
                {
                    return;
                }
            }
            else if (autoSwap.getValue())
            {
                swapHandler.handleSwaps();
                if (swapHandler.canAutoSwap())
                {
                    Managers.INVENTORY.setSelectedSlot(weaponSlot.getSlot());
                }
            }
        }

        running = true;
        boolean passedDelay = mc.player.getAttackStrengthScale(0.5f) >= delay.getValue();

        boolean canCrit = !awaitCrits.getValue() || mc.player.onGround() || mc.player.getDeltaMovement().y < 0.0;
        if (passedDelay && canCrit)
        {
            attackEntity(entity);
        }

        if (silentSwap.getValue())
        {
            Managers.INVENTORY.endSwap();
        }
    }

    public void attackEntity(final Entity entity)
    {
        boolean sprinting = mc.player.isSprinting();
        if (sprinting)
        {
            sendPacket(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.STOP_SPRINTING));
        }

        sendAttackPackets(entity, swing.getValue());
        mc.player.resetOnlyAttackStrengthTicker();

        if (sprinting)
        {
            sendPacket(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.START_SPRINTING));
        }
    }

    private ItemSlot getAuraWeaponSlot()
    {
        float fallDist = Managers.FALL_DIST.getFallDistance();
        if (maceAura.getValue() && fallDist > 1.5f)
        {
            ItemSlot maceItem = InventoryUtil.getItem(Items.MACE);
            float damage = getMaceBonusDamage(fallDist, maceItem.getItemStack());
            if (damage >= minBonusDamage.getValue())
            {
                return maceItem;
            }
        }

        ItemSlot swordSlot = InventoryUtil.getItemSlot((ItemStack itemStack) -> itemStack.is(ItemTags.SWORDS));
        if (swordSlot.getSlot() != InventoryUtil.INVALID_SLOT)
        {
            return swordSlot;
        }

        ItemSlot axeSlot = InventoryUtil.getItemSlot((ItemStack itemStack) -> itemStack.is(ItemTags.AXES));
        if (axeSlot.getSlot() != InventoryUtil.INVALID_SLOT)
        {
            return axeSlot;
        }

        return new ItemSlot(mc.player.getInventory(), InventoryUtil.getItemSlot(Items.TRIDENT));
    }

    private Entity getAuraTarget()
    {
        Entity target = null;
        for (Entity entity : mc.level.entitiesForRendering())
        {
            if (entity.equals(mc.player) || !entity.isAlive() || !isValid(entity))
            {
                continue;
            }

            double dist = mc.player.distanceToSqr(entity);
            if (dist > Mth.square(attackRange.getValue()))
            {
                continue;
            }

            target = entity;
        }

        return target;
    }

    private float getMaceBonusDamage(float fallDistance, ItemStack itemStack)
    {
        int densityLevel = EnchantUtil.getLevel(Enchantments.DENSITY, itemStack);
        int h = (int) Math.floor(Math.max(0.0f, fallDistance));
        float i = h <= 3.0f ? 4.0f * h : (h <= 8.0f ? 12.0f + 2.0f * (h - 3.0f) : 22.0f + h - 8.0f);

        float damage = 6.0f + i;

        if (densityLevel > 0)
        {
            damage += 0.5f * densityLevel * h;
        }

        return damage;
    }
}
