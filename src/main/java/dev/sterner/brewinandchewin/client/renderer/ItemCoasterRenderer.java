package dev.sterner.brewinandchewin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.sterner.brewinandchewin.common.block.entity.ItemCoasterBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ItemCoasterRenderer implements BlockEntityRenderer<ItemCoasterBlockEntity> {
    public ItemCoasterRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(ItemCoasterBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        ItemStack boardStack = entity.getStoredItem();
        int posLong = (int) entity.getBlockPos().asLong();
        if (!boardStack.isEmpty() && entity.getLevel() != null) {
            matrices.pushPose();
            matrices.translate(0.5, 0.3 + (double) (Mth.sin((float) entity.getLevel().getGameTime() / 50.0F) / 40.0F), 0.5);
            float f3 = 3.2F * ((float) entity.getLevel().getGameTime() + 1.0F) / 5.0F;
            matrices.mulPose(Axis.YP.rotationDegrees(f3));
            matrices.scale(0.5F, 0.5F, 0.5F);
            Minecraft.getInstance().getItemRenderer().renderStatic(boardStack, ItemDisplayContext.FIXED, light, overlay, matrices, vertexConsumers, entity.getLevel(), posLong);
            matrices.popPose();
        }
    }
}
