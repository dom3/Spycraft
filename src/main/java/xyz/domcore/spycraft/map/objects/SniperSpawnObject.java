package xyz.domcore.spycraft.map.objects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.block.Sign;
import xyz.domcore.spycraft.game.Game;
import xyz.domcore.spycraft.map.Map;
import xyz.domcore.spycraft.map.MapObject;

import java.util.List;

public class SniperSpawnObject extends MapObject {
    public SniperSpawnObject() {
        id = "sniperspawn";
    }

    @Override
    public boolean validate(List<Component> args) {
        return ((TextComponent)args.getFirst()).content().contains("[sniper]");
    }

    @Override
    public void executeInGame(Game game) {
        super.executeInGame(game);
    }

    @Override
    public Map addToMap(Map map) {
        map.sniperSpawn = sign.getLocation();
        return map;
    }
}
