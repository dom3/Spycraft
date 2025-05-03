package xyz.domcore.spycraft.game;

import org.bukkit.Location;
import xyz.domcore.spycraft.PluginCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameSpawner {
    public Game game;
    public ArrayList<Location> locationList;

    public GameSpawner(Game game) {
        this.game = game;
        this.locationList = (ArrayList<Location>)game.map.spawns.clone();
    }

    public Location next() {
        PluginCore.getPlugin(PluginCore.class).getLogger().info("Locations: " + locationList.size());
        Location loc = locationList.get(new Random().nextInt(locationList.size()));
        locationList.remove(loc);
        loc.setWorld(game.gameWorld.getCBWorld());
        return loc;
    }
}
