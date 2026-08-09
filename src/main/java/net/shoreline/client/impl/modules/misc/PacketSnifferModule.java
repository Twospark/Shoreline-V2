package net.shoreline.client.impl.modules.misc;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.SettingGroup;
import net.shoreline.client.impl.event.LevelEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.eventbus.api.Subscribe;

public class PacketSnifferModule extends Toggleable
{
    Setting<Boolean> logPacket = new BooleanSetting.Builder("Log")
            .setDescription("Logs the packets in chat")
            .setDefaultValue(true).build();
    Setting<Boolean> logPacketSpam = new BooleanSetting.Builder("PacketSpam")
            .setDescription("Detects and logs potential packet spam")
            .setDefaultValue(true).build();
    Setting<Boolean> cancelPacket = new BooleanSetting.Builder("Cancel")
            .setDescription("Cancels the packets from sending/recieving")
            .setDefaultValue(false).build();

    Setting<Boolean> cPacketMovePlayerPosRot = new BooleanSetting.Builder("CPacketMovePlayerPosRot")
            .setDefaultValue(false)
            .setDescription("Logs CPacketMovePlayerPacket.PosRot").setDefaultValue(false).build();
    Setting<Boolean> cPacketMovePlayerPos = new BooleanSetting.Builder("CPacketMovePlayerPos")
            .setDefaultValue(false)
            .setDescription("Logs CPacketMovePlayerPacket.Pos").setDefaultValue(false).build();
    Setting<Boolean> cPacketMovePlayerRot = new BooleanSetting.Builder("CPacketMovePlayerRot")
            .setDefaultValue(false)
            .setDescription("Logs CPacketMovePlayerPacket.Rot").setDefaultValue(false).build();
    Setting<Boolean> cPacketMovePlayerStatus = new BooleanSetting.Builder("CPacketMovePlayerStatus")
            .setDefaultValue(false)
            .setDescription("Logs CPacketMovePlayerPacket.StatusOnly").setDefaultValue(false).build();
    Setting<Boolean> cPacketMoveVehicle = new BooleanSetting.Builder("CPacketMoveVehicle")
            .setDefaultValue(false)
            .setDescription("Logs CPacketMoveVehicle").build();
    Setting<Void> clientPackets = new SettingGroup.Builder("Client")
            .addAll(cPacketMovePlayerPosRot, cPacketMovePlayerPos, cPacketMovePlayerRot, cPacketMovePlayerStatus,
                    cPacketMoveVehicle)
            .build();

    Setting<Void> serverPackets = new SettingGroup.Builder("Server")
            .addAll().build();

    public PacketSnifferModule()
    {
        super("PacketSniffer", new String[]{"PacketLogger"},
                "Logs client packets", Category.MISCELLANEOUS);
    }

    @Subscribe
    public void onDisconnected(LevelEvent.Disconnect event)
    {

    }

    @Subscribe
    public void onPacketSend(PacketEvent.Send<?> event)
    {
        if (event.getPacket() instanceof ServerboundMovePlayerPacket.PosRot && cPacketMovePlayerPosRot.getValue())
        {

        }
        else if (event.getPacket() instanceof ServerboundMovePlayerPacket.Pos && cPacketMovePlayerPos.getValue())
        {

        }
        else if (event.getPacket() instanceof ServerboundMovePlayerPacket.Rot && cPacketMovePlayerRot.getValue())
        {

        }
        else if (event.getPacket() instanceof ServerboundMovePlayerPacket.StatusOnly && cPacketMovePlayerStatus.getValue())
        {

        }
        else if (event.getPacket() instanceof ServerboundMoveVehiclePacket && cPacketMoveVehicle.getValue())
        {

        }
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive<?> event)
    {

    }
}
