package dev.sterner.brewinandchewin;

import dev.sterner.brewinandchewin.common.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BrewinAndChewin implements ModInitializer {
    public static final String MODID = "brewinandchewin";
    public static final ResourceKey<CreativeModeTab> ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MODID, "main"));

    public static final Logger LOGGER = LogManager.getLogger();
    public static final boolean DEBUG_MODE = false;

    @Override
    public void onInitialize() {
        BCObjects.init();
        BCBlockEntityTypes.init();
        BCStatusEffects.init();
        BCRecipeTypes.init();
        BCScreenHandlerTypes.init();
        BCLootFunctionsRegistry.init();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ITEM_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(BCObjects.BEER))
                .title(Component.translatable(MODID + ".group.main"))
                .build());
    }
}
