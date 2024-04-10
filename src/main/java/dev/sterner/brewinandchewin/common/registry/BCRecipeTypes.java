package dev.sterner.brewinandchewin.common.registry;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.recipe.KegRecipe;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public interface BCRecipeTypes {
    Map<RecipeSerializer<?>, ResourceLocation> RECIPE_SERIALIZERS = new LinkedHashMap<>();
    Map<RecipeType<?>, ResourceLocation> RECIPE_TYPES = new LinkedHashMap<>();

    RecipeSerializer<KegRecipe> KEG_RECIPE_SERIALIZER = register("fermenting", new KegRecipe.Serializer());
    RecipeType<KegRecipe> KEG_RECIPE_TYPE = register("fermenting");

    static <T extends Recipe<?>> RecipeSerializer<T> register(String name, RecipeSerializer<T> serializer) {
        RECIPE_SERIALIZERS.put(serializer, new ResourceLocation(BrewinAndChewin.MODID, name));
        return serializer;
    }

    static <T extends Recipe<?>> RecipeType<T> register(String name) {
        RecipeType<T> type = new RecipeType<>() {
            @Override
            public String toString() {
                return name;
            }
        };
        RECIPE_TYPES.put(type, new ResourceLocation(BrewinAndChewin.MODID, name));
        return type;
    }

    static void init() {
        RECIPE_SERIALIZERS.keySet().forEach(recipeSerializer -> Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, RECIPE_SERIALIZERS.get(recipeSerializer), recipeSerializer));
        RECIPE_TYPES.keySet().forEach(recipeType -> Registry.register(BuiltInRegistries.RECIPE_TYPE, RECIPE_TYPES.get(recipeType), recipeType));
    }
}
