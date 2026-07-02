package net.mat0u5.modid.event;

import net.mat0u5.modid.Main;
//? if <= 1.16 {
/*import net.minecraft.entity.player.ServerPlayerEntity;
*///?} else {
import net.minecraft.server.level.ServerPlayer;
 //?}

public class ExampleEventHandler {

	//? if <= 1.16 {
	/*public static void onPlayerHurt(ServerPlayerEntity player) {
	*///?} else {
	public static void onPlayerHurt(ServerPlayer player) {
	 //?}
		Main.LOGGER.info("{} took damage.", player.getDisplayName());
	}
}
