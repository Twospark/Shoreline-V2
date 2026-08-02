package net.shoreline.client.impl.modules.misc;

import net.minecraft.world.entity.Pose;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.SettingGroup;
import net.shoreline.client.impl.event.LevelEvent;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.util.entity.FakePlayerEntity;
import net.shoreline.eventbus.api.Subscribe;

public class FakePlayerModule extends Toggleable
{
    Setting<Boolean> crawlingPose = new BooleanSetting.Builder("Crawling")
            .setDescription("Sets the player in the crawling pose")
            .setDefaultValue(false).build();
    Setting<Boolean> record = new BooleanSetting.Builder("Record")
            .setDescription("Will record your movement, toggle this and press play to play the recording")
            .setDefaultValue(false).build();
    Setting<Boolean> play = new BooleanSetting.Builder("Play")
            .setDefaultValue(false)
            .setDescription("Will play the current recording").build();
    Setting<Void> movement = new SettingGroup.Builder("Movement")
            .addAll(record, play).build();

    private FakePlayerEntity fakePlayer;

    public FakePlayerModule()
    {
        super("FakePlayer", "Spawns a fake player", Category.MISCELLANEOUS);
    }

    @Override
    public void onEnable()
    {
        if (!checkNull())
        {
            fakePlayer = new FakePlayerEntity(mc.player, "FakePlayer");
            fakePlayer.spawnPlayer();
        }
    }

    @Override
    public void onDisable()
    {
        if (!checkNull() && fakePlayer != null && !fakePlayer.isRemoved())
        {
            fakePlayer.despawnPlayer();
        }
    }

    @Subscribe
    public void onDisconnect(LevelEvent.Disconnect event)
    {
        disable();
    }

    @Subscribe
    public void onTick(TickEvent event)
    {
        if (checkNull() || fakePlayer == null)
        {
            return;
        }

        fakePlayer.setPose(crawlingPose.getValue() ? Pose.SWIMMING : Pose.STANDING);
        fakePlayer.baseTick();
    }
}
