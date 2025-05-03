package xyz.domcore.spycraft.game;

import org.bukkit.block.Block;
import xyz.domcore.spycraft.map.MapObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InteractionPoint {
    public MapObject mapObject;
    public ArrayList<Block> blocks;
    public List<InteractionButton> buttons = new ArrayList<>();

    public InteractionPoint(MapObject mapObject, ArrayList<Block> blocks) {
        this.mapObject = mapObject;
        this.blocks = blocks;
    }

    public InteractionPoint setButtons(InteractionButton... buttons) {
        this.buttons.addAll(Arrays.asList(buttons));
        return this;
    }
}
