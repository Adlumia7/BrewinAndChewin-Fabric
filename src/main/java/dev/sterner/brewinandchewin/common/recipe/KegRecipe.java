package dev.sterner.brewinandchewin.common.recipe;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.sterner.brewinandchewin.common.registry.BCRecipeTypes;
import java.util.ArrayList;
import java.util.List;

import dev.sterner.brewinandchewin.common.util.RecipeMatcher;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class KegRecipe implements Recipe<RecipeWrapper> {
    public static final int INPUT_SLOTS = 4;

    private final ResourceLocation id;
    private final String group;
    public final NonNullList<Ingredient> ingredientList;
    private final Ingredient fluidItem;
    public final ItemStack output;
    private final ItemStack container;
    private final float experience;
    private final int fermentTime;
    private final int temperature;

    public KegRecipe(ResourceLocation id, String group, NonNullList<Ingredient> ingredientList, Ingredient fluidItem, ItemStack output, ItemStack container, float experience, int fermentTime, int temperature) {
        this.id = id;
        this.group = group;
        this.ingredientList = ingredientList;
        this.output = output;
        this.temperature = temperature;
        if (!container.isEmpty()) {
            this.container = container;
        } else if (output.getItem().getCraftingRemainingItem() != null) {
            this.container = new ItemStack(output.getItem().getCraftingRemainingItem());
        } else {
            this.container = ItemStack.EMPTY;
        }
        this.fluidItem = fluidItem;

        this.experience = experience;
        this.fermentTime = fermentTime;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.addAll(this.ingredientList);
        if (!this.fluidItem.isEmpty()) {
            ingredients.add(this.fluidItem);
        }

        return ingredients;
    }



    @Override
    public boolean matches(RecipeWrapper container, Level level) {
        List<ItemStack> inputs = new ArrayList();
        int i = 0;

        for (int j = 0; j < 4; ++j) {
            ItemStack itemstack = container.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                inputs.add(itemstack);
            }
        }

        if (this.fluidItem != null) {
            return i == this.ingredientList.size() && RecipeMatcher.findMatches(inputs, this.ingredientList) != null && this.fluidItem.test(container.getItem(4));
        } else {
            return i == this.ingredientList.size() && RecipeMatcher.findMatches(inputs, this.ingredientList) != null;
        }
    }

    @Override
    public ItemStack assemble(RecipeWrapper container, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= ingredientList.size();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryManager) {
        return this.output;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BCRecipeTypes.KEG_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BCRecipeTypes.KEG_RECIPE_TYPE;
    }

    public ItemStack getOutputContainer() {
        return this.container;
    }

    public Ingredient getFluidItem() {
        return this.fluidItem;
    }

    public float getExperience() {
        return this.experience;
    }

    public int getFermentTime() {
        return this.fermentTime;
    }

    public int getTemperature() {
        return this.temperature;
    }

    public static class Serializer implements RecipeSerializer<KegRecipe> {

        @Override
        public KegRecipe fromJson(ResourceLocation id, JsonObject json) {
            final String groupIn = GsonHelper.getAsString(json, "group", "");

            final NonNullList<Ingredient> inputItemsIn = readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (inputItemsIn.isEmpty()) {
                throw new JsonParseException("No ingredients for cooking recipe");
            } else if (inputItemsIn.size() > KegRecipe.INPUT_SLOTS) {
                throw new JsonParseException("Too many ingredients for cooking recipe! The max is " + KegRecipe.INPUT_SLOTS);
            } else {
                final JsonObject jsonResult = GsonHelper.getAsJsonObject(json, "result");
                final ItemStack outputIn = new ItemStack(GsonHelper.getAsItem(jsonResult, "item"), GsonHelper.getAsInt(jsonResult, "count", 1));
                Ingredient fluidItemIn = Ingredient.EMPTY;
                if (GsonHelper.isValidNode(json, "fluiditem")) {
                    final JsonObject jsonContainer = GsonHelper.getAsJsonObject(json, "fluiditem");
                    fluidItemIn = Ingredient.fromJson(jsonContainer);
                }

                ItemStack container = ItemStack.EMPTY;
                if (GsonHelper.isValidNode(json, "container")) {
                    final JsonObject jsonContainer = GsonHelper.getAsJsonObject(json, "container");
                    container = new ItemStack(GsonHelper.getAsItem(jsonContainer, "item"), GsonHelper.getAsInt(jsonContainer, "count", 1));
                }

                float experienceIn = GsonHelper.getAsFloat(json, "experience", 0.0F);
                int fermentTimeIn = GsonHelper.getAsInt(json, "fermentingtime", 200);
                int temperatureIn = GsonHelper.getAsInt(json, "temperature", 3);
                return new KegRecipe(id, groupIn, inputItemsIn, fluidItemIn, outputIn, container, experienceIn, fermentTimeIn, temperatureIn);
            }
        }

        private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
            NonNullList<Ingredient> defaultedList = NonNullList.create();
            for (int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
                if (!ingredient.isEmpty()) {
                    defaultedList.add(ingredient);
                }
            }
            return defaultedList;
        }

        @Override
        public KegRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String groupIn = buf.readUtf();
            int i = buf.readVarInt();
            NonNullList<Ingredient> inputItemsIn = NonNullList.withSize(i, Ingredient.EMPTY);

            inputItemsIn.replaceAll(ignored -> Ingredient.fromNetwork(buf));

            Ingredient fluidItem = Ingredient.fromNetwork(buf);
            ItemStack outputIn = buf.readItem();
            ItemStack container = buf.readItem();
            float experienceIn = buf.readFloat();
            int fermentTimeIn = buf.readVarInt();
            int temperatureIn = buf.readVarInt();
            return new KegRecipe(id, groupIn, inputItemsIn, fluidItem, outputIn, container, experienceIn, fermentTimeIn, temperatureIn);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, KegRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeVarInt(recipe.ingredientList.size());

            for (Ingredient ingredient : recipe.ingredientList) {
                ingredient.toNetwork(buf);
            }

            recipe.fluidItem.toNetwork(buf);
            buf.writeItem(recipe.output);
            buf.writeItem(recipe.container);
            buf.writeFloat(recipe.experience);
            buf.writeVarInt(recipe.fermentTime);
            buf.writeVarInt(recipe.temperature);
        }
    }
}
