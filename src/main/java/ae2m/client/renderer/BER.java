package ae2m.client.renderer;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Wrapper class for BlockEntityRenderer. Provides additional utility methods and fields.
 *
 * @param <T> The BlockEntity type to render.
 */
public abstract class BER<T extends BlockEntity> implements BlockEntityRenderer<T> {

    private final BlockEntityRendererProvider.Context context;
    private final BlockRenderDispatcher blockRenderer;
    private final ItemRenderer itemRenderer;
    private float rotation = -1;

    public BER (BlockEntityRendererProvider.Context context) {
        super();
        this.context = context;
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();

        if (getContext() == null) {
            throw new IllegalStateException("Context is null. This is likely due to the renderer being registered before the context is available.");
        }
    }

    public void renderItem (PoseStack ms, ItemStack stack, float offset, MultiBufferSource buffers, int combinedLight, Level level) {
    }

    public void renderBlock (PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
    }

    public BlockRenderDispatcher getBlockRenderer () {
        return blockRenderer;
    }

    public ItemRenderer getItemRenderer () {
        return itemRenderer;
    }

    public BlockEntityRendererProvider.Context getContext () {
        return context;
    }

    public void setRotation (float rotation) {
        Preconditions.checkArgument(rotation >= 0, "Rotation must be greater than or equal to 0");
        this.rotation = rotation;
    }

    public void resetRotation () {
        setRotation(0);
    }

    public void incrementRotation (float increment) {
        Preconditions.checkArgument(increment >= 0, "Increment must be greater than or equal to 0");
        rotation += increment;
    }

    public void incrementOrResetRot (float increment) {
        Preconditions.checkArgument(increment > 0, "Increment must be greater than or equal to 0");
        if (rotation >= 360) {
            resetRotation();
        } else {
            incrementRotation(increment);
        }
    }

    public float getRotation () {
        Preconditions.checkArgument(rotation >= 0, "Rotation not set. You must call setRotation() before calling getRotation()");
        return rotation;
    }

}
