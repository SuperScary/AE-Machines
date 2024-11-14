package com.example.examplemod.datagen.providers.models;

import com.example.examplemod.core.Mod;
import com.example.examplemod.core.definitions.BlockDefinition;
import com.example.examplemod.core.registries.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockModelProvider extends BlockStateProvider {

    public BlockModelProvider (PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Mod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels () {
        blockWithItem(ModBlocks.EXAMPLE_BLOCK);
    }

    private void leavesBlock (BlockDefinition<Block> blockRegistryObject) {
        simpleBlock(blockRegistryObject.block(),
                models().cubeAll(blockRegistryObject.id().getPath(), blockTexture(blockRegistryObject.block())).renderType("cutout"));
    }

    private void saplingBlock (BlockDefinition<Block> blockRegistryObject) {
        simpleBlock(blockRegistryObject.block(),
                models().cross(blockRegistryObject.id().getPath(), blockTexture(blockRegistryObject.block())).renderType("cutout"));
    }

    private void blockItem (BlockDefinition<Block> blockRegistryObject, String appendix) {
        simpleBlockItem(blockRegistryObject.block(), new ModelFile.UncheckedModelFile(Mod.MOD_ID + ":block/" + blockRegistryObject.id().getPath() + appendix));
    }

    private void blockItem (BlockDefinition<?> blockRegistryObject) {
        simpleBlockItem(blockRegistryObject.block(), new ModelFile.UncheckedModelFile(Mod.MOD_ID + ":block/" + blockRegistryObject.id().getPath()));
    }

    private void blockWithItem (BlockDefinition<?> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.block(), cubeAll(blockRegistryObject.block()));
    }

}