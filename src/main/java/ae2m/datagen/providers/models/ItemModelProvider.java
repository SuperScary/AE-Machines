package ae2m.datagen.providers.models;

import ae2m.core.AE2M;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import ae2m.core.registries.AE2MItems;
import ae2m.datagen.IDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider implements IDataProvider {

    public ItemModelProvider (PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, AE2M.MOD_ID, existingFileHelper);
    }

    private static ResourceLocation makeId (String id) {
        return id.contains(":") ? ResourceLocation.parse(id) : AE2M.getResource(id);
    }

    @Override
    protected void registerModels () {
        for (var item : AE2MItems.getItems()) {
            basicItem(item.asItem());
        }
    }

    private ItemModelBuilder blockOff (BlockDefinition<?> block) {
        return withExistingParent(block.id().getPath(), AE2M.getResource("block/" + block.id().getPath() + "/" + block.id().getPath() + "_off"));
    }

    private ItemModelBuilder flatSingleLayer (ItemDefinition<?> item, String texture) {
        String id = item.id().getPath();
        return singleTexture(id, mcLoc("item/generated"), "layer0", makeId(texture));
    }

    private ItemModelBuilder flatSingleLayer (ResourceLocation id, String texture) {
        return singleTexture(id.getPath(), mcLoc("item/generated"), "layer0", makeId(texture));
    }

    private ItemModelBuilder builtInItemModel (String name) {
        var model = getBuilder("item/" + name);
        return model;
    }

}
