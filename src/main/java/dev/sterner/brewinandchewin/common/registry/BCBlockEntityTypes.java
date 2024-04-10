package dev.sterner.brewinandchewin.common.registry;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.entity.FermentationControllerBlockEntity;
import dev.sterner.brewinandchewin.common.block.entity.ItemCoasterBlockEntity;
import dev.sterner.brewinandchewin.common.block.entity.KegBlockEntity;
import dev.sterner.brewinandchewin.common.block.entity.TankardBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import java.util.LinkedHashMap;
import java.util.Map;

public interface BCBlockEntityTypes {
    Map<BlockEntityType<?>, ResourceLocation> BLOCK_ENTITY_TYPES = new LinkedHashMap<>();

    BlockEntityType<KegBlockEntity> KEG = register("keg", FabricBlockEntityTypeBuilder.create(KegBlockEntity::new, BCObjects.KEG).build());

    BlockEntityType<ItemCoasterBlockEntity> COASTER = register("coaster", FabricBlockEntityTypeBuilder.create(ItemCoasterBlockEntity::new, BCObjects.COASTER).build());

    BlockEntityType<FermentationControllerBlockEntity> FERMENTATION_CONTROLLER = register("fermentation_controller", FabricBlockEntityTypeBuilder.create(FermentationControllerBlockEntity::new, BCObjects.FERMENTATION_CONTROLLER).build());

    BlockEntityType<TankardBlockEntity> TANKARD = register("tankard", FabricBlockEntityTypeBuilder.create(TankardBlockEntity::new,
            BCObjects.TANKARD_BLOCK
    ).build());


    static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        BLOCK_ENTITY_TYPES.put(type, new ResourceLocation(BrewinAndChewin.MODID, name));
        return type;
    }

    static void init() {
        BLOCK_ENTITY_TYPES.keySet().forEach(blockEntityType -> Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, BLOCK_ENTITY_TYPES.get(blockEntityType), blockEntityType));
    }
}
