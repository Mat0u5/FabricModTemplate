package net.mat0u5.modid.platform.fabric;

//? fabric {

import net.mat0u5.modid.event.ExampleEventHandler;
import net.fabricmc.fabric.api.entity.event.v1.*;
import net.minecraft.server.level.ServerPlayer;

public class FabricEventSubscriber {

	public static void registerEvents() {
		//? if <= 1.19 {
		/*ServerPlayerEvents.ALLOW_DEATH.register((entity, source, damageTaken) -> {
			if (entity instanceof ServerPlayer && damageTaken > 0) {
				ExampleEventHandler.onPlayerHurt((ServerPlayer) entity);
			}
			return true;
		});
		*///?} else if <= 1.21 {
		/*ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, damageTaken) -> {
			if (entity instanceof ServerPlayer && damageTaken > 0) {
				ExampleEventHandler.onPlayerHurt((ServerPlayer) entity);
			}
			return true;
		});
		*///?} else {
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
			if (entity instanceof ServerPlayer && damageTaken > 0) {
				ExampleEventHandler.onPlayerHurt((ServerPlayer) entity);
			}
		});
		//?}
	}
}
//?}
