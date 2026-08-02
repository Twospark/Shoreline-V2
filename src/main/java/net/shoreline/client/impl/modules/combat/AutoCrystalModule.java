package net.shoreline.client.impl.modules.combat;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.*;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.interact.PlaceInteraction;
import net.shoreline.client.impl.inventory.InventoryUtil;
import net.shoreline.client.impl.inventory.SilentSwapType;
import net.shoreline.client.impl.inventory.SwapHandler;
import net.shoreline.client.impl.level.entity.state.EntityState;
import net.shoreline.client.impl.level.entity.state.LivingEntityState;
import net.shoreline.client.impl.mining.MiningData;
import net.shoreline.client.impl.modules.combat.crystal.CrystalCalcManager;
import net.shoreline.client.impl.modules.combat.crystal.CrystalData;
import net.shoreline.client.impl.modules.combat.crystal.CrystalOptimizer;
import net.shoreline.client.impl.modules.impl.ObsidianPlacerModule;
import net.shoreline.client.impl.modules.impl.Priorities;
import net.shoreline.client.impl.modules.world.SpeedMineModule;
import net.shoreline.client.impl.network.NetworkUtil;
import net.shoreline.client.impl.rotation.RotationUtil;
import net.shoreline.client.impl.rotation.util.ClientRotationEvent;
import net.shoreline.client.impl.rotation.util.RotateMode;
import net.shoreline.client.impl.rotation.util.Rotation;
import net.shoreline.client.util.entity.DamageUtil;
import net.shoreline.client.impl.level.explosion.ExplosionUtil;
import net.shoreline.client.util.level.LevelUtil;
import net.shoreline.client.util.math.Timer;
import net.shoreline.eventbus.api.Subscribe;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class AutoCrystalModule extends ObsidianPlacerModule
{
    public static AutoCrystalModule INSTANCE;
    private static final AABB FULL_CRYSTAL_BB = new AABB(-0.5, 0.0, -0.5, 0.5, 2.0, 0.5);
    private static final AABB HALF_CRYSTAL_BB = new AABB(-0.5, 0.0, -0.5, 0.5, 1.0, 0.5);

    Setting<Boolean> multitaskConfig = new BooleanSetting.Builder("Multitask")
            .setDescription("Allows using items while interacting")
            .setDefaultValue(true).build();
    Setting<Boolean> swingConfig = new BooleanSetting.Builder("Swing")
            .setDescription("Swings the hand when attacking")
            .setDefaultValue(true).build();

    Setting<Float> targetRange = new NumberSetting.Builder<Float>("TargetRange")
            .setMin(1.0f).setMax(15.0f).setDefaultValue(10.0f).setFormat("m")
            .setDescription("The range to target entities").build();
    Setting<Integer> extrapolateTicks = new NumberSetting.Builder<Integer>("Extrapolate")
            .setMin(0).setDefaultValue(0).setMax(10).setFormat(" ticks")
            .setDescription("The number of ticks ahead to predict movement").build();
    Setting<Boolean> targetNakeds = new BooleanSetting.Builder("Nakeds")
            .setDescription("Targets nakeds").setVisible(targetPlayers::getValue).setDefaultValue(true).build();
    Setting<Void> targetConfig = new SettingGroup.Builder("Target")
            .addAll(targetRange, extrapolateTicks, targetPlayers, targetNakeds, targetHostiles, targetPassives).build();

    Setting<Float> breakRange = new NumberSetting.Builder<Float>("BreakRange")
            .setMin(1.0f).setMax(6.0f).setDefaultValue(4.0f).setFormat("m")
            .setDescription("The range to break crystals").build();
    Setting<Float> breakTrace = new NumberSetting.Builder<Float>("BreakTrace")
            .setMin(0.0f).setMax(6.0f).setDefaultValue(3.0f).setFormat("m")
            .setDescription("The range to break crystals through walls").build();
    Setting<Integer> breakDelay = new NumberSetting.Builder<Integer>("BreakDelay")
            .setMin(0).setMax(1000).setDefaultValue(100).setFormat("ms")
            .setDescription("The delay between breaking crystals").build();
    Setting<Integer> ticksExisted = new NumberSetting.Builder<Integer>("MinExisted")
            .setMin(0).setMax(10).setDefaultValue(0).setFormat(" ticks")
            .setDescription("The minimum ticks existed before breaking crystals").build();
    Setting<Void> breakConfig = new SettingGroup.Builder("Break")
            .addAll(breakRange, breakTrace, breakDelay, ticksExisted).build();

    Setting<Float> placeRange = new NumberSetting.Builder<Float>("PlaceRange")
            .setMin(1.0f).setMax(6.0f).setDefaultValue(4.0f).setFormat("m")
            .setDescription("The range to place crystals").build();
    Setting<Float> placeTrace = new NumberSetting.Builder<Float>("PlaceTrace")
            .setMin(0.0f).setMax(6.0f).setDefaultValue(3.0f).setFormat("m")
            .setDescription("The range to place crystals through walls").build();
    Setting<Integer> placeLimit = new NumberSetting.Builder<Integer>("PlaceLimit")
            .setMin(1).setMax(10).setDefaultValue(2)
            .setDescription("The limit of crystal placements per tick").build();
    Setting<Boolean> protocolPlace = new BooleanSetting.Builder("Protocol")
            .setDescription("Prevents placements in 1x1 areas")
            .setDefaultValue(false).build();
    Setting<Boolean> basePlace = new BooleanSetting.Builder("Support")
            .setDescription("Places an obsidian block if there is none")
            .setDefaultValue(false).build();
    Setting<Void> placeConfig = new SettingGroup.Builder("Place")
            .addAll(placeRange, placeTrace, placeLimit, protocolPlace, basePlace).build();

    Setting<Boolean> sequentialBreak = new BooleanSetting.Builder("InstantBreak")
            .setDescription("Breaks immediately after a placement")
            .setDefaultValue(false).build();
    Setting<Boolean> sequentialPlace = new BooleanSetting.Builder("InstantPlace")
            .setDescription("Places immediately after breaking a crystal")
            .setDefaultValue(false).build();
    Setting<Boolean> predictAttack = new BooleanSetting.Builder("PredictAttack")
            .setDescription("Attempts to predict the next attack (works better on low ping)")
            .setDefaultValue(false).build();
    Setting<Void> sequentialConfig = new SettingGroup.Builder("Sequential")
            .addAll(sequentialBreak, sequentialPlace, predictAttack).build();

    Setting<Timing> predictPlace = new EnumSetting.Builder<Timing>("PredictPlace")
            .setDescription("Attempts to predict the next place")
            .setDefaultValue(Timing.OFF).build();
    Setting<Boolean> cevBreak = new BooleanSetting.Builder("CevBreak")
            .setDescription("Targets crystal placements above the target")
            .setDefaultValue(false).build();
    Setting<Boolean> targetItems = new BooleanSetting.Builder("TargetItems")
            .setDescription("Targets dropped items blocking placements")
            .setDefaultValue(false).build();
    Setting<Integer> prePlace = new NumberSetting.Builder<Integer>("PrePlace")
            .setMin(0).setMax(10).setDefaultValue(5).setFormat(" ticks")
            .setDescription("Ticks before predicting placement")
            .setVisible(() -> targetItems.getValue()).build();
    Setting<Void> antiSurroundConfig = new SettingGroup.Builder("SurroundBreak")
            .addAll(predictPlace, cevBreak, targetItems, prePlace).build();

    Setting<Float> minDamage = new NumberSetting.Builder<Float>("MinDamage")
            .setMin(2.0f).setMax(10.0f).setDefaultValue(4.0f)
            .setDescription("The minimum damage to consider crystals").build();
    Setting<Float> maxSelfDamage = new NumberSetting.Builder<Float>("MaxSelfDamage")
            .setMin(2.0f).setMax(20.0f).setDefaultValue(12.0f)
            .setDescription("The maximum damage a crystal can do to the player").build();
    Setting<Boolean> overrideConfig = new BooleanSetting.Builder("Override")
            .setDescription("Allows overriding minimum damage (e.g. allows crystal spam)")
            .setDefaultValue(true).build();
    Setting<Float> armorMultiplier = new NumberSetting.Builder<Float>("ArmorMultiplier")
            .setMin(1.0f).setMax(5.0f).setDefaultValue(1.0f).setFormat("x")
            .setVisible(() -> overrideConfig.getValue())
            .setDescription("The minimum armor damage to consider spamming crystals").build();
    Setting<Float> damageMultiplier = new NumberSetting.Builder<Float>("DamageMultiplier")
            .setMin(1.0f).setMax(5.0f).setDefaultValue(1.0f).setFormat("x")
            .setVisible(() -> overrideConfig.getValue())
            .setDescription("Place if we can kill target in this many crystals").build();
    Setting<Boolean> ignoreTerrain = new BooleanSetting.Builder("IgnoreTerrain")
            .setDescription("Ignores explodable terrain during damage calculations")
            .setDefaultValue(false).build();
    Setting<Void> damageConfig = new SettingGroup.Builder("Damage")
            .addAll(minDamage, maxSelfDamage, overrideConfig, armorMultiplier,
                    damageMultiplier, ignoreTerrain).build();

    Setting<RotateMode> rotateConfig = new EnumSetting.Builder<RotateMode>("Rotate")
            .setDescription("Rotates to before interacting")
            .setDefaultValue(RotateMode.NONE).build();

    Setting<Boolean> autoSwap = new BooleanSetting.Builder("AutoSwap")
            .setDescription("Automatically swaps to crystals before placing")
            .setDefaultValue(false).build();
    Setting<Boolean> swapBack = new BooleanSetting.Builder("SwapBack")
            .setVisibilityDependant(true)
            .setDescription("Swaps back to your previously held slot")
            .setVisible(() -> autoSwap.getValue())
            .setDefaultValue(false).build();
    Setting<Boolean> silentSwap = new BooleanSetting.Builder("SilentSwap")
            .setVisibilityDependant(true)
            .setDescription("Silently swaps to crystals before placing")
            .setVisible(() -> autoSwap.getValue())
            .setDefaultValue(false).build();
    Setting<Boolean> antiWeakness = new BooleanSetting.Builder("AntiWeakness")
            .setVisibilityDependant(true)
            .setDescription("Swaps to sword before attacking crystals")
            .setVisible(() -> autoSwap.getValue() && silentSwap.getValue())
            .setDefaultValue(false).build();
    Setting<SilentSwapType> silentType = new EnumSetting.Builder<SilentSwapType>("Swap")
            .setDescription("The silent swap type")
            .setVisible(() -> autoSwap.getValue() && silentSwap.getValue())
            .setDefaultValue(SilentSwapType.HOTBAR).build();
    Setting<Void> swapConfig = new SettingGroup.Builder("Swap")
            .addAll(autoSwap, silentSwap, antiWeakness, silentType).build();

    private final ConcurrentMap<PlaceInteraction, Long> placePackets = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Long> attackPackets = new ConcurrentHashMap<>();
    private final AtomicInteger crystalsPlaced = new AtomicInteger();

    private final CrystalCalcManager crystalCalc;
    private final CrystalOptimizer optimizer;

    private final SwapHandler swapHandler = new SwapHandler();
    private final Timer attackTimer = new Timer();
    private final Timer placeTimer = new Timer();

    private CrystalData<BlockPos> currentPlace;
    private CrystalData<EntityState> currentAttack;

    private boolean silentRotated;

    public AutoCrystalModule()
    {
        super("AutoCrystal", "Best CA on the market", Category.COMBAT);
        this.crystalCalc = new CrystalCalcManager(this);
        this.optimizer = new CrystalOptimizer();
        INSTANCE = this;
    }

    @Override
    public void onDisable()
    {
        currentAttack = null;
        currentPlace = null;
        attackPackets.clear();
        placePackets.clear();
        silentRotated = false;
    }

    @Override
    public boolean checkNull()
    {
        return super.checkNull() || (mc.player.isUsingItem() && !multitaskConfig.getValue());
    }

    @Subscribe(priority = Priorities.AUTO_CRYSTAL)
    public void onTick(TickEvent event)
    {
        if (checkNull() || mc.player.isSpectator())
        {
            crystalCalc.cancelRun();
            currentAttack = null;
            currentPlace = null;
            return;
        }

        List<CrystalData<BlockPos>> latestCrystalBases = crystalCalc.getBaseResults();
        List<CrystalData<EntityState>> latestCrystalEntities = crystalCalc.getEntityResults();

        currentAttack = getBestCrystal(latestCrystalEntities);

        List<CrystalData<BlockPos>> placements = crystalCalc.getBasePlacements();
        currentPlace = getBestCrystal(basePlace.getValue() && placements.isEmpty() ? latestCrystalBases : placements);
    }

    @Subscribe
    public void onTick_Post(TickEvent.Post event)
    {
        if (checkNull())
        {
            return;
        }

        crystalsPlaced.set(0);
        crystalCalc.runCalc();
    }

    @Subscribe
    public void onClientRotation(ClientRotationEvent event)
    {
        float[] rotations = null;
        silentRotated = false;

        final InteractionHand hand = getCrystalHand();
        if (currentAttack != null)
        {
            rotations = runAttack(currentAttack, hand);
        }

        if (currentPlace != null)
        {
            rotations = runPlace(currentPlace, hand);
        } else if (SpeedMineModule.INSTANCE.isUsedByAutoMine() && predictPlace.getValue() != Timing.OFF)
        {
            MiningData currentMine = SpeedMineModule.INSTANCE.getMainMiningBlock();
            CrystalData.Immediate<BlockPos> prePlaceData = validateMiningData(currentMine);
            if (prePlaceData != null)
            {
                currentPlace = prePlaceData;
                rotations = runPlaceInternal(prePlaceData, hand);
            }
        }

        if (silentRotated)
        {
            Managers.ROTATION.resetSilentRotation();
            return;
        }

        if (rotations != null && rotateConfig.getValue() == RotateMode.NORMAL)
        {
            event.setCanceled(true  );
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
        }
    }

    private float[] runPlace(CrystalData<BlockPos> placement, InteractionHand hand)
    {
        BlockPos crystalPos = placement.getValue();
        if (basePlace.getValue() && !canUseOnBlock(crystalPos))
        {
            if (!runSingleObbyPlacement(crystalPos))
            {
                currentPlace = null;
                return null;
            }
        }

        return runPlaceInternal(placement, hand);
    }

    private float[] runPlaceInternal(CrystalData<BlockPos> placement, InteractionHand hand)
    {
        BlockPos crystalPos = placement.getValue();
        Vec3 crystalVec = crystalPos.getBottomCenter().add(0.0, 1.5, 0.0);
        float[] rotations = RotationUtil.getRotationsTo(mc.player.getEyePosition(), crystalVec);
        if (rotateConfig.getValue() == RotateMode.SILENT && !silentRotated)
        {
            Managers.ROTATION.setSilentRotation(new Rotation(rotations[0], rotations[1]));
            silentRotated = true;
        }

        placeCrystal(crystalPos, crystalVec, hand);
        return rotations;
    }

    private void placeCrystal(BlockPos blockPos, Vec3 crystalVec, InteractionHand hand)
    {
        int slot = InventoryUtil.getItemSlot(Items.END_CRYSTAL, silentType.getValue());
        if (slot == -1)
        {
            return;
        }

        if (crystalsPlaced.get() > placeLimit.getValue())
        {
            return;
        }

        if (currentAttack == null && crystalVec != null)
        {
            AABB placeArea = FULL_CRYSTAL_BB.move(crystalVec);
            List<EndCrystal> blocking = LevelUtil.collectEntitiesInBox(EndCrystal.class,
                    placeArea,
                    e -> ExplosionUtil.crystalDamageToEntity(mc.level, mc.player, crystalVec) <= maxSelfDamage.getValue());

            if (!blocking.isEmpty())
            {
                attackCrystal(blocking.getFirst().getId(), hand);
                attackTimer.reset();
            }
        }

        AABB baseBox = new AABB(blockPos);
        Vec3 eyePos = mc.player.getEyePosition();
        Vec3 cut = new Vec3(Mth.clamp(eyePos.x(), baseBox.minX, baseBox.maxX),
                Mth.clamp(eyePos.y(), baseBox.minY, baseBox.maxY),
                Mth.clamp(eyePos.z(), baseBox.minZ, baseBox.maxZ));

        Direction placeDir = getPlaceDirection(blockPos, baseBox, eyePos, cut);
        BlockHitResult result = new BlockHitResult(cut, placeDir, blockPos, baseBox.contains(eyePos));

        if (silentSwap.getValue())
        {
            if (!Managers.INVENTORY.startSwap(slot, silentType.getValue()))
            {
                return;
            }
        }
        else if (autoSwap.getValue())
        {
            swapHandler.handleSwaps();
            if (swapHandler.canAutoSwap())
            {
                Managers.INVENTORY.setSelectedSlot(slot);
            }
        }

        PlaceInteraction placeInteraction = PlaceInteraction.builder()
                .pos(blockPos)
                .direction(placeDir)
                .build();

        if (Managers.INVENTORY.isHolding(Items.END_CRYSTAL, hand))
        {
            sendSequencedPacket(id -> new ServerboundUseItemOnPacket(hand, result, id));
            if (swingConfig.getValue())
            {
                mc.player.swing(hand);
            }
            else
            {
                sendPacket(new ServerboundSwingPacket(hand));
            }
        }

        if (silentSwap.getValue())
        {
            Managers.INVENTORY.endSwap(silentType.getValue());
        }

        placePackets.put(placeInteraction, System.currentTimeMillis());
        crystalsPlaced.incrementAndGet();
    }

    private float[] runAttack(CrystalData<EntityState> attack, InteractionHand hand)
    {
        EntityState crystalState = attack.getValue();
        Vec3 crystalVec = crystalState.getPosition().add(0.0, 0.5, 0.0);
        float[] rotations = RotationUtil.getRotationsTo(mc.player.getEyePosition(), crystalVec);
        if (rotateConfig.getValue() == RotateMode.SILENT && !silentRotated)
        {
            Managers.ROTATION.setSilentRotation(new Rotation(rotations[0], rotations[1]));
            silentRotated = true;
        }

        if (breakDelay.getValue() == 0 || attackTimer.passed(breakDelay.getValue()))
        {
            attackCrystal(crystalState.getId(), hand);
            attackTimer.reset();
        }

        return rotations;
    }

    private void attackCrystal(int crystalId, InteractionHand hand)
    {
        MobEffectInstance weakness = mc.player.getEffect(MobEffects.WEAKNESS);
        MobEffectInstance strength = mc.player.getEffect(MobEffects.STRENGTH);

        boolean canBreakCrystal = weakness == null || (strength != null && strength.getAmplifier() >= weakness.getAmplifier());
        if (!canBreakCrystal && antiWeakness.getValue())
        {
            int slot = getAntiWeaknessSlot();
            if (slot == -1 || !Managers.INVENTORY.startSwap(slot, silentType.getValue()))
            {
                return;
            }
        }

        sendAttackPacketsInternal(crystalId, swingConfig.getValue(), hand);
        optimizer.setDead(crystalId);

        if (!canBreakCrystal)
        {
            Managers.INVENTORY.endSwap(silentType.getValue());
        }

        attackPackets.put(crystalId, System.currentTimeMillis());
    }

    private <T> CrystalData<T> getBestCrystal(List<CrystalData<T>> crystals)
    {
        CrystalData<T> bestCrystal = getBestCrystal(crystals, false);
        if (bestCrystal == null || bestCrystal.getDamageToTarget() < minDamage.getValue())
        {
            return getBestCrystal(crystals, true);
        }

        return bestCrystal;
    }

    public <T extends CrystalData<?>> T getBestCrystal(List<T> crystals, boolean onlyImmediate)
    {
        if (crystals.isEmpty())
        {
            return null;
        }

        T bestCrystal = null;
        double bestDamage = 0.0f;
        for (T data : crystals)
        {
            if (onlyImmediate && !(data instanceof CrystalData.Immediate<?>))
            {
                continue;
            }

            CrystalData<?> candidate = validateCrystalData((CrystalData<?>) data, bestDamage);
            if (candidate == null)
            {
                continue;
            }

            bestDamage = candidate.getDamageToTarget();
            bestCrystal = data;
        }

        return bestCrystal;
    }

    private <T> CrystalData<T> validateCrystalData(CrystalData<T> data, double currentBest)
    {
        LivingEntityState state = data.getTarget();
        if (state == null || state.isDead())
        {
            return null;
        }

        Entity entity = state.getEntity();
        if (!(entity instanceof LivingEntity target) || target.isDeadOrDying())
        {
            return null;
        }

        float baseDamage = (float) data.getDamageToTarget();
        float baseSelfDamage = (float) data.getDamageToPlayer();

        if (baseDamage <= 0.0f || baseSelfDamage > maxSelfDamage.getValue())
        {
            return null;
        }

        float selfDamage = ExplosionUtil.getAppliedDamageToEntity(mc.player, baseSelfDamage);
        if (selfDamage > maxSelfDamage.getValue() || DamageUtil.getHealth(mc.player) - selfDamage < 0.5f)
        {
            return null;
        }

        float targetDamage = ExplosionUtil.getAppliedDamageToEntity(target, baseDamage);
        if (targetDamage > currentBest)
        {
            data.setDamageToTarget(targetDamage);
            data.setDamageToPlayer(selfDamage);
            return data;
        }

        return null;
    }

    private CrystalData.Immediate<BlockPos> validateMiningData(MiningData currentMine)
    {
        if (currentMine == null || !currentMine.isDoneMining())
        {
            return null;
        }

        Player target = Managers.TARGETING.getTarget();
        if (target == null)
        {
            return null;
        }

        BlockPos minePos = currentMine.getBlockPos();
        BlockPos placePos = minePos.below();
        double dist = mc.player.distanceToSqr(placePos.getCenter());
        if (dist > Mth.square(placeRange.getValue()))
        {
            return null;
        }

        BlockState state = mc.level.getBlockState(placePos);
        if (!state.is(Blocks.OBSIDIAN) && !state.is(Blocks.BEDROCK))
        {
            return null;
        }

        if (hasEntityBlockingCrystal(getCrystalBox(minePos), true))
        {
            return null;
        }

        float selfDamage = (float) ExplosionUtil.crystalDamageToEntity(mc.level,
                mc.player,
                minePos.getBottomCenter(),
                true,
                Set.of(minePos));

        if (selfDamage > maxSelfDamage.getValue() || DamageUtil.getHealth(mc.player) - selfDamage < 0.5f)
        {
            return null;
        }

        float damage = (float) ExplosionUtil.crystalDamageToEntity(mc.level,
                target,
                minePos.getBottomCenter(),
                true,
                Set.of(minePos));

        if (damage < minDamage.getValue())
        {
            return null;
        }

        LivingEntityState targetState = new LivingEntityState(target);
        Vec3 crystalVec = minePos.getBottomCenter();
        return new CrystalData.Immediate<>("AS",
                placePos,
                crystalVec,
                targetState,
                damage,
                selfDamage);
    }

    private int getAntiWeaknessSlot()
    {
        return InventoryUtil.getItemSlot((ItemStack itemStack) ->
                itemStack.is(ItemTags.SWORDS) || itemStack.is(ItemTags.AXES))
                    .getSlot();
    }

    public boolean canUseOnBlock(BlockPos blockPos)
    {
        return canUseOnBlock(mc.level, blockPos) && !hasEntityBlockingCrystal(getCrystalBox(blockPos.above()), false);
    }

    public boolean canUseOnBlock(BlockGetter blockView, BlockPos pos)
    {
        BlockState state = blockView.getBlockState(pos);
        if (!state.is(Blocks.OBSIDIAN) && !state.is(Blocks.BEDROCK))
        {
            return false;
        }

        BlockPos p2 = pos.above();
        BlockState state2 = blockView.getBlockState(p2);
        if (protocolPlace.getValue() && !blockView.getBlockState(p2.above()).isAir())
        {
            return false;
        }

        return state2.isAir() || state2.is(Blocks.FIRE);
    }

    public boolean hasEntityBlockingCrystal(AABB box, boolean ignoreItems)
    {
        for (Entity entity : LevelUtil.collectEntitiesInBox(box))
        {
            if (!canIgnoreEntity(entity.getType(), ignoreItems))
            {
                return true;
            }
        }

        return false;
    }

    private Direction getPlaceDirection(BlockPos blockPos, AABB box, Vec3 eyePos, Vec3 cut)
    {
        if (eyePos.y >= box.maxY)
        {
            return Direction.UP;
        }
        else if (blockPos.getY() >= mc.level.getMaxSectionY())
        {
            return Direction.DOWN;
        }

        return Direction.getApproximateNearest(eyePos.x - cut.x, eyePos.y - cut.y, eyePos.z - cut.z);
    }

    public boolean canIgnoreEntity(EntityType<?> entity, boolean ignoreItems)
    {
        return entity == EntityType.EXPERIENCE_ORB || entity == EntityType.END_CRYSTAL || ignoreItems && entity == EntityType.ITEM;
    }

    public AABB getCrystalBox(BlockPos blockPos)
    {
        AABB crystalBB = NetworkUtil.getServerIp().contains("crystalpvp.cc") ? HALF_CRYSTAL_BB : FULL_CRYSTAL_BB;
        return crystalBB.move(blockPos.getBottomCenter());
    }

    private InteractionHand getCrystalHand()
    {
        ItemStack offhand = mc.player.getOffhandItem();
        if (offhand.getItem() instanceof EndCrystalItem)
        {
            return InteractionHand.OFF_HAND;
        }

        return InteractionHand.MAIN_HAND;
    }

    public boolean isRunning()
    {
        return isEnabled() && (currentAttack != null || currentPlace != null);
    }

    private enum Timing
    {
        TICK, INSTANT, OFF
    }
}
