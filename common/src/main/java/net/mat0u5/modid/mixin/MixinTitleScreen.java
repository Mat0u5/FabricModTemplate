package net.mat0u5.modid.mixin;

import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("ModID");

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        LOGGER.info("Hello from example architectury common mixin!");
    }
}