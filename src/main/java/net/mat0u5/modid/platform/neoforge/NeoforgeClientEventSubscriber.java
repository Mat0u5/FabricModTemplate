package net.mat0u5.modid.platform.neoforge;

//? neoforge {

/*import net.mat0u5.modid.Main;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
//? if <= 1.20.3 {
/^import net.neoforged.fml.common.Mod;
^///?} else {
import net.neoforged.fml.common.EventBusSubscriber;
//?}

//? if <= 1.20.3 {
/^@Mod.EventBusSubscriber(modid = Main.MOD_ID, value = Dist.CLIENT)
^///?} else {
@EventBusSubscriber(modid = Main.MOD_ID, value = Dist.CLIENT)
//?}
public class NeoforgeClientEventSubscriber {
	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		Main.onInitializeClient();
	}
}
*///?}
