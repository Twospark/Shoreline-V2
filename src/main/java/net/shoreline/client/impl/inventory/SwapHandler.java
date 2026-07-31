package net.shoreline.client.impl.inventory;

import net.shoreline.client.api.common.Feature;
import net.shoreline.client.util.input.InputUtil;
import net.shoreline.client.util.math.Timer;

public class SwapHandler extends Feature
{
    private final Timer lastSwapTime = new Timer();

    public SwapHandler()
    {
        super("AutoSwap");
    }

    public void handleSwaps()
    {
        if (!mc.options.keyUse.isDown() && !mc.options.keyAttack.isDown() && !InputUtil.isInputtingHotbar())
        {
            return;
        }

        lastSwapTime.reset();
    }

    public boolean canAutoSwap()
    {
        return lastSwapTime.passed(500);
    }
}