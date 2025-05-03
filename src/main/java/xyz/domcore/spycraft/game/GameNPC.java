package xyz.domcore.spycraft.game;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.ai.event.NavigatorCallback;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.RotationTrait;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.domcore.spycraft.ColorConstants;
import xyz.domcore.spycraft.NPCRole;
import xyz.domcore.spycraft.PluginCore;
import xyz.domcore.spycraft.Utils;
import xyz.domcore.spycraft.map.MapObject;
import xyz.domcore.spycraft.map.ObjectPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameNPC {
    public Game game;
    public NPCRole npcRole;
    public NPC npc;
    public long aiTick = 0;
    public long aiTickLimit = 0;
    public PointData currentBlock;
    public PointData targetBlock;

    public boolean doingAction;
    public int actionTick = 0;
    public int actionDelay = 0;
    public BukkitRunnable actionTask;
    public String action;

    boolean firstMove = false;

    public GameNPC(Game game, NPCRole npcRole, NPC npc) {
        this.game = game;
        this.npcRole = npcRole;
        this.npc = npc;
    }

    public static class PointData {
        public InteractionPoint point;
        public Block block;
        public PointData(InteractionPoint point, Block block) {
            this.point = point;
            this.block = block;
        }
    }

    public void tickAI() {
        if (this.npc.getNavigator().isNavigating() && game.roundInfo.spy != this) return;
        if (!doingAction)
            aiTick++;
        if (!firstMove) {
            moveToRandomLocation();
        }
        if (aiTick >= aiTickLimit && firstMove) {
            aiTick = 0;
            aiTickLimit = Utils.randomNumberLong(7, 25);
            if (this != game.roundInfo.spy)
                moveToRandomLocation();
        }
        if (this.doingAction) {
            this.actionTick++;
            game.gameWorld.getCBWorld().spawnParticle(Particle.HAPPY_VILLAGER, this.npc.getEntity().getLocation().add(0,2.5,0), 10);
            if (game.roundInfo.spy == this)
                game.spy.sendActionBar(Component.text(this.action).appendSpace().append(Component.text("[" + ((actionTick * 100)/actionDelay) + "%]").color(ColorConstants.BLUE)));
            if (this.actionTick >= actionDelay) {
                actionTask.runTask(PluginCore.getPlugin(PluginCore.class));
                this.doingAction = false;
                if (game.roundInfo.spy == this)
                    game.spy.sendActionBar(Component.text("Done!").color(ColorConstants.GREEN));
            }
        }
    }

    public void startAction(String action, int delay, BukkitRunnable task) {
        if (doingAction) {
            PluginCore.getPlugin(PluginCore.class).getLogger().warning("Already doing action...");
            return;
        }
        doingAction = true;
        this.action = action;
        actionTick = 0;
        actionDelay = delay;
        actionTask = task;
    }

    public void cancelAction() {
        cancelAction("Cancelled!");
    }
    public void cancelAction(String cancelReason) {
        if (!doingAction) return;

        doingAction = false;
        actionTick = 0;
        actionDelay = 0;
        this.actionTask = null;
        if (game.roundInfo.spy == this)
            game.spy.sendActionBar(Component.text(cancelReason).color(ColorConstants.RED));
    }

    Block getAvailableBlock(InteractionPoint point) {
        for (Block block : point.blocks) {
            if (game.npcAtBlock(block)) {
                continue;
            }
            return block;
        }
        return null;
    }

    List<MapObject> getUniqueMapObjects(List<MapObject> mapObjects) {
        ArrayList<MapObject> mos = new ArrayList<>();
        for (MapObject mo : mapObjects) {
            if (!mo.interactionPoint.blocks.isEmpty()) {
                if (currentBlock == null || mo.interactionPoint != currentBlock.point) {
                    Block block = getAvailableBlock(mo.interactionPoint);
                    if (block != null) {
                        mos.add(mo);
                    }
                }
            }
        }
        return mos;
    }

    PointData getAvailablePoint() {
        int random = new Random().nextInt(100);
        ObjectPriority priority = (random > 15 ? ObjectPriority.COMMON : ObjectPriority.RARE);
        List<MapObject> unique = getUniqueMapObjects(game.getAIMapObjects(priority));
        MapObject mapObject = unique.get(new Random().nextInt(unique.size()));
        Block block = getAvailableBlock(mapObject.interactionPoint);
        return new PointData(mapObject.interactionPoint, block);
    }


    public void moveToRandomLocation() {
        if (!game.map.mapObjects.isEmpty()) {
            PointData point = getAvailablePoint();
            if (point == null) {
                PluginCore.getPlugin(PluginCore.class).getLogger().warning("No available location.");
                return;
            }
            game.roundInfo.spy.spyData(this);
            targetBlock = point;
            moveTo(point.block.getLocation().add(0,1,0).toCenterLocation());
            firstMove = true;
        }
    }

    private void spyData(GameNPC npc) {
        if (game.roundInfo.doubleAgent == npc || game.roundInfo.ambassador == npc) {
            if (doingAction) {
                if (action.equalsIgnoreCase("Contacting Double Agent...")) {
                    cancelAction("The Double Agent walked away.");
                }
                if (action.equalsIgnoreCase("Bugging Ambassador...")) {
                    cancelAction("The Ambassador walked away.");
                }
            }
        }
    }

    public void moveTo(Location location) {
        currentBlock = null;
        if (this == game.roundInfo.spy)
            game.updateSpyInventory(null);
        npc.getNavigator().setTarget(location);
        npc.getNavigator().getLocalParameters().baseSpeed(0.5f);
        npc.getNavigator().getLocalParameters().destinationTeleportMargin(1.5f);
        npc.getNavigator().getLocalParameters().addSingleUseCallback(new NavigatorCallback() {
            @Override
            public void onCompletion(CancelReason cancelReason) {
                currentBlock = targetBlock;
                //PluginCore.getPlugin(PluginCore.class).getLogger().info(currentBlock.point.mapObject.lookPosition().toString());
                if (currentBlock != null) {
                    RotationTrait rot = npc.getOrAddTrait(RotationTrait.class);
                    rot.getGlobalParameters().linkedBody(true);
                    rot.getPhysicalSession().rotateToFace(currentBlock.point.mapObject.lookPosition(currentBlock.block).toCenterLocation());
                    if (npc == game.roundInfo.spy.npc) {
                        game.updateSpyInventory(currentBlock);
                    } else {
                        if (!currentBlock.point.buttons.isEmpty()) {
                            List<InteractionButton> interactionButtons = currentBlock.point.buttons.stream().filter(interactionButton -> !interactionButton.missionButton).toList();
                            InteractionButton button = interactionButtons.get(new Random().nextInt(interactionButtons.size()));
                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    button.interact(GameNPC.this);
                                }
                            }.runTaskLater(PluginCore.getPlugin(PluginCore.class), Utils.randomNumberLong(1,4));
                        }
                    }
                }
            }
        });
    }

    public void setItemInMainHand(Material material) {
        Equipment equipment = this.npc.getOrAddTrait(Equipment.class);
        equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(material));
    }

    public void clearMainHand() {
        Equipment equipment = this.npc.getOrAddTrait(Equipment.class);
        equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.AIR));
    }
    public void delete() {
        this.cancelAction();
    }
}
