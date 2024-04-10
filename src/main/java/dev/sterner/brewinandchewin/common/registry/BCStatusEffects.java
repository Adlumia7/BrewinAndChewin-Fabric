package dev.sterner.brewinandchewin.common.registry;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.statuseffect.EmptyStatusEffect;
import dev.sterner.brewinandchewin.common.statuseffect.SatisfactionEffect;
import dev.sterner.brewinandchewin.common.statuseffect.TipsyEffect;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public interface BCStatusEffects {
    Map<MobEffect, ResourceLocation> STATUS_EFFECTS = new LinkedHashMap<>();

    MobEffect TIPSY = create("tipsy", new TipsyEffect(MobEffectCategory.BENEFICIAL, 0x605448));
    MobEffect SWEET_HEART = create("sweet_heart", new EmptyStatusEffect(MobEffectCategory.BENEFICIAL, 0x605448));
    MobEffect SATISFACTION = create("satisfaction", new SatisfactionEffect(MobEffectCategory.BENEFICIAL, 0x605448));

    static <T extends MobEffect> T create(String name, T effect) {
        STATUS_EFFECTS.put(effect, new ResourceLocation(BrewinAndChewin.MODID, name));
        return effect;
    }

    static void init() {
        STATUS_EFFECTS.keySet().forEach(effect -> Registry.register(BuiltInRegistries.MOB_EFFECT, STATUS_EFFECTS.get(effect), effect));
    }
}
