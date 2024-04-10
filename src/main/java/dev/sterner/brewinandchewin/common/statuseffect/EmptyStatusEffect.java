package dev.sterner.brewinandchewin.common.statuseffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EmptyStatusEffect extends MobEffect {
    public EmptyStatusEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }
}
