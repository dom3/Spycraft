package xyz.domcore.spycraft.map.objects;

import fr.skytasul.glowingentities.GlowingBlocks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.domcore.spycraft.ColorConstants;
import xyz.domcore.spycraft.PluginCore;
import xyz.domcore.spycraft.game.*;
import xyz.domcore.spycraft.map.Map;
import xyz.domcore.spycraft.map.MapObject;
import xyz.domcore.spycraft.map.ObjectPriority;
import xyz.domcore.spycraft.missions.MissionSwap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StatueObject extends MapObject {

    private Material[] statueMaterials = {
            Material.POTTED_CACTUS,
            Material.POTTED_BLUE_ORCHID,
            Material.POTTED_DANDELION,
            Material.POTTED_POPPY
    };

    public StatueObject() {
        id = "statue";
    }

    @Override
    public boolean validate(List<Component> args) {
        return ((TextComponent)args.getFirst()).content().contains("[statue]") && !((TextComponent) args.get(1)).content().isEmpty();
    }

    @Override
    public void executeInGame(Game game) {
        game.gameWorld.getCBWorld().getBlockAt(sign.getLocation()).setType(statueMaterials[new Random().nextInt(statueMaterials.length)]);
    }

    @Override
    public Map addToMap(Map map) {
        map.statues.add(this);
        return map;
    }

    Material getOtherThan(Material material) {
        List<Material> mats = new ArrayList<>(Arrays.stream(statueMaterials).toList());
        mats.remove(material);
        return mats.get(new Random().nextInt(mats.size()));
    }

    @Override
    public void createPoint(Game game) {
        InteractionButton swapStatue = new InteractionButton("swap-statue", Material.POPPY, "Swap Statue", true, (g) -> {
            GameMission mission = game.getMissionByType(MissionSwap.class);
            if (mission.isDone()) {
                game.spy.sendMessage(Component.text("Already swapped statue.").color(ColorConstants.RED));
                return;
            }
            g.npc.setItemInMainHand(Material.POPPY);
            g.npc.startAction("Swapping statue..", 5, new BukkitRunnable() {
                @Override
                public void run() {
                    game.gameWorld.getCBWorld().getBlockAt(sign.getLocation()).setType(getOtherThan(game.gameWorld.getCBWorld().getBlockAt(sign.getLocation()).getType()));
                    mission.incrementData();
                    g.npc.clearMainHand();
                }
            });
        });
        InteractionButton inspectStatue = new InteractionButton("inspect-statue", Material.SPYGLASS, "Inspect Statue", false, (g) -> {
            g.npc.setItemInMainHand(Material.POPPY);
            g.npc.startAction("Inspecting statue..", 5, new BukkitRunnable() {
                @Override
                public void run() {
                    g.npc.clearMainHand();
                }
            });
        });
        ArrayList<Block> blocks = new ArrayList<>();
        if (sign.getSide(Side.FRONT).lines().get(1) instanceof TextComponent textComponent) {
            String[] offsets = textComponent.content().split(" ");
            if (offsets.length == 3) {
                int offsetX = Integer.parseInt(offsets[0]);
                int offsetY = Integer.parseInt(offsets[1]);
                int offsetZ = Integer.parseInt(offsets[2]);
                Block bottomBlock = game.gameWorld.getCBWorld().getBlockAt(sign.getLocation().subtract(0, 2, 0).add(offsetX,offsetY,offsetZ));
                blocks.add(bottomBlock);
            }
        }
        interactionPoint.setButtons(
                swapStatue,
                inspectStatue
        );
        interactionPoint.blocks = blocks;
    }

    @Override
    public ObjectPriority objectPriority() {
        return ObjectPriority.COMMON;
    }

    @Override
    public boolean aiNavigable() {
        return true;
    }
}
