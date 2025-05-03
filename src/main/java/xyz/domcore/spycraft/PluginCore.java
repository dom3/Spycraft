package xyz.domcore.spycraft;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import dev.jorel.commandapi.CommandAPI;
import fr.skytasul.glowingentities.GlowingBlocks;
import fr.skytasul.glowingentities.GlowingEntities;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.domcore.spycraft.command.VersionCommand;
import xyz.domcore.spycraft.command.game.NextRound;
import xyz.domcore.spycraft.command.game.QueueCommand;
import xyz.domcore.spycraft.command.map.CheckMap;
import xyz.domcore.spycraft.command.map.RegisterMap;
import xyz.domcore.spycraft.game.Game;
import xyz.domcore.spycraft.game.GameListener;
import xyz.domcore.spycraft.game.GameManager;
import xyz.domcore.spycraft.map.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class PluginCore extends JavaPlugin {

    private static List<Map> maps = new ArrayList<>();
    private static ConfigurationManager configManager;

    private static GlowingEntities glowingEntities;
    private static GlowingBlocks glowingBlocks;

    public static int version = 0;

    @Override
    public void onLoad() {
        configManager = new ConfigurationManager(this);

        CommandAPI.registerCommand(RegisterMap.class);
        CommandAPI.registerCommand(CheckMap.class);
        CommandAPI.registerCommand(QueueCommand.class);
        CommandAPI.registerCommand(NextRound.class);
        CommandAPI.registerCommand(VersionCommand.class);

        configManager.saveDefault("maps.yml");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        CommandAPI.onEnable();
        Multiverse.init();
        GameManager.startQueueLoop();
        glowingEntities = new GlowingEntities(this);
        glowingBlocks = new GlowingBlocks(this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        version = new Random().nextInt(999);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        CommandAPI.onDisable();
        for (Game game : GameManager.getGames()) {
            Multiverse.getWorldManager().deleteWorld(game.gameWorld.getName());
        }
        glowingEntities.disable();
        glowingBlocks.disable();
    }

    public static List<Map> getMaps() {
        return maps;
    }

    public static ConfigurationManager getConfigManager() {
        return configManager;
    }

    public static YamlConfiguration getMapConfig() {
        return configManager.get("maps.yml");
    }

    public static GlowingBlocks getGlowingBlocks() {
        return glowingBlocks;
    }

    public static GlowingEntities getGlowingEntities() {
        return glowingEntities;
    }
}
