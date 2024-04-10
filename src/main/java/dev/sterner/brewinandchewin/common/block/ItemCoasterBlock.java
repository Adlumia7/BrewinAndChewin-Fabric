package dev.sterner.brewinandchewin.common.block;

import dev.sterner.brewinandchewin.common.block.entity.ItemCoasterBlockEntity;
import dev.sterner.brewinandchewin.common.registry.BCTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.*;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ItemCoasterBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape SHAPE;

    public ItemCoasterBlock() {
        super(Properties.copy(Blocks.WHITE_CARPET).sound(SoundType.WOOD).strength(0.2F));
        this.registerDefaultState(((this.defaultBlockState()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ItemCoasterBlockEntity(pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ItemCoasterBlockEntity itemCoasterBlockEntity) {
            if (!itemCoasterBlockEntity.isEmpty()) {
                return itemCoasterBlockEntity.getStoredItem();
            }
        }
        return super.getCloneItemStack(world, pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ItemCoasterBlockEntity itemCoasterBlockEntity) {
            ItemStack heldStack = player.getItemInHand(hand);
            ItemStack offhandStack = player.getOffhandItem();
            if (itemCoasterBlockEntity.isEmpty()) {
                if (!offhandStack.isEmpty()) {
                    if (hand.equals(InteractionHand.MAIN_HAND) && !offhandStack.is(BCTags.OFFHAND_EQUIPMENT) && !(heldStack.getItem() instanceof BlockItem)) {
                        return InteractionResult.PASS;
                    }

                    if (hand.equals(InteractionHand.OFF_HAND) && offhandStack.is(BCTags.OFFHAND_EQUIPMENT)) {
                        return InteractionResult.PASS;
                    }
                }

                if (heldStack.isEmpty()) {
                    return InteractionResult.PASS;
                }

                if (itemCoasterBlockEntity.addItem(player.getAbilities().instabuild ? heldStack.copy() : heldStack)) {
                    world.playSound(null, ((float) pos.getX() + 0.5F), pos.getY(), (float) pos.getZ() + 0.5F, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5F, 0.8F);
                    return InteractionResult.SUCCESS;
                }

                if (!heldStack.isEmpty()) {
                    return InteractionResult.CONSUME;
                }
            } else if (hand.equals(InteractionHand.MAIN_HAND)) {
                if (!player.isCreative()) {
                    if (!player.getInventory().add(itemCoasterBlockEntity.removeItem())) {
                        Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemCoasterBlockEntity.removeItem());
                    }
                } else {
                    itemCoasterBlockEntity.removeItem();
                }

                world.playSound(null, (float) pos.getX() + 0.5F, pos.getY(), (float) pos.getZ() + 0.5F, SoundEvents.WOOL_HIT, SoundSource.BLOCKS, 0.5F, 0.5F);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }


    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ItemCoasterBlockEntity) {
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((ItemCoasterBlockEntity) tileEntity).getStoredItem());
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluid = ctx.getLevel().getFluidState(ctx.getClickedPos());
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return direction == Direction.DOWN && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() :
                super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos floorPos = pos.below();
        return canSupportRigidBlock(world, floorPos) || canSupportCenter(world, floorPos, Direction.UP);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        if (tileEntity instanceof ItemCoasterBlockEntity) {
            return !((ItemCoasterBlockEntity) tileEntity).isEmpty() ? 15 : 0;
        } else {
            return 0;
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }


    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
    }
}
