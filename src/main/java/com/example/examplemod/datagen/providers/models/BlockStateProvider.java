package com.example.examplemod.datagen.providers.models;

import com.example.examplemod.core.Mod;
import com.example.examplemod.core.definitions.BlockDefinition;
import com.example.examplemod.datagen.IDataProvider;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public abstract class BlockStateProvider extends net.neoforged.neoforge.client.model.generators.BlockStateProvider implements IDataProvider {

    private static final VariantProperty<VariantProperties.Rotation> Z_ROT = new VariantProperty<>(Mod.MOD_ID + ":z", r -> new JsonPrimitive(r.ordinal() * 90));

    public BlockStateProvider (PackOutput packOutput, String modid, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, existingFileHelper);
    }

    protected static PropertyDispatch createFacingDispatch (int baseRotX, int baseRotY) {
        return PropertyDispatch.property(BlockStateProperties.FACING)
                .select(Direction.DOWN, applyRotation(Variant.variant(), baseRotX + 90, baseRotY, 0))
                .select(Direction.UP, applyRotation(Variant.variant(), baseRotX + 270, baseRotY, 0))
                .select(Direction.NORTH, applyRotation(Variant.variant(), baseRotX, baseRotY, 0))
                .select(Direction.SOUTH, applyRotation(Variant.variant(), baseRotX, baseRotY + 180, 0))
                .select(Direction.WEST, applyRotation(Variant.variant(), baseRotX, baseRotY + 270, 0))
                .select(Direction.EAST, applyRotation(Variant.variant(), baseRotX, baseRotY + 90, 0));
    }

    protected static Variant applyRotation (Variant variant, int angleX, int angleY, int angleZ) {
        angleX = normalizeAngle(angleX);
        angleY = normalizeAngle(angleY);
        angleZ = normalizeAngle(angleZ);

        if (angleX != 0) {
            variant = variant.with(VariantProperties.X_ROT, rotationByAngle(angleX));
        }
        if (angleY != 0) {
            variant = variant.with(VariantProperties.Y_ROT, rotationByAngle(angleY));
        }
        if (angleZ != 0) {
            variant = variant.with(Z_ROT, rotationByAngle(angleZ));
        }
        return variant;
    }

    private static int normalizeAngle (int angle) {
        return angle - (angle / 360) * 360;
    }

    private static VariantProperties.Rotation rotationByAngle (int angle) {
        return switch (angle) {
            case 0 -> VariantProperties.Rotation.R0;
            case 90 -> VariantProperties.Rotation.R90;
            case 180 -> VariantProperties.Rotation.R180;
            case 270 -> VariantProperties.Rotation.R270;
            default -> throw new IllegalArgumentException("Invalid angle: " + angle);
        };
    }

    private static <T extends Comparable<T>> Condition addConditionTerm (Condition.TerminalCondition condition,
                                                                         BlockState blockState,
                                                                         Property<T> property) {
        return condition.term(property, blockState.getValue(property));
    }

    protected void simpleBlockAndItem (BlockDefinition<?> block) {
        var model = cubeAll(block.block());
        simpleBlock(block.block(), model);
        simpleBlockItem(block.block(), model);
    }

    protected void simpleBlockAndItem (BlockDefinition<?> block, ModelFile model) {
        simpleBlock(block.block(), model);
        simpleBlockItem(block.block(), model);
    }

    protected void simpleBlockAndItem (BlockDefinition<?> block, String textureName) {
        var model = models().cubeAll(block.id().getPath(), Mod.getResource(textureName));
        simpleBlock(block.block(), model);
        simpleBlockItem(block.block(), model);
    }

    protected void wall (BlockDefinition<WallBlock> block, String texture) {
        wallBlock(block.block(), Mod.getResource(texture));
        itemModels().wallInventory(block.id().getPath(), Mod.getResource(texture));
    }

    protected void slabBlock (BlockDefinition<SlabBlock> slab, BlockDefinition<?> base) {
        var texture = blockTexture(base.block()).getPath();
        slabBlock(slab, base, texture, texture, texture);
    }

    protected void slabBlock (BlockDefinition<SlabBlock> slab, BlockDefinition<?> base, String bottomTexture,
                              String sideTexture, String topTexture) {
        var side = Mod.getResource(sideTexture);
        var bottom = Mod.getResource(bottomTexture);
        var top = Mod.getResource(topTexture);

        var bottomModel = models().slab(slab.id().getPath(), side, bottom, top);
        simpleBlockItem(slab.block(), bottomModel);
        slabBlock(
                slab.block(),
                bottomModel,
                models().slabTop(slab.id().getPath() + "_top", side, bottom, top),
                models().getExistingFile(base.id()));
    }

    protected void stairsBlock (BlockDefinition<StairBlock> stairs, BlockDefinition<?> base) {
        var texture = "block/" + base.id().getPath();

        stairsBlock(stairs, texture, texture, texture);
    }

    protected void stairsBlock (BlockDefinition<StairBlock> stairs, String bottomTexture, String sideTexture,
                                String topTexture) {
        var baseName = stairs.id().getPath();

        var side = Mod.getResource(sideTexture);
        var bottom = Mod.getResource(bottomTexture);
        var top = Mod.getResource(topTexture);

        ModelFile stairsModel = models().stairs(baseName, side, bottom, top);
        ModelFile stairsInner = models().stairsInner(baseName + "_inner", side, bottom, top);
        ModelFile stairsOuter = models().stairsOuter(baseName + "_outer", side, bottom, top);
        stairsBlock(stairs.block(), stairsModel, stairsInner, stairsOuter);
        simpleBlockItem(stairs.block(), stairsModel);
    }

    protected final MultiVariantGenerator multiVariantGenerator (BlockDefinition<?> blockDef, Variant... variants) {
        if (variants.length == 0) {
            variants = new Variant[]{Variant.variant()};
        }
        var builder = MultiVariantGenerator.multiVariant(blockDef.block(), variants);
        registeredBlocks.put(blockDef.block(), () -> builder.get().getAsJsonObject());
        return builder;
    }

    protected final MultiPartGenerator multiPartGenerator (BlockDefinition<?> blockDef) {
        var multipart = MultiPartGenerator.multiPart(blockDef.block());
        registeredBlocks.put(blockDef.block(), () -> multipart.get().getAsJsonObject());
        return multipart;
    }

    @Override
    public @NotNull String getName () {
        return super.getName() + " " + getClass().getName();
    }

}
