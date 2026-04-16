package net.mat0u5.forge;

import net.minecraftforge.fml.common.Mod;

import net.mat0u5.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModForge {
    public ExampleModForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
