package dev.sterner.brewinandchewin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.FermentationControllerBlock;
import dev.sterner.brewinandchewin.common.block.entity.FermentationControllerBlockEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;


public class FermentationControllerBlockEntityRenderer implements BlockEntityRenderer<FermentationControllerBlockEntity> {

    private final Font textRenderer;
    private final Indicator MODEL;
    private final Indicator MODEL_SMALL;
    private final ResourceLocation TEXTURE = new ResourceLocation(BrewinAndChewin.MODID, "textures/block/indicator.png");
    private final int MAGIC_OFFSET_NUMBER = 56;

    public FermentationControllerBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.textRenderer = ctx.getFont();
        this.MODEL = new Indicator(ctx.bakeLayer(Indicator.LAYER));
        this.MODEL_SMALL = new Indicator(ctx.bakeLayer(Indicator.LAYER_SMALL));
    }

    @Override
    public void render(FermentationControllerBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (BrewinAndChewin.DEBUG_MODE) {
            renderDebugText(entity, matrices, vertexConsumers, light);
        }

        float xOffset = entity.getTemperature();
        float xOffsetTarget = entity.getTargetTemperature();

        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(HorizontalDirectionalBlock.FACING).toYRot()));


        if (!entity.getBlockState().getValue(FermentationControllerBlock.VERTICAL)) {
            matrices.pushPose();

            matrices.mulPose(Axis.ZP.rotationDegrees(180));
            matrices.mulPose(Axis.XP.rotationDegrees(90));

            renderIndicator(matrices, vertexConsumers, xOffset, xOffsetTarget, overlay);
            matrices.popPose();
        } else {
            matrices.pushPose();
            renderIndicator(matrices, vertexConsumers, xOffset, xOffsetTarget, overlay);
            matrices.popPose();
        }

        matrices.popPose();
    }

    private void renderIndicator(PoseStack matrices, MultiBufferSource vertexConsumers, float xOffset, float xOffsetTarget, int overlay) {
        matrices.translate(xOffset / MAGIC_OFFSET_NUMBER, -((double) (7 + 16) / 16), 0.501);
        MODEL.renderToBuffer(matrices, vertexConsumers.getBuffer(RenderType.entitySolid(TEXTURE)), 15728880, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.translate((xOffsetTarget / MAGIC_OFFSET_NUMBER) - xOffset / MAGIC_OFFSET_NUMBER, -((double) 5 / 16), 0);
        MODEL_SMALL.renderToBuffer(matrices, vertexConsumers.getBuffer(RenderType.entitySolid(TEXTURE)), 15728880, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderDebugText(FermentationControllerBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();
        setTextAngles(matrices, new Vec3(0, 2, 0));
        matrices.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(HorizontalDirectionalBlock.FACING).toYRot()));

        var orderedText = Component.translatable(entity.getTargetTemperature() + " : " + entity.getTemperature());
        float f = (float) (-this.textRenderer.width(orderedText) / 2);

        this.textRenderer.drawInBatch(orderedText, f, 1, 0, false, matrices.last().pose(), vertexConsumers, Font.DisplayMode.POLYGON_OFFSET, 0, light);
        matrices.popPose();
    }

    private void setTextAngles(PoseStack matrices, Vec3 translation) {
        float f = 0.05F;
        matrices.translate(translation.x, translation.y, translation.z);
        matrices.scale(f, -f, f);
    }

    public static class Indicator extends EntityModel<Entity> {
        public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(BrewinAndChewin.MODID, "indicator"), "main");
        public static final ModelLayerLocation LAYER_SMALL = new ModelLayerLocation(new ResourceLocation(BrewinAndChewin.MODID, "indicator_small"), "main");
        private final ModelPart main;

        public Indicator(ModelPart root) {
            this.main = root.getChild("main");
        }

        public static LayerDefinition getTexturedModelData() {
            MeshDefinition modelData = new MeshDefinition();
            PartDefinition modelPartData = modelData.getRoot();
            modelPartData.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-0.4F, -2.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
            return LayerDefinition.create(modelData, 16, 16);
        }

        public static LayerDefinition getTexturedModelDataSmall() {
            MeshDefinition modelData = new MeshDefinition();
            PartDefinition modelPartData = modelData.getRoot();
            modelPartData.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-0.4F, -2.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
            return LayerDefinition.create(modelData, 16, 16);
        }

        @Override
        public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        }

        @Override
        public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        }
    }
}
