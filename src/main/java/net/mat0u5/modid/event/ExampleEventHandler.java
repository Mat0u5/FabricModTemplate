package net.mat0u5.modid.event;

import net.mat0u5.modid.Main;
import net.minecraft.server.level.ServerPlayer;

public class ExampleEventHandler {

	public static void onPlayerHurt(ServerPlayer player) {
		Main.LOGGER.info("{} took damage.", player.getDisplayName());
	}
}
