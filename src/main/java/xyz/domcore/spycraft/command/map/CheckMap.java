package xyz.domcore.spycraft.command.map;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import xyz.domcore.spycraft.ColorConstants;
import xyz.domcore.spycraft.map.Map;
import xyz.domcore.spycraft.map.MapManager;

@Command("checkmap")
@Permission("spycraft.checkmap")
public class CheckMap {

    @Default
    public static void check(Player player) {
        player.sendMessage(Component.text("Please specify a map"));
    }

    @Default
    public static void check(Player player, @AStringArgument String map) {
        Map mapObj = MapManager.serializeMap(map);

        player.sendMessage(Component.text("Map: " + map).decorate(TextDecoration.BOLD));
        if (!mapObj.viewpoints.isEmpty()) {
            player.sendMessage(Component.text("You have " + mapObj.viewpoints.size() + " viewpoints"));
        } else {
            player.sendMessage(Component.text("Missing viewpoints").color(ColorConstants.RED));
        }
        if (!mapObj.statues.isEmpty()) {
            player.sendMessage(Component.text("You have " + mapObj.statues.size() + " statues"));
        } else {
            player.sendMessage(Component.text("Missing statues.").color(ColorConstants.RED));
        }
        if (!mapObj.talkCircles.isEmpty()) {
            player.sendMessage(Component.text("You have " + mapObj.talkCircles.size() + " talk circles"));
        } else {
            player.sendMessage(Component.text("Missing talk circles.").color(ColorConstants.RED));
        }
        if (!mapObj.spawns.isEmpty()) {
            player.sendMessage(Component.text("You have " + mapObj.spawns.size() + " spawns"));
        } else {
            player.sendMessage(Component.text("Missing spawn.").color(ColorConstants.RED));
        }
        if (mapObj.sniperSpawn != null) {
            player.sendMessage(Component.text("Found sniper spawn"));
        } else {
            player.sendMessage(Component.text("Missing sniper spawns.").color(ColorConstants.RED));
        }
    }
}
