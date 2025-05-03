package xyz.domcore.spycraft.command.game;

import dev.jorel.commandapi.annotations.Alias;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Subcommand;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import xyz.domcore.spycraft.ColorConstants;
import xyz.domcore.spycraft.game.GameManager;

@Command("queue")
@Alias("q")
public class QueueCommand {

    @Default
    public static void queue(Player player) {
        if (GameManager.getQueues().contains(player)) {
            player.sendMessage(Component.text("You are already in the queue, use \"/queue leave\" to exit the queue.").color(ColorConstants.RED));
            return;
        }
        if (GameManager.playerInGame(player)) {
            player.sendMessage(Component.text("Cannot queue while in a game.").color(ColorConstants.RED));
            return;
        }
        if (GameManager.getQueues().size() >= 10) {
            player.sendMessage(Component.text("Queue is very full.").color(ColorConstants.RED));
            return;
        }
        GameManager.getQueues().add(player);
        sendQueueUpdate();
    }

    @Subcommand("leave")
    public static void leaveQueue(Player player) {
        if (!GameManager.getQueues().contains(player)) {
            player.sendMessage(Component.text("You are not in the queue, use \"/queue\" to join.").color(ColorConstants.RED));
            return;
        }
        GameManager.getQueues().remove(player);
        sendQueueUpdate();
        player.sendMessage(Component.text("Left queue.").color(ColorConstants.BLUE));
    }

    private static void sendQueueUpdate() {
        for (Player p : GameManager.getQueues()) {
            int playersInQueue = (GameManager.getQueues().size()-1);
            String infoBox = (playersInQueue == 1 ? "(" + playersInQueue + " other player in queue.)" : "(" + playersInQueue + " other players in queue.)");
            p.sendActionBar(Component.text("Searching for players... " + infoBox).color(ColorConstants.BLUE));
        }
    }
}
