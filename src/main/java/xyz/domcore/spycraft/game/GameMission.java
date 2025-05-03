package xyz.domcore.spycraft.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import xyz.domcore.spycraft.ColorConstants;
import xyz.domcore.spycraft.missions.MissionData;

import static xyz.domcore.spycraft.ColorConstants.getColorScheme;

public class GameMission {
    public MissionData data;
    public int currentValue;

    public GameMission(MissionData data) {
        this.data = data;
        this.currentValue = data.startingValue();
    }

    public void incrementData() {
        if (currentValue < data.maxValue()) {
            currentValue++;
        }
    }

    public boolean isDone() {
        return currentValue >= data.maxValue();
    }

    public Component display(boolean spy) {
        return spy ?
                Component.text("• ").append(data.name().append(Component.text(": ")).append(Component.text(currentValue + "/" + data.maxValue()).color(isDone() ? ColorConstants.GREEN : getColorScheme(true)))) :
                Component.text("• ").append(data.name());
    }
}
