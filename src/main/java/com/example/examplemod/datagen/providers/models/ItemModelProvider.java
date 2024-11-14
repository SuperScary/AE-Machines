package com.example.examplemod.datagen.providers.models;

import com.example.examplemod.core.Mod;
import com.example.examplemod.core.definitions.BlockDefinition;
import com.example.examplemod.core.definitions.ItemDefinition;
import com.example.examplemod.core.registries.ModItems;
import com.example.examplemod.datagen.IDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider implements IDataProvider {

    public ItemModelProvider (PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, Mod.MOD_ID, existingFileHelper);
    }

    private static ResourceLocation makeId (String id) {
        return id.contains(":") ? ResourceLocation.parse(id) : Mod.getResource(id);
    }

    @Override
    protected void registerModels () {
        // Register item models here
        basicItem(ModItems.EXAMPLE_ITEM.asItem());
    }

    private ItemModelBuilder blockOff (BlockDefinition<?> block) {
        return withExistingParent(block.id().getPath(), Mod.getResource("block/" + block.id().getPath() + "/" + block.id().getPath() + "_off"));
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
