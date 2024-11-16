package ae2m.datagen.providers.recipes;

import ae2m.datagen.IDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public abstract class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IDataProvider {

    public RecipeProvider (PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(packOutput, provider);
    }

}