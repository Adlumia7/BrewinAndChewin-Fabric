package dev.sterner.brewinandchewin.common.block;

import dev.sterner.brewinandchewin.common.block.entity.FermentationControllerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;


public class FermentationControllerBlock extends BaseEntityBlock {

    public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);
    public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");

    public FermentationControllerBlock() {
        super(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).strength(2, 4).sound(SoundType.METAL).noOcclusion());
        registerDefaultState(this.stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH).setValue(STATE, State.NONE).setValue(VERTICAL, true));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FermentationControllerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return (tickerWorld, pos, tickerState, blockEntity) -> {
            if (blockEntity instanceof FermentationControllerBlockEntity be) {
                be.tick(world, pos, state);
            }
        };
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        updateTemp(world, pos, state);
        super.setPlacedBy(world, pos, state, placer, itemStack);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        updateTemp(world, pos, state);
        super.neighborChanged(state, world, pos, sourceBlock, sourcePos, notify);
    }

    private void updateTemp(Level world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof FermentationControllerBlockEntity blockEntity) {
            Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);

            Direction right = facing.getClockWise();
            Direction left = facing.getCounterClockWise();

            int coldPower = getReceivedRedstonePower(world, pos, right);
            int hotPower = getReceivedRedstonePower(world, pos, left);
            int totalPower = hotPower - coldPower;
            blockEntity.setTargetTemperature(totalPower);
        }
    }

    public int getReceivedRedstonePower(Level world, BlockPos pos, Direction direction) {
        int i = 0;

        int j = world.getSignal(pos.relative(direction), direction);
        if (j >= 15) {
            return 15;
        }

        if (j > i) {
            i = j;
        }
        return i;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, STATE, VERTICAL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Player player = ctx.getPlayer();
        boolean bl = player == null || !player.isShiftKeyDown();
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, ctx.getHorizontalDirection().getOpposite()).setValue(STATE, State.NONE).setValue(VERTICAL, bl);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(HorizontalDirectionalBlock.FACING, rotation.rotate(state.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public enum State implements StringRepresentable {
        NONE("none"),
        HOT("hot"),
        COLD("cold");

        private final String name;

        State(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
