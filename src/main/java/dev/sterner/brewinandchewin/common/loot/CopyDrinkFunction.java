package dev.sterner.brewinandchewin.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.entity.KegBlockEntity;
import dev.sterner.brewinandchewin.common.registry.BCLootFunctionsRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.Nullable;

public class CopyDrinkFunction extends LootItemConditionalFunction {
    public static final ResourceLocation ID = new ResourceLocation(BrewinAndChewin.MODID, "copy_meal");

    private CopyDrinkFunction(LootItemCondition[] conditions) {
        super(conditions);
    }

    public static LootItemConditionalFunction.Builder<?> builder() {
        return simpleBuilder(CopyDrinkFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        BlockEntity tile = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (tile instanceof KegBlockEntity kegBlockEntity) {
            CompoundTag tag = kegBlockEntity.writeDrink(new CompoundTag());
            if (!tag.isEmpty()) {
                stack.addTagElement("BlockEntityTag", tag);
            }
        }
        return stack;
    }

    @Override
    @Nullable
    public LootItemFunctionType getType() {
        return BCLootFunctionsRegistry.COPY_DRINK.type();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<CopyDrinkFunction> {

        @Override
        public CopyDrinkFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
            return new CopyDrinkFunction(conditions);
        }
    }
}