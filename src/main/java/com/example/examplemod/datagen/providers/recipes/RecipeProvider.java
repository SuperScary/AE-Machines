package com.example.examplemod.datagen.providers.recipes;

import com.example.examplemod.datagen.IDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public abstract class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IDataProvider {

    public RecipeProvider (PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(packOutput, provider);
    }

}