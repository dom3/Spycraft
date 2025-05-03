package xyz.domcore.spycraft.map.objects;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.domcore.spycraft.ColorConstants;
import xyz.domcore.spycraft.NPCRole;
import xyz.domcore.spycraft.PluginCore;
import xyz.domcore.spycraft.game.*;
import xyz.domcore.spycraft.map.Map;
import xyz.domcore.spycraft.map.MapObject;
import xyz.domcore.spycraft.missions.MissionBug;
import xyz.domcore.spycraft.missions.MissionContact;

import java.util.ArrayList;
import java.util.List;

public class TalkCircleObject extends MapObject {
    public TalkCircleObject() {
        id = "talkcircle";
    }

    @Override
    public boolean validate(List<Component> args) {
        return ((TextComponent)args.getFirst()).content().contains("[talkcircle]");
    }

    @Override
    public void executeInGame(Game game) {

    }

    @Override
    public Map addToMap(Map map) {
        map.talkCircles.add(this);
        return map;
    }

    @Override
    public void createPoint(Game game) {
        InteractionButton talk = new InteractionButton("talk", Material.BOOK, "Talk", false, useButtonEvent -> {
            useButtonEvent.npc.startAction("Talking...", 10, new BukkitRunnable() {
                @Override
                public void run() {
                }
            });
        });
        InteractionButton contact = new InteractionButton("contact", Material.BAMBOO_BUTTON, "Contact Double Agent", true, useButtonEvent -> {
            GameMission mission = game.getMissionByType(MissionContact.class);
            if (mission.isDone()) {
                game.spy.sendMessage(Component.text("Double agent already contacted.").color(ColorConstants.RED));
                return;
            }
            for (GameNPC npc : game.roundInfo.gameNPCs) {
                if (npc.currentBlock != null && npc.currentBlock.point == this.interactionPoint) {
                    if (game.roundInfo.doubleAgent == npc) {
                        useButtonEvent.npc.startAction("Contacting Double Agent...", 7, new BukkitRunnable() {
                            @Override
                            public void run() {
                                mission.incrementData();
                                game.gameWorld.getCBWorld().playSound(Sound.sound(Key.key("entity.villager.ambient"), Sound.Source.AMBIENT, 1f, 1f));
                            }
                        });
                        return;
                    }
                }
            }
            game.spy.sendMessage(Component.text("Not near double agent.").color(ColorConstants.RED));
        });
        InteractionButton bug = new InteractionButton("bug", Material.SPYGLASS, "Bug Ambassador", true, useButtonEvent -> {
            GameMission mission = game.getMissionByType(MissionBug.class);
            if (mission.isDone()) {
                game.spy.sendMessage(Component.text("Ambassador already bugged.").color(ColorConstants.RED));
                return;
            }

            for (GameNPC npc : game.roundInfo.gameNPCs) {
                if (npc.currentBlock != null && npc.currentBlock.point == this.interactionPoint && npc.currentBlock.block.getLocation().distance(game.roundInfo.spy.currentBlock.block.getLocation()) <= 1) {
                    if (game.roundInfo.ambassador == npc) {
                        useButtonEvent.npc.setItemInMainHand(Material.SPYGLASS);
                        useButtonEvent.npc.startAction("Bugging Ambassador...", 5, new BukkitRunnable() {
                            @Override
                            public void run() {
                                useButtonEvent.npc.clearMainHand();
                                mission.incrementData();

                            }
                        });
                        return;
                    }
                }
            }
            game.spy.sendMessage(Component.text("Not near ambassador.").color(ColorConstants.RED));
        });
        ArrayList<Block> blocks = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x==0 && z==0) continue; // ensure center is not glowed

                Block bottomBlock = game.gameWorld.getCBWorld().getBlockAt(sign.getLocation().add(x,-1,z));
                blocks.add(bottomBlock);
            }
        }
        interactionPoint.setButtons(
                talk,
                contact,
                bug
        );
        interactionPoint.blocks = blocks;
    }

    @Override
    public boolean aiNavigable() {
        return true;
    }

    @Override
    public Location lookPosition(Block block) {
        return sign.getLocation().add(0,1,0);
    }
}
