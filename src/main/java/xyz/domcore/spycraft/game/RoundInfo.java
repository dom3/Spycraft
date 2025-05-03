package xyz.domcore.spycraft.game;

import fr.mrmicky.fastboard.adventure.FastBoard;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;
import xyz.domcore.spycraft.map.MapObject;
import xyz.domcore.spycraft.map.objects.StatueObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RoundInfo {
    public ArrayList<GameNPC> gameNPCs = new ArrayList<>();
    public GameNPC ambassador;
    public GameNPC spy;
    public GameNPC doubleAgent;
    public HashMap<StatueObject, Material> statueData = new HashMap<>();
    public BukkitTask roundLoop;
    public BukkitTask particleLoop;
    public int time;
    public FastBoard spyBoard;
    public FastBoard sniperBoard;
    public ArrayList<GameMission> gameMissions = new ArrayList<>();
}
