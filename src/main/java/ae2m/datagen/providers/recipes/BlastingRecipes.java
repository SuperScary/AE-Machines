package ae2m.datagen.providers.recipes;

import ae2m.core.AE2M;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BlastingRecipes extends RecipeProvider {

    public BlastingRecipes (PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(packOutput, provider);
    }

    @Override
    public @NotNull String getName () {
        return AE2M.NAME + "Blasting Recipes";
    }

    @Override
    public void buildRecipes (@NotNull RecipeOutput consumer) {

    }

}