package ae2m.datagen.providers.recipes;

import ae2m.core.AE2M;
import appeng.datagen.providers.recipes.AE2RecipeProvider;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import ae2m.core.registries.AE2MItems;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class InscriberRecipes extends AE2RecipeProvider {

    public InscriberRecipes (PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes (@NotNull RecipeOutput consumer) {
        base(consumer);

        InscriberRecipeBuilder.inscribe(Blocks.IRON_ORE, AE2MItems.STEEL_INGOT, 1)
                .setBottom(Ingredient.of(AE2MItems.INGOT_PRESS))
                .setMode(InscriberProcessType.INSCRIBE)
                .save(consumer, AE2M.getResource("inscriber/steel_ingot_from_iron_ore"));

        InscriberRecipeBuilder.inscribe(Blocks.IRON_BLOCK, AE2MItems.BLANK_PRESS, 1)
                .setTop(Ingredient.of(Items.QUARTZ))
                .setMode(InscriberProcessType.PRESS)
                .save(consumer, AE2M.getResource("inscriber/blank_press"));

        InscriberRecipeBuilder.inscribe(AE2MItems.STEEL_INGOT, AE2MItems.STEEL_PLATE, 1)
                .setTop(Ingredient.of(AE2MItems.BLANK_PRESS))
                .setMode(InscriberProcessType.PRESS)
                .save(consumer, AE2M.getResource("inscriber/steel_plate"));

    }

    protected void base (RecipeOutput consumer) {
        InscriberRecipeBuilder.inscribe(Tags.Items.INGOTS, AE2MItems.INGOT_PRESS, 1)
                .setBottom(Ingredient.of(AE2MItems.BLANK_PRESS))
                .setMode(InscriberProcessType.PRESS)
                .save(consumer, AE2M.getResource("inscriber/ingot_press"));

        InscriberRecipeBuilder.inscribe(Items.NETHER_STAR, AE2MItems.DEEP_STORAGE_PRESS, 1)
                .setBottom(Ingredient.of(Items.NETHER_STAR))
                .setMode(InscriberProcessType.PRESS)
                .save(consumer, AE2M.getResource("inscriber/deep_storage_press"));

    }

    @Override
    public @NotNull String getName () {
        return AE2M.NAME + " Inscriber Recipes";
    }
}
