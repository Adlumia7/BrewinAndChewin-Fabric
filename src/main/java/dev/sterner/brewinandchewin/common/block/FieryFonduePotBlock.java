package dev.sterner.brewinandchewin.common.block;

import dev.sterner.brewinandchewin.common.registry.BCObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.registry.ModParticleTypes;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class FieryFonduePotBlock extends Block {
    public static final VoxelShape INSIDE = Block.box(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    public static final VoxelShape SHAPE;
    public static final IntegerProperty LEVEL;
    public static final DirectionProperty FACING;

    public FieryFonduePotBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(((this.defaultBlockState()).setValue(LEVEL, 3)).setValue(FACING, Direction.NORTH));
    }

    protected double getContentHeight(BlockState state) {
        return (6.0 + (double) state.getValue(LEVEL) * 3.0) / 16.0;
    }

    public boolean isEntityInsideContent(BlockState state, BlockPos pos, Entity entity) {
        return entity.getY() < (double) pos.getY() + this.getContentHeight(state) && entity.getBoundingBox().maxY > (double) pos.getY() + 0.25;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (this.isEntityInsideContent(state, pos, entity)) {
            entity.lavaHurt();
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL, FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int servings = state.getValue(LEVEL);
        ItemStack bowl = new ItemStack(Items.BOWL);
        ItemStack fondue = new ItemStack(BCObjects.FIERY_FONDUE);
        ItemStack heldStack = player.getItemInHand(hand);
        if (ItemStack.isSameItem(heldStack, bowl)) {
            if (!player.getAbilities().instabuild) {
                heldStack.shrink(1);
            }

            if (!player.getInventory().add(fondue)) {
                player.drop(fondue, false);
            }

            BlockState newState = world.getBlockState(pos).getValue(LEVEL) > 1 ? state.setValue(LEVEL, servings - 1) : Blocks.CAULDRON.defaultBlockState();
            world.setBlock(pos, newState, 3);
            if (state.getValue(LEVEL) == 1) {
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.BONE));
            }

            world.playSound(null, pos, SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return INSIDE;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(LEVEL);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);
        if (random.nextFloat() < 0.8F) {
            double x = (double) pos.getX() + 0.5 + (random.nextDouble() * 0.6 - 0.3);
            double y = (double) pos.getY() + this.getContentHeight(state);
            double z = (double) pos.getZ() + 0.5 + (random.nextDouble() * 0.6 - 0.3);
            world.addParticle(ModParticleTypes.STEAM.get(), x, y, z, 0.0, 0.0, 0.0);
            double x1 = (double) pos.getX() + 0.5;
            double y1 = pos.getY();
            double z1 = (double) pos.getZ() + 0.5;
            if (random.nextInt(10) == 0) {
                world.playLocalSound(x1, y1, z1, ModSounds.BLOCK_COOKING_POT_BOIL_SOUP.get(), SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.2F + 0.9F, false);
            }
        }
    }

    static {
        SHAPE = Shapes.join(
                Shapes.block(),
                Shapes.or(
                        box(0.0, 0.0, 4.0, 16.0, 3.0, 12.0),
                        box(4.0, 0.0, 0.0, 12.0, 3.0, 16.0),
                        box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), INSIDE),
                BooleanOp.ONLY_FIRST);

        LEVEL = BlockStateProperties.LEVEL_CAULDRON;
        FACING = HorizontalDirectionalBlock.FACING;
    }
}
