package net.modid.fabric;

import net.modid.fabriclike.ExampleModFabricLike;
import net.fabricmc.api.ModInitializer;

public class ExampleModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ExampleModFabricLike.init();
    }
}
