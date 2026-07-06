package net.mat0u5.modid.platform.forge;

//? forge {
/*//? if <= 1.12 {
/^import net.mat0u5.modid.Main;
//? if <= 1.7 {
/^¹import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
¹^///?} else {
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
//?}
//? if <= 1.9
//import net.minecraftforge.common.MinecraftForge;

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
		//? if <= 1.9
		//MinecraftForge.EVENT_BUS.register(new ForgeEventSubscriber());
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
