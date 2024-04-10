package dev.sterner.brewinandchewin.common.block;

import dev.sterner.brewinandchewin.common.block.entity.KegBlockEntity;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import dev.sterner.brewinandchewin.common.util.BCTextUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.utility.MathUtils;

import java.util.List;

public class KegBlock extends BaseEntityBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");

    protected static final VoxelShape SHAPE_X = Block.box(1.0D, 0.0D, 0.0D, 15.0D, 16.0D, 16.0D);
    protected static final VoxelShape SHAPE_Z = Block.box(0.0D, 0.0D, 1.0D, 16.0D, 16.0D, 15.0D);
    protected static final VoxelShape SHAPE_VERTICAL = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public KegBlock() {
        super(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).strength(2, 3).sound(SoundType.WOOD));
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(VERTICAL, false).setValue(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KegBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (!world.isClientSide()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof KegBlockEntity kegBlockEntity) {
                kegBlockEntity.updateTemperature();
                ItemStack servingStack = kegBlockEntity.useHeldItemOnMeal(heldStack);
                if (servingStack != ItemStack.EMPTY) {
                    if (!player.getInventory().add(servingStack)) {
                        player.drop(servingStack, false);
                    }
                    world.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                } else {
                    MenuProvider screenHandlerFactory = state.getMenuProvider(world, pos);
                    if (screenHandlerFactory != null) {
                        player.openMenu(screenHandlerFactory);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (!state.getValue(VERTICAL)) {
            if ((state.getValue(FACING) == Direction.SOUTH || state.getValue(FACING) == Direction.NORTH)) {
                return SHAPE_X;
            }
            if ((state.getValue(FACING) == Direction.EAST || state.getValue(FACING) == Direction.WEST)) {
                return SHAPE_Z;
            }
        }
        return SHAPE_VERTICAL;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof KegBlockEntity kegBlockEntity) {
            kegBlockEntity.updateTemperature();
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction direction = ctx.getNearestLookingDirection();
        FluidState fluid = ctx.getLevel().getFluidState(ctx.getClickedPos());
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(VERTICAL, true).setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
        }
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        if (world.getBlockEntity(pos) instanceof KegBlockEntity blockEntity) {
            blockEntity.updateTemperature();
        }
        return state;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(world, pos, state);
        KegBlockEntity kegBlockEntity = (KegBlockEntity) world.getBlockEntity(pos);
        if (kegBlockEntity != null) {
            CompoundTag nbt = kegBlockEntity.writeDrink(new CompoundTag());
            if (!nbt.isEmpty()) {
                stack.addTagElement("BlockEntityTag", nbt);
            }
            if (kegBlockEntity.hasCustomName()) {
                stack.setHoverName(kegBlockEntity.getCustomName());
            }
        }
        return stack;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof KegBlockEntity kegBlockEntity) {
                Containers.dropContents(world, pos, kegBlockEntity.getDroppableInventory());
                kegBlockEntity.getUsedRecipesAndPopExperience(world, Vec3.atCenterOf(pos));
                world.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, world, pos, newState, moved);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        super.appendHoverText(stack, world, tooltip, options);
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        if (nbt != null) {
            CompoundTag inventoryTag = nbt.getCompound("Inventory");
            if (inventoryTag.contains("Items", 9)) {
                NonNullList<ItemStack> inventory = NonNullList.withSize(KegBlockEntity.INVENTORY_SIZE, ItemStack.EMPTY);
                ContainerHelper.loadAllItems(inventoryTag, inventory);
                ItemStack meal = inventory.get(KegBlockEntity.MEAL_DISPLAY_SLOT);
                if (!meal.isEmpty()) {
                    MutableComponent textServingsOf = meal.getCount() == 1
                            ? BCTextUtils.getTranslation("tooltip.keg.single_serving")
                            : BCTextUtils.getTranslation("tooltip.keg.many_servings", meal.getCount());
                    tooltip.add(textServingsOf.withStyle(ChatFormatting.GRAY));
                    MutableComponent textMealName = meal.getHoverName().copy();
                    tooltip.add(textMealName.withStyle(meal.getRarity().color));
                }
            }
        } else {
            MutableComponent textEmpty = BCTextUtils.getTranslation("tooltip.keg.empty");

        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, VERTICAL, WATERLOGGED);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomHoverName()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof KegBlockEntity kegBlockEntity) {
                kegBlockEntity.setCustomName(itemStack.getHoverName());
                kegBlockEntity.updateTemperature();
            }
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof KegBlockEntity kegBlockEntity) {
            return MathUtils.calcRedstoneFromItemHandler(kegBlockEntity.inventory);
        }

        return 0;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        if (world.isClientSide()) {
            return createTickerHelper(type, BCBlockEntityTypes.KEG, KegBlockEntity::animationTick);
        } else {
            return createTickerHelper(type, BCBlockEntityTypes.KEG, KegBlockEntity::fermentingTick);
        }
    }

}
