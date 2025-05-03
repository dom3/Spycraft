package xyz.domcore.spycraft;

import java.util.List;
import java.util.Random;

public class Utils {
    public static long convertSecToTick(int seconds) {
        return seconds * 20L;
    }

    public static <T> T getRandomObjectFromArray(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    public static long randomNumberLong(long min, long max) {
        return new Random().nextLong(max-min+1)+min;
    }
}
