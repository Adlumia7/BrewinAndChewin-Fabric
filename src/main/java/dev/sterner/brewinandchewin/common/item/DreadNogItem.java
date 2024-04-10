package dev.sterner.brewinandchewin.common.item;

import dev.sterner.brewinandchewin.common.registry.BCStatusEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DreadNogItem extends BoozeItem {
    public DreadNogItem(int potency, int duration, Properties settings) {
        super(potency, duration, settings);
    }

    @Override
    public void affectConsumer(ItemStack stack, Level level, LivingEntity consumer) {
        MobEffectInstance badOmenEffect = consumer.getEffect(MobEffects.BAD_OMEN);
        if (!consumer.hasEffect(MobEffects.BAD_OMEN)) {
            consumer.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 12000, 0), consumer);
        } else if (badOmenEffect != null && badOmenEffect.getAmplifier() < 2) {
            consumer.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 12000, badOmenEffect.getAmplifier() + 1), consumer);
        }
        if (consumer.hasEffect(BCStatusEffects.TIPSY)) {
            MobEffectInstance tipsyEffect = consumer.getEffect(BCStatusEffects.TIPSY);
            if (tipsyEffect != null) {
                consumer.addEffect(new MobEffectInstance(BCStatusEffects.TIPSY, tipsyEffect.getDuration() + (duration * 600), tipsyEffect.getAmplifier() + potency), consumer);
            }
        } else if (!consumer.hasEffect(BCStatusEffects.TIPSY)) {
            consumer.addEffect(new MobEffectInstance(BCStatusEffects.TIPSY, duration * 1200, potency - 1), consumer);
        }
    }
}
