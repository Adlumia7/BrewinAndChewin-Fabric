package dev.sterner.brewinandchewin.common.block.entity;

import dev.sterner.brewinandchewin.common.block.FermentationControllerBlock;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;

import static dev.sterner.brewinandchewin.common.block.FermentationControllerBlock.STATE;

public class FermentationControllerBlockEntity extends SyncedBlockEntity {

    private int targetTemp = 0;
    private int temperature = 0;
    private final int MAX_TEMP = 16;
    private final int MIN_TEMP = -MAX_TEMP;

    private int ticker = 0;

    public FermentationControllerBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntityTypes.FERMENTATION_CONTROLLER, pos, state);
    }

    public void tick(Level world, BlockPos pos, BlockState state) {
        if (world != null && !world.isClientSide) {
            if (getTemperature() != getTargetTemperature()) {
                ticker++;
                if (ticker > 20 * 5) {
                    if (getTemperature() > getTargetTemperature()) {
                        decreaseTemp();
                    } else {
                        increaseTemp();
                    }
                    updateState(world, pos, state);
                    ticker = 0;
                }
            } else {
                ticker = 0;
            }
        }
    }

    private void updateState(Level world, BlockPos pos, BlockState state) {
        int temperature = getTemperature();
        if (temperature == 0 && state.getValue(STATE) != FermentationControllerBlock.State.NONE) {
            world.setBlock(pos, state.setValue(STATE, FermentationControllerBlock.State.NONE), Block.UPDATE_ALL_IMMEDIATE);

        } else if (temperature > 0 && state.getValue(STATE) != FermentationControllerBlock.State.HOT) {
            world.setBlock(pos, state.setValue(STATE, FermentationControllerBlock.State.HOT), Block.UPDATE_ALL_IMMEDIATE);

        } else if (temperature < 0 && state.getValue(STATE) != FermentationControllerBlock.State.COLD) {
            world.setBlock(pos, state.setValue(STATE, FermentationControllerBlock.State.COLD), Block.UPDATE_ALL_IMMEDIATE);
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        temperature = nbt.getInt("Temperature");
        targetTemp = nbt.getInt("TargetTemperature");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("Temperature", getTemperature());
        nbt.putInt("TargetTemperature", getTargetTemperature());
    }

    private void sync() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public int getTargetTemperature() {
        return targetTemp;
    }

    public void setTargetTemperature(int targetTemp) {
        this.targetTemp = targetTemp;
        sync();
    }

    public int getTemperature() {
        return temperature;
    }

    private void setTemperature(int temperature) {
        this.temperature = temperature;
        sync();
    }

    public void increaseTemp() {
        if (getTemperature() < MAX_TEMP) {
            setTemperature(getTemperature() + 1);
        }
    }

    public void decreaseTemp() {
        if (getTemperature() > MIN_TEMP) {
            setTemperature(getTemperature() - 1);
        }
    }
}
