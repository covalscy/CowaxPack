package com.cowax.cowaxpack.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue IFF_RENDER_RANGE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("overlay");
        IFF_RENDER_RANGE = builder
                .comment("Maximum distance for rendering friendly IFF markers (in blocks)")
                .defineInRange("iffRenderRange", 768.0D, 32.0D, 4096.0D);
        builder.pop();

        SPEC = builder.build();
    }
}

