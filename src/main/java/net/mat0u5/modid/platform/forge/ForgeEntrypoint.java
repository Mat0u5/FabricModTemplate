package net.mat0u5.modid.platform.forge;

//? forge {
/*//? if <= 1.12 {
/^import net.mat0u5.modid.Main;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Main.MOD_ID, name = Main.MOD_ID, version = "${version}", acceptableRemoteVersions = "*")
public class ForgeEntrypoint {
	public ForgeEntrypoint() {
		Main.onInitialize();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		if (FMLCommonHandler.instance().getSide().isClient()) {
			Main.onInitializeClient();
		}
	}
}
^///?} else {
import net.mat0u5.modid.Main;
import net.minecraftforge.fml.common.Mod;

@Mod(Main.MOD_ID)
public class ForgeEntrypoint {

	public ForgeEntrypoint() {
		Main.onInitialize();
	}
}
//?}

*///?}
