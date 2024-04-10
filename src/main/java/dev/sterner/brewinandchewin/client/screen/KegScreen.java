package dev.sterner.brewinandchewin.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.screen.KegBlockScreenHandler;
import dev.sterner.brewinandchewin.common.util.BCTextUtils;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class KegScreen extends AbstractContainerScreen<KegBlockScreenHandler> {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(BrewinAndChewin.MODID, "textures/gui/keg.png");
    private static final Rectangle PROGRESS_ARROW = new Rectangle(72, 44, 0, 9);
    private static final Rectangle FRIGID_BAR = new Rectangle(72, 39, 6, 4);
    private static final Rectangle COLD_BAR = new Rectangle(78, 39, 7, 4);
    private static final Rectangle WARM_BAR = new Rectangle(91, 39, 7, 4);
    private static final Rectangle HOT_BAR = new Rectangle(98, 39, 7, 4);

    private static final Rectangle BUBBLE_1 = new Rectangle(69, 14, 9, 24);
    private static final Rectangle BUBBLE_2 = new Rectangle(98, 14, 9, 24);
    private static final int[] BUBBLELENGTHS = new int[]{24, 20, 16, 12, 8, 4, 0};
    private boolean mouseDown;

    public KegScreen(KegBlockScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 27;
        this.titleLabelY = 17;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.renderTemperatureTooltip(context, mouseX, mouseY);
        this.renderMealDisplayTooltip(context, mouseX, mouseY);
    }

    private void renderTemperatureTooltip(GuiGraphics ctx, int mouseX, int mouseY) {
        if (this.isHovering(77, 39, 33, 4, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            MutableComponent key = null;
            int i = this.menu.getTemperature();
            if (i < -8) {
                key = BCTextUtils.getTranslation("container.keg.frigid");
            }
            if (i < -4 && i > -9) {
                key = BCTextUtils.getTranslation("container.keg.cold");
            }
            if (i < 5 && i > -5) {
                key = BCTextUtils.getTranslation("container.keg.normal");
            }
            if (i > 4 && i < 9) {
                key = BCTextUtils.getTranslation("container.keg.warm");
            }
            if (i > 8) {
                key = BCTextUtils.getTranslation("container.keg.hot");
            }
            tooltip.add(key);

            ctx.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    protected void renderMealDisplayTooltip(GuiGraphics ctx, int mouseX, int mouseY) {
        if (this.minecraft != null && this.minecraft.player != null && this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.hoveredSlot.index == 5) {
                List<Component> tooltip = new ArrayList<>();

                ItemStack mealStack = this.hoveredSlot.getItem();
                tooltip.add(((MutableComponent) mealStack.getHoverName()).withStyle(mealStack.getRarity().color));

                ItemStack containerStack = this.menu.blockEntity.getContainer();
                String container = !containerStack.isEmpty() ? containerStack.getItem().getDescription().getString() : "";

                tooltip.add(BCTextUtils.getTranslation("container.keg.served_in", container).withStyle(ChatFormatting.GRAY));

                ctx.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
            } else {
                ctx.renderTooltip(font, this.hoveredSlot.getItem(), mouseX, mouseY);

            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        super.renderLabels(context, mouseX, mouseY);
        context.drawString(this.font, this.playerInventoryTitle, 8, (this.imageHeight - 96 + 2), 4210752, false);
    }


    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.minecraft == null) {
            return;
        }

        context.blit(INVENTORY_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        // Render progress arrow
        int l = this.menu.getFermentProgressionScaled();
        context.blit(INVENTORY_LOCATION, this.leftPos + PROGRESS_ARROW.x, this.topPos + PROGRESS_ARROW.y, 176, 28, l + 1, PROGRESS_ARROW.height);

        int temp = this.menu.getTemperature();
        if (temp < -4 && temp > -9) {
            context.blit(INVENTORY_LOCATION, this.leftPos + COLD_BAR.x, this.topPos + COLD_BAR.y, 182, 0, COLD_BAR.width, COLD_BAR.height);
        }
        if (temp < -8) {
            context.blit(INVENTORY_LOCATION, this.leftPos + COLD_BAR.x, this.topPos + COLD_BAR.y, 182, 0, COLD_BAR.width, COLD_BAR.height);
            context.blit(INVENTORY_LOCATION, this.leftPos + FRIGID_BAR.x, this.topPos + FRIGID_BAR.y, 176, 0, FRIGID_BAR.width, FRIGID_BAR.height);
        }
        if (temp > 4 && temp < 9) {
            context.blit(INVENTORY_LOCATION, this.leftPos + WARM_BAR.x, this.topPos + WARM_BAR.y, 195, 0, WARM_BAR.width, WARM_BAR.height);
        }
        if (temp > 8) {
            context.blit(INVENTORY_LOCATION, this.leftPos + WARM_BAR.x, this.topPos + WARM_BAR.y, 195, 0, WARM_BAR.width, WARM_BAR.height);
            context.blit(INVENTORY_LOCATION, this.leftPos + HOT_BAR.x, this.topPos + HOT_BAR.y, 202, 0, HOT_BAR.width, HOT_BAR.height);
        }

        int i = this.menu.getFermentingTicks();
        if (i > 0) {
            int j;
            j = BUBBLELENGTHS[i / 5 % 7];
            context.blit(INVENTORY_LOCATION, this.leftPos + BUBBLE_1.x, this.topPos + BUBBLE_1.y, 176, 4, BUBBLE_1.width, BUBBLE_1.height - j);
            context.blit(INVENTORY_LOCATION, this.leftPos + BUBBLE_2.x, this.topPos + BUBBLE_2.y, 186, 4, BUBBLE_2.width, BUBBLE_2.height - j);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.mouseDown) {
            this.mouseDown = false;
            return true;
        } else {
            return super.mouseReleased(mouseX, mouseY, button);
        }
    }
}
