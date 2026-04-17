package net.mat0u5.modid.event;

import net.mat0u5.modid.ModTemplate;
import net.minecraft.server.level.ServerPlayer;

public class ExampleEventHandler {

	public static void onPlayerHurt(ServerPlayer player) {
		ModTemplate.LOGGER.info("{} took damage. PVP is disallowed.", player.getDisplayName());
		// MinecraftServer.motd is private... only here to test ATs/AWs
		//? if > 1.19.2 {
		ModTemplate.LOGGER.info("Server MOTD: {}", player.level().getServer().motd);
		//?}
	}
}
