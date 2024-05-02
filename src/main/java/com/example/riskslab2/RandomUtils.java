package com.example.riskslab2;

import java.util.Random;

public final class RandomUtils {

    private RandomUtils() {
        // Utility classes should not be instantiated
    }

    public static double getRandomNumberInRange(double start, double end) {
        return start + new Random().nextDouble(end - start);
    }
}
