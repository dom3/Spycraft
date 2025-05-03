package xyz.domcore.spycraft;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class NPCPacketHelper {
    public static void addGlow(Entity entity, boolean glowing, Player... receivers) {
        // Create the metadata packet
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, entity.getEntityId());

        // Create the data for the Entity Metadata packet
        WrappedDataWatcher watcher = new WrappedDataWatcher(entity);
        WrappedDataWatcher.WrappedDataWatcherObject glowingObject = new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
        watcher.setObject(glowingObject, (byte) (glowing ? 0x40 : 0));

        // Write the metadata to the packet
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

        // Send the packet to each receiver
        for (Player receiver : receivers) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, packet);
        }
    }
}