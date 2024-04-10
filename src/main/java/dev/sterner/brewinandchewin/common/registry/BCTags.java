package dev.sterner.brewinandchewin.common.registry;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface BCTags {
    TagKey<Item> RAW_MEATS = TagKey.create(Registries.ITEM, new ResourceLocation(BrewinAndChewin.MODID, "raw_meats"));
    TagKey<Item> HORROR_LASAGNA_MEATS = TagKey.create(Registries.ITEM, new ResourceLocation(BrewinAndChewin.MODID, "horror_lasagna_meats"));
    TagKey<Item> OFFHAND_EQUIPMENT = TagKey.create(Registries.ITEM, new ResourceLocation(BrewinAndChewin.MODID, "offhand_equipment"));
    TagKey<Block> FREEZE_SOURCES = TagKey.create(Registries.BLOCK, new ResourceLocation(BrewinAndChewin.MODID, "freeze_sources"));

    TagKey<Item> RAW_FISHES = TagKey.create(Registries.ITEM, new ResourceLocation("c", "raw_fishes"));
    TagKey<Item> VEGETABLES = TagKey.create(Registries.ITEM, new ResourceLocation("c", "vegetables"));
}
