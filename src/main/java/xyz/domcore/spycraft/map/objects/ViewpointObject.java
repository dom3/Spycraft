package xyz.domcore.spycraft.map.objects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.domcore.spycraft.game.Game;
import xyz.domcore.spycraft.game.InteractionButton;
import xyz.domcore.spycraft.game.InteractionPoint;
import xyz.domcore.spycraft.game.InteractionPointBuilder;
import xyz.domcore.spycraft.map.Map;
import xyz.domcore.spycraft.map.MapObject;
import xyz.domcore.spycraft.map.ObjectPriority;

import java.util.ArrayList;
import java.util.List;

public class ViewpointObject extends MapObject {
    public ViewpointObject() {
        id = "viewpoint";
    }

    @Override
    public boolean validate(List<Component> args) {
        return ((TextComponent)args.getFirst()).content().contains("[viewpoint]") && !((TextComponent) args.get(1)).content().isEmpty();
    }

    @Override
    public void executeInGame(Game game) {
        super.executeInGame(game);
    }

    @Override
    public Map addToMap(Map map) {
        map.viewpoints.add(this.sign.getLocation());
        return map;
    }

    @Override
    public void createPoint(Game game) {
        InteractionButton addTime = new InteractionButton("add-time", Material.CLOCK, "Add Time", true, (g) -> {
            g.npc.setItemInMainHand(Material.CLOCK);
            g.npc.startAction("Adding time...", 3, new BukkitRunnable() {
                @Override
                public void run() {
                    g.npc.clearMainHand();
                    game.roundInfo.time -= 30;
                }
            });
        });
        InteractionButton checkWatch = new InteractionButton("check-watch", Material.CLOCK, "Check Watch", false, (g) -> {
            g.npc.setItemInMainHand(Material.CLOCK);
            g.npc.startAction("Adding time...", 3, new BukkitRunnable() {
                @Override
                public void run() {
                    g.npc.clearMainHand();
                }
            });
        });
        ArrayList<Block> blocks = new ArrayList<>();
        InteractionPoint point = new InteractionPoint(this, blocks);
        if (sign.getSide(Side.FRONT).lines().get(1) instanceof TextComponent textComponent) {
            String[] offsets = textComponent.content().split(" "); //z 3
            if (offsets.length == 2) {
                String direction = offsets[0];
                int offset = Integer.parseInt(offsets[1]);

                for (int i = 0; i <= offset; i++) {
                    double x = (direction.equalsIgnoreCase("x") ? i : 0);
                    double z = (direction.equalsIgnoreCase("z") ? i : 0);
                    Block bottomBlock = game.gameWorld.getCBWorld().getBlockAt(sign.getLocation().subtract(0, 1, 0).add(x,0,z));
                    blocks.add(bottomBlock);
                }
            }
        }
        point.setButtons(
                addTime,
                checkWatch
        );
        interactionPoint = point;
    }

    @Override
    public boolean aiNavigable() {
        return true;
    }

    @Override
    public ObjectPriority objectPriority() {
        return ObjectPriority.RARE;
    }

    @Override
    public Location lookPosition(Block block) {
        BlockFace face = ((Sign) sign.getBlockData()).getRotation();
        return block.getLocation().add(0,2,0).add(face.getOppositeFace().getDirection().multiply(0.5));
    }
}
