package xyz.domcore.spycraft.game;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class InteractionButton implements Comparable<InteractionButton> {
    public String id;
    public Material material;
    public String name;
    public boolean missionButton;
    public Consumer<UseButtonEvent> onUse;

    @Override
    public int compareTo(@NotNull InteractionButton o) {
        return Boolean.compare(!this.missionButton, !o.missionButton);
    }

    public static class UseButtonEvent {
        public GameNPC npc;
        public GameNPC.PointData pointData;

        public UseButtonEvent(GameNPC npc, GameNPC.PointData pointData) {
            this.npc = npc;
            this.pointData = pointData;
        }
    }

    public InteractionButton(String id, Material material, String name, boolean missionButton, Consumer<UseButtonEvent> onUse) {
        this.id = id;
        this.material = material;
        this.name = name;
        this.missionButton = missionButton;
        this.onUse = onUse;
    }

    public void interact(GameNPC gameNPC) {
        this.onUse.accept(new InteractionButton.UseButtonEvent(gameNPC, gameNPC.currentBlock));
    }

    public boolean spyOnly() {
        return missionButton;
    }
}
