package xyz.domcore.spycraft.missions;

import net.kyori.adventure.text.Component;

public class MissionBug extends MissionData{
    @Override
    public Component name() {
        return Component.text("Bug Ambassador");
    }

    @Override
    public int startingValue() {
        return 0;
    }

    @Override
    public int maxValue() {
        return 1;
    }
}
