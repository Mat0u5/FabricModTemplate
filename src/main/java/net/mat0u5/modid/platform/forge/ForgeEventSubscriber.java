package net.mat0u5.modid.platform.forge;

//? forge {

/*import net.mat0u5.modid.Main;
import net.mat0u5.modid.event.ExampleEventHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
//? if <= 1.21.5 {
/^import net.minecraftforge.eventbus.api.SubscribeEvent;
^///?} else {
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
//?}
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MOD_ID)
public class ForgeEventSubscriber {

	@SubscribeEvent
	public static void onPlayerDamage(LivingDamageEvent event) {
		if (event.getEntity() instanceof ServerPlayer player && event.getAmount() > 0) {
			ExampleEventHandler.onPlayerHurt(player);
		}
	}
}
*///?}
