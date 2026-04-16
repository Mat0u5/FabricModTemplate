package net.mat0u5.modid.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.mat0u5.modid.ExampleModClient;

public final class ExampleModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run the Fabric-like setup.
        ExampleModClient.init();
    }
}
