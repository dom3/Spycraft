package xyz.domcore.spycraft.map;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import xyz.domcore.spycraft.game.Game;
import xyz.domcore.spycraft.game.InteractionPoint;

import java.util.ArrayList;
import java.util.List;

public class MapObject {
    public Sign sign;
    public String id;
    public InteractionPoint interactionPoint = new InteractionPoint(this, new ArrayList<>());

    public MapObject() {
    }

    /**
     * How the map serializer knows which sign relates to which map object.
     * @param args
     * @return If the arguments match this.
     */
    public boolean validate(List<Component> args) {
        return true;
    }

    public void executeInGame(Game game) {
    }

    public Map addToMap(Map map) {
        return map;
    }

    public void createPoint(Game game) {
    }

    public boolean aiNavigable() {
        return false;
    }

    public ObjectPriority objectPriority() {
        return ObjectPriority.COMMON;
    }

    public Location lookPosition(Block block) {
        return sign.getLocation();
    }
}
