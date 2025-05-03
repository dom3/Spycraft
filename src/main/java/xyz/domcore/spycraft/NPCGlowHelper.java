package xyz.domcore.spycraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class NPCGlowHelper {
    public static void addGlow(Entity entity, ChatColor color, Player... receivers) throws ReflectiveOperationException {
        for (Player receiver : receivers) {
            PluginCore.getGlowingEntities().setGlowing(entity, receiver, color);
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntityTeam(entity);
            if (team != null)
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }
    }
}
