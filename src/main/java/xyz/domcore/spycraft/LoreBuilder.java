package xyz.domcore.spycraft;

import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class LoreBuilder {
    public static Builder newBuilder() {
        return new Builder();
    }
    public static class Builder {
        List<Component> lore;

        public Builder() {
            lore = new ArrayList<>();
        }

        public Builder add(Component component) {
            lore.add(component);
            return this;
        }

        public List<Component> build() {
            return lore;
        }
    }
}
