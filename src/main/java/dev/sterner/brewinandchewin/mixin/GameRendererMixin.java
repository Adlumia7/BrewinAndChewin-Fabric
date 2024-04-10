package dev.sterner.brewinandchewin.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @ModifyArg(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 0))
    private float adjust1(float amount) {
        return BC$modifyAmplifier(amount);
    }

    @ModifyArg(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 1))
    private float adjust2(float amount) {
        return BC$modifyAmplifier(amount);
    }

    private float BC$modifyAmplifier(float amount) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player != null && player.hasEffect(MobEffects.CONFUSION) && player.getEffect(MobEffects.CONFUSION).getAmplifier() > 0) {
            int amplifier = player.getEffect(MobEffects.CONFUSION).getAmplifier() + 1;
            return amount / (float) amplifier;
        } else {
            return amount;
        }
    }
}