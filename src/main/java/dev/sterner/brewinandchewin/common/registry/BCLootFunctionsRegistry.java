package dev.sterner.brewinandchewin.common.registry;

import dev.sterner.brewinandchewin.common.loot.CopyDrinkFunction;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public enum BCLootFunctionsRegistry {
    COPY_DRINK("copy_drink", CopyDrinkFunction.Serializer::new);

    private final String pathName;
    private final Supplier<LootItemConditionalFunction.Serializer<? extends LootItemFunction>> lootFunctionSerializerSupplier;
    private LootItemConditionalFunction.Serializer<? extends LootItemFunction> serializer;
    private LootItemFunctionType type;

    BCLootFunctionsRegistry(String pathName, Supplier lootFunctionSerializerSupplier) {
        this.pathName = pathName;
        this.lootFunctionSerializerSupplier = lootFunctionSerializerSupplier;
    }

    public static void init() {
        BCLootFunctionsRegistry[] var0 = values();

        for (BCLootFunctionsRegistry value : var0) {
            Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, new ResourceLocation("brewinandchewin", value.pathName), value.type());
        }

    }

    public LootItemFunctionType type() {
        if (this.type == null) {
            this.type = new LootItemFunctionType(this.serializer());
        }

        return this.type;
    }

    public LootItemConditionalFunction.Serializer<? extends LootItemFunction> serializer() {
        if (this.serializer == null) {
            this.serializer = this.lootFunctionSerializerSupplier.get();
        }

        return this.serializer;
    }
}