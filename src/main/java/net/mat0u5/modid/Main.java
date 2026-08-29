package net.mat0u5.modid;

import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class Main {

	public static final String MOD_ID = "modid";
	public static final String MOD_VERSION = "1.0.0";
	public static final String MOD_FRIENDLY_NAME = "ModId Name";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static void onInitialize() {
		LOGGER.info("Initializing {}", MOD_ID);
		LOGGER.info("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	public static void serverInit(MinecraftServer server) {
		Main.LOGGER.info("[{}] Server Init!", Main.MOD_ID);
		List<String> list = List.of("Test", "Downgrader");
		Map<String, String> map = Map.of("Key", "Value");

		Main.LOGGER.info("[DowngraderTest] Success! List: " + list);
		Main.LOGGER.info("[DowngraderTest] Success! Map: " + map);

		try {
			//? if forge && <=1.15 {
			/*int ticks = server.tickCounter;
			 *///?} else {
			int ticks = server.tickCount;
			//?}
			Main.LOGGER.info("[AccessTest] OK - the private MinecraftServer tick counter is accessible (value: {})", ticks);
		} catch (Throwable t) {
			Main.LOGGER.error("[AccessTest] FAILED - the access widener / access transformer was not applied", t);
		}
	}

	public static void levelLoad() {
		Main.LOGGER.info("[{}] Level Loaded!", Main.MOD_ID);
	}
}
