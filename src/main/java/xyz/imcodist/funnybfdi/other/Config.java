package xyz.imcodist.funnybfdi.other;

import eu.midnightdust.lib.config.MidnightConfig;

public class Config extends MidnightConfig {
    @Entry public static boolean enabled = true;

    @Comment(centered = true) public static Comment mouthCategory;
    @Entry(isSlider = true, min = 0.2f, max = 1.8f) public static float mouthSpeed = 1.0f;
    @Entry public static boolean mouthTransitions = true;

    @Comment(centered = true) public static Comment mouthTransformCategory;
    @Entry(isSlider = true, min = 0.0f, max = 2.0f) public static float mouthSize = 1.0f;
    @Entry(min = -150.0f, max = 150.0f) public static float mouthOffsetX = 0.0f;
    @Entry(min = -150.0f, max = 150.0f) public static float mouthOffsetY = 0.0f;
    @Entry(min = -150.0f, max = 150.0f) public static float mouthOffsetZ = 0.0f;
}
