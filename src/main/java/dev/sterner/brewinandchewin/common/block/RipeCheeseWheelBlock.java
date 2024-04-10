package dev.sterner.brewinandchewin.common.block;

import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import dev.sterner.brewinandchewin.common.util.BCTextUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import vectorwing.farmersdelight.common.tag.ModTags;

public class RipeCheeseWheelBlock extends Block {
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    public static final IntegerProperty SERVINGS = IntegerProperty.create("servings", 0, 3);
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.box(2.0D, 0.0D, 2.0D, 8.0D, 6.0D, 8.0D),
            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 8.0D),
            Shapes.or(Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 8.0D), Block.box(2.0D, 0.0D, 8.0D, 8.0D, 6.0D, 14.0D)),
            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D),
    };
    public final Item cheeseType;

    public RipeCheeseWheelBlock(Item cheeseType, Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(SERVINGS, 3));
        this.cheeseType = cheeseType;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return !world.isEmptyBlock(pos.below());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(SERVINGS)];
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int servings = state.getValue(SERVINGS);
        ItemStack heldStack = player.getItemInHand(hand);

        if (servings > 0) {
            if (heldStack.is(ModTags.KNIVES)) {
                world.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                popResource(world, pos, new ItemStack(cheeseType, 1));
                world.setBlock(pos, state.setValue(SERVINGS, servings - 1), 3);
            } else {
                player.displayClientMessage(BCTextUtils.getTranslation("block.cheese.use_knife"), true);
            }
        }
        if (servings == 0) {
            if (heldStack.is(ModTags.KNIVES)) {
                world.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                popResource(world, pos, new ItemStack(cheeseType, 1));
                world.destroyBlock(pos, false);
            } else {
                player.displayClientMessage(BCTextUtils.getTranslation("block.cheese.use_knife"), true);
            }
        }
        return InteractionResult.SUCCESS;
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
