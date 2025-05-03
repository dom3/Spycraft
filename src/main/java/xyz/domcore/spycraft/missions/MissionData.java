package xyz.domcore.spycraft.missions;

import net.kyori.adventure.text.Component;

public abstract class MissionData {
    public abstract Component name();
    public abstract int startingValue();
    public abstract int maxValue();
}
