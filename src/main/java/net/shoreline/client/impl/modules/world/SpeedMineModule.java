package net.shoreline.client.impl.modules.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.*;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.LevelEvent;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.network.AttackBlockEvent;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.impl.inventory.ItemSlot;
import net.shoreline.client.impl.inventory.SilentSwapType;
import net.shoreline.client.impl.mining.MiningData;
import net.shoreline.client.impl.mining.MiningPackets;
import net.shoreline.client.impl.mining.MiningUtil;
import net.shoreline.client.impl.modules.combat.AutoMineModule;
import net.shoreline.client.impl.render.BoxRender;
import net.shoreline.client.impl.render.animation.Animation;
import net.shoreline.client.impl.render.animation.Easing;
import net.shoreline.client.util.entity.PlayerUtil;
import net.shoreline.eventbus.api.Subscribe;

import java.awt.*;

@Getter
public class SpeedMineModule extends Toggleable
{
    public static SpeedMineModule INSTANCE;

    Setting<MiningPackets> miningPackets = new EnumSetting.Builder<MiningPackets>("Mode")
            .setDescription("The mode for block click packets")
            .setDefaultValue(MiningPackets.NORMAL).build();
    Setting<Boolean> doubleMine = new BooleanSetting.Builder("DoubleMine")
            .setDescription("Rotates before mining block")
            .setDefaultValue(false).build();
    Setting<RemineMode> remineMode = new EnumSetting.Builder<RemineMode>("Remine")
            .setDescription("The mode for remining mined blocks")
            .setDefaultValue(RemineMode.OFF).build();
    Setting<Integer> instantLimit = new NumberSetting.Builder<Integer>("Limit")
            .setMin(1).setMax(20).setDefaultValue(10)
            .setDescription("Maximum mines per interval")
            .setVisible(() -> remineMode.getValue() == RemineMode.INSTANT).build();
    Setting<Integer> limitInterval = new NumberSetting.Builder<Integer>("Interval")
            .setMin(25).setMax(1000).setDefaultValue(100).setFormat("ms")
            .setDescription("The interval for the limit")
            .setVisible(() -> remineMode.getValue() == RemineMode.INSTANT).build();
    Setting<Float> rangeConfig = new NumberSetting.Builder<Float>("Range")
            .setMin(1.0f).setMax(6.0f).setDefaultValue(4.0f).setFormat("m")
            .setDescription("The max range to mine").build();
    Setting<Float> speedConfig = new NumberSetting.Builder<Float>("Speed")
            .setMin(0.5f).setMax(1.0f).setDefaultValue(1.0f)
            .setDescription("The mining progress before breaking").build();
    Setting<Boolean> multitaskConfig = new BooleanSetting.Builder("Multitask")
            .setDescription("Allows using items while mining")
            .setDefaultValue(true).build();
    Setting<Boolean> rotateConfig = new BooleanSetting.Builder("Rotate")
            .setDescription("Rotates before mining block")
            .setDefaultValue(false).build();
    Setting<SilentSwapType> swapType = new EnumSetting.Builder<SilentSwapType>("Swap")
            .setDescription("The silent swap type")
            .setDefaultValue(SilentSwapType.HOTBAR).build();
    Setting<Boolean> autoDisable = new BooleanSetting.Builder("AutoDisable")
            .setDescription("Disables on death to prevent double mine failing")
            .setDefaultValue(true).build();

    Setting<BoxRender> boxMode = new EnumSetting.Builder<BoxRender>("BoxMode")
            .setDescription("Box rendering mode")
            .setDefaultValue(BoxRender.FILL).build();
    Setting<Color> miningColor = new ColorSetting.Builder("Mining")
            .setDescription("The color when mining a block")
            .setDefaultValue(Color.RED).build();
    Setting<Color> breakingColor = new ColorSetting.Builder("Breaking")
            .setDescription("The color when breaking a block")
            .setDefaultValue(Color.GREEN).build();
    Setting<Void> renderConfig = new SettingGroup.Builder("Render")
            .addAll(boxMode, miningColor, breakingColor).build();

    @Setter
    private boolean isManualMining;

    private MiningData mainMiningBlock, packetMiningBlock;
    private MiningRenderState mainState, packetState;

    private MiningData pendingClear;

    private long instantStartMs;
    private int instantCount;

    public SpeedMineModule()
    {
        super("SpeedMine", new String[] {"SpeedyGonzales"}, "Mine faster", Category.WORLD);
        INSTANCE = this;
    }

    @Override
    public void onDisable()
    {
        if (checkNull())
        {
            return;
        }

        clearMain();
        pendingClear = null;
        instantStartMs = 0L;
        instantCount = 0;
    }

    @Override
    public String getDisplayInfo()
    {
        return mainMiningBlock != null ? (mainMiningBlock.isDoneMining() ? ChatFormatting.GREEN : ChatFormatting.WHITE) +
                String.format("%.1f", Math.min(mainMiningBlock.getBlockDamage(), 1.0f)) : super.getDisplayInfo();
    }

    @Subscribe
    public void onDisconnect(LevelEvent.Disconnect event)
    {
        if (autoDisable.getValue())
        {
            disable();
        }

        clearPacket();
        clearMain();
        pendingClear = null;
        instantStartMs = 0L;
        instantCount = 0;
    }

    @Subscribe
    public void onTick(TickEvent event)
    {
        if (checkNull())
        {
            return;
        }

        if (isEnabled())
        {
            tickMain();
        }

        tickPacket();
        if (isManualMining && mainMiningBlock == null)
        {
            isManualMining = false;
        }
    }

    @Subscribe
    public void onAttackBlock(AttackBlockEvent event)
    {
        if (checkNull() || !PlayerUtil.isInSurvival(mc.player))
        {
            return;
        }

        event.setCanceled(true  );
        if (isMining(event.getPos()) || !MiningUtil.canMineBlock(event.getState()))
        {
            return;
        }

        isManualMining = true;
        startMining(event.getPos(), event.getDirection());
        mc.player.swing(InteractionHand.MAIN_HAND, false);
    }

    @Subscribe
    public void onRenderWorld(RenderWorldEvent event)
    {
        if (mainState != null)
        {
            if (mainState.getAnimation().getFactor() < 0.01f)
            {
                mainState = null;
                return;
            }

            mainState.data.render(event.getRenderer(),
                    event.getPartialTicks(),
                    boxMode.getValue(),
                    miningColor.getValue().getRGB(),
                    breakingColor.getValue().getRGB(),
                    (float) Easing.SMOOTH.ease(mainState.getAnimation().getFactor()),
                    speedConfig.getValue());
        }

        if (packetState != null)
        {
            if (packetState.getAnimation().getFactor() < 0.01f)
            {
                packetState = null;
                return;
            }

            packetState.data.render(event.getRenderer(),
                    event.getPartialTicks(),
                    boxMode.getValue(),
                    miningColor.getValue().getRGB(),
                    breakingColor.getValue().getRGB(),
                    (float) Easing.SMOOTH.ease(packetState.getAnimation().getFactor()), 1.0f);
        }
    }

    public void startMining(BlockPos blockPos, Direction direction)
    {
        float oldDamage = -1.0f;
        if (doubleMine.getValue())
        {
            if (pendingClear != null)
            {
                if (pendingClear.equals(mainMiningBlock) && mainMiningBlock.getBlockDamage() < speedConfig.getValue())
                {
                    clearMain();
                    if (pendingClear.getBlockPos().equals(blockPos))
                    {
                        oldDamage = pendingClear.getBlockDamage();
                    }
                }

                pendingClear = null;
            }

            if (mainMiningBlock != null && !mainMiningBlock.isBlockMined())
            {
                if (packetMiningBlock == null || packetMiningBlock.isBlockMined())
                {
                    packetMiningBlock = mainMiningBlock.copy(1.0f);
                    packetState = new MiningRenderState(packetMiningBlock, new Animation(true, 300));
                }
            }
        }

        ItemSlot slot = AutoToolModule.INSTANCE.getBestTool(blockPos);

        mainMiningBlock = MiningData.builder()
                .blockPos(blockPos)
                .direction(direction)
                .maxProgress(speedConfig.getValue())
                .player(mc.player)
                .miningStack(slot == null ? mc.player.getMainHandItem() : slot.getItemStack())
                .build();

        if (oldDamage > 0.0f)
        {
            mainMiningBlock.setBlockDamage(oldDamage);
        }

        mainState = new MiningRenderState(mainMiningBlock, new Animation(true, 300));
        miningPackets.getValue().sendStartPackets(this,
                mainMiningBlock.getBlockPos(),
                mainMiningBlock.getDirection());

        mainMiningBlock.setStarted(true);
    }

    private boolean tryConsumeInstantBudget()
    {
        if (remineMode.getValue() != RemineMode.INSTANT)
        {
            return true;
        }

        long now = System.currentTimeMillis();
        int windowMs = limitInterval.getValue();
        if (windowMs <= 0)
        {
            windowMs = 100;
        }

        if (instantStartMs == 0L || now - instantStartMs >= (long) windowMs)
        {
            instantStartMs = now;
            instantCount = 0;
        }

        int limit = instantLimit.getValue();
        if (limit < 1)
        {
            limit = 1;
        }

        return instantCount < limit;
    }

    private void tickMain()
    {
        if (mainMiningBlock == null)
        {
            return;
        }

        if (mainMiningBlock.getSquaredDistanceTo() > Mth.square(rangeConfig.getValue()))
        {
            clearMain();
            return;
        }

        if (!mainMiningBlock.isStarted() && !mainMiningBlock.isAir() && remineMode.getValue() == RemineMode.FAST)
        {
            miningPackets.getValue().sendStartPackets(this,
                    mainMiningBlock.getBlockPos(),
                    mainMiningBlock.getDirection());

            mainMiningBlock.setStarted(true);
        }

        boolean multiTasking = mc.player.isUsingItem() && !multitaskConfig.getValue();
        float blockDamage = mainMiningBlock.tickDelta(multiTasking);
        if (blockDamage < speedConfig.getValue())
        {
            return;
        }

        if (mainMiningBlock.isAir())
        {
            mainMiningBlock.resetTicksMining();

            if (isManualMining)
            {
                isManualMining = false;
            }

            if (remineMode.getValue() != RemineMode.INSTANT)
            {
                mainMiningBlock.setBlockDamage(0.0f);
                mainMiningBlock.setLastDamage(0.0f);
                mainMiningBlock.setStarted(false);
                return;
            }

            if (!tryConsumeInstantBudget())
            {
                return;
            }

        }
        else if (mainMiningBlock.hasMinedFor(30))
        {
            clearMain();
            return;
        }

        if (multiTasking)
        {
            return;
        }

        ItemSlot bestTool = AutoToolModule.INSTANCE.getBestTool(mainMiningBlock.getBlockPos());
        if (bestTool != null && !Managers.INVENTORY.startSwap(bestTool.getSlot(), swapType.getValue()))
        {
            return;
        }

        instantCount++;
        miningPackets.getValue().sendStopPackets(this,
                mainMiningBlock.getBlockPos(),
                mainMiningBlock.getDirection());

        Managers.INVENTORY.endSwap(swapType.getValue());

        if (remineMode.getValue() == RemineMode.OFF)
        {
            clearMain();
        }
    }

    private void tickPacket()
    {
        if (packetMiningBlock == null)
        {
            return;
        }

        boolean multiTasking = mc.player.isUsingItem() && !multitaskConfig.getValue();
        float blockDamage = packetMiningBlock.tickDelta(multiTasking);
        if (blockDamage < speedConfig.getValue())
        {
            return;
        }

        if (multiTasking)
        {
            return;
        }

        if (packetMiningBlock.isBlockMined() || packetMiningBlock.hasMinedFor(30))
        {
            if (mainMiningBlock != null && mainMiningBlock.getBlockDamage() < speedConfig.getValue())
            {
                pendingClear = mainMiningBlock;
            }

            clearPacket();
        }
        else
        {
            ItemSlot bestTool = AutoToolModule.INSTANCE.getBestTool(packetMiningBlock.getBlockPos());
            if (bestTool == null)
            {
                return;
            }

            Managers.INVENTORY.startMultitickSwap(bestTool.getSlot());
        }
    }

    private void clearMain()
    {
        if (mainMiningBlock != null)
        {
            if (!mainMiningBlock.isDoneMining())
            {
                mainMiningBlock.abort(this);
            }

            mainMiningBlock = null;
        }

        if (mainState != null)
        {
            mainState.getAnimation().setState(false);
        }
    }

    private void clearPacket()
    {
        if (packetMiningBlock != null)
        {
            Managers.INVENTORY.endMultitickSwap();
            packetMiningBlock = null;
        }

        if (packetState != null)
        {
            packetState.getAnimation().setState(false);
        }
    }

    public boolean hasFreeMine()
    {
        return mainMiningBlock == null || packetMiningBlock == null;
    }

    public boolean isMining(BlockPos blockPos)
    {
        return mainMiningBlock != null && mainMiningBlock.getBlockPos().equals(blockPos)
                || packetMiningBlock != null && packetMiningBlock.getBlockPos().equals(blockPos);
    }

    public boolean isUsedByAutoMine()
    {
        return AutoMineModule.INSTANCE.isEnabled() && isEnabled();
    }

    @Getter
    @RequiredArgsConstructor
    private static class MiningRenderState
    {
        private final MiningData data;
        private final Animation animation;
    }

    private enum RemineMode
    {
        INSTANT, FAST, OFF
    }
}
