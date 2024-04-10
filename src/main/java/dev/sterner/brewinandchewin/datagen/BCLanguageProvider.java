package dev.sterner.brewinandchewin.datagen;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.registry.BCObjects;
import dev.sterner.brewinandchewin.common.registry.BCStatusEffects;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import java.util.HashSet;
import java.util.Set;

public class BCLanguageProvider extends FabricLanguageProvider {
    protected BCLanguageProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        Set<Block> blocks = new HashSet<>(BCObjects.BLOCKS.keySet());
        Set<Item> items = new HashSet<>(BCObjects.ITEMS.keySet());
        Set<MobEffect> effects = new HashSet<>(BCStatusEffects.STATUS_EFFECTS.keySet());
        blocks.removeIf(b -> b instanceof WallTorchBlock);
        blocks.removeIf(b -> b instanceof WallSignBlock);
        blocks.forEach(b -> {
            String name = b.getDescriptionId().replaceFirst("block.brewinandchewin.", "");
            name = toTitleCase(correctBlockItemName(name), "_").replaceAll("Of", "of");
            builder.add(b.getDescriptionId(), name);
        });
        items.removeIf(i -> i instanceof BlockItem);
        items.forEach(i ->
        {
            String name = i.getDescriptionId().replaceFirst("item.brewinandchewin.", "");
            name = toTitleCase(correctBlockItemName(name), "_").replaceAll("Of", "of");
            builder.add(i.getDescriptionId(), name);
        });

        effects.forEach(e -> {
            String name = toTitleCase(e.getDescriptionId().replaceFirst("effect.brewinandchewin.", ""), "_");
            builder.add(e.getDescriptionId(), name);
        });

        builder.add(BrewinAndChewin.MODID + ".group.main", "Brewin' and Chewin'");
        builder.add("brewinandchewin.container.keg", "Keg");
        builder.add("brewinandchewin.tooltip.tipsy1", "Tipsy I (%s:00)");
        builder.add("brewinandchewin.tooltip.tipsy2", "Tipsy II (%s:00)");
        builder.add("brewinandchewin.tooltip.tipsy3", "Tipsy III (%s:00)");
        builder.add("brewinandchewin.jei.fermenting", "Fermenting");
        builder.add("brewinandchewin.rei.fermenting", "Fermenting");
        builder.add("emi.category.brewinandchewin.fermenting", "Fermenting");
        builder.add("brewinandchewin.container.keg.served_in", "Served in: %s");
        builder.add("brewinandchewin.tooltip.keg.empty", "Empty");
        builder.add("brewinandchewin.tooltip.keg.single_serving", "Holds 1 serving of:");
        builder.add("brewinandchewin.tooltip.keg.many_servings", "Holds %s serving of:");
        builder.add("brewinandchewin.tooltip.dread_nog", "Bad Omen (10:00)");
        builder.add("brewinandchewin.block.cheese.use_knife", "You need a Knife to cut this.");

        addTemperature("frigid", builder);
        addTemperature("cold", builder);
        addTemperature("normal", builder);
        addTemperature("warm", builder);
        addTemperature("hot", builder);
/*
        builder.add(BCObjects.QUICHE, "Quiche");
        builder.add(BCObjects.QUICHE_SLICE, "Quiche Slice");
        builder.add(BCObjects.FIERY_FONDUE, "Fiery Fondue");
        builder.add(BCObjects.HORROR_LASAGNA, "Horror Lasagna");
        builder.add(BCObjects.CREAMY_ONION_SOUP, "Creamy Onion Soup");
        builder.add(BCObjects.SCARLET_PIEROGIES, "Scarlet Pierogies");

 */

    }

    public void addTemperature(String temperature, TranslationBuilder builder) {
        builder.add("brewinandchewin.container.keg." + temperature, "Temperature: " + toTitleCase(temperature, "_"));
    }

    public static String toTitleCase(String givenString, String regex) {
        String[] stringArray = givenString.split(regex);
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : stringArray) {
            stringBuilder.append(Character.toUpperCase(string.charAt(0))).append(string.substring(1)).append(regex);
        }
        return stringBuilder.toString().trim().replaceAll(regex, " ").substring(0, stringBuilder.length() - 1);
    }

    public String correctBlockItemName(String name) {
        if ((!name.endsWith("_bricks"))) {
            if (name.contains("bricks")) {
                name = name.replaceFirst("bricks", "brick");
            }
        }
        if (name.contains("_fence") || name.contains("_button")) {
            if (name.contains("planks")) {
                name = name.replaceFirst("_planks", "");
            }
        }
        return name;
    }
}
