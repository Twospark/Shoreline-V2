package net.shoreline.client.impl.modules.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Block;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.LevelEvent;
import net.shoreline.client.impl.interact.InteractDirection;
import net.shoreline.client.impl.interact.PlaceInteraction;
import net.shoreline.client.impl.modules.client.InteractionsModule;
import net.shoreline.client.impl.render.animation.Animation;
import net.shoreline.eventbus.EventBus;
import net.shoreline.eventbus.listener.LambdaListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PlacerModule extends CombatModule
{
    protected final InteractionsModule interactConfig = InteractionsModule.INSTANCE;

    protected final List<BlockPos> placements = new ArrayList<>();
    protected final ConcurrentMap<BlockPos, Animation> fadeOutAnimations = new ConcurrentHashMap<>();

    public PlacerModule(String name, String description, Category category)
    {
        this(name, new String[0], description, category);
    }

    public PlacerModule(final String name,
                        final String[] nameAliases,
                        final String description,
                        final Category category)
    {
        super(name, nameAliases, description, category);
        EventBus.getInstance().register(new LambdaListener<>(LevelEvent.Disconnect.class, e -> disable()));
    }

    protected boolean placeBlock(BlockPos placePos, Block block)
    {
        return placeBlock(placePos, block, !interactConfig.getNoGlitchBlocks().getValue(), interactConfig.getStrictDirection().getValue());
    }

    protected boolean placeBlock(BlockPos placePos, Block block, boolean clientInteract, boolean strictDir)
    {
        final PlaceInteraction placeInteraction = PlaceInteraction.builder()
                .pos(placePos)
                .direction(InteractDirection.getInteractDirection(placePos, strictDir))
                .hand(InteractionHand.MAIN_HAND)
                .block(block)
                .clientInteract(clientInteract)
                .build();

        boolean result = Managers.INTERACTION.placeBlock(placeInteraction);
        if (result)
        {
            fadeOutAnimations.put(placePos, new Animation(true, 500));
        }

        return result;
    }

    protected boolean runSingleBlockPlacement(BlockPos placePos, Block block, int slot)
    {
        if (!canPlaceBlock(placePos, block) || !Managers.INTERACTION.startPlacement(slot))
        {
            return false;
        }

        boolean result = placeBlock(placePos, block);
        Managers.INTERACTION.endPlacement();
        return result;
    }

    protected void createPlacementsFromPositions(Block block, Collection<BlockPos> posList, double range)
    {
        placements.clear();

        if (posList.isEmpty())
        {
            return;
        }

        for (BlockPos blockPos : posList)
        {
            double dist = mc.player.distanceToSqr(blockPos.getCenter());
            if (dist > range * range)
            {
                continue;
            }

            if (!canPlaceBlock(blockPos, block))
            {
                continue;
            }

            placements.add(blockPos);
        }
    }

    protected boolean canPlaceBlock(BlockPos blockPos, Block block)
    {
        return mc.level.getBlockState(blockPos).canBeReplaced()
                && Managers.INTERACTION.canPlaceBlock(blockPos, block);
    }
}