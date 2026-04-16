package net.modid.neoforge;

import net.modid.ExampleExpectPlatform;
import net.neoforged.fml.loading.FMLPaths; // Changed package

import java.nio.file.Path;

public class ExampleExpectPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}