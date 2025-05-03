package xyz.domcore.spycraft.game;

import java.util.ArrayList;

public class InteractionPointBuilder {
    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        public ArrayList<InteractionPoint> interactionPoints = new ArrayList<>();

        public Builder() {
        }

        public Builder addPoint(InteractionPoint point) {
            this.interactionPoints.add(point);
            return this;
        }

        public ArrayList<InteractionPoint> build() {
            return interactionPoints;
        }
    }
}
