package dev.sterner.brewinandchewin.datagen;

import dev.sterner.brewinandchewin.common.registry.BCObjects;
import dev.sterner.brewinandchewin.common.registry.BCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.function.Consumer;

public class BCRecipeProvider extends FabricRecipeProvider {
    public static final int FERMENTING_TIME = 12000;        // 5 seconds

    public BCRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    protected void generateRecipes(Consumer<FinishedRecipe> exporter) {

        //Brews
        BCKegRecipeBuilder.kegRecipe(BCObjects.BEER, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, Items.WATER_BUCKET, 3)
                .addIngredient(Items.WHEAT)
                .addIngredient(Items.WHEAT)
                .addIngredient(Items.WHEAT)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.VODKA, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, Items.WATER_BUCKET, 3)
                .addIngredient(Items.POTATO)
                .addIngredient(Items.POTATO)
                .addIngredient(Items.POTATO)
                .addIngredient(Items.WHEAT)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.MEAD, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, Items.HONEY_BOTTLE, 3)
                .addIngredient(Items.WHEAT)
                .addIngredient(Items.WHEAT)
                .addIngredient(Items.SWEET_BERRIES)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.RICE_WINE, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, Items.WATER_BUCKET, 3)
                .addIngredient(ModItems.RICE.get())
                .addIngredient(ModItems.RICE.get())
                .addIngredient(ModItems.RICE.get())
                .addIngredient(ModItems.RICE.get())
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.EGG_GROG, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, Items.MILK_BUCKET, 3)
                .addIngredient(Items.EGG)
                .addIngredient(Items.EGG)
                .addIngredient(ModItems.CABBAGE_LEAF.get())
                .addIngredient(Items.SUGAR)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.STRONGROOT_ALE, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.BEER, 3)
                .addIngredient(Items.BEETROOT)
                .addIngredient(Items.POTATO)
                .addIngredient(Items.BROWN_MUSHROOM)
                .addIngredient(Items.CARROT)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.SACCHARINE_RUM, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.MEAD, 4)
                .addIngredient(Items.SWEET_BERRIES)
                .addIngredient(Items.SUGAR_CANE)
                .addIngredient(Items.SUGAR_CANE)
                .addIngredient(Items.MELON_SLICE)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.PALE_JANE, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.RICE_WINE, 4)
                .addIngredient(Items.HONEY_BOTTLE)
                .addIngredient(ModItems.TREE_BARK.get())
                .addIngredient(Items.LILY_OF_THE_VALLEY)
                .addIngredient(Items.SUGAR)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.DREAD_NOG, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.EGG_GROG, 1)
                .addIngredient(Items.EGG)
                .addIngredient(Items.EGG)
                .addIngredient(Items.TURTLE_EGG)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.SALTY_FOLLY, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.VODKA, 2)
                .addIngredient(Items.SEA_PICKLE)
                .addIngredient(Items.DRIED_KELP)
                .addIngredient(Items.DRIED_KELP)
                .addIngredient(Items.SEAGRASS)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.STEEL_TOE_STOUT, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.STRONGROOT_ALE, 1)
                .addIngredient(Items.CRIMSON_FUNGUS)
                .addIngredient(Items.IRON_INGOT)
                .addIngredient(Items.NETHER_WART)
                .addIngredient(Items.WHEAT)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.GLITTERING_GRENADINE, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, Items.WATER_BUCKET, 2)
                .addIngredient(Items.GLOW_BERRIES)
                .addIngredient(Items.GLOW_INK_SAC)
                .addIngredient(Items.GLOWSTONE_DUST)
                .addIngredient(Items.GLOW_BERRIES)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.BLOODY_MARY, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.VODKA, 4)
                .addIngredient(ModItems.TOMATO.get())
                .addIngredient(ModItems.TOMATO.get())
                .addIngredient(ModItems.CABBAGE_LEAF.get())
                .addIngredient(Items.SWEET_BERRIES)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.RED_RUM, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.BLOODY_MARY, 5)
                .addIngredient(Items.CRIMSON_FUNGUS)
                .addIngredient(Items.NETHER_WART)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .addIngredient(Items.SHROOMLIGHT)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.WITHERING_DROSS, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.SALTY_FOLLY, 5)
                .addIngredient(Items.WITHER_ROSE)
                .addIngredient(Items.INK_SAC)
                .addIngredient(Items.NETHER_WART)
                .addIngredient(Items.BONE)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.KIMCHI, 2, FERMENTING_TIME, 0.6F, 3)
                .addIngredient(ModItems.CABBAGE_LEAF.get())
                .addIngredient(BCTags.VEGETABLES)
                .addIngredient(Items.KELP)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.JERKY, 3, FERMENTING_TIME, 0.6F, 3)
                .addIngredient(BCTags.RAW_MEATS)
                .addIngredient(BCTags.RAW_MEATS)
                .addIngredient(BCTags.RAW_MEATS)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.PICKLED_PICKLES, 2, FERMENTING_TIME, 0.6F, Items.HONEY_BOTTLE, 2)
                .addIngredient(Items.SEA_PICKLE)
                .addIngredient(Items.SEA_PICKLE)
                .addIngredient(Items.GLOWSTONE_DUST)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.KIPPERS, 3, FERMENTING_TIME, 0.6F, 3)
                .addIngredient(BCTags.RAW_FISHES)
                .addIngredient(BCTags.RAW_FISHES)
                .addIngredient(Items.KELP)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.COCOA_FUDGE, 1, FERMENTING_TIME, 0.6F, Items.MILK_BUCKET, 5)
                .addIngredient(Items.SUGAR)
                .addIngredient(Items.COCOA_BEANS)
                .addIngredient(Items.COCOA_BEANS)
                .build(exporter);

        //Foods
        BCKegRecipeBuilder.kegRecipe(BCObjects.UNRIPE_FLAXEN_CHEESE_WHEEL, 1, FERMENTING_TIME, 0.6F, Items.HONEYCOMB, Items.MILK_BUCKET, 4)
                .addIngredient(Items.BROWN_MUSHROOM)
                .addIngredient(Items.BROWN_MUSHROOM)
                .addIngredient(Items.SUGAR)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.UNRIPE_SCARLET_CHEESE_WHEEL, 1, FERMENTING_TIME, 0.6F, Items.HONEYCOMB, Items.MILK_BUCKET, 4)
                .addIngredient(Items.CRIMSON_FUNGUS)
                .addIngredient(Items.CRIMSON_FUNGUS)
                .addIngredient(Items.SUGAR)
                .build(exporter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, BCObjects.HAM_AND_CHEESE_SANDWICH, 2)
                .requires(Items.BREAD)
                .requires(ModItems.SMOKED_HAM.get())
                .requires(BCObjects.FLAXEN_CHEESE_WEDGE)
                .requires(Items.BREAD)
                .unlockedBy("has_cheese", InventoryChangeTrigger.TriggerInstance.hasItems(BCObjects.FLAXEN_CHEESE_WEDGE))
                .save(exporter);
        //Crafting
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, BCObjects.PIZZA)
                .pattern("fff")
                .pattern("mtp")
                .pattern("www")
                .define('w', Items.WHEAT)
                .define('m', Items.BROWN_MUSHROOM)
                .define('t', ModItems.TOMATO.get())
                .define('p', ModItems.BEEF_PATTY.get())
                .define('f', BCObjects.FLAXEN_CHEESE_WEDGE)
                .unlockedBy("has_cheese", InventoryChangeTrigger.TriggerInstance.hasItems(BCObjects.FLAXEN_CHEESE_WEDGE))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, BCObjects.KEG)
                .pattern("ipi")
                .pattern("ihi")
                .pattern("ppp")
                .define('i', Items.IRON_INGOT)
                .define('h', Items.HONEYCOMB)
                .define('p', ItemTags.PLANKS)
                .unlockedBy("has_honeycomb", InventoryChangeTrigger.TriggerInstance.hasItems(Items.HONEYCOMB))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, BCObjects.TANKARD, 4)
                .pattern("p p")
                .pattern("i i")
                .pattern("ppp")
                .define('i', Items.IRON_NUGGET)
                .define('p', ItemTags.PLANKS)
                .unlockedBy("has_nugget", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_NUGGET))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BCObjects.FERMENTATION_CONTROLLER, 1)
                .pattern("III")
                .pattern("SAN")
                .pattern("BRB")
                .define('I', Items.IRON_INGOT)
                .define('S', Items.SOUL_SOIL)
                .define('A', Items.AMETHYST_SHARD)
                .define('N', Items.NETHERRACK)
                .define('B', Items.BRICKS)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_amethyst", InventoryChangeTrigger.TriggerInstance.hasItems(Items.AMETHYST_SHARD))
                .save(exporter);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        generateRecipes(exporter);
    }
}
