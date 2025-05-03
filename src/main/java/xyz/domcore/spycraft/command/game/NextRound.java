package xyz.domcore.spycraft.command.game;

import dev.jorel.commandapi.annotations.Alias;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import org.bukkit.entity.Player;
import xyz.domcore.spycraft.game.Game;
import xyz.domcore.spycraft.game.GameManager;

@Command("nextround")
@Alias("nr")
@Permission("spycraft.nextround")
public class NextRound {
    @Default
    public static void nextRound(Player player) {
        Game game = GameManager.getPlayerGame(player);
        assert game != null;
        GameManager.nextRound(game);
    }
}
