package xyz.domcore.spycraft.command;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import xyz.domcore.spycraft.ColorConstants;
import xyz.domcore.spycraft.PluginCore;

@Command("spyversion")
public class VersionCommand {
    @Default
    public static void version(Player player) {
        player.sendMessage(Component.text("Version: ").append(Component.text(PluginCore.version).color(ColorConstants.BLUE)));
    }
}
