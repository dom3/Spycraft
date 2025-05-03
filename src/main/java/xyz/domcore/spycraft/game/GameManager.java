package xyz.domcore.spycraft.game;

import fr.mrmicky.fastboard.adventure.FastBoard;
import fr.skytasul.glowingentities.GlowingEntities;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.SkinTrait;
import net.citizensnpcs.trait.SleepTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xyz.domcore.spycraft.*;
import xyz.domcore.spycraft.map.Map;
import xyz.domcore.spycraft.map.MapManager;
import xyz.domcore.spycraft.map.MapObject;
import xyz.domcore.spycraft.missions.MissionBug;
import xyz.domcore.spycraft.missions.MissionContact;
import xyz.domcore.spycraft.missions.MissionData;
import xyz.domcore.spycraft.missions.MissionSwap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {
    private static final long QUEUE_TIMER = 10L;
    private static final List<Player> queues = new ArrayList<>();
    private static final List<Game> games = new ArrayList<>();
    public static final MissionData[] missions = new MissionData[] {
            new MissionSwap(),
            new MissionContact(),
            new MissionBug()
    };

    public static final int ROUND_TIME = 120;

    public static void startQueueLoop() {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (queues.size() > 1) {
                    Player player1 = queues.get(0);
                    Player player2 = queues.get(1);

                    queues.remove(player1);
                    queues.remove(player2);
                    Game game = GameManager.createGame(player1, player2);
                    GameManager.startGame(game);
                    player1.sendMessage(Component.text("You vs " + player2.getName()).color(ColorConstants.BLUE));
                    player2.sendMessage(Component.text("You vs " + player1.getName()).color(ColorConstants.BLUE));
                }
            }
        }.runTaskTimer(PluginCore.getPlugin(PluginCore.class), 0, QUEUE_TIMER*20);
    }

    private static GameNPC getRandomFromList(List<GameNPC> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    public static void generateNPCs(Game game) {
        for (int i = 0; i < SkinURLS.SKIN_URL_LIST.length; i++) {
            SkinURLS.Skin skinUrl = SkinURLS.SKIN_URL_LIST[i];
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, game.id+"-"+i);
            //npc.getTraitNullable(SkinTrait.class).setSkinName(skinUrl, true); DOESNT WORK
            GameNPC gameNPC = new GameNPC(game, NPCRole.NORMAL, npc);
            game.roundInfo.gameNPCs.add(gameNPC);
            //npc.getOrAddTrait(SkinTrait.class).setTexture(skinUrl.value, skinUrl.signature);
            npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(skinUrl.name, skinUrl.signature, skinUrl.value);
            npc.setName(game.id + "-" + skinUrl.name);
            //npc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, false);
            //npc.getOrAddTrait(HologramTrait.class).addLine(skinUrl.name);
        }


        GameSpawner gameSpawner = new GameSpawner(game);

        for (GameNPC gameNPC : game.roundInfo.gameNPCs) {
            gameNPC.npc.spawn(gameSpawner.next().toCenterLocation());
        }

        ArrayList<GameNPC> npcs = (ArrayList<GameNPC>) game.roundInfo.gameNPCs.clone();

        /* Spy */
        GameNPC spyNPC = getRandomFromList(npcs);
        game.roundInfo.spy = spyNPC;
        game.sendMessage(Component.text("SPY: " + spyNPC.npc.getId()));
        game.spy.teleport(spyNPC.npc.getEntity().getLocation());
        npcs.remove(spyNPC);
        //Teams.SPY.addEntity(spyNPC.npc.getEntity());

        /* Ambassador */
        GameNPC ambassador = getRandomFromList(npcs);
        game.roundInfo.ambassador = ambassador;
        game.sendMessage(Component.text("AMB: " + ambassador.npc.getId()));
        npcs.remove(ambassador);
        //Teams.AMBASSADOR.addEntity(ambassador.npc.getEntity());

        /* Double Agent */
        GameNPC doubleAgent = getRandomFromList(npcs);
        game.roundInfo.doubleAgent = doubleAgent;
        npcs.remove(doubleAgent);
        game.sendMessage(Component.text("DBL: " + doubleAgent.npc.getId()));

        /* Suspected Double Agents */
        GameNPC suspected1 = getRandomFromList(npcs);
        game.sendMessage(Component.text("SUS1: " + suspected1.npc.getId()));
        npcs.remove(suspected1);
        GameNPC suspected2 = getRandomFromList(npcs);
        game.sendMessage(Component.text("SUS2: " + suspected2.npc.getId()));
        npcs.remove(suspected2);

        //Teams.DOUBLE_AGENT.addEntities(doubleAgent.npc.getEntity(), suspected1.npc.getEntity(), suspected2.npc.getEntity());

        try {
            NPCGlowHelper.addGlow(ambassador.npc.getEntity(),ChatColor.DARK_PURPLE,game.sniper,game.spy);
            NPCGlowHelper.addGlow(doubleAgent.npc.getEntity(),ChatColor.YELLOW,game.sniper,game.spy);
            NPCGlowHelper.addGlow(spyNPC.npc.getEntity(),ChatColor.GREEN,game.spy);
            NPCGlowHelper.addGlow(suspected1.npc.getEntity(),ChatColor.YELLOW,game.sniper);
            NPCGlowHelper.addGlow(suspected2.npc.getEntity(),ChatColor.YELLOW,game.sniper);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Game createGame(Player spy, Player sniper) {
        Game game = new Game();
        game.id = new Random().nextInt(100000);
        game.spy = spy;
        game.sniper = sniper;

        games.add(game);
        return game;
    }

    public static void createGameWorld(Game game, Map map) {
        game.map = map;
        PluginCore.getPlugin(PluginCore.class).getLogger().info("Creating game #" + game.id);

        if (Multiverse.getWorldManager().cloneWorld(map.world.getName(), "game-" + game.id)) {
            game.gameWorld = Multiverse.getWorldManager().getMVWorld("game-" + game.id);
        } else {
            PluginCore.getPlugin(PluginCore.class).getLogger().severe("Failed to create game world");
        }
    }

    public static List<Game> getGames() {
        return games;
    }

    public static List<Player> getQueues() {
        return queues;
    }

    private static Location getMapPointFromGame(Game game, Location location) {
        return new Location(game.gameWorld.getCBWorld(), location.x(), location.y(), location.z(), location.getYaw(), location.getPitch());
    }

    public static Game getPlayerGame(Player player) {
        for (Game game : games) {
            if (game.sniper == player || game.spy == player) {
                return game;
            }
        }
        return null;
    }

    public static boolean playerInGame(Player player) {
        return getPlayerGame(player) != null;
    }

    public static void nextRound(Game game) {
        game.round++;

        if (game.round > 4) {
            endGame(game);
        } else {
            if (game.round > 1)
                resetRound(game);
            game.started = true;
            game.sendMessage(Component.text("Starting round " + game.round).color(ColorConstants.BLUE));
            game.roundInfo = new RoundInfo();
            Player oldSpy = game.spy;
            game.spy = game.sniper;
            game.sniper = oldSpy;
            game.spy.setGameMode(GameMode.SURVIVAL);
            game.spy.hidePlayer(PluginCore.getPlugin(PluginCore.class), game.sniper);
            game.sniper.hidePlayer(PluginCore.getPlugin(PluginCore.class), game.spy);
            game.sniper.setGameMode(GameMode.ADVENTURE);
            PluginCore.getPlugin(PluginCore.class).getLogger().info(game.map.sniperSpawn.x()+1 + "");
            game.sniper.teleport(getMapPointFromGame(game, game.map.sniperSpawn.toCenterLocation()));

            generateNPCs(game);

            generateObjects(game);
            generateMissions(game);

            game.spy.setAllowFlight(true);
            game.sniper.setAllowFlight(true);
            game.spy.setFlying(true);
            game.sniper.setFlying(true);

            game.spy.sendMessage(Component.text("You are SPY").color(ColorConstants.BLUE));
            game.sniper.sendMessage(Component.text("You are SNIPER").color(ColorConstants.BLUE));

            game.roundInfo.spyBoard = new FastBoard(game.spy);
            game.roundInfo.spyBoard.updateTitle(Component.text("SPY").color(ColorConstants.RED));
            game.roundInfo.sniperBoard = new FastBoard(game.sniper);
            game.roundInfo.sniperBoard.updateTitle(Component.text("SNIPER").color(ColorConstants.BLUE));

            game.updateSpyInventory(null);
            game.updateSniperInventory();

            game.roundInfo.roundLoop = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!games.isEmpty()) {
                        for (Game game : games) {
                            if (game.started) {
                                game.updateNPCs();
                                game.roundInfo.time++;
                                game.updateBoards();
                                if (game.roundInfo.gameMissions.stream().allMatch(GameMission::isDone)) {
                                    game.sendMessage(Component.text("SPY won by completing all missions!").color(ColorConstants.GREEN));
                                    game.started = false;
                                    nextRound(game);
                                }
                                if (game.roundInfo.time > GameManager.ROUND_TIME) {
                                    game.sendMessage(Component.text("SNIPER won by time!").color(ColorConstants.GREEN));
                                    game.started = false;
                                    nextRound(game);
                                }
                            }
                        }
                    }
                }
            }.runTaskTimer(PluginCore.getPlugin(PluginCore.class), 0, 20L);
            game.roundInfo.particleLoop = new BukkitRunnable() {

                @Override
                public void run() {
                    if (!games.isEmpty()) {
                        Location startLocation = game.sniper.getEyeLocation();
                        Vector direction = startLocation.getDirection();
                        for (int i = 0; i < 20; i++) {
                            Location particleLocation = startLocation.clone().add(direction.clone().multiply(i));
                            game.spy.spawnParticle(Particle.DUST, particleLocation, 0,
                                    new Particle.DustOptions(Color.RED, 1.0f));
                        }
                    }
                }
            }.runTaskTimer(PluginCore.getPlugin(PluginCore.class), 0, Math.min(Bukkit.getServer().getOnlinePlayers().size(), 6));
        }
    }

    private static void generateMissions(Game game) {
        for (MissionData missionData : missions) {
            game.roundInfo.gameMissions.add(new GameMission(missionData));
        }
    }

    private static void generateObjects(Game game) {
        for (MapObject mapObject : game.map.mapObjects) {
            mapObject.interactionPoint.blocks.clear();
            mapObject.interactionPoint.buttons.clear();

            game.gameWorld.getCBWorld().getBlockAt(mapObject.sign.getLocation()).setType(Material.AIR);
            mapObject.executeInGame(game);


            mapObject.createPoint(game);



            for (Block block : mapObject.interactionPoint.blocks) {
                try {
                    PluginCore.getGlowingBlocks().setGlowing(block, game.spy, ChatColor.BLUE);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void resetRound(Game game) {
        game.roundInfo.roundLoop.cancel();
        game.roundInfo.particleLoop.cancel();
        for (GameNPC gameNPC : game.roundInfo.gameNPCs) {
            CitizensAPI.getNPCRegistry().deregister(gameNPC.npc);
            gameNPC.delete();
        }
        game.roundInfo.gameNPCs.clear();
        game.spy.setGameMode(GameMode.ADVENTURE);
        game.sniper.setGameMode(GameMode.ADVENTURE);
        game.spy.showPlayer(PluginCore.getPlugin(PluginCore.class), game.sniper);
        game.sniper.showPlayer(PluginCore.getPlugin(PluginCore.class), game.spy);
        game.spy.getInventory().clear();
        game.sniper.getInventory().clear();
        game.spy.setAllowFlight(false);
        game.sniper.setAllowFlight(false);
        game.spy.setFlying(false);
        game.sniper.setFlying(false);

        game.roundInfo.statueData.clear();

        game.roundInfo.spyBoard.delete();
        game.roundInfo.spyBoard=null;
        game.roundInfo.sniperBoard.delete();
        game.roundInfo.sniperBoard=null;

        game.roundInfo.gameMissions.clear();

        game.roundInfo = null;
    }

    public static void endGame(Game game) {
        resetRound(game);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + game.sniper.getName());
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + game.spy.getName());
        game.sendMessage(Component.text("Game ended!").color(ColorConstants.RED));
        games.remove(game);
        Multiverse.getWorldManager().removePlayersFromWorld(game.gameWorld.getName());
        Multiverse.getWorldManager().deleteWorld(game.gameWorld.getName());
        Multiverse.getWorldManager().removeWorldFromConfig(game.gameWorld.getName());
    }

    public static void startGame(Game game) {
        if (game.started) return;
        startVoting(game);

    }

    public static void startVoting(Game game) {
        if (game.voting) return;
        game.voting = true;
        ArrayList<String> maps = new ArrayList<>(MapManager.getMapList());
        ArrayList<String> mapChoices = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String map = maps.get(new Random().nextInt(maps.size()));
            maps.remove(map);
            mapChoices.add(map);
        }
        game.sendMessage(Component.text("Banning Phase").color(ColorConstants.BLUE).decorate(TextDecoration.BOLD));
        game.sendMessage(Component.text(game.spy.getName() + " is banning..").color(ColorConstants.BLUE));
        game.spy.sendMessage(Component.text("Click on which map you want to ban."));
        sendVoteOptions(game, mapChoices, game.spy);
    }

    private static boolean hasVoted(Game game, int index) {
        return game.spyVote == index || game.sniperVote == index;
    }

    private static boolean hasVoted(Game game, Player player) {
        if (game.spy == player) {
            return game.spyVote != -1;
        }
        if (game.sniper == player) {
            return game.sniperVote != -1;
        }
        return false;
    }

    public static void sendVoteOptions(Game game, ArrayList<String> mapChoices, Player player) {
        for (int i = 0; i < mapChoices.size(); i++) {
            String map = mapChoices.get(i);
            int finalI = i;
            Component c = Component.text("[BAN]").color(ColorConstants.GOLD).clickEvent(ClickEvent.callback(audience -> {
                if (!game.voting || hasVoted(game, player)) {
                    player.sendMessage(Component.text("Game expired.").color(ColorConstants.RED));
                    return;
                }
                vote(game, mapChoices, finalI, player);
            })).asComponent();
            player.sendMessage(Component.text("- " + map).color(ColorConstants.BLUE).append((hasVoted(game, i) ? Component.empty() : Component.space().append(c))).decorate((hasVoted(game, i)) ? TextDecoration.STRIKETHROUGH : TextDecoration.BOLD));
        }
        startVoteTimer(game, mapChoices, player);
    }

    public static void vote(Game game, ArrayList<String> mapChoices, int option, Player player) {
        if (!game.voting) return;
        if (game.spy == player) {
            game.spyVote = option;
            game.spy.sendMessage(Component.text("Banned " + mapChoices.get(option)));
            sendVoteOptions(game, mapChoices, game.sniper);
        } else if (game.sniper == player) {
            game.sniperVote = option;
            game.sniper.sendMessage(Component.text("Banned " + mapChoices.get(option)));
            mapChoices.remove(game.spyVote);
            mapChoices.remove((game.sniperVote < game.spyVote ? 0 : game.sniperVote-1));
            beginGame(game, mapChoices.getFirst());
        }
    }

    public static void startVoteTimer(Game game,ArrayList<String> mapChoices, Player player) {
        if (!game.voting) return;
        boolean isSpy = (game.spy == player);
        new BukkitRunnable() {

            @Override
            public void run() {
                if (isSpy) {
                    if (!hasVoted(game, player)) {
                        for (int i = 0; i < mapChoices.size(); i++) {
                            if (!hasVoted(game, player)) {
                                vote(game, mapChoices, i, player);
                                break;
                            }
                        }
                        player.sendMessage(Component.text("You took too long").color(ColorConstants.RED));
                    }
                } else {
                    if (!hasVoted(game, player)) {
                        for (int i = 0; i < mapChoices.size(); i++) {
                            if (!hasVoted(game, player)) {
                                if (!hasVoted(game, i)) {
                                    vote(game, mapChoices, i, player);
                                    break;
                                }
                            }
                        }
                        player.sendMessage(Component.text("You took too long").color(ColorConstants.RED));
                    }
                }
            }
        }.runTaskLater(PluginCore.getPlugin(PluginCore.class), 200);
    }

    public static void beginGame(Game game, String map) {
        Map m = MapManager.serializeMap(map);
        game.sendMessage(Component.text("Loading map " + map));
        createGameWorld(game, m);
        nextRound(game);
        game.started = true;
    }

    public static void afterActionReport(Game game, GameNPC shot) {
        if (game.roundInfo.spy == shot) {

            game.sendMessage(Component.text("Spy was shot!").color(ColorConstants.BLUE).decorate(TextDecoration.BOLD));
            game.sendMessage(Component.text("Spy: " + game.roundInfo.spy.npc.getName()).color(ColorConstants.BLUE));
        } else {
            game.sendMessage(Component.text("Civilian was shot!").color(ColorConstants.RED).decorate(TextDecoration.BOLD));
            game.sendMessage(Component.text("Spy: " + game.roundInfo.spy.npc.getName()).color(ColorConstants.RED));
            game.sendMessage(Component.text("Civilian: " + shot.npc.getName()).color(ColorConstants.RED));
        }
        for (GameNPC gameNPC : game.roundInfo.gameNPCs) {
            gameNPC.cancelAction();
        }
    }

    /**
     * EVENTS
     */

    public static void onBlockBroken(Game game, BlockBreakEvent event) {
        event.setCancelled(true);
    }

    public static void onBlockPlaced(Game game, BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    public static void onInteract(Game game, PlayerInteractEvent event) {

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem() != null && event.getItem().getType() != Material.BOW)
                event.setCancelled(true);
            if (event.getHand() == EquipmentSlot.HAND) {
                if (event.getItem() == null) return;
                if (event.getClickedBlock() == null) return;
                if (event.getPlayer() == game.spy && event.getItem().getType()==Material.BLAZE_ROD) {
                    game.spy.sendMessage(Component.text("Moving..").color(ColorConstants.BLUE));
                    if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.BLAZE_ROD) return;
                    if (!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().displayName().contains(ItemNames.ITEM_MOVE)) return;
                    if (game.roundInfo.spy == null) return;
                    if (game.npcAtBlock(event.getClickedBlock())) {
                        game.spy.sendMessage(Component.text("Someone is already moving there...").color(ColorConstants.RED));
                        return;
                    }
                    InteractionPoint point = game.getPointByBlock(event.getClickedBlock());
                    game.roundInfo.spy.targetBlock = new GameNPC.PointData(point, event.getClickedBlock());
                    game.roundInfo.spy.moveTo(event.getClickedBlock().getLocation().add(0,1,0).toCenterLocation());
                    return;
                }
            }
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (event.getPlayer() == game.spy) {
                if (event.getHand() == EquipmentSlot.HAND) {
                    if (game.roundInfo.spy.currentBlock != null && event.getItem() != null) {
                        InteractionButton button = game.getButtonByItem(game.roundInfo.spy.currentBlock.point, event.getItem());
                        if (button != null) {
                            button.interact(game.roundInfo.spy);
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    public static void onShoot(Game game, EntityShootBowEvent event) {
        Player player = (Player) event.getEntity();
        event.setCancelled(true);
        if (game.sniper == player) {
            RayCastUtility.EntityRayCastResult target = RayCastUtility.rayCastEntities(player, 50, true, RayCastUtility.Precision.SEMI_ACCURATE_ENTITY);
            if (target.isEmpty() || (!target.isEmpty() && game.getGameNPCByEntity(target.getEntity()) == null)) {
                game.sniper.sendMessage(Component.text("Invalid target.").color(ColorConstants.RED));
                game.updateSniperInventory();
                return;
            }
            GameNPC npc = game.getGameNPCByEntity(target.getEntity());
            npc.npc.getOrAddTrait(SleepTrait.class).setSleeping(npc.npc.getEntity().getLocation());
            game.started = false;
            game.spy.getInventory().clear();
            game.sniper.getInventory().clear();
            afterActionReport(game, npc);
            new BukkitRunnable() {
                @Override
                public void run() {
                    nextRound(game);
                }
            }.runTaskLater(PluginCore.getPlugin(PluginCore.class), 5*20);
        }
    }

    public static void onQuit(Play event) {

    }
}
