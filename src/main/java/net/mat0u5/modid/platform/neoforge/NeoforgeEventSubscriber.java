package net.mat0u5.modid.platform.neoforge;

//? neoforge {

/*import net.mat0u5.modid.Main;
import net.mat0u5.modid.event.ExampleEventHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
//? if <= 1.20.3 {
/^import net.neoforged.fml.common.Mod;
^///?} else {
import net.neoforged.fml.common.EventBusSubscriber;
 //?}

//? if <= 1.20.3 {
/^@Mod.EventBusSubscriber
^///?} else {
@EventBusSubscriber
 //?}
public class NeoforgeEventSubscriber {

	//? if <= 1.20.5 {
	/^@SubscribeEvent
	public static void onPlayerDamage(LivingDamageEvent event) {
		if (event.getEntity() instanceof ServerPlayer player && event.getAmount() > 0) {
			ExampleEventHandler.onPlayerHurt(player);
		}
	}
	^///?} else {
	@SubscribeEvent
	public static void onPlayerDamage(LivingDamageEvent.Post event) {
		if (event.getEntity() instanceof ServerPlayer player && event.getNewDamage() > 0) {
			ExampleEventHandler.onPlayerHurt(player);
		}
	}
	//?}
}
*///?}
