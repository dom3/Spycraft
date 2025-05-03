package xyz.domcore.spycraft.map;

import org.bukkit.Location;
import org.bukkit.World;
import xyz.domcore.spycraft.map.objects.StatueObject;
import xyz.domcore.spycraft.map.objects.TalkCircleObject;

import java.util.ArrayList;
import java.util.List;

public class Map {
    public String name;
    public World world;
    public List<MapObject> mapObjects;
    public ArrayList<Location> spawns;
    public List<Location> viewpoints;
    public Location sniperSpawn;
    public List<TalkCircleObject> talkCircles;
    public List<StatueObject> statues;

    public Map() {
        mapObjects = new ArrayList<>();
        spawns = new ArrayList<>();
        viewpoints = new ArrayList<>();
        statues = new ArrayList<>();
        talkCircles = new ArrayList<>();
    }
}
