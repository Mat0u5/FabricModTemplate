package net.mat0u5.modid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	public static final String MOD_ID = "modid";
	public static final String MOD_VERSION = "1.0.0";
	public static final String MOD_FRIENDLY_NAME = "ModId Name";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void onInitialize() {
		LOGGER.info("Initializing {}", MOD_ID);
		LOGGER.info("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	public static void onInitializeClient() {
		LOGGER.info("Initializing {} Client", MOD_ID);
		LOGGER.info("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}
}
