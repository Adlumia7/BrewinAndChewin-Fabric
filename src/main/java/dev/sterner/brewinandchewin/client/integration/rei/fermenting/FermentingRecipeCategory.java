package dev.sterner.brewinandchewin.client.integration.rei.fermenting;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.client.integration.rei.BCREIPlugin;
import dev.sterner.brewinandchewin.common.registry.BCObjects;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

public class FermentingRecipeCategory implements DisplayCategory<FermentingRecipeDisplay> {
    private static final java.awt.Rectangle FRIGID_BAR = new java.awt.Rectangle(48, 23, 6, 4);
    private static final java.awt.Rectangle COLD_BAR = new java.awt.Rectangle(54, 23, 7, 4);
    private static final java.awt.Rectangle WARM_BAR = new java.awt.Rectangle(67, 23, 7, 4);
    private static final java.awt.Rectangle HOT_BAR = new java.awt.Rectangle(74, 23, 7, 4);
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BrewinAndChewin.MODID, "textures/gui/jei/keg.png");

    @Override
    public CategoryIdentifier<? extends FermentingRecipeDisplay> getCategoryIdentifier() {
        return BCREIPlugin.FERMENTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(BrewinAndChewin.MODID + ".rei.fermenting");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(BCObjects.KEG);
    }

    @Override
    public List<Widget> setupDisplay(FermentingRecipeDisplay display, Rectangle bounds) {
        Point origin = bounds.getLocation();
        final List<Widget> widgets = new ArrayList<>();
        int temp = display.getTemp();

        widgets.add(Widgets.createRecipeBase(bounds));
        Rectangle bgBounds = BCREIPlugin.centeredIntoRecipeBase(origin, 116, 56);
        widgets.add(Widgets.createTexturedWidget(GUI_TEXTURE, bgBounds, 29, 16));

        List<EntryIngredient> ingredientEntries = display.getIngredientEntries();
        if (ingredientEntries != null) {
            for (int i = 0; i < ingredientEntries.size(); i++) {
                Point slotLoc = new Point(bgBounds.x + 4 + i % 2 * 18, bgBounds.y + 12 + (i / 2) * 18);
                widgets.add(Widgets.createSlot(slotLoc).entries(ingredientEntries.get(i)).markInput().disableBackground());
            }
        }

        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 56, bgBounds.y + 2)).entries(display.getLiquid()).markInput().disableBackground());

        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 60, bgBounds.y + 38))
                .entries(display.getContainerOutput()).markInput().disableBackground());

        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 93, bgBounds.y + 10))
                .entries(display.getOutputEntries().get(0)).markOutput().disableBackground());
        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 93, bgBounds.y + 39))
                .entries(display.getOutputEntries().get(0)).markOutput().disableBackground());


        if (temp <= 2) {
            widgets.add(Widgets.createTexturedWidget(GUI_TEXTURE,
                    new Rectangle(bgBounds.x + COLD_BAR.x, bgBounds.y + COLD_BAR.y, COLD_BAR.width, COLD_BAR.height), 182, 0));
        }
        if (temp <= 1) {
            widgets.add(Widgets.createTexturedWidget(GUI_TEXTURE,
                    new Rectangle(bgBounds.x + FRIGID_BAR.x, bgBounds.y + FRIGID_BAR.y, COLD_BAR.width, COLD_BAR.height), 176, 0));
        }
        if (temp >= 4) {
            widgets.add(Widgets.createTexturedWidget(GUI_TEXTURE,
                    new Rectangle(bgBounds.x + WARM_BAR.x, bgBounds.y + WARM_BAR.y, WARM_BAR.width, WARM_BAR.height), 195, 0));
        }
        if (temp >= 5) {
            widgets.add(Widgets.createTexturedWidget(GUI_TEXTURE,
                    new Rectangle(bgBounds.x + HOT_BAR.x, bgBounds.y + HOT_BAR.y, HOT_BAR.width, HOT_BAR.height), 202, 0));
        }

        /*
        widgets.add(Widgets.createTexturedWidget(GUI_TEXTURE,
                new Rectangle(bgBounds.x + 18, bgBounds.y + 39, 17, 15), 176, 0));


         */

        return widgets;
    }
}
