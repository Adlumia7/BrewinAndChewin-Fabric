package dev.sterner.brewinandchewin.common.block;

import dev.sterner.brewinandchewin.common.registry.BCObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PizzaBlock extends Block {
    public static final IntegerProperty SERVINGS = IntegerProperty.create("servings", 0, 3);

    protected static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.box(0.0D, 0.0D, 0.0D, 8.0D, 2.0D, 8.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 8.0D),
            Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 8.0D), Block.box(0.0D, 0.0D, 8.0D, 8.0D, 2.0D, 16.0D)),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
    };

    public PizzaBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(SERVINGS, 3));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(SERVINGS)];
    }


    /*@Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }*/

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide()) {
            if (this.takeServing(world, pos, state, player, hand).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
        }
        return this.takeServing(world, pos, state, player, hand);
    }

    private InteractionResult takeServing(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand) {
        int servings = state.getValue(SERVINGS);
        ItemStack serving = new ItemStack(BCObjects.PIZZA_SLICE, 1);
        ItemStack heldStack = player.getItemInHand(hand);
        if (!player.getInventory().add(serving)) {
            player.drop(serving, false);
        }
        if (true) {
            if (world.getBlockState(pos).getValue(SERVINGS) == 0) {
                world.removeBlock(pos, false);
            } else if (world.getBlockState(pos).getValue(SERVINGS) > 0) {
                world.setBlock(pos, state.setValue(SERVINGS, servings - 1), 3);
            }
        }
        world.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.below()).isSolid();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SERVINGS);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return state.getValue(SERVINGS);
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState state) {
        return false;
    }
}
