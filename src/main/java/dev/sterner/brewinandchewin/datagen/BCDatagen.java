package dev.sterner.brewinandchewin.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;

public class BCDatagen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var v = fabricDataGenerator.createPack();
        v.addProvider(BCLanguageProvider::new);
        //v.addProvider(BCRecipeProvider::new);
    }
}
