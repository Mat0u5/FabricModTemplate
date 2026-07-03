package net.mat0u5.modid.mixin;

import net.mat0u5.modid.Main;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(MinecraftServer.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class ExampleMixin {

	@Inject(method = "<init>", at = @At("RETURN"))
	private void serverInit(CallbackInfo ci) {
		Main.LOGGER.info("[{}] Server Init!", Main.MOD_ID);
		List<String> list = List.of("Test", "Downgrader");
		Map<String, String> map = Map.of("Key", "Value");

		System.out.println("[DowngraderTest] Success! List: " + list);
		System.out.println("[DowngraderTest] Success! Map: " + map);
	}

	//? if forge && <= 1.14 {
	/*@Inject(method = "loadAllWorlds", at = @At("RETURN"))
	*///?} else if forge && <= 1.15 {
	/*@Inject(method = "loadInitialChunks", at = @At("HEAD"))
	*///?} else if forge && <= 1.16 {
	/*@Inject(method = "func_240800_l__", at = @At("RETURN"))
	*///?} else {
	@Inject(method = "loadLevel", at = @At("RETURN"))
	//?}
	private void afterLoadLevel(CallbackInfo ci) {
		Main.LOGGER.info("[{}] Level Loaded!", Main.MOD_ID);
	}

}
