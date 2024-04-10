package dev.sterner.brewinandchewin.common.block.screen;

import com.mojang.datafixers.util.Pair;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.entity.KegBlockEntity;
import dev.sterner.brewinandchewin.common.registry.BCObjects;
import dev.sterner.brewinandchewin.common.registry.BCScreenHandlerTypes;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerContainer;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMealSlot;

import java.util.Objects;

import static net.fabricmc.api.EnvType.CLIENT;
import static net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS;

public class KegBlockScreenHandler extends AbstractContainerMenu {
    public static final ResourceLocation EMPTY_CONTAINER_SLOT_MUG = new ResourceLocation(BrewinAndChewin.MODID, "item/empty_container_slot_mug");

    public final KegBlockEntity blockEntity;
    private final ContainerData kegData;
    private final ContainerLevelAccess canInteractWithCallable;
    public final ItemStackHandlerContainer inventory;
    protected final Level world;

    public KegBlockScreenHandler(final int windowId, final Inventory playerInventory, final KegBlockEntity blockEntity, ContainerData kegData) {
        super(BCScreenHandlerTypes.KEG_SCREEN_HANDLER, windowId);
        this.blockEntity = blockEntity;
        this.kegData = kegData;
        this.inventory = blockEntity.inventory;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.world = playerInventory.player.level();

        // Ingredient Slots - 2 Rows x 2 Columns
        int startX = 8;
        int startY = 18;
        int inputStartX = 28;
        int inputStartY = 28;
        int borderSlotSize = 18;
        for (int row = 0; row < 2; ++row) {
            for (int column = 0; column < 2; ++column) {
                this.addSlot(new Slot(inventory, (row * 2) + column,
                        inputStartX + (column * borderSlotSize),
                        inputStartY + (row * borderSlotSize)));
            }
        }

        this.addSlot(new Slot(this.inventory, 4, 80, 18));
        this.addSlot(new CookingPotMealSlot(this.inventory, 5, 117, 23));
        this.addSlot(new Slot(this.inventory, 6, 85, 55) {
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(LOCATION_BLOCKS, EMPTY_CONTAINER_SLOT_MUG);
            }
        });
        this.addSlot(new KegResultSlot(playerInventory.player, blockEntity, inventory,7, 117, 55));
        this.addSlot(new KegResultSlot(playerInventory.player, blockEntity, inventory,8, 143, 55));

        // Main Player Inventory
        int startPlayerInvY = startY * 4 + 12;
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInventory, 9 + (row * 9) + column, startX + (column * borderSlotSize),
                        startPlayerInvY + (row * borderSlotSize)));
            }
        }

        // Hotbar
        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, startX + (column * borderSlotSize), 142));
        }


        this.addDataSlots(kegData);
    }

    public KegBlockScreenHandler(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getBlockEntity(playerInventory, data), new SimpleContainerData(5));
    }

    private static KegBlockEntity getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());

        if (tileAtPos instanceof KegBlockEntity kegBlockEntity) {
            return kegBlockEntity;
        }

        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(canInteractWithCallable, playerIn, BCObjects.KEG);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        int indexDrinkDisplay = 4;
        int indexFluidItemInput = 5;
        int indexContainerInput = 6;
        int indexOutput = 7;
        int indexContainerOutput = 8;
        int startPlayerInv = indexContainerOutput + 1;
        int endPlayerInv = startPlayerInv + 36;
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index != indexOutput && index != indexContainerOutput) {
                if (index > indexContainerOutput) {
                    if (itemstack1.getItem() == BCObjects.TANKARD && !this.moveItemStackTo(itemstack1, indexContainerInput, indexContainerInput + 1, false)) {
                        return ItemStack.EMPTY;
                    }

                    if (!this.moveItemStackTo(itemstack1, 0, indexFluidItemInput, false)) {
                        return ItemStack.EMPTY;
                    }

                    if (!this.moveItemStackTo(itemstack1, 0, indexDrinkDisplay, false)) {
                        return ItemStack.EMPTY;
                    }

                    if (!this.moveItemStackTo(itemstack1, indexContainerInput, indexOutput, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, startPlayerInv, endPlayerInv, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, startPlayerInv, endPlayerInv, true)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    @Environment(CLIENT)
    public int getFermentProgressionScaled() {
        int i = this.kegData.get(0);
        int j = this.kegData.get(1);
        return j != 0 && i != 0 ? i * 33 / j : 0;
    }

    @Environment(CLIENT)
    public int getFermentingTicks() {
        return this.kegData.get(0);
    }

    @Environment(CLIENT)
    public int getTemperature() {
        return this.kegData.get(2);
    }

    @Environment(CLIENT)
    public int getAdjustedTemperature() {
        return this.kegData.get(3);
    }
}
