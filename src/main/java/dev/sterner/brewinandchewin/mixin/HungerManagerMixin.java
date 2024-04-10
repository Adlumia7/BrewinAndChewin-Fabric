package dev.sterner.brewinandchewin.mixin;

import dev.sterner.brewinandchewin.common.registry.BCStatusEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class HungerManagerMixin {

    @Unique
    private Player player;

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void findPlayer(Player player, CallbackInfo ci) {
        this.player = player;
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"))
    private float disableNaturalRegen(float amount) {
        if (player.hasEffect(BCStatusEffects.TIPSY)) {
            MobEffectInstance effect = player.getEffect(BCStatusEffects.TIPSY);
            if (effect != null) {
                return effect.getAmplifier() > 0 ? 0.0F : amount;
            }
        }
        return amount;
    }
}
