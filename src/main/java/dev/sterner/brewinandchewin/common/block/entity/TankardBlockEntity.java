package dev.sterner.brewinandchewin.common.block.entity;

import dev.sterner.brewinandchewin.common.item.BoozeBlockItem;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;

public class TankardBlockEntity extends SyncedBlockEntity {

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);

    public TankardBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntityTypes.TANKARD, pos, state);
    }

    public InteractionResult onUse(Level world, BlockState state, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getMainHandItem();
        int occupiedCount = (int) getItems().stream().filter(i -> !i.isEmpty()).count();
        if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (occupiedCount == 0) {
                return InteractionResult.PASS;
            }

            boolean bl = false;
            if (ItemStack.isSameItemSameTags(inventory.get(occupiedCount - 1), itemStack)) {
                itemStack.grow(1);
                inventory.set(occupiedCount - 1, ItemStack.EMPTY);
                bl = true;
            } else if (itemStack.isEmpty()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, inventory.get(occupiedCount - 1));
                inventory.set(occupiedCount - 1, ItemStack.EMPTY);
                bl = true;
            }
            if (occupiedCount == 1 && bl) {
                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        } else {
            if (itemStack.getItem() instanceof BoozeBlockItem boozeItem) {
                if (occupiedCount == 1) {
                    inventory.set(1, new ItemStack(boozeItem, 1));
                    player.getMainHandItem().shrink(1);
                } else if (occupiedCount == 2) {
                    inventory.set(2, new ItemStack(boozeItem, 1));
                    player.getMainHandItem().shrink(1);
                } else {
                    return InteractionResult.CONSUME;
                }
            }
        }
        inventoryChanged();

        return InteractionResult.PASS;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        ContainerHelper.loadAllItems(nbt, this.inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        ContainerHelper.saveAllItems(nbt, this.inventory);
    }

    public NonNullList<ItemStack> getItems() {
        return inventory;
    }
}
