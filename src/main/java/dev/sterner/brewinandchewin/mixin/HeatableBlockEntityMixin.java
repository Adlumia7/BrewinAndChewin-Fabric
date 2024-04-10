package dev.sterner.brewinandchewin.mixin;

import dev.sterner.brewinandchewin.common.block.FermentationControllerBlock;
import dev.sterner.brewinandchewin.common.block.entity.FermentationControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.HeatableBlockEntity;

@Mixin(HeatableBlockEntity.class)
public interface HeatableBlockEntityMixin {

    @Inject(method = "isHeated", at = @At(value = "RETURN"), cancellable = true)
    private void bc$isHeated(Level world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            if (world.getBlockEntity(pos.below()) instanceof FermentationControllerBlockEntity blockEntity && world.getBlockState(pos.below()).getValue(FermentationControllerBlock.VERTICAL)) {
                if (blockEntity.getTemperature() > 4) {
                    cir.setReturnValue(true);
                }
            }
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos pos1 = pos.relative(direction);
                if (world.getBlockEntity(pos1) instanceof FermentationControllerBlockEntity be && !world.getBlockState(pos1).getValue(FermentationControllerBlock.VERTICAL)) {
                    if (be.getTemperature() > 4) {
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }
}
