package com.artingl.easyrg.misc.Protocol.Clientside;

import com.artingl.easyrg.PluginMain;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PacketPlayOutScoreboardTeam extends PacketAdapter {
    public PacketPlayOutScoreboardTeam() {
        super(PluginMain.instance, ListenerPriority.HIGHEST, PacketType.Play.Server.ENTITY_EQUIPMENT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
            Entity entity = event.getPacket().getEntityModifier(event).read(0);
            Player player = event.getPlayer();

            if (!entity.getScoreboardTags().contains("EASYRG_SELECTION_" + player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

}
