package dev.sterner.brewinandchewin.client.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.recipe.KegRecipe;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class FermentingEMIRecipe implements EmiRecipe {
    private static final Rectangle FRIGID_BAR = new java.awt.Rectangle(48, 23, 6, 4);
    private static final Rectangle COLD_BAR = new java.awt.Rectangle(54, 23, 7, 4);
    private static final Rectangle WARM_BAR = new java.awt.Rectangle(67, 23, 7, 4);
    private static final Rectangle HOT_BAR = new java.awt.Rectangle(74, 23, 7, 4);
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BrewinAndChewin.MODID, "textures/gui/jei/keg.png");
    private final ResourceLocation id;
    private final EmiIngredient containerOutput;
    private final int cookTime;
    private final int temp;
    final EmiIngredient liquid;
    final List<EmiIngredient> ingredients;
    final EmiStack output;

    public FermentingEMIRecipe(KegRecipe recipe) {
        this.id = recipe.getId();
        this.containerOutput = EmiStack.of(recipe.getOutputContainer());
        this.cookTime = recipe.getFermentTime();
        this.temp = recipe.getTemperature();
        this.liquid = EmiIngredient.of(recipe.getFluidItem());
        this.ingredients = recipe.ingredientList.stream().map(EmiIngredient::of).toList();
        this.output = EmiStack.of(recipe.output);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return BCEMIPlugin.FERMENTING_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return ingredients;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output.getEmiStacks();
    }

    @Override
    public int getDisplayWidth() {
        return 116;
    }

    @Override
    public int getDisplayHeight() {
        return 56;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        widgets.addDrawable(0, 0, 116, 56, ((draw, mouseX, mouseY, delta) -> {
            draw.blit(GUI_TEXTURE, 0, 0, 29, 16, 116, 56, 256, 256);
        }));

        List<EmiIngredient> v = this.ingredients;
        if (v != null) {
            for (int i = 0; i < v.size(); i++) {
                widgets.addSlot(v.get(i), 3 + i % 2 * 18, 11 + (i / 2) * 18);
            }
        }

        widgets.addSlot(liquid, 56 - 1, 2 - 1);
        widgets.addSlot(this.containerOutput, 60, 38);
        widgets.addSlot(getOutputs().get(0), 93 - 1, 10 - 4).drawBack(false);
        widgets.addSlot(getOutputs().get(0), 93 - 1, 39 - 1);

        if (temp <= 2) {
            widgets.addDrawable(COLD_BAR.x, COLD_BAR.y, COLD_BAR.width, COLD_BAR.height, ((draw, mouseX, mouseY, delta) -> {
                draw.blit(GUI_TEXTURE, 0, 0, 182, 0, COLD_BAR.width, COLD_BAR.height, 256, 256);
            }));

        }
        if (temp <= 1) {
            widgets.addDrawable(FRIGID_BAR.x, FRIGID_BAR.y, FRIGID_BAR.width, FRIGID_BAR.height, ((draw, mouseX, mouseY, delta) -> {
                draw.blit(GUI_TEXTURE, 0, 0, 176, 0, FRIGID_BAR.width, FRIGID_BAR.height, 256, 256);
            }));
        }
        if (temp >= 4) {
            widgets.addDrawable(WARM_BAR.x, WARM_BAR.y, WARM_BAR.width, WARM_BAR.height, ((draw, mouseX, mouseY, delta) -> {
                draw.blit(GUI_TEXTURE, 0, 0, 195, 0, WARM_BAR.width, WARM_BAR.height, 256, 256);
            }));
        }
        if (temp >= 5) {
            widgets.addDrawable(HOT_BAR.x, HOT_BAR.y, HOT_BAR.width, HOT_BAR.height, ((draw, mouseX, mouseY, delta) -> {
                draw.blit(GUI_TEXTURE, 0, 0, 202, 0, HOT_BAR.width, HOT_BAR.height, 256, 256);
            }));
        }
    }
}
