package com.artingl.easyrg.misc.Protocol;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Protocol.Clientside.PacketPlayOutScoreboardTeam;
import com.comphenix.protocol.ProtocolLibrary;

public class PacketsRegistry {

    public static void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketPlayOutScoreboardTeam());
    }

    public static void unregister() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(PluginMain.instance);
    }
}
