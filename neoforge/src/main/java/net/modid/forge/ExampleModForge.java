package net.modid.neoforge;

import dev.architectury.platform.neoforge.EventBuses; // Changed to neoforge
import net.modid.ExampleMod;
import net.neoforged.fml.common.Mod; // Changed package
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ExampleMod.MOD_ID)
public class ExampleModNeoForge {
    public ExampleModNeoForge(IEventBus modEventBus) {
        // NeoForge 1.20.1+ passes the modEventBus directly in the constructor!
        EventBuses.registerModEventBus(ExampleMod.MOD_ID, modEventBus);
        ExampleMod.init();
    }
}