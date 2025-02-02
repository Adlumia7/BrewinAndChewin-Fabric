package dev.sterner.brewinandchewin.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.registry.ModParticleTypes;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class FonduePotBlock extends Block {
    public static final VoxelShape INSIDE = Block.box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    public static final VoxelShape SHAPE = Shapes.join(Shapes.block(), Shapes.or(
                    Block.box(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D),
                    Block.box(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D),
                    Block.box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE),
            BooleanOp.ONLY_FIRST);
    public static final int MIN_FILL_LEVEL = 1;
    public static final int MAX_FILL_LEVEL = 3;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;

    public FonduePotBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(LEVEL, 3));
    }

    public boolean isFull(BlockState state) {
        return state.getValue(LEVEL) == 3;
    }

    protected double getContentHeight(BlockState state) {
        return (6.0D + (double) state.getValue(LEVEL) * 3.0D) / 16.0D;
    }

    public boolean isEntityInsideContent(BlockState state, BlockPos pos, Entity entity) {
        return entity.getY() < (double) pos.getY() + this.getContentHeight(state) && entity.getBoundingBox().maxY > (double) pos.getY() + 0.25D;
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (this.isEntityInsideContent(state, pos, entity)) {
            entity.lavaHurt();
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return state.getValue(LEVEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return INSIDE;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);
        if (random.nextFloat() < 0.8F) {
            double x = (double) pos.getX() + 0.5D + (random.nextDouble() * 0.6D - 0.3D);
            double y = (double) pos.getY() + this.getContentHeight(state);
            double z = (double) pos.getZ() + 0.5D + (random.nextDouble() * 0.6D - 0.3D);
            world.addParticle(ModParticleTypes.STEAM.get(), x, y, z, 0.0D, 0.0D, 0.0D);

            double x1 = (double) pos.getX() + 0.5D;
            double y1 = pos.getY();
            double z1 = (double) pos.getZ() + 0.5D;
            if (random.nextInt(10) == 0) {
                world.playLocalSound(x1, y1, z1, ModSounds.BLOCK_COOKING_POT_BOIL_SOUP.get(), SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.2F + 0.9F, false);
            }
        }
    }
}
