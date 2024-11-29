package org.davidCMs.engine.utils;

import org.joml.Random;

public class GlobalRandom {
    public static final Random random = new Random(System.nanoTime());
}
