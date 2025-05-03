package xyz.domcore.spycraft.missions;

import net.kyori.adventure.text.Component;

public class MissionSwap extends MissionData{
    @Override
    public Component name() {
        return Component.text("Swap Statue");
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
