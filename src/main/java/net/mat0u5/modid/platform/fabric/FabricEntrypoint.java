package net.mat0u5.modid.platform.fabric;

//? fabric {

import net.mat0u5.modid.Main;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		Main.onInitialize();
		FabricEventSubscriber.registerEvents();
	}
}
//?}
