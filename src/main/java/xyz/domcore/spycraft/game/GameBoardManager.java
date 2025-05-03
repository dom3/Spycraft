package xyz.domcore.spycraft.game;

import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import xyz.domcore.spycraft.ColorConstants;

import java.util.ArrayList;
import java.util.Arrays;

public class GameBoardManager {
    public static void baseBoard(Game game, FastBoard board, boolean spy, ArrayList<Component> lines) {
        ArrayList<Component> l = new ArrayList<>();
        l.add(Component.empty());
        l.add(Component.text("Round:").appendSpace().append(Component.text(game.round).color(ColorConstants.getColorScheme(spy))));
        l.add(Component.text("Time:").appendSpace().append(Component.text(GameManager.ROUND_TIME - game.roundInfo.time).color(ColorConstants.getColorScheme(spy))));
        l.add(Component.empty());
        l.addAll(lines);
        l.add(Component.empty());
        l.add(Component.text("Spycraft").color(ColorConstants.getColorScheme(spy)));
        board.updateLines(
            l
        );
    }
}
