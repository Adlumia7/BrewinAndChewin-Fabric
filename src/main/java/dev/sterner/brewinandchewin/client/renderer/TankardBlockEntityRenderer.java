package dev.sterner.brewinandchewin.client.renderer;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.sterner.brewinandchewin.common.block.TankardBlock;
import dev.sterner.brewinandchewin.common.block.entity.TankardBlockEntity;
import dev.sterner.brewinandchewin.common.registry.BCObjects;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class TankardBlockEntityRenderer implements BlockEntityRenderer<TankardBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    private Map<Item, BlockState> itemBlockStateMap() {
        return Util.make(Maps.newHashMap(), map -> {
            map.put(BCObjects.BEER, BCObjects.BEER_TANKARD_BLOCK.defaultBlockState());
            map.put(BCObjects.BLOODY_MARY, BCObjects.BLOODY_MARY_TANKARD_BLOCK.defaultBlockState());
            map.put(BCObjects.EGG_GROG, BCObjects.EGG_GROG_TANKARD_BLOCK.defaultBlockState());
            map.put(BCObjects.GLITTERING_GRENADINE, BCObjects.GLITTERING_GRENADINE_TANKARD_BLOCK.defaultBlockState());
            map.put(BCObjects.MEAD, BCObjects.MEAD_TANKARD_BLOCK.defaultBlockState());
            map.put(BCObjects.PALE_JANE, BCObjects.PALE_JANE_TANKARD_BLOCK.defaultBlockState());
            map.put(BCObjects.RED_RUM, BCObjects.RED_RUM_TANKARD_BLOCK.defaultBlockState());
            map.put(BCObjects.RICE_WINE, BCObjects.RICE_WINE_TANKARD_BLOCK.defaultBlockState());
            map.put(BCObjects.SACCHARINE_RUM, BCObjects.SACCHARINE_RUM_TANKARD_BLOCK.defaultBlockState());
            map.put(BCObjects.SALTY_FOLLY, BCObjects.SALTY_FOLLY_TANKARD_BLOCK.defaultBlockState());
            map.put(BCObjects.STEEL_TOE_STOUT, BCObjects.STEEL_TOE_STOUT_TANKARD_BLOCK.defaultBlockState());
            map.put(BCObjects.STRONGROOT_ALE, BCObjects.STRONGROT_ALE_TANKARD_BLOCK.defaultBlockState());

            map.put(BCObjects.WITHERING_DROSS, BCObjects.WITHERING_DROSS_TANKARD_BLOCK.defaultBlockState());
            map.put(BCObjects.KOMBUCHA, BCObjects.KOMBUCHA_TANKARD_BLOCK.defaultBlockState());
            map.put(BCObjects.VODKA, BCObjects.VODKA_TANKARD_BLOCK.defaultBlockState());
            map.put(BCObjects.DREAD_NOG, BCObjects.DREAD_NOG_TANKARD_BLOCK.defaultBlockState());
        });
    }

    public TankardBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.blockRenderer = ctx.getBlockRenderDispatcher();
    }

    @Override
    public void render(TankardBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (entity.getLevel() == null) {
            return;
        }
        float[] x = {0};
        float[] z = {0};
        int[] rot = {0};

        int count = (int) entity.getItems().stream().filter(i -> !i.isEmpty()).count();
        /*
        if (count == 2) {
            x = new float[]{2f / 16, -12.5f / 16};
            z = new float[]{4f / 16, -12f / 16};
            rot = new int[]{0, 180};
        } else if (count == 3) {
            x = new float[]{2f / 16, -13.5f / 16, -20f / 16};
            z = new float[]{4f / 16, -3f / 16, -12f / 16};
            rot = new int[]{0, 90, 180};
        }

         */
        if (count == 2) {
            x = new float[]{2f / 16, 7.5f / 16};
            z = new float[]{8.5f / 16, 4f / 16};
            rot = new int[]{0, 180};
        } else if (count == 3) {
            x = new float[]{2f / 16, 6.5f / 16, 9.5f / 16};
            z = new float[]{7f / 16, 9.5f / 16, 4.5f / 16};
            rot = new int[]{0, 90, 180};
        }

        matrices.pushPose();

        for (int i = 0; i < entity.getItems().size(); i++) {
            ItemStack itemStack = entity.getItems().get(i);
            if (itemBlockStateMap().containsKey(itemStack.getItem())) {
                matrices.pushPose();
                matrices.translate(0.5, 0, 0.5);
                matrices.mulPose(Axis.YP.rotationDegrees(rot[i]));
                float rotation = RotationSegment.convertToDegrees(entity.getBlockState().getValue(TankardBlock.ROTATION));
                matrices.mulPose(Axis.YP.rotationDegrees(-rotation));
                matrices.translate(-0.5, 0, -0.5);

                matrices.translate(x[i] / 2, 0, z[i] / 2);
                blockRenderer.renderBatched(itemBlockStateMap().get(itemStack.getItem()), entity.getBlockPos(), entity.getLevel(), matrices, vertexConsumers.getBuffer(RenderType.cutout()), true, entity.getLevel().getRandom());
                matrices.popPose();
            }
        }

        matrices.popPose();
    }
}
