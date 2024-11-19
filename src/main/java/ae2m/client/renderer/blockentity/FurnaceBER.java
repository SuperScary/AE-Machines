package ae2m.client.renderer.blockentity;

import ae2m.blockentity.machine.FurnaceBlockEntity;
import ae2m.client.renderer.BER;
import appeng.api.orientation.BlockOrientation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class FurnaceBER extends BER<FurnaceBlockEntity> {

    public FurnaceBER (BlockEntityRendererProvider.Context context) {
        super(context);
        setRotation(0f);
    }

    @Override
    public void render (@NotNull FurnaceBlockEntity entity, float partialTick, @NotNull PoseStack ms, @NotNull MultiBufferSource bufferSource, int light, int overlay) {
        ms.pushPose();
        ms.translate(0.5F, 0.5F, 0.5F);
        BlockOrientation orientation = BlockOrientation.get(entity);
        ms.mulPose(orientation.getQuaternion());
        ms.translate(-0.5F, -0.5F, -0.5F);
        ms.popPose();

        var itemStack = entity.getInternalInventory().getStackInSlot(0);
        var state = entity.getBlockState();

        if (!itemStack.isEmpty()) {
            renderItem(ms, itemStack, 0.f, bufferSource, light, entity.getLevel());

            if (!state.getValue(BlockStateProperties.WATERLOGGED) && entity.isActive()) {
                renderBlock(ms, bufferSource, light, overlay);
            }
        }

        incrementOrResetRot(0.08f);

    }

    @Override
    public void renderItem (PoseStack ms, ItemStack stack, float o, MultiBufferSource buffers, int combinedLight, Level level) {
        if (!stack.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5, 0.5 + o, 0.5);
            ms.mulPose(new Quaternionf().rotationY(Mth.DEG_TO_RAD * getRotation()));

            var model = getItemRenderer().getItemModelShaper().getItemModel(stack);
            var quads = model.getQuads(null, null, RandomSource.create(), ModelData.EMPTY, null);
            if (!quads.isEmpty()) {
                if (!(stack.getItem() instanceof BlockItem)) {
                    ms.scale(0.75f, 0.75f, 0.75f);
                    ms.translate(0, 0.1, 0);
                } else {
                    ms.scale(0.5f, 0.5f, 0.5f);
                    ms.translate(0, 0, 0);
                }
            }

            RenderSystem.applyModelViewMatrix();
            getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, combinedLight, OverlayTexture.NO_OVERLAY, ms, buffers, level, 0);
            ms.popPose();
        }
    }

    @Override
    public void renderBlock (PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        var fire = Blocks.FIRE.defaultBlockState();

        poseStack.pushPose();
        poseStack.translate(.15f, .1f, .15f);
        poseStack.scale(0.75f, 0.2f, 0.75f);
        getBlockRenderer().renderSingleBlock(fire, poseStack, bufferSource, light, overlay, ModelData.EMPTY, RenderType.CUTOUT);
        poseStack.popPose();
    }

}
