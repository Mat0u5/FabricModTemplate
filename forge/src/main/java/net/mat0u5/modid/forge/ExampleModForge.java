package net.mat0u5.modid.forge;

import net.minecraftforge.fml.common.Mod;

import net.mat0u5.modid.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModForge {
    public ExampleModForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
