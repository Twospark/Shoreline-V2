package net.shoreline.client.impl.interact;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.shoreline.client.Shoreline;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.impl.event.level.BlockCollisionEvent;
import net.shoreline.client.impl.inventory.SilentSwapType;
import net.shoreline.client.impl.modules.client.InteractionsModule;
import net.shoreline.client.impl.modules.combat.AuraModule;
import net.shoreline.client.impl.modules.world.AirPlaceModule;
import net.shoreline.client.impl.network.NetworkHandler;
import net.shoreline.client.impl.rotation.RotationUtil;
import net.shoreline.client.impl.rotation.util.Rotation;
import net.shoreline.client.util.level.BlockUtil;
import net.shoreline.client.util.level.LevelUtil;
import net.shoreline.eventbus.EventBus;
import net.shoreline.eventbus.api.Subscribe;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InteractionManager extends NetworkHandler
{
    private final InteractionsModule interactConfig = InteractionsModule.INSTANCE;
    private final AirPlaceModule airPlace = AirPlaceModule.INSTANCE;

    private final ConcurrentLinkedDeque<PlaceInteraction> placeInteractions = new ConcurrentLinkedDeque<>();
    private final ConcurrentMap<Entity, Integer> placedEntityIds = new ConcurrentHashMap<>();

    private boolean placementLock;

    private long limitWindowStartMs;
    private AtomicInteger limitWindowCount = new AtomicInteger();

    public InteractionManager()
    {
        super("Interactions");
        EventBus.getInstance().subscribe(this);
    }

    @Subscribe(priority = Integer.MIN_VALUE)
    public void onTickPost(TickEvent.Post event)
    {
        long now = System.currentTimeMillis();

        if (interactConfig.getIntervalMode().getValue())
        {
            if (now - limitWindowStartMs >= 100L)
            {
                limitWindowStartMs = now;
                limitWindowCount.set(0);
                PlaceInteraction.GLOBAL_COUNT.set(0);
                ItemInteraction.GLOBAL_COUNT.set(0);
            }
        }
        else
        {
            PlaceInteraction.GLOBAL_COUNT.set(0);
            ItemInteraction.GLOBAL_COUNT.set(0);
        }

        placeInteractions.removeIf(d -> now - d.getInteractionTime() > 1000);
    }

    @Subscribe
    public void onPacketInbound(PacketEvent.Receive<?> event)
    {
        if (checkNull())
        {
            return;
        }

        if (event.getPacket() instanceof ClientboundBlockUpdatePacket packet)
        {
            for (PlaceInteraction placeInteraction : placeInteractions)
            {
                if (!placeInteraction.getInteract().equals(packet.getPos()))
                {
                    continue;
                }

                placeInteraction.setStatus(
                        packet.getBlockState().is(placeInteraction.getBlock()) ?
                                InteractStatus.SERVER_CONFIRMED :
                                InteractStatus.SERVER_MISMATCH);
                break;
            }
        }
    }

    @Subscribe
    public void onBlockCollide(BlockCollisionEvent event)
    {
        if (checkNull() || !event.getState().isAir() || !interactConfig.getSimulation().getValue())
        {
            return;
        }

        for (PlaceInteraction placeInteraction : placeInteractions)
        {
            if (placeInteraction.getStatus() != InteractStatus.UNCONFIRMED || !placeInteraction.getInteract().equals(event.getBlockPos()))
            {
                continue;
            }

            VoxelShape collisionShape = placeInteraction.getBlock().defaultBlockState().getCollisionShape(mc.level, event.getBlockPos());
            event.setCanceled(true);
            event.setCollisionShape(collisionShape);
            return;
        }
    }

    private boolean tryConsumePaperLimit(int amount)
    {
        long now = System.currentTimeMillis();
        if (now - limitWindowStartMs >= 100L)
        {
            limitWindowStartMs = now;
            limitWindowCount.set(0);
            PlaceInteraction.GLOBAL_COUNT.set(0);
            ItemInteraction.GLOBAL_COUNT.set(0);
        }

        if (limitWindowCount.get() + amount > 8)
        {
            return false;
        }

        limitWindowCount.addAndGet(amount);
        return true;
    }

    public boolean placeBlock(PlaceInteraction placeInteraction)
    {
        final BlockPos blockPos = placeInteraction.getPos();
        if (!mc.level.isInsideBuildHeight(blockPos))
        {
            return false;
        }

        long now = System.currentTimeMillis();
        placeInteractions.removeIf(d -> now - d.getInteractionTime() > 1000L);

        if (check(blockPos) || isEntityBlocking(blockPos, placeInteraction.getBlock(), true))
        {
            return false;
        }

        boolean result = placeBlockInternal(placeInteraction);
        if (result)
        {
            placeInteractions.add(placeInteraction);
            PlaceInteraction.GLOBAL_COUNT.incrementAndGet();
        }

        return result;
    }

    public boolean check(BlockPos blockPos)
    {
        if (PlaceInteraction.GLOBAL_COUNT.get() > interactConfig.getBpt().getValue())
        {
            return true;
        }

        Optional<PlaceInteraction> interact = placeInteractions.stream()
                .filter(d -> d.getInteract().equals(blockPos))
                .findFirst();

        return interact.isPresent() && System.currentTimeMillis() - interact.get().getInteractionTime()
                < interactConfig.getInteractDelay().getValue();
    }

    public boolean canPlaceBlock(BlockPos blockPos, Block block)
    {
        return !isEntityBlocking(blockPos, block, false);
    }

    public boolean isEntityBlocking(BlockPos blockPos, Block block, boolean merge)
    {
        final BlockState state = block.defaultBlockState();
        final VoxelShape shape = state.getCollisionShape(mc.level, blockPos);
        if (shape.isEmpty())
        {
            return false;
        }

        final AABB boundingBox = shape.bounds();
        boolean attacked = false;

        for (Entity entity : LevelUtil.collectEntitiesInBox(shape.bounds()))
        {
            if (entity.isRemoved())
            {
                continue;
            }

            // changed this from shape collision check to just
            // a simple bb intersection check, don't think it
            // matters in most cases, and this is cheaper.
            if (!boundingBox.intersects(entity.getBoundingBox()))
            {
                continue;
            }

            if (entity instanceof EndCrystal && placedEntityIds.getOrDefault(entity, 0) <= interactConfig.getInteractAttempts().getValue())
            {
                if (merge)
                {
                    if (interactConfig.getAttackCrystals().getValue() && !attacked)
                    {
                        AuraModule.INSTANCE.sendAttackPackets(entity, false);
                        attacked = true;
                    }

                    placedEntityIds.merge(entity, 1, Integer::sum);
                }

                continue;
            }

            return true;
        }

        return false;
    }

    private boolean placeBlockInternal(PlaceInteraction placeInteraction)
    {
        Direction direction = placeInteraction.getDirection();
        boolean airPlacing = placeInteraction.getHand() == InteractionHand.MAIN_HAND && direction == null && airPlace.isEnabled() && !airPlace.isForceAirPlace();
        if (airPlacing)
        {
            direction = Direction.DOWN;
            placeInteraction.setDirection(direction);

            if (airPlace.isGrim())
            {
                sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, direction));
            }
        }

        if (direction == null || interactConfig.getIntervalMode().getValue() && !tryConsumePaperLimit(1))
        {
            return false;
        }

        Vec3 eyePos = mc.player.getEyePosition();
        boolean shouldSneak = !airPlacing && BlockUtil.isInteractable(placeInteraction.getInteractPos()) && !mc.player.isShiftKeyDown();
        if (shouldSneak)
        {
        }

        if (interactConfig.getInteractRotate().getValue())
        {
            float[] rots = RotationUtil.getRotationsTo(eyePos, placeInteraction.getInteractVec());
            Managers.ROTATION.setSilentRotation(new Rotation(rots[0], rots[1]));
        }

        if (airPlacing && airPlace.isGrim())
        {
            placeInteraction.setHand(InteractionHand.OFF_HAND);
        }

        if (airPlacing && airPlace.isGrim())
        {
            sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, direction));
        }

        InteractionResult actionResult = placeInteraction.applyInteraction();
        boolean success = actionResult != null && actionResult.consumesAction();
        if (success)
        {
            sendPacket(new ServerboundSwingPacket(placeInteraction.getHand()));
        }

        if (shouldSneak)
        {

        }

        if (airPlacing && airPlace.isGrim())
        {
            sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, direction));
        }

        return success;
    }

    public boolean startPlacement(int slot)
    {
        if (placementLock || slot == -1)
        {
            return false;
        }

        if (mc.player.isUsingItem() && !interactConfig.getMultiTask().getValue())
        {
            return false;
        }

        if (!Managers.INVENTORY.startSwap(slot, SilentSwapType.HOTBAR))
        {
            return false;
        }

        return placementLock = true;
    }

    public void endPlacement()
    {
        if (interactConfig.getInteractRotate().getValue())
        {
            Managers.ROTATION.resetSilentRotation();
        }

        Managers.INVENTORY.endSwap(SilentSwapType.HOTBAR);
        placementLock = false;
    }

    public void interactItem(ItemInteraction itemInteraction)
    {
        if (interactConfig.getIntervalMode().getValue() && !tryConsumePaperLimit(1))
        {
            return;
        }

        InteractionResult actionResult = itemInteraction.applyInteraction();
        ItemInteraction.GLOBAL_COUNT.incrementAndGet();
        boolean success = actionResult != null && actionResult.consumesAction();
        if (success)
        {
            sendPacket(new ServerboundSwingPacket(itemInteraction.getHand()));
        }
    }

    public void playBlockPlaceSound(BlockPos blockPos, BlockState state)
    {
        SoundType blockSoundGroup = state.getSoundType();
        runOnThread(() -> mc.level.playSound(mc.player,
                blockPos,
                blockSoundGroup.getPlaceSound(),
                SoundSource.BLOCKS,
                (blockSoundGroup.getVolume() + 1.0f) / 2.0f,
                blockSoundGroup.getPitch() * 0.8f));
    }
}
