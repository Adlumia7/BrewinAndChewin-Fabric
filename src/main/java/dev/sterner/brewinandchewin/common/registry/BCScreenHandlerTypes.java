package dev.sterner.brewinandchewin.common.registry;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.screen.KegBlockScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public interface BCScreenHandlerTypes {
    ExtendedScreenHandlerType<KegBlockScreenHandler> KEG_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(KegBlockScreenHandler::new);


    static void init() {
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(BrewinAndChewin.MODID, "keg_screen"), KEG_SCREEN_HANDLER);
    }
}
