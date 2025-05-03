package xyz.domcore.spycraft;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;

public class Multiverse {
    private static MultiverseCore core;
    private static MVWorldManager worldManager;

    public static void init() {
        core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        assert core != null;
        worldManager = core.getMVWorldManager();
    }

    public static MultiverseCore getCore() {
        return core;
    }

    public static MVWorldManager getWorldManager() {
        return worldManager;
    }
}
