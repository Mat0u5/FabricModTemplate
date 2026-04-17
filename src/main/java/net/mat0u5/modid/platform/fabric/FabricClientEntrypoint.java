package net.mat0u5.modid.platform.fabric;

//? fabric {

import net.mat0u5.modid.Main;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		Main.onInitializeClient();
	}

}
//?}
