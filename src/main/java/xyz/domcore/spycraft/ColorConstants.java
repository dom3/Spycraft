package xyz.domcore.spycraft;

import net.kyori.adventure.text.format.TextColor;

public class ColorConstants {
    public static final TextColor RED = TextColor.color(0xeb4034);
    public static final TextColor BLUE = TextColor.color(0x34c6eb);
    public static final TextColor GREEN = TextColor.color(0x5beb34);
    public static final TextColor GOLD = TextColor.color(0xfcba03);

    public static TextColor getColorScheme(boolean spy) {
        return spy ? RED : BLUE;
    }
}
