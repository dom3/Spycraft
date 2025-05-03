package xyz.domcore.spycraft.game;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListener implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Game game = GameManager.getPlayerGame(event.getPlayer());
        if (game != null) {
            GameManager.onBlockBroken(game, event);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Game game = GameManager.getPlayerGame(event.getPlayer());
        if (game != null) {
            GameManager.onBlockPlaced( game, event);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Game game = GameManager.getPlayerGame(event.getPlayer());
        if (game != null) {
            GameManager.onInteract(game, event);

        }
    }

    @EventHandler
    public void onMove(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            Game game = GameManager.getPlayerGame(player);
            if (game != null) {
                GameManager.onShoot(game, event);

            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer() instanceof Player player) {
            Game game = GameManager.getPlayerGame(player);
            if (game != null) {
                GameManager.onJoin(game, event);

            }
        }
    }
}
