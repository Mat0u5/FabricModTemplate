package net.mat0u5.quilt;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import net.mat0u5.fabriclike.ExampleModFabricLike;

public final class ExampleModQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        // Run the Fabric-like setup.
        ExampleModFabricLike.init();
    }
}
