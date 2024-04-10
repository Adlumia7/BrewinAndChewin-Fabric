package dev.sterner.brewinandchewin.common.registry;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.*;
import dev.sterner.brewinandchewin.common.item.BoozeBlockItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import vectorwing.farmersdelight.common.item.ConsumableItem;

import java.util.LinkedHashMap;
import java.util.Map;

public interface BCObjects {
    Map<Block, ResourceLocation> BLOCKS = new LinkedHashMap<>();
    Map<Item, ResourceLocation> ITEMS = new LinkedHashMap<>();

    Item TANKARD = register("tankard", new Item(settings()));

    Block TANKARD_BLOCK = register("tankard", new TankardBlock(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);

    Block BEER_TANKARD_BLOCK = register("beer_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);
    Block BLOODY_MARY_TANKARD_BLOCK = register("bloody_mary_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);
    Block GLITTERING_GRENADINE_TANKARD_BLOCK = register("glittering_grenadine_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);
    Block EGG_GROG_TANKARD_BLOCK = register("egg_grog_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);
    Block MEAD_TANKARD_BLOCK = register("mead_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);
    Block RED_RUM_TANKARD_BLOCK = register("red_rum_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);
    Block PALE_JANE_TANKARD_BLOCK = register("pale_jane_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);
    Block RICE_WINE_TANKARD_BLOCK = register("rice_wine_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);
    Block SACCHARINE_RUM_TANKARD_BLOCK = register("saccharine_rum_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);
    Block SALTY_FOLLY_TANKARD_BLOCK = register("salty_folly_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);
    Block STEEL_TOE_STOUT_TANKARD_BLOCK = register("steel_toe_stout_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);
    Block STRONGROT_ALE_TANKARD_BLOCK = register("strongrot_ale_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);

    Block VODKA_TANKARD_BLOCK = register("vodka_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);
    Block WITHERING_DROSS_TANKARD_BLOCK = register("withering_dross_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);
    Block KOMBUCHA_TANKARD_BLOCK = register("kombucha_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);
    Block DREAD_NOG_TANKARD_BLOCK = register("dread_nog_tankard", new Block(FabricBlockSettings.copy(Blocks.SPRUCE_PLANKS)), settings(), false);


    Item BEER = register("beer", new BoozeBlockItem(TANKARD_BLOCK, 1, 8, drinkItem()));
    Item VODKA = register("vodka", new BoozeBlockItem(TANKARD_BLOCK, 1, 12, drinkItem()));
    Item MEAD = register("mead", new BoozeBlockItem(TANKARD_BLOCK, 1, 8, drinkItem()));
    Item RICE_WINE = register("rice_wine", new BoozeBlockItem(TANKARD_BLOCK, 1, 5, drinkItem().food(BCFoodComponents.RICE_WINE)));
    Item EGG_GROG = register("egg_grog", new BoozeBlockItem(TANKARD_BLOCK, 1, 0, drinkItem().food(BCFoodComponents.EGG_GROG)));
    Item STRONGROOT_ALE = register("strongroot_ale", new BoozeBlockItem(TANKARD_BLOCK, 2, 12, drinkItem().food(BCFoodComponents.STRONGROOT_ALE)));
    Item SACCHARINE_RUM = register("saccharine_rum", new BoozeBlockItem(TANKARD_BLOCK, 2, 8, drinkItem().food(BCFoodComponents.SACCHARINE_RUM)));
    Item PALE_JANE = register("pale_jane", new BoozeBlockItem(TANKARD_BLOCK, 2, 5, drinkItem().food(BCFoodComponents.PALE_JANE)));

    Item DREAD_NOG = register("dread_nog", new BoozeBlockItem(TANKARD_BLOCK, 3, 5, drinkItem()));

    Item SALTY_FOLLY = register("salty_folly", new BoozeBlockItem(TANKARD_BLOCK, 2, 10, drinkItem().food(BCFoodComponents.SALTY_FOLLY)));
    Item STEEL_TOE_STOUT = register("steel_toe_stout", new BoozeBlockItem(TANKARD_BLOCK, 3, 10, drinkItem().food(BCFoodComponents.STEEL_TOE_STOUT)));
    Item GLITTERING_GRENADINE = register("glittering_grenadine", new BoozeBlockItem(TANKARD_BLOCK, 1, 5, drinkItem().food(BCFoodComponents.GLITTERING_GRENADINE)));
    Item BLOODY_MARY = register("bloody_mary", new BoozeBlockItem(TANKARD_BLOCK, 1, 12, drinkItem().food(BCFoodComponents.BLOODY_MARY)));
    Item RED_RUM = register("red_rum", new BoozeBlockItem(TANKARD_BLOCK, 1, 18, drinkItem().food(BCFoodComponents.RED_RUM)));
    Item WITHERING_DROSS = register("withering_dross", new BoozeBlockItem(TANKARD_BLOCK, 3, 20, drinkItem().food(BCFoodComponents.WITHERING_DROSS)));
    Item KOMBUCHA = register("kombucha", new BoozeBlockItem(TANKARD_BLOCK, 1, 5, drinkItem().food(BCFoodComponents.KOMBUHCA)));

    Item KIMCHI = register("kimchi", new Item(settings().food(BCFoodComponents.KIMCHI)));
    Item JERKY = register("jerky", new Item(settings().food(BCFoodComponents.JERKY)));
    Item PICKLED_PICKLES = register("pickled_pickles", new Item(settings().food(BCFoodComponents.PICKLED_PICKLES)));
    Item KIPPERS = register("kippers", new Item(settings().food(BCFoodComponents.KIPPERS)));
    Item COCOA_FUDGE = register("cocoa_fudge", new Item(settings().food(BCFoodComponents.COCOA_FUDGE)));

    Item FLAXEN_CHEESE_WEDGE = register("flaxen_cheese_wedge", new Item(settings().food(BCFoodComponents.FLAXEN_CHEESE)));
    Item SCARLET_CHEESE_WEDGE = register("scarlet_cheese_wedge", new Item(settings().food(BCFoodComponents.SCARLET_CHEESE)));

    Item FIERY_FONDUE = register("fiery_fondue", new Item(settings().food(BCFoodComponents.FIERY_FONDUE)));
    Item QUICHE_SLICE = register("quiche_slice", new Item(settings().food(BCFoodComponents.QUICHE_SLICE)));

    Item VEGETABLE_OMELET = register("vegetable_omelet", new ConsumableItem(bowlFoodItem().food(BCFoodComponents.VEGETABLE_OMELET)));
    Item CHEESY_PASTA = register("cheesy_pasta", new ConsumableItem(bowlFoodItem().food(BCFoodComponents.CHEESY_PASTA)));
    Item CREAMY_ONION_SOUP = register("creamy_onion_soup", new ConsumableItem(bowlFoodItem().food(BCFoodComponents.CREAMY_ONION_SOUP)));
    Item SCARLET_PIEROGIES = register("scarlet_pierogies", new ConsumableItem(bowlFoodItem().food(BCFoodComponents.SCARLET_PIEROGIES)));
    Item HORROR_LASAGNA = register("horror_lasagna", new ConsumableItem(bowlFoodItem().food(BCFoodComponents.HORROR_LASAGNA)));

    Block QUICHE = register("quiche", new QuicheBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON)), settings(), true);
    Block FIERY_FONDUE_POT = register("fiery_fondue_pot", new FieryFonduePotBlock(BlockBehaviour.Properties.copy(Blocks.CAKE)), settings().stacksTo(1), true);


    Block FLAXEN_CHEESE_WHEEL = register("flaxen_cheese_wheel", new RipeCheeseWheelBlock(FLAXEN_CHEESE_WEDGE, FabricBlockSettings.copy(Blocks.CAKE)), settings().stacksTo(16), true);
    Block SCARLET_CHEESE_WHEEL = register("scarlet_cheese_wheel", new RipeCheeseWheelBlock(SCARLET_CHEESE_WEDGE, FabricBlockSettings.copy(Blocks.CAKE)), settings().stacksTo(16), true);
    Block UNRIPE_FLAXEN_CHEESE_WHEEL = register("unripe_flaxen_cheese_wheel", new UnripeCheeseWheelBlock(FLAXEN_CHEESE_WHEEL, FabricBlockSettings.copy(Blocks.CAKE)), settings().stacksTo(16), true);
    Block UNRIPE_SCARLET_CHEESE_WHEEL = register("unripe_scarlet_cheese_wheel", new UnripeCheeseWheelBlock(SCARLET_CHEESE_WHEEL, FabricBlockSettings.copy(Blocks.CAKE)), settings().stacksTo(16), true);

    Item PIZZA_SLICE = register("pizza_slice", new Item(settings().food(BCFoodComponents.PIZZA_SLICE)));
    Block PIZZA = register("pizza", new PizzaBlock(FabricBlockSettings.copy(Blocks.CAKE)), settings().stacksTo(16), true);

    Item HAM_AND_CHEESE_SANDWICH = register("ham_and_cheese_sandwich", new Item(settings().food(BCFoodComponents.HAM_AND_CHEESE_SANDWICH)));

    Block KEG = register("keg", new KegBlock(), settings(), true);
    Block COASTER = register("item_coaster", new ItemCoasterBlock(), settings(), true);
    Block FERMENTATION_CONTROLLER = register("fermentation_controller", new FermentationControllerBlock(), settings(), true);

    static Item.Properties drinkItem() {
        return (new Item.Properties()).craftRemainder(TANKARD).stacksTo(16);
    }

    static Item.Properties bowlFoodItem() {
        return (new Item.Properties()).craftRemainder(Items.BOWL).stacksTo(16);
    }

    static Item.Properties settings() {
        return new Item.Properties();
    }

    static <T extends Item> T register(String name, T item) {
        ITEMS.put(item, new ResourceLocation(BrewinAndChewin.MODID, name));
        return item;
    }

    static <T extends Block> T register(String name, T block, Item.Properties settings, boolean createItem) {
        BLOCKS.put(block, new ResourceLocation(BrewinAndChewin.MODID, name));
        if (createItem) {
            ITEMS.put(new BlockItem(block, settings), BLOCKS.get(block));
        }
        return block;
    }

    static void init() {
        BLOCKS.keySet().forEach(block -> Registry.register(BuiltInRegistries.BLOCK, BLOCKS.get(block), block));
        ITEMS.keySet().forEach(item -> Registry.register(BuiltInRegistries.ITEM, ITEMS.get(item), item));
        ItemGroupEvents.modifyEntriesEvent(BrewinAndChewin.ITEM_GROUP).register(entries -> ITEMS.keySet().forEach(entries::accept));
    }
}
