package dev.sterner.brewinandchewin.common.block.entity;

import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;

public class ItemCoasterBlockEntity extends SyncedBlockEntity {

    private final ItemStackHandlerContainer inventory;

    private boolean isItemCarvingBoard = false;

    public ItemCoasterBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntityTypes.COASTER, pos, state);
        this.inventory = createHandler();
    }

    private ItemStackHandlerContainer createHandler() {
        return new ItemStackHandlerContainer()
        {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            protected void onContentsChanged(int slot) {
                inventoryChanged();
            }
        };
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.isItemCarvingBoard = tag.getBoolean("IsItemCarved");
        inventory.deserializeNBT(tag.getCompound("Inventory"));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
        tag.putBoolean("IsItemCarved", this.isItemCarvingBoard);
    }

    public boolean addItem(ItemStack itemStack) {
        if (this.isEmpty() && !itemStack.isEmpty()) {
            this.inventory.setItem(0, itemStack.split(1));
            this.isItemCarvingBoard = false;
            this.inventoryChanged();
            return true;
        } else {
            return false;
        }
    }

    public boolean carveToolOnBoard(ItemStack tool) {
        if (this.addItem(tool)) {
            this.isItemCarvingBoard = true;
            return true;
        } else {
            return false;
        }
    }

    public ItemStack removeItem() {
        if (!this.isEmpty()) {
            this.isItemCarvingBoard = false;
            ItemStack item = this.getStoredItem().split(1);
            this.inventoryChanged();
            return item;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public ItemStack getStoredItem() {
        return this.inventory.getItem(0);
    }

    public boolean isEmpty() {
        return this.inventory.getItem(0).isEmpty();
    }

    public boolean isItemCarvingBoard() {
        return this.isItemCarvingBoard;
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = new CompoundTag();
        this.saveAdditional(nbt);
        return nbt;
    }
}
