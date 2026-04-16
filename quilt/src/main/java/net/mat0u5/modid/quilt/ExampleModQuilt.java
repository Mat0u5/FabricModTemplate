package net.mat0u5.modid.quilt;

import net.fabricmc.api.ModInitializer;

import net.mat0u5.modid.fabriclike.ExampleModFabricLike;

public final class ExampleModQuilt implements ModInitializer {

    @Override
    public void onInitialize() {
        // Run the Fabric-like setup.
        ExampleModFabricLike.init();
    }
}
