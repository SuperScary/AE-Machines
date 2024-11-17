package ae2m.client.renderer.blockentity;

import ae2m.blockentity.machine.FurnaceBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;

public class FurnaceBER implements BlockEntityRenderer<FurnaceBlockEntity> {

    public FurnaceBER (BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    public void render (FurnaceBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        var renderer = Minecraft.getInstance().getItemRenderer();
        var itemStack = entity.getInternalInventory().getStackInSlot(0);
        var state = entity.getBlockState();

        if (!itemStack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.53, 0.5);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(Axis.YN.rotationDegrees(entity.getFront().toYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(270));

            renderer.renderStatic(itemStack, ItemDisplayContext.FIXED, getLightLevel(entity.getLevel(), entity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.getLevel(), 1);
            poseStack.popPose();

            if (!state.getValue(BlockStateProperties.WATERLOGGED) && entity.isActive()) {
                renderFireBlock(poseStack, bufferSource, light, overlay);
            }
        }

    }

    private void renderFireBlock (PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        var renderer = Minecraft.getInstance().getBlockRenderer();
        var fire = Blocks.FIRE.defaultBlockState();

        poseStack.pushPose();
        poseStack.translate(.05f, .1f, .05f);
        poseStack.scale(0.85f, 0.2f, 0.85f);
        renderer.renderSingleBlock(fire, poseStack, bufferSource, light, overlay, ModelData.EMPTY, RenderType.CUTOUT);
        poseStack.popPose();
    }

    private int getLightLevel (Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }

}
