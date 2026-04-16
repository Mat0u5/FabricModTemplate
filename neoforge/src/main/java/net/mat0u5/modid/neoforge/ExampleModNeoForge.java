package net.mat0u5.modid.neoforge;

import net.mat0u5.modid.ExampleMod;
import net.neoforged.fml.common.Mod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
