package dev.sterner.brewinandchewin.common.registry;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import vectorwing.farmersdelight.common.registry.ModEffects;

public interface BCFoodComponents {
    FoodProperties MEAD = new FoodProperties.Builder()
            .nutrition(6).saturationMod(0.1F)
            .effect(new MobEffectInstance(BCStatusEffects.SWEET_HEART, 6000, 0), 1.0F).build();
    FoodProperties RICE_WINE = new FoodProperties.Builder()
            .effect(new MobEffectInstance(BCStatusEffects.SATISFACTION, 6000, 0), 1.0F).build();
    FoodProperties EGG_GROG = new FoodProperties.Builder()
            .effect(new MobEffectInstance(MobEffects.ABSORPTION, 3600, 0), 1.0F).build();
    FoodProperties STRONGROOT_ALE = new FoodProperties.Builder()
            .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0), 1.0F).build();
    FoodProperties SACCHARINE_RUM = new FoodProperties.Builder()
            .nutrition(6).saturationMod(0.1F)
            .effect(new MobEffectInstance(BCStatusEffects.SWEET_HEART, 9600, 1), 1.0F).build();
    FoodProperties PALE_JANE = new FoodProperties.Builder()
            .effect(new MobEffectInstance(BCStatusEffects.SATISFACTION, 9600, 1), 1.0F).build();
    FoodProperties SALTY_FOLLY = new FoodProperties.Builder()
            .effect(new MobEffectInstance(MobEffects.WATER_BREATHING, 9600, 0), 1.0F).build();
    FoodProperties STEEL_TOE_STOUT = new FoodProperties.Builder()
            .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 12000, 1), 1.0F).build();
    FoodProperties GLITTERING_GRENADINE = new FoodProperties.Builder()
            .effect(new MobEffectInstance(MobEffects.GLOWING, 12000, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.NIGHT_VISION, 12000, 0), 1.0F).build();
    FoodProperties BLOODY_MARY = new FoodProperties.Builder()
            .effect(new MobEffectInstance(ModEffects.COMFORT.get(), 6000, 0), 1.0F).build();
    FoodProperties RED_RUM = new FoodProperties.Builder()
            .effect(new MobEffectInstance(ModEffects.COMFORT.get(), 9600, 1), 1.0F).build();
    FoodProperties WITHERING_DROSS = new FoodProperties.Builder()
            .effect(new MobEffectInstance(MobEffects.BLINDNESS, 12000, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.WEAKNESS, 12000, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 12000, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.WITHER, 12000, 0), 1.0F).build();
    FoodProperties KOMBUHCA = new FoodProperties.Builder()
            .effect(new MobEffectInstance(BCStatusEffects.SATISFACTION, 3600, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DIG_SPEED, 6000, 0), 1.0F).build();


    FoodProperties KIMCHI = new FoodProperties.Builder()
            .nutrition(2).saturationMod(0.6F)
            .effect(new MobEffectInstance(BCStatusEffects.SATISFACTION, 2400, 0), 1.0F).build();
    FoodProperties JERKY = new FoodProperties.Builder()
            .nutrition(3).saturationMod(0.7F).fast().build();
    FoodProperties PICKLED_PICKLES = new FoodProperties.Builder()
            .nutrition(4).saturationMod(0.3F)
            .effect(new MobEffectInstance(MobEffects.NIGHT_VISION, 2400, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.LUCK, 6000, 0), 1.0F).build();
    FoodProperties KIPPERS = new FoodProperties.Builder()
            .nutrition(6).saturationMod(0.5F)
            .effect(new MobEffectInstance(MobEffects.WATER_BREATHING, 600, 0), 1.0F).build();
    FoodProperties COCOA_FUDGE = new FoodProperties.Builder()
            .nutrition(4).saturationMod(0.8F)
            .effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 0), 1.0F).build();

    FoodProperties FLAXEN_CHEESE = new FoodProperties.Builder()
            .nutrition(4).saturationMod(1.0F).build();
    FoodProperties SCARLET_CHEESE = new FoodProperties.Builder()
            .nutrition(4).saturationMod(1.0F).build();

    FoodProperties PIZZA_SLICE = new FoodProperties.Builder()
            .nutrition(3).saturationMod(1.0F).build();
    FoodProperties HAM_AND_CHEESE_SANDWICH = new FoodProperties.Builder()
            .nutrition(9).saturationMod(1.0F).build();

    //Bowl Foods

    FoodProperties QUICHE_SLICE = new FoodProperties.Builder()
            .nutrition(3).saturationMod(0.8F).fast().build();
    FoodProperties FIERY_FONDUE = new FoodProperties.Builder()
            .nutrition(16).saturationMod(1.0F)
            .effect(new MobEffectInstance(ModEffects.NOURISHMENT.get(), 7200, 0), 1.0F).build();

    FoodProperties VEGETABLE_OMELET = new FoodProperties.Builder()
            .nutrition(12).saturationMod(0.75F)
            .effect(new MobEffectInstance(ModEffects.NOURISHMENT.get(), 4800, 0), 1.0F).build();
    FoodProperties CHEESY_PASTA = new FoodProperties.Builder()
            .nutrition(12).saturationMod(0.8F)
            .effect(new MobEffectInstance(ModEffects.NOURISHMENT.get(), 4800, 0), 1.0F).build();

    FoodProperties CREAMY_ONION_SOUP = new FoodProperties.Builder()
            .nutrition(12).saturationMod(0.75F)
            .effect(new MobEffectInstance(ModEffects.COMFORT.get(), 4800, 0), 1.0F).build();

    FoodProperties SCARLET_PIEROGIES = new FoodProperties.Builder()
            .nutrition(14).saturationMod(0.75F)
            .effect(new MobEffectInstance(ModEffects.NOURISHMENT.get(), 4800, 0), 1.0F).build();
    FoodProperties HORROR_LASAGNA = new FoodProperties.Builder()
            .nutrition(14).saturationMod(0.75F)
            .effect(new MobEffectInstance(ModEffects.NOURISHMENT.get(), 7200, 0), 1.0F).build();
}
