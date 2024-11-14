package com.example.examplemod.datagen.providers.tag;

import com.example.examplemod.core.Mod;
import com.example.examplemod.datagen.IDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagGenerator extends BlockTagsProvider implements IDataProvider {

    public BlockTagGenerator (PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Mod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags (HolderLookup.@NotNull Provider provider) {
        // Add block tags here

    }

}