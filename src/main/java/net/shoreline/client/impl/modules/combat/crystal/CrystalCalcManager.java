package net.shoreline.client.impl.modules.combat.crystal;

import net.minecraft.core.BlockPos;
import net.shoreline.client.api.thread.AsyncFeature;
import net.shoreline.client.impl.level.entity.state.EntityState;
import net.shoreline.client.impl.modules.combat.AutoCrystalModule;

import java.util.ArrayList;
import java.util.List;

public class CrystalCalcManager extends AsyncFeature<List<CrystalData<?>>>
{
    private final AutoCrystalModule module;
    private final CrystalBaseScanner scanner;

    public CrystalCalcManager(AutoCrystalModule module)
    {
        super("End Crystals", new ArrayList<>());
        this.module = module;
        this.scanner = new CrystalBaseScanner(module);
    }

    public void runCalc()
    {
        if (currentResult == null || currentResult.isDone())
        {
            scanner.createLevelLookup(mc.level, mc.player, true);
            currentResult = submitCallable(() ->
            {
                List<CrystalData<?>> crystalData = new ArrayList<>();
                crystalData.addAll(scanner.scanBlocks());
                crystalData.addAll(scanner.scanCrystals());
                return crystalData;
            });
        }
    }

    @SuppressWarnings("unchecked cast")
    public List<CrystalData<BlockPos>> getBaseResults()
    {
        return getResult().stream()
                .filter(d -> d.getValue() instanceof BlockPos)
                .map(d -> (CrystalData<BlockPos>) d)
                .toList();
    }

    public List<CrystalData<BlockPos>> getBasePlacements()
    {
        return getBaseResults().stream()
                .filter(d -> module.canUseOnBlock(d.getValue()))
                .toList();
    }

    @SuppressWarnings("unchecked cast")
    public List<CrystalData<EntityState>> getEntityResults()
    {
        return getResult().stream()
                .filter(d -> d.getValue() instanceof EntityState)
                .map(d -> (CrystalData<EntityState>) d)
                .toList();
    }
}
