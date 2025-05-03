package xyz.domcore.spycraft.command.map;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.domcore.spycraft.ColorConstants;
import xyz.domcore.spycraft.map.MapManager;

@Command("registermap")
@Permission("spycraft.registermap")
public class RegisterMap {
    @Default
    public static void register(Player player) {
        player.sendMessage(Component.text("Correct syntax: /register <name of map>").color(ColorConstants.RED));

    }

    @Default
    public static void register(Player player, @AStringArgument String name) {
        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(player); // WorldEdit's native Player class extends Actor
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(actor);

        Region region;
        World selectionWorld = localSession.getSelectionWorld();

        try {
            if (selectionWorld == null) throw new IncompleteRegionException();
            region = localSession.getSelection(selectionWorld);

            BlockVector3 bounds1 = region.getMinimumPoint();
            BlockVector3 bounds2 = region.getMaximumPoint();

            player.sendMessage(Component.text("Registering " + name).color(ColorConstants.BLUE));
            MapManager.registerMap(
                    name,
                    new Location(BukkitAdapter.adapt(selectionWorld), bounds1.x(),bounds1.y(),bounds1.z()),
                    new Location(BukkitAdapter.adapt(selectionWorld), bounds2.x(),bounds2.y(),bounds2.z())
            );
            player.sendMessage(Component.text("Registered " + name).color(ColorConstants.GREEN));
        } catch (IncompleteRegionException ex) {
            actor.printError(TextComponent.of("Please make a region selection first."));
        }
    }
}
