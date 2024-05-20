package dev.sterner.brewinandchewin.common.block.entity;

import com.google.common.collect.Lists;
import dev.sterner.brewinandchewin.common.block.FermentationControllerBlock;
import dev.sterner.brewinandchewin.common.block.KegBlock;
import dev.sterner.brewinandchewin.common.block.screen.KegBlockScreenHandler;
import dev.sterner.brewinandchewin.common.recipe.KegRecipe;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import dev.sterner.brewinandchewin.common.registry.BCRecipeTypes;
import dev.sterner.brewinandchewin.common.registry.BCTags;
import dev.sterner.brewinandchewin.common.util.BCTextUtils;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerContainer;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.mixin.accessor.RecipeManagerAccessor;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import vectorwing.farmersdelight.common.tag.ModTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KegBlockEntity extends SyncedBlockEntity implements ExtendedScreenHandlerFactory, Nameable, RecipeHolder {
    public static final int MEAL_DISPLAY_SLOT = 5;
    public static final int CONTAINER_SLOT = 7;
    public static final int OUTPUT_SLOT = 8;
    public static final int INVENTORY_SIZE = OUTPUT_SLOT + 1;

    public final ItemStackHandlerContainer inventory;

    private int fermentTime;
    private int fermentTimeTotal;
    private ItemStack drinkContainerStack;
    private Component customName;
    public int kegTemperature;
    protected final ContainerData kegData;
    private final Object2IntOpenHashMap<ResourceLocation> usedRecipeTracker;
    private ResourceLocation lastRecipeID;
    private boolean checkNewRecipe;

    public KegBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntityTypes.KEG, pos, state);
        this.inventory = createHandler();
        this.drinkContainerStack = ItemStack.EMPTY;
        this.kegData = new KegSyncedData();
        this.usedRecipeTracker = new Object2IntOpenHashMap<>();
        this.checkNewRecipe = true;
    }

    private ItemStackHandlerContainer createHandler() {
        return new ItemStackHandlerContainer(INVENTORY_SIZE)
        {
            @Override
            protected void onContentsChanged(int slot) {
                if (slot >= 0 && slot < MEAL_DISPLAY_SLOT) {
                    checkNewRecipe = true;
                }
                inventoryChanged();
            }
        };
    }


    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        inventory.deserializeNBT(compound.getCompound("Inventory"));
        fermentTime = compound.getInt("FermentTime");
        fermentTimeTotal = compound.getInt("FermentTimeTotal");
        drinkContainerStack = ItemStack.of(compound.getCompound("Container"));
        kegTemperature = compound.getInt("Temperature");
        if (compound.contains("CustomName", 8)) {
            customName = Component.Serializer.fromJson(compound.getString("CustomName"));
        }
        CompoundTag compoundRecipes = compound.getCompound("RecipesUsed");
        for (String key : compoundRecipes.getAllKeys()) {
            usedRecipeTracker.put(new ResourceLocation(key), compoundRecipes.getInt(key));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("FermentTime", fermentTime);
        compound.putInt("FermentTimeTotal", fermentTimeTotal);
        compound.put("Container", drinkContainerStack.save(new CompoundTag()));
        compound.putInt("Temperature", kegTemperature);
        if (customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(customName));
        }
        compound.put("Inventory", inventory.serializeNBT());
        CompoundTag compoundRecipes = new CompoundTag();
        usedRecipeTracker.forEach((recipeId, craftedAmount) -> compoundRecipes.putInt(recipeId.toString(), craftedAmount));
        compound.put("RecipesUsed", compoundRecipes);
    }

    public CompoundTag writeDrink(CompoundTag compound) {
        if (!this.getDrink().isEmpty()) {
            NonNullList<ItemStack> drops = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

            for (int i = 0; i < INVENTORY_SIZE; ++i) {
                drops.set(i, i == MEAL_DISPLAY_SLOT ? this.inventory.getItem(i) : ItemStack.EMPTY);
            }

            if (this.customName != null) {
                compound.putString("CustomName", Component.Serializer.toJson(this.customName));
            }

            compound.put("Container", drinkContainerStack.save(new CompoundTag()));
            compound.put("Inventory", ContainerHelper.saveAllItems(new CompoundTag(), drops));
        }
        return compound;
    }

    // ======== BASIC FUNCTIONALITY ========

    public static void fermentingTick(Level level, BlockPos pos, BlockState state, KegBlockEntity keg) {
        boolean didInventoryChange = false;
        keg.updateTemperature();
        if (keg.hasInput()) {
            Optional<KegRecipe> recipe = keg.getMatchingRecipe(new RecipeWrapper(keg.inventory));

            if (recipe.isPresent() && keg.canFerment(recipe.get())) {
                didInventoryChange = keg.processFermenting(recipe.get(), keg);
            } else {
                keg.fermentTime = 0;
            }
        } else if (keg.fermentTimeTotal > 0) {
            keg.fermentTime = Mth.clamp(keg.fermentTime - 2, 0, keg.fermentTimeTotal);
        }

        ItemStack drinkStack = keg.getDrink();
        if (!drinkStack.isEmpty()) {
            if (!keg.doesDrinkHaveContainer(drinkStack)) {
                keg.moveDrinkToOutput();
                didInventoryChange = true;
            } else if (!keg.inventory.getItem(6).isEmpty()) {
                keg.useStoredContainersOnMeal();
                didInventoryChange = true;
            }
        }

        if (didInventoryChange) {
            keg.inventoryChanged();
        }
    }

    public void updateTemperature() {
        if (this.level != null && this.level.dimensionType().ultraWarm()) {
            this.kegTemperature = 10;
            return;
        }

        ArrayList<BlockState> states = new ArrayList<>();
        int range = 1;

        int heat;
        int cold;
        for (heat = -range; heat <= range; ++heat) {
            for (int y = -range; y <= range; ++y) {
                for (cold = -range; cold <= range; ++cold) {
                    states.add(this.level.getBlockState(this.getBlockPos().offset(heat, y, cold)));
                }
            }
        }

        heat = states.stream()
                .filter(s -> s.is(ModTags.HEAT_SOURCES))
                .filter(s -> s.hasProperty(BlockStateProperties.LIT))
                .filter(s -> s.getValue(BlockStateProperties.LIT))
                .mapToInt(s -> 1)
                .sum();

        heat += states.stream()
                .filter(s -> s.is(ModTags.HEAT_SOURCES))
                .filter(s -> !s.hasProperty(BlockStateProperties.LIT))
                .mapToInt(s -> 1)
                .sum();

        BlockState stateBelow = this.level.getBlockState(this.getBlockPos().below());
        if (stateBelow.is(ModTags.HEAT_CONDUCTORS)) {
            BlockState stateFurtherBelow = this.level.getBlockState(this.getBlockPos().below(2));
            if (stateFurtherBelow.is(ModTags.HEAT_SOURCES)) {
                if (stateFurtherBelow.hasProperty(BlockStateProperties.LIT)) {
                    if (stateFurtherBelow.getValue(BlockStateProperties.LIT)) {
                        ++heat;
                    }
                } else {
                    ++heat;
                }
            }
        }

        cold = states.stream()
                .filter(s -> s.is(BCTags.FREEZE_SOURCES))
                .mapToInt((s) -> 1)
                .sum();

        float biomeTemperature = this.level.getBiome(this.getBlockPos()).value().getBaseTemperature();
        if (biomeTemperature <= 0.0F) {
            cold += 2;
        } else if (biomeTemperature == 2.0F) {
            heat += 2;
        }

        int fcTemp = 0;
        if (this.level.getBlockEntity(worldPosition.below()) instanceof FermentationControllerBlockEntity blockEntity && level.getBlockState(worldPosition.below()).getValue(FermentationControllerBlock.VERTICAL)) {
            fcTemp = blockEntity.getTemperature();
        } else {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos pos1 = worldPosition.relative(direction);
                if (level.getBlockEntity(pos1) instanceof FermentationControllerBlockEntity blockEntity && !level.getBlockState(pos1).getValue(FermentationControllerBlock.VERTICAL)) {
                    if (direction.getOpposite() == level.getBlockState(pos1).getValue(HorizontalDirectionalBlock.FACING)) {
                        fcTemp = blockEntity.getTemperature();
                        break;
                    }
                }
            }
        }

        this.kegTemperature = heat - cold + fcTemp;
    }

    public int getTemperature() {
        if (this.kegTemperature < -4 && this.kegTemperature > -9) {
            return 2;
        }
        if (this.kegTemperature < -8) {
            return 1;
        }
        if (this.kegTemperature > 4 && this.kegTemperature < 9) {
            return 4;
        }
        if (this.kegTemperature > 8) {
            return 5;
        }
        return 3;
    }

    public static void animationTick(Level level, BlockPos pos, BlockState state, KegBlockEntity keg) {
    }

    private Optional<KegRecipe> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
        if (level == null) return Optional.empty();

        if (lastRecipeID != null) {
            Recipe<RecipeWrapper> recipe = ((RecipeManagerAccessor) level.getRecipeManager()).getRecipeMap(BCRecipeTypes.KEG_RECIPE_TYPE).get(lastRecipeID);
            if (recipe instanceof KegRecipe) {
                if (recipe.matches(inventoryWrapper, level)) {
                    return Optional.of((KegRecipe) recipe);
                }
                if (ItemStack.isSameItem(recipe.getResultItem(this.level.registryAccess()), getDrink())) {
                    return Optional.empty();
                }
            }
        }

        if (checkNewRecipe) {
            Optional<KegRecipe> recipe = level.getRecipeManager().getRecipeFor(BCRecipeTypes.KEG_RECIPE_TYPE, inventoryWrapper, level);

            if (recipe.isPresent()) {
                ResourceLocation newRecipeID = recipe.get().getId();

                lastRecipeID = newRecipeID;
                return recipe;
            }
        }

        checkNewRecipe = false;
        return Optional.empty();
    }

    public void onContentsChanged(int slot) {
        if (slot >= 0 && slot < 5) {
            this.checkNewRecipe = true;
        }

        this.inventoryChanged();
    }

    public ItemStack getContainer() {
        if (!drinkContainerStack.isEmpty()) {
            return drinkContainerStack;
        } else {
            return new ItemStack(getDrink().getItem().getCraftingRemainingItem());
        }
    }

    private boolean hasInput() {
        for (int i = 0; i < 5; ++i) {
            if (!inventory.getItem(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    protected boolean canFerment(KegRecipe recipe) {
        if (level == null) return false;

        int recipeTemp = recipe.getTemperature();
        if (this.hasInput() && (recipeTemp == 3 || recipeTemp == this.getTemperature())) {
            ItemStack resultStack = recipe.getResultItem(level.registryAccess());
            if (resultStack.isEmpty()) {
                return false;
            } else {
                ItemStack fluidStack = this.inventory.getItem(4);
                ItemStack storedContainerStack = this.inventory.getItem(8);
                if (!storedContainerStack.isEmpty() && fluidStack.getItem().getCraftingRemainingItem() != null && !fluidStack.getItem().getCraftingRemainingItem().equals(storedContainerStack.getItem())) {
                    return false;
                } else if (storedContainerStack.getCount() >= storedContainerStack.getMaxStackSize()) {
                    return false;
                } else {
                    ItemStack storedDrinkStack = this.inventory.getItem(5);
                    if (storedDrinkStack.isEmpty()) {
                        return true;
                    } else if (!ItemStack.isSameItem(storedDrinkStack, resultStack)) {
                        return false;
                    } else if (storedDrinkStack.getCount() + resultStack.getCount() <= this.inventory.getSlotLimit(5)) {
                        return true;
                    } else {
                        return storedDrinkStack.getCount() + resultStack.getCount() <= resultStack.getMaxStackSize();
                    }
                }
            }
        } else {
            return false;
        }
    }

    private boolean processFermenting(KegRecipe recipe, KegBlockEntity keg) {
        if (this.level == null) {
            return false;
        } else {
            ++this.fermentTime;
            this.fermentTimeTotal = recipe.getFermentTime();
            if (this.fermentTime < this.fermentTimeTotal) {
                return false;
            } else {
                this.fermentTime = 0;
                this.drinkContainerStack = recipe.getOutputContainer();
                ItemStack resultStack = recipe.getResultItem(level.registryAccess());
                ItemStack storedMealStack = this.inventory.getItem(5);
                if (storedMealStack.isEmpty()) {
                    this.inventory.setItem(5, resultStack.copy());
                } else if (ItemStack.isSameItem(storedMealStack, resultStack)) {
                    storedMealStack.grow(resultStack.getCount());
                }

                ItemStack storedContainers = this.inventory.getItem(8);
                ItemStack fluidStack = this.inventory.getItem(4);
                if (storedContainers.isEmpty()) {
                    this.inventory.setItem(8, new ItemStack(fluidStack.copy().getItem().getCraftingRemainingItem()));
                    if (fluidStack.getCount() == 1) {
                        this.inventory.setItem(4, ItemStack.EMPTY);
                    } else {
                        this.inventory.setItem(4, new ItemStack(fluidStack.getItem(), fluidStack.getCount() - 1));
                    }
                } else if (ItemStack.isSameItem(storedContainers, this.inventory.getItem(4).getItem().getCraftingRemainingItem().getDefaultInstance())) {
                    storedContainers.grow(resultStack.getCount());
                    if (fluidStack.getCount() == 1) {
                        this.inventory.setItem(4, ItemStack.EMPTY);
                    } else {
                        this.inventory.setItem(4, new ItemStack(fluidStack.getItem(), fluidStack.getCount() - 1));
                    }
                }

                keg.setRecipeUsed(recipe);

                for (int i = 0; i < 4; ++i) {
                    ItemStack slotStack = this.inventory.getItem(i);
                    if (slotStack.getItem().hasCraftingRemainingItem()) {
                        Direction direction = this.getBlockState().getValue(KegBlock.FACING).getCounterClockWise();
                        double x = (double) this.getBlockPos().getX() + 0.5 + (double) direction.getStepX() * 0.25;
                        double y = (double) this.getBlockPos().getY() + 0.7;
                        double z = (double) this.getBlockPos().getZ() + 0.5 + (double) direction.getStepZ() * 0.25;

                        ItemEntity entity = new ItemEntity(level, x, y, z, this.inventory.getItem(i).getItem().getCraftingRemainingItem().getDefaultInstance());
                        entity.setDeltaMovement(((float) direction.getStepX() * 0.08F), 0.25, ((float) direction.getStepZ() * 0.08F));
                        level.addFreshEntity(entity);
                    }

                    if (!slotStack.isEmpty()) {
                        slotStack.shrink(1);
                    }
                }

                return true;
            }
        }
    }



    public void unlockLastRecipe(Player player) {
        List<Recipe<?>> usedRecipes = this.getUsedRecipesAndPopExperience(player.level(), player.position());
        player.awardRecipes(usedRecipes);
        this.usedRecipeTracker.clear();
    }

    public List<Recipe<?>> getUsedRecipesAndPopExperience(Level level, Vec3 pos) {
        List<Recipe<?>> list = Lists.newArrayList();

        for (Object2IntMap.Entry<ResourceLocation> identifierEntry : this.usedRecipeTracker.object2IntEntrySet()) {
            level.getRecipeManager().byKey(identifierEntry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                splitAndSpawnExperience((ServerLevel) level, pos, identifierEntry.getIntValue(), ((KegRecipe) recipe).getExperience());
            });
        }

        return list;
    }

    private static void splitAndSpawnExperience(ServerLevel level, Vec3 pos, int craftedAmount, float experience) {
        int expTotal = Mth.floor((float) craftedAmount * experience);
        float expFraction = Mth.frac((float) craftedAmount * experience);
        if (expFraction != 0.0F && Math.random() < (double) expFraction) {
            ++expTotal;
        }

        ExperienceOrb.award(level, pos, expTotal);
    }


    public ItemStack getDrink() {
        return this.inventory.getItem(5);
    }

    public NonNullList<ItemStack> getDroppableInventory() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            drops.add(i == MEAL_DISPLAY_SLOT ? ItemStack.EMPTY : inventory.getItem(i));
        }

        return drops;
    }

    private void moveDrinkToOutput() {
        ItemStack mealStack = inventory.getItem(5);
        ItemStack outputStack = inventory.getItem(7);
        int mealCount = Math.min(mealStack.getCount(), mealStack.getMaxStackSize() - outputStack.getCount());
        if (outputStack.isEmpty()) {
            inventory.setItem(7, mealStack.split(mealCount));
        } else if (outputStack.getItem() == mealStack.getItem()) {
            mealStack.shrink(mealCount);
            outputStack.grow(mealCount);
        }
    }

    private void useStoredContainersOnMeal() {
        ItemStack mealStack = inventory.getItem(5);
        ItemStack containerInputStack = inventory.getItem(6);
        ItemStack outputStack = inventory.getItem(7);

        if (isContainerValid(containerInputStack) && outputStack.getCount() < outputStack.getMaxStackSize()) {
            int smallerStackCount = Math.min(mealStack.getCount(), containerInputStack.getCount());
            int mealCount = Math.min(smallerStackCount, mealStack.getMaxStackSize() - outputStack.getCount());
            if (outputStack.isEmpty()) {
                containerInputStack.shrink(mealCount);
                inventory.setItem(7, mealStack.split(mealCount));
            } else if (outputStack.getItem() == mealStack.getItem()) {
                mealStack.shrink(mealCount);
                containerInputStack.shrink(mealCount);
                outputStack.grow(mealCount);
            }
        }
    }

    public ItemStack useHeldItemOnMeal(ItemStack container) {
        if (isContainerValid(container) && !getDrink().isEmpty()) {
            container.shrink(1);
            return getDrink().split(1);
        }
        return ItemStack.EMPTY;
    }

    private boolean doesDrinkHaveContainer(ItemStack meal) {
        return !drinkContainerStack.isEmpty() || meal.getItem().hasCraftingRemainingItem();
    }

    public boolean isContainerValid(ItemStack containerItem) {
        if (containerItem.isEmpty() || this.getDrink().isEmpty()) {
            return false;
        } else {
            return !this.drinkContainerStack.isEmpty() ? ItemStack.isSameItem(this.drinkContainerStack, containerItem) : ItemStack.isSameItem(this.getDrink().getItem().getCraftingRemainingItem().getDefaultInstance(), containerItem);
        }
    }

    @Override
    public Component getName() {
        return customName != null ? customName : BCTextUtils.getTranslation("container.keg");
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return customName;
    }

    public void setCustomName(Component name) {
        customName = name;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new KegBlockScreenHandler(syncId, inv, this, kegData);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = new CompoundTag();
        saveAdditional(nbt);

        return nbt;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(worldPosition);
    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation recipeID = recipe.getId();
            usedRecipeTracker.addTo(recipeID, 1);
        }
    }

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return null;
    }

    private class KegSyncedData implements ContainerData {

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> KegBlockEntity.this.fermentTime;
                case 1 -> KegBlockEntity.this.fermentTimeTotal;
                case 2 -> KegBlockEntity.this.kegTemperature;
                case 3 -> KegBlockEntity.this.getTemperature();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> KegBlockEntity.this.fermentTime = value;
                case 1 -> KegBlockEntity.this.fermentTimeTotal = value;
                case 2 -> KegBlockEntity.this.kegTemperature = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
