package com.example.examplemod.datagen.providers.tag;

import com.example.examplemod.core.Mod;
import com.example.examplemod.datagen.IDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator extends ItemTagsProvider implements IDataProvider {

    public ItemTagGenerator (PackOutput packOutput, CompletableFuture<HolderLookup.Provider> future, CompletableFuture<TagLookup<Block>> completableFuture, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, future, completableFuture, Mod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags (HolderLookup.@NotNull Provider provider) {
        // Add item tags here
    }

    @Override
    public @NotNull String getName () {
        return "Mod ItemTags";
    }

}