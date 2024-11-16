package ae2m.datagen.providers.models;

import appeng.core.definitions.BlockDefinition;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import ae2m.block.DecorativeBlock;
import ae2m.core.AE2M;
import ae2m.core.registries.AE2MBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockModelProvider extends BlockStateProvider {

    public BlockModelProvider (PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, AE2M.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels () {
        for (var block : AE2MBlocks.getBlocks()) {
            if (block.block() instanceof DecorativeBlock) blockWithItem(block);
        }

        var furnace = models().getExistingFile(AE2M.getResource("furnace"));
        multiVariantGenerator(AE2MBlocks.FURNACE, Variant.variant().with(VariantProperties.MODEL, furnace.getLocation()))
                .with(createFacingDispatch(0, 0));

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
        simpleBlockItem(blockRegistryObject.block(), new ModelFile.UncheckedModelFile(AE2M.MOD_ID + ":block/" + blockRegistryObject.id().getPath() + appendix));
    }

    private void blockItem (BlockDefinition<?> blockRegistryObject) {
        simpleBlockItem(blockRegistryObject.block(), new ModelFile.UncheckedModelFile(AE2M.MOD_ID + ":block/" + blockRegistryObject.id().getPath()));
    }

    private void blockWithItem (BlockDefinition<?> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.block(), cubeAll(blockRegistryObject.block()));
    }

}