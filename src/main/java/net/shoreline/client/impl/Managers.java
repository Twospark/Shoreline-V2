package net.shoreline.client.impl;

import lombok.experimental.UtilityClass;
import net.shoreline.client.impl.combat.TargetManager;
import net.shoreline.client.impl.command.manager.CommandManager;
import net.shoreline.client.impl.config.ConfigManager;
import net.shoreline.client.impl.event.ClientEvent;
import net.shoreline.client.impl.interact.InteractionManager;
import net.shoreline.client.impl.inventory.InventoryManager;
import net.shoreline.client.impl.level.FallDistManager;
import net.shoreline.client.impl.mining.MiningManager;
import net.shoreline.client.impl.modules.ModuleManager;
import net.shoreline.client.impl.network.NetworkManager;
import net.shoreline.client.impl.render.TextManager;
import net.shoreline.client.impl.rotation.RotationManager;
import net.shoreline.client.impl.social.SocialManager;
import net.shoreline.eventbus.EventBus;

@UtilityClass
public class Managers
{
    public static NetworkManager NETWORK;
    public static ConfigManager CONFIG;
    public static ModuleManager MODULES;
    public static TextManager TEXT;
    public static RotationManager ROTATION;
    public static InventoryManager INVENTORY;
    public static FallDistManager FALL_DIST;
    public static InteractionManager INTERACTION;
    public static SocialManager SOCIAL;
    public static MiningManager MINING;
    public static TargetManager TARGETING;
    public static CommandManager COMMANDS;

    public void init()
    {
        NETWORK     = new NetworkManager();
        MODULES     = new ModuleManager();
        TEXT        = new TextManager();
        ROTATION    = new RotationManager();
        INVENTORY   = new InventoryManager();
        FALL_DIST   = new FallDistManager();
        INTERACTION = new InteractionManager();
        SOCIAL      = new SocialManager();
        MINING      = new MiningManager();
        TARGETING   = new TargetManager();
        COMMANDS    = new CommandManager();
        CONFIG      = new ConfigManager(); // configs last.

        EventBus.getInstance().post(new ClientEvent.Loaded());
    }
}
