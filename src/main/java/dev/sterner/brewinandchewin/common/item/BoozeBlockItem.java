package dev.sterner.brewinandchewin.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class BoozeBlockItem extends BoozeItem {
    private final Block block;

    public BoozeBlockItem(Block block, int potency, int duration, Properties settings) {
        super(potency, duration, settings);
        this.block = block;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState blockState = world.getBlockState(pos);

        if (blockState.is(this.block)) {
            return InteractionResult.CONSUME;
        }

        InteractionResult actionResult = this.place(new BlockPlaceContext(context));
        if (!actionResult.consumesAction() && this.isEdible()) {
            InteractionResult actionResult2 = this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
            return actionResult2 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : actionResult2;
        } else {
            return actionResult;
        }
    }

    public InteractionResult place(BlockPlaceContext itemPlacementContext) {
        if (!this.getBlock().isEnabled(itemPlacementContext.getLevel().enabledFeatures())) {
            return InteractionResult.FAIL;
        } else if (!itemPlacementContext.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockState blockState = this.getPlacementState(itemPlacementContext);
            if (blockState == null) {
                return InteractionResult.FAIL;
            } else if (!this.place(itemPlacementContext, blockState)) {
                return InteractionResult.FAIL;
            } else {
                BlockPos blockPos = itemPlacementContext.getClickedPos();
                Level world = itemPlacementContext.getLevel();
                Player playerEntity = itemPlacementContext.getPlayer();
                ItemStack itemStack = itemPlacementContext.getItemInHand();
                BlockState blockState2 = world.getBlockState(blockPos);
                if (blockState2.is(blockState.getBlock())) {
                    blockState2 = this.placeFromNbt(blockPos, world, itemStack, blockState2);
                    writeNbtToBlockEntity(world, playerEntity, blockPos, itemStack);
                    blockState2.getBlock().setPlacedBy(world, blockPos, blockState2, playerEntity, itemStack);
                    if (playerEntity instanceof ServerPlayer) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) playerEntity, blockPos, itemStack);
                    }
                }

                SoundType blockSoundGroup = blockState2.getSoundType();
                world.playSound(
                        playerEntity,
                        blockPos,
                        this.getPlaceSound(blockState2),
                        SoundSource.BLOCKS,
                        (blockSoundGroup.getVolume() + 1.0F) / 2.0F,
                        blockSoundGroup.getPitch() * 0.8F
                );
                world.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(playerEntity, blockState2));
                if (playerEntity == null || !playerEntity.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }

                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
    }

    protected SoundEvent getPlaceSound(BlockState state) {
        return state.getSoundType().getPlaceSound();
    }

    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState blockState = this.getBlock().getStateForPlacement(context);
        return blockState != null && this.canPlace(context, blockState) ? blockState : null;
    }

    private BlockState placeFromNbt(BlockPos pos, Level world, ItemStack stack, BlockState state) {
        BlockState blockState = state;
        CompoundTag nbtCompound = stack.getTag();
        if (nbtCompound != null) {
            CompoundTag nbtCompound2 = nbtCompound.getCompound("BlockStateTag");
            StateDefinition<Block, BlockState> stateManager = state.getBlock().getStateDefinition();

            for (String string : nbtCompound2.getAllKeys()) {
                Property<?> property = stateManager.getProperty(string);
                if (property != null) {
                    String string2 = nbtCompound2.get(string).getAsString();
                    blockState = with(blockState, property, string2);
                }
            }
        }

        if (blockState != state) {
            world.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
        }

        return blockState;
    }

    private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String name) {
        return property.getValue(name).map(value -> state.setValue(property, value)).orElse(state);
    }

    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        Player playerEntity = context.getPlayer();
        CollisionContext shapeContext = playerEntity == null ? CollisionContext.empty() : CollisionContext.of(playerEntity);
        return (!this.checkStatePlacement() || state.canSurvive(context.getLevel(), context.getClickedPos()))
                && context.getLevel().isUnobstructed(state, context.getClickedPos(), shapeContext);
    }

    protected boolean checkStatePlacement() {
        return true;
    }

    protected boolean place(BlockPlaceContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos(), state, Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE);
    }

    public static boolean writeNbtToBlockEntity(Level world, @Nullable Player player, BlockPos pos, ItemStack stack) {
        MinecraftServer minecraftServer = world.getServer();
        if (minecraftServer == null) {
            return false;
        } else {
            CompoundTag nbtCompound = getBlockEntityNbt(stack);
            if (nbtCompound != null) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity != null) {
                    if (!world.isClientSide && blockEntity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks())) {
                        return false;
                    }

                    CompoundTag nbtCompound2 = blockEntity.saveWithoutMetadata();
                    CompoundTag nbtCompound3 = nbtCompound2.copy();
                    nbtCompound2.merge(nbtCompound);
                    if (!nbtCompound2.equals(nbtCompound3)) {
                        blockEntity.load(nbtCompound2);
                        blockEntity.setChanged();
                        return true;
                    }
                }
            }

            return false;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        this.getBlock().appendHoverText(stack, world, tooltip, context);
    }

    public Block getBlock() {
        return this.block;
    }

    @Nullable
    public static CompoundTag getBlockEntityNbt(ItemStack stack) {
        return stack.getTagElement("BlockEntityTag");
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.getBlock().requiredFeatures();
    }
}
