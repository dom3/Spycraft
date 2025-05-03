package xyz.domcore.spycraft.map;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.configuration.ConfigurationSection;
import xyz.domcore.spycraft.PluginCore;
import xyz.domcore.spycraft.map.objects.*;

import java.util.ArrayList;
import java.util.List;

public class MapManager {
    public static Class<?>[] mapObjects = {
            SpawnObject.class,
            SniperSpawnObject.class,
            ViewpointObject.class,
            StatueObject.class,
            TalkCircleObject.class,
    };

    public static void registerMap(String name, Location boundsX, Location boundsY) {
        ConfigurationSection section = PluginCore.getMapConfig().createSection(name);
        section.set("bounds1", boundsX);
        section.set("bounds2", boundsY);
        PluginCore.getConfigManager().save("maps.yml");

    }

    public static ConfigurationSection getMapSection(String name) {
        return PluginCore.getMapConfig().getConfigurationSection(name);
    }

    public static List<String> getMaps() {
        return PluginCore.getMapConfig().getKeys(false).stream().toList();
    }

    public static Map serializeMap(String map) {
        Map mapObj = new Map();
        ConfigurationSection configuration = MapManager.getMapSection(map);
        Location location1 = configuration.getLocation("bounds1");
        Location location2 = configuration.getLocation("bounds2");
        assert location1 != null;
        assert location2 != null;
        mapObj.world = location1.getWorld();
        RegionSelector selector = new CuboidRegionSelector(
                BukkitAdapter.adapt(location1.getWorld()),
                new BlockVector3(location1.getBlockX(), location1.getBlockY(), location1.getBlockZ()),
                new BlockVector3(location2.getBlockX(), location2.getBlockY(), location2.getBlockZ())
        );
        try {
            for (BlockVector3 blockVector : selector.getRegion()) {
                Block block = location1.getWorld().getBlockAt(blockVector.x(), blockVector.y(), blockVector.z());
                if (block.getType() == Material.AIR) continue;
                if (block.getState() instanceof Sign sign) {
                    SignSide side = sign.getSide(Side.FRONT);
                    for (Class<?> mapObject : mapObjects) {
                        try {
                            if (MapObject.class.isAssignableFrom(mapObject)) {
                                // Create an instance of the class
                                MapObject newMapObject = (MapObject) mapObject.getDeclaredConstructor().newInstance();

                                PluginCore.getPlugin(PluginCore.class).getLogger().info(newMapObject.id);

                                // Validate the instance
                                if (newMapObject.validate(side.lines())) {
                                    // Set the sign and add to the map
                                    PluginCore.getPlugin(PluginCore.class).getLogger().info("Validated");
                                    newMapObject.sign = sign;
                                    mapObj = newMapObject.addToMap(mapObj); // Adjust as necessary
                                    mapObj.mapObjects.add(newMapObject);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IncompleteRegionException e) {
            throw new RuntimeException(e);
        }
        return mapObj;
    }

    public static List<String> getMapList() {
        return PluginCore.getMapConfig().getValues(false).keySet().stream().toList();
    }

    public static ArrayList<Map> loadMaps() {
        ArrayList<Map> map = new ArrayList<>();
        for (java.util.Map.Entry<String, Object> entry : PluginCore.getMapConfig().getValues(false).entrySet()) {
            map.add(serializeMap(entry.getKey()));
            PluginCore.getPlugin(PluginCore.class).getLogger().info("Loaded map " + entry.getKey());
        }
        return map;
    }
}
