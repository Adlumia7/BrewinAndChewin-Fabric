package dev.sterner.brewinandchewin.common.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.CheeseWheelBlock;
import dev.sterner.brewinandchewin.common.block.KegBlock;
import dev.sterner.brewinandchewin.common.block.PizzaBlock;
import dev.sterner.brewinandchewin.common.block.RipeCheeseWheelBlock;
import dev.sterner.brewinandchewin.common.item.BoozeItem;
import dev.sterner.brewinandchewin.common.item.DreadNogItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class BCObjects {
    public static final Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();
    public static final Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

    public static final Item TANKARD = register("tankard", new Item(settings()));

    public static final Item BEER = register("beer", new BoozeItem(1,8, settings().maxCount(16).recipeRemainder(TANKARD)));
    public static final Item VODKA = register("vodka", new BoozeItem(1,12, settings().maxCount(16).recipeRemainder(TANKARD)));
    public static final Item MEAD = register("mead", new BoozeItem(1,8, settings().maxCount(16).recipeRemainder(TANKARD)));
    public static final Item RICE_WINE = register("rice_wine", new BoozeItem(1,5, settings().maxCount(16).recipeRemainder(TANKARD).food(BCFoodComponents.RICE_WINE)));
    public static final Item EGG_GROG = register("egg_grog", new BoozeItem(1,0, settings().maxCount(16).recipeRemainder(TANKARD).food(BCFoodComponents.EGG_GROG)));
    public static final Item STRONGROOT_ALE = register("strongroot_ale", new BoozeItem(2,12, settings().maxCount(16).recipeRemainder(TANKARD).food(BCFoodComponents.STRONGROOT_ALE)));
    public static final Item SACCHARINE_RUM = register("saccharine_rum", new BoozeItem(2,8, settings().maxCount(16).recipeRemainder(TANKARD).food(BCFoodComponents.SACCHARINE_RUM)));
    public static final Item PALE_JANE = register("pale_jane", new BoozeItem(2,5, settings().maxCount(16).recipeRemainder(TANKARD).food(BCFoodComponents.PALE_JANE)));

    public static final Item DREAD_NOG = register("dread_nog", new DreadNogItem(3,5, settings().maxCount(16).recipeRemainder(TANKARD)));

    public static final Item SALTY_FOLLY = register("salty_folly", new BoozeItem(2,10, settings().maxCount(16).recipeRemainder(TANKARD).food(BCFoodComponents.SALTY_FOLLY)));
    public static final Item STEEL_TOE_STOUT = register("steel_toe_stout", new BoozeItem(3,10, settings().maxCount(16).recipeRemainder(TANKARD).food(BCFoodComponents.STEEL_TOE_STOUT)));
    public static final Item GLITTERING_GRENADINE = register("glittering_grenadine", new BoozeItem(1,5, settings().maxCount(16).recipeRemainder(TANKARD).food(BCFoodComponents.GLITTERING_GRENADINE)));
    public static final Item BLOODY_MARY = register("bloody_mary", new BoozeItem(1,12, settings().maxCount(16).recipeRemainder(TANKARD).food(BCFoodComponents.BLOODY_MARY)));
    public static final Item RED_RUM = register("red_rum", new BoozeItem(1,18, settings().maxCount(16).recipeRemainder(TANKARD).food(BCFoodComponents.RED_RUM)));
    public static final Item WITHERING_DROSS = register("withering_dross", new BoozeItem(3,20, settings().maxCount(16).recipeRemainder(TANKARD).food(BCFoodComponents.WITHERING_DROSS)));
    public static final Item KOMBUCHA = register("kombucha", new BoozeItem(1,5, settings().maxCount(16).recipeRemainder(TANKARD).food(BCFoodComponents.KOMBUHCA)));

    public static final Item KIMCHI = register("kimchi", new Item(settings().food(BCFoodComponents.KIMCHI)));
    public static final Item JERKY = register("jerky", new Item(settings().food(BCFoodComponents.JERKY)));
    public static final Item PICKLED_PICKLES = register("pickled_pickles", new Item(settings().food(BCFoodComponents.PICKLED_PICKLES)));
    public static final Item KIPPERS = register("kippers", new Item(settings().food(BCFoodComponents.KIPPERS)));
    public static final Item COCOA_FUDGE = register("cocoa_fudge", new Item(settings().food(BCFoodComponents.COCOA_FUDGE)));

    public static final Item FLAXEN_CHEESE_WEDGE = register("flaxen_cheese_wedge", new Item(settings().food(BCFoodComponents.FLAXEN_CHEESE)));
    public static final Item SCARLET_CHEESE_WEDGE = register("scarlet_cheese_wedge", new Item(settings().food(BCFoodComponents.SCARLET_CHEESE)));

    public static final Block KEG = register("keg", new KegBlock(), settings(), true);

    public static final Block FLAXEN_CHEESE_WHEEL = register("flaxen_cheese_wheel", new RipeCheeseWheelBlock(FLAXEN_CHEESE_WEDGE, FabricBlockSettings.copy(Blocks.CAKE)), settings().maxCount(16), true);
    public static final Block SCARLET_CHEESE_WHEEL = register("scarlet_cheese_wheel", new RipeCheeseWheelBlock(SCARLET_CHEESE_WEDGE, FabricBlockSettings.copy(Blocks.CAKE)), settings().maxCount(16), true);
    public static final Block UNRIPE_FLAXEN_CHEESE_WHEEL = register("unripe_flaxen_cheese_wheel", new CheeseWheelBlock(FLAXEN_CHEESE_WHEEL, FabricBlockSettings.copy(Blocks.CAKE)), settings().maxCount(16), true);
    public static final Block UNRIPE_SCARLET_CHEESE_WHEEL = register("unripe_scarlet_cheese_wheel", new CheeseWheelBlock(SCARLET_CHEESE_WHEEL, FabricBlockSettings.copy(Blocks.CAKE)), settings().maxCount(16), true);

    public static final Item PIZZA_SLICE = register("pizza_slice", new Item(settings().food(BCFoodComponents.PIZZA_SLICE)));
    public static final Block PIZZA = register("pizza", new PizzaBlock(FabricBlockSettings.copy(Blocks.CAKE)), settings().maxCount(16), true);

    public static final Item HAM_AND_CHEESE_SANDWICH = register("ham_and_cheese_sandwich", new Item(settings().food(BCFoodComponents.HAM_AND_CHEESE_SANDWICH)));

    private static Item.Settings settings() {
        return new Item.Settings().group(FarmersDelightMod.ITEM_GROUP);
    }

    private static <T extends Item> T register(String name, T item) {
        ITEMS.put(item, new Identifier(BrewinAndChewin.MODID, name));
        return item;
    }

    private static <T extends Block> T register(String name, T block, Item.Settings settings, boolean createItem) {
        BLOCKS.put(block, new Identifier(BrewinAndChewin.MODID, name));
        if (createItem) {
            ITEMS.put(new BlockItem(block, settings), BLOCKS.get(block));
        }
        return block;
    }

    public static void init() {
        BLOCKS.keySet().forEach(block -> Registry.register(Registry.BLOCK, BLOCKS.get(block), block));
        ITEMS.keySet().forEach(item -> Registry.register(Registry.ITEM, ITEMS.get(item), item));
    }
}