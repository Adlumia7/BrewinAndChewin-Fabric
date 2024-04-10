package dev.sterner.brewinandchewin.common.item;

import dev.sterner.brewinandchewin.common.registry.BCObjects;
import dev.sterner.brewinandchewin.common.registry.BCStatusEffects;
import dev.sterner.brewinandchewin.common.util.BCTextUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import vectorwing.farmersdelight.common.item.DrinkableItem;

public class BoozeItem extends DrinkableItem {
    protected final int potency;
    protected final int duration;

    public BoozeItem(int potency, int duration, Properties settings) {
        super(settings);
        this.potency = potency;
        this.duration = duration;
    }

    @Override
    public void affectConsumer(ItemStack stack, Level world, LivingEntity user) {
        if (user.hasEffect(BCStatusEffects.TIPSY)) {
            MobEffectInstance effect = user.getEffect(BCStatusEffects.TIPSY);
            if (effect != null) {
                if (effect.getAmplifier() == 8) {
                    user.addEffect(new MobEffectInstance(BCStatusEffects.TIPSY, effect.getDuration() + (duration * 600), effect.getAmplifier() + 1), user);
                }
                if (effect.getAmplifier() == 7 && potency > 2) {
                    user.addEffect(new MobEffectInstance(BCStatusEffects.TIPSY, effect.getDuration() + (duration * 600), effect.getAmplifier() + 2), user);
                }
                if (effect.getAmplifier() < 9) {
                    user.addEffect(new MobEffectInstance(BCStatusEffects.TIPSY, effect.getDuration() + (duration * 600), effect.getAmplifier() + potency), user);
                }
                if (effect.getAmplifier() == 9) {
                    user.addEffect(new MobEffectInstance(BCStatusEffects.TIPSY, effect.getDuration() + (duration * 600), effect.getAmplifier()), user);
                }
            }
        } else if (!user.hasEffect(BCStatusEffects.TIPSY)) {
            user.addEffect(new MobEffectInstance(BCStatusEffects.TIPSY, duration * 1200, potency - 1), user);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        if (potency == 1) {
            MutableComponent textTipsy = BCTextUtils.getTranslation("tooltip.tipsy1", duration);
            tooltip.add(textTipsy.withStyle(ChatFormatting.RED));
        }
        if (potency == 2) {
            MutableComponent textTipsy = BCTextUtils.getTranslation("tooltip.tipsy2", duration);
            tooltip.add(textTipsy.withStyle(ChatFormatting.RED));
        }
        if (potency == 3) {
            MutableComponent textTipsy = BCTextUtils.getTranslation("tooltip.tipsy3", duration);
            tooltip.add(textTipsy.withStyle(ChatFormatting.RED));
        }
        BCTextUtils.addFoodEffectTooltip(stack, tooltip, 1.0F);
        if (ItemStack.isSameItem(stack, new ItemStack(BCObjects.DREAD_NOG))) {
            MutableComponent textEmpty = BCTextUtils.getTranslation("tooltip." + this);
            tooltip.add(textEmpty.withStyle(ChatFormatting.RED));
        }
    }
}
