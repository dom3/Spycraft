package xyz.domcore.spycraft.game;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.domcore.spycraft.ColorConstants;
import xyz.domcore.spycraft.ItemNames;
import xyz.domcore.spycraft.LoreBuilder;
import xyz.domcore.spycraft.map.Map;
import xyz.domcore.spycraft.map.MapObject;
import xyz.domcore.spycraft.map.ObjectPriority;
import xyz.domcore.spycraft.missions.MissionData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static xyz.domcore.spycraft.ColorConstants.getColorScheme;

public class Game {
    public int id = 0;
    public int round = 0;
    public Map map;
    public Player spy;
    public Player sniper;
    public MultiverseWorld gameWorld;
    public boolean started = false;
    public boolean voting = false;
    public int spyVote=-1;
    public int sniperVote=-1;
    public RoundInfo roundInfo = new RoundInfo();


    public void sendMessage(Component message) {
        this.spy.sendMessage(message);
        this.sniper.sendMessage(message);
    }

    public void updateNPCs() {
        for (GameNPC gameNPC : roundInfo.gameNPCs) {
            gameNPC.tickAI();
        }
    }



    public void updateBoards() {
        ArrayList<Component> spyMissions = new ArrayList<>();
        spyMissions.add(Component.text("Missions:").color(getColorScheme(true)));
        for (GameMission mission : roundInfo.gameMissions) {
            spyMissions.add(mission.display(true));
        }
        ArrayList<Component> sniperMissions = new ArrayList<>();
        sniperMissions.add(Component.text("Spy's Missions:").color(getColorScheme(false)));
        for (GameMission mission : roundInfo.gameMissions) {
            sniperMissions.add(mission.display(false));
        }
        GameBoardManager.baseBoard(
                this,
                this.roundInfo.spyBoard,
                true,
                spyMissions
                );
        GameBoardManager.baseBoard(
                this,
                this.roundInfo.sniperBoard,
                false,
                sniperMissions
        );
    }

    public InteractionPoint getPointByBlock(Block block) {
        for (MapObject mapObject : map.mapObjects) {
            if (mapObject.interactionPoint.blocks.contains(block)) {
                return mapObject.interactionPoint;
            }
        }
        return null;
    }

    public boolean npcAtBlock(Block block) {
        for (GameNPC npc : roundInfo.gameNPCs) {
            if (npc.currentBlock != null && npc.currentBlock.block.equals(block) || npc.targetBlock != null && npc.targetBlock.block.equals(block)) {
                return true;
            }
        }
        return false;
    }

    public List<MapObject> getAIMapObjects(ObjectPriority priority) {
        ArrayList<MapObject> mapObjects = new ArrayList<>();
        for (MapObject mapObject : map.mapObjects) {
            if (mapObject.aiNavigable() && mapObject.objectPriority() == priority) {
                mapObjects.add(mapObject);
            }
        }
        return mapObjects;
    }

    public void updateSpyInventory(GameNPC.PointData currentBlock) {
        this.spy.getInventory().clear();

        /* Movement */
        ItemStack movementItem = ItemStack.of(Material.BLAZE_ROD);
        ItemMeta movementMeta = movementItem.getItemMeta();
        movementMeta.displayName(ItemNames.ITEM_MOVE);
        movementMeta.lore(LoreBuilder.newBuilder().add(Component.text("Right-click a block to move to a specific location.").color(ColorConstants.BLUE)).build());
        movementItem.setItemMeta(movementMeta);
        this.spy.getInventory().addItem(movementItem);

        /* Separator */
        ItemStack separator = ItemStack.of(Material.END_ROD);
        ItemMeta separatorMeta = separator.getItemMeta();
        separatorMeta.displayName(Component.empty());
        separatorMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        separator.setItemMeta(separatorMeta);
        this.spy.getInventory().addItem(separator);

        if (currentBlock == null) return;
        /* Interaction Buttons */
        ArrayList<InteractionButton> interactionButtons = new ArrayList<>(currentBlock.point.buttons);
        Collections.sort(interactionButtons);
        for (InteractionButton button : interactionButtons) {
            ItemStack interactionButton = new ItemStack(button.material);
            ItemMeta meta = interactionButton.getItemMeta();
            meta.displayName(Component.text(button.name).color(button.missionButton ? ColorConstants.GOLD : ColorConstants.BLUE));
            ArrayList<String> lore = new ArrayList<>();
            lore.add(button.id);
            meta.setLore(lore);
            interactionButton.setItemMeta(meta);
            this.spy.getInventory().addItem(interactionButton);
        }
    }

    public void updateSniperInventory() {
        this.sniper.getInventory().clear();

        ItemStack sniperItem = ItemStack.of(Material.BOW);
        ItemMeta sniperMeta = sniperItem.getItemMeta();
        sniperMeta.displayName(ItemNames.ITEM_SNIPER);
        sniperMeta.lore(LoreBuilder.newBuilder().add(Component.text("Aim at an npc to fire.").color(ColorConstants.BLUE)).build());
        sniperItem.setItemMeta(sniperMeta);
        this.sniper.getInventory().addItem(sniperItem);

        ItemStack bulletItem = ItemStack.of(Material.ARROW);
        ItemMeta bulletMeta = bulletItem.getItemMeta();
        bulletMeta.displayName(ItemNames.ITEM_BULLET);
        bulletMeta.lore(LoreBuilder.newBuilder().add(Component.text("You have one shot.").color(ColorConstants.BLUE)).build());
        bulletItem.setItemMeta(bulletMeta);
        this.sniper.getInventory().addItem(bulletItem);
    }

    public InteractionButton getButtonByItem(InteractionPoint point, ItemStack stack) {
        String id = stack.getItemMeta().getLore().getFirst();
        for (InteractionButton button : point.buttons) {
            String b2 = button.id;
            if (id.equalsIgnoreCase(b2)) {
                return button;
            }
        }
        return null;
    }

    public GameMission getMissionByType(Class<?> missionDataClass) {
        for (GameMission mission : roundInfo.gameMissions) {
            if (mission.data.getClass().equals(missionDataClass)) {
                return mission;
            }
        }
        return null;
    }

    public GameNPC getGameNPCByEntity(Entity entity) {
        for (GameNPC npc : roundInfo.gameNPCs) {
            if (npc.npc.getEntity().getUniqueId().equals(entity.getUniqueId())) {
                return npc;
            }
        }
        return null;
    }
}
