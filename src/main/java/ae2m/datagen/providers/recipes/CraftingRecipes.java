package ae2m.datagen.providers.recipes;

import ae2m.core.AE2M;
import appeng.api.util.AEColor;
import appeng.core.definitions.*;
import appeng.datagen.providers.tags.ConventionTags;
import appeng.items.storage.StorageTier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import ae2m.core.definitions.Tags;
import ae2m.core.registries.AE2MBlocks;
import ae2m.core.registries.AE2MItems;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class CraftingRecipes extends RecipeProvider {

    public CraftingRecipes (PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(packOutput, provider);
    }

    @Override
    public @NotNull String getName () {
        return AE2M.NAME + " Crafting Recipes";
    }

    @Override
    protected void buildRecipes (@NotNull RecipeOutput consumer) {
        component(consumer, AE2MItems.TIER_1M, StorageTier.SIZE_256K, AEItems.SKY_DUST.asItem(), null);
        component(consumer, AE2MItems.TIER_4M, AE2MItems.TIER_1M, AE2MItems.STEEL_PLATE, null);
        component(consumer, AE2MItems.TIER_16M, AE2MItems.TIER_4M, AE2MItems.STEEL_PLATE, null);
        component(consumer, AE2MItems.TIER_64M, AE2MItems.TIER_16M, AE2MItems.STEEL_PLATE, null);
        component(consumer, AE2MItems.TIER_256M, AE2MItems.TIER_64M, AE2MItems.STEEL_PLATE, null);
        component(consumer, AE2MItems.TIER_1B, AE2MItems.TIER_256M, AE2MItems.STEEL_PLATE, null);

        housing(consumer, AE2MItems.STEEL_ITEM_HOUSING, Tags.STEEL_INGOTS);

        cell(consumer, AE2MItems.ITEM_CELL_1M, AE2MItems.CELL_COMPONENT_1M, AE2MItems.STEEL_ITEM_HOUSING, Tags.STEEL_INGOTS);
        cell(consumer, AE2MItems.ITEM_CELL_4M, AE2MItems.CELL_COMPONENT_4M, AE2MItems.STEEL_ITEM_HOUSING, Tags.STEEL_INGOTS);
        cell(consumer, AE2MItems.ITEM_CELL_16M, AE2MItems.CELL_COMPONENT_16M, AE2MItems.STEEL_ITEM_HOUSING, Tags.STEEL_INGOTS);
        cell(consumer, AE2MItems.ITEM_CELL_64M, AE2MItems.CELL_COMPONENT_64M, AE2MItems.STEEL_ITEM_HOUSING, Tags.STEEL_INGOTS);
        cell(consumer, AE2MItems.ITEM_CELL_256M, AE2MItems.CELL_COMPONENT_256M, AE2MItems.STEEL_ITEM_HOUSING, Tags.STEEL_INGOTS);
        cell(consumer, AE2MItems.ITEM_CELL_1B, AE2MItems.CELL_COMPONENT_1B, AE2MItems.STEEL_ITEM_HOUSING, Tags.STEEL_INGOTS);

        metallicBlock(consumer, AE2MItems.STEEL_INGOT, AE2MBlocks.STEEL_BLOCK);

        machines(consumer);
    }

    private void machines (RecipeOutput consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AE2MBlocks.FURNACE)
                .pattern("IRI")
                .pattern("MFE")
                .pattern("III")
                .define('I', ConventionTags.IRON_INGOT)
                .define('E', AEParts.EXPORT_BUS)
                .define('M', AEParts.IMPORT_BUS)
                .define('F', Blocks.FURNACE)
                .define('R', ConventionTags.GLASS_CABLE)
                .unlockedBy("has_furnace", has(Blocks.FURNACE))
                .save(consumer, AE2M.getResource("machines/furnace"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AE2MBlocks.BLAST_FURNACE)
                .pattern("IRI")
                .pattern("MFE")
                .pattern("III")
                .define('I', ConventionTags.IRON_INGOT)
                .define('E', AEParts.EXPORT_BUS)
                .define('M', AEParts.IMPORT_BUS)
                .define('F', Blocks.BLAST_FURNACE)
                .define('R', ConventionTags.GLASS_CABLE)
                .unlockedBy("has_furnace", has(Blocks.FURNACE))
                .save(consumer, AE2M.getResource("machines/blasting"));
    }

    private void metallicBlock (RecipeOutput consumer, ItemDefinition<?> item, BlockDefinition<?> output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', item)
                .unlockedBy("has_" + item.id().getPath(), has(item))
                .save(consumer, AE2M.getResource("blocks/" + output.id().getPath()));
        deconstructMetallicBlock(consumer, output, item);
    }

    private void deconstructMetallicBlock (RecipeOutput consumer, BlockDefinition<?> input, ItemDefinition<?> output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, output)
                .requires(input)
                .unlockedBy("has_" + input.id().getPath(), has(input))
                .save(consumer, "blocks/deconstruct/" + output.id().getPath() + "_from_" + input.id().getPath());
    }

    private void component (RecipeOutput output, StorageTier tier, StorageTier preceding, ItemLike binderItem, TagKey<Item> binderTag) {
        var precedingComponent = preceding.componentSupplier().get();
        var recipe = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.componentSupplier().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aca");

        if (binderItem != null) {
            recipe.define('a', binderItem);
        } else if (binderTag != null) {
            recipe.define('a', binderTag);
        }

        recipe.define('b', AE2MItems.DEEP_STORAGE_PROCESSOR)
                .define('c', precedingComponent)
                .define('d', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .unlockedBy("has_accumulation_processor", has(AE2MItems.DEEP_STORAGE_PROCESSOR))
                .unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(precedingComponent).getPath(), has(precedingComponent))
                .save(output, AE2M.getResource("cells/" + BuiltInRegistries.ITEM.getKey(tier.componentSupplier().get()).getPath()));
    }

    private void housing (RecipeOutput consumer, ItemDefinition<?> housing, TagKey<Item> housingMaterial) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, housing)
                .pattern("aba")
                .pattern("b b")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('d', housingMaterial)
                .unlockedBy("has_dusts/sky_stone", has(AEItems.SKY_DUST))
                .save(consumer, AE2M.getResource("cells/" + housing.id().getPath()));
    }

    private void cell (RecipeOutput consumer, ItemDefinition<?> cell, ItemDefinition<?> component, ItemDefinition<?> housing, TagKey<Item> housingMaterial) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, cell)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ddd")
                .define('a', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('b', AEItems.SKY_DUST)
                .define('c', component)
                .define('d', housingMaterial)
                .unlockedBy("has_" + component.id().getPath(), has(component))
                .save(consumer, AE2M.getResource("cells/standard/" + cell.id().getPath()));
        cell(consumer, cell, component, housing);
    }

    private void cell (RecipeOutput consumer, ItemDefinition<?> cell, ItemDefinition<?> component, ItemDefinition<?> housing) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                .requires(housing)
                .requires(component)
                .unlockedBy("has_" + component.id().getPath(), has(component))
                .unlockedBy("has_" + housing.id().getPath(), has(housing))
                .save(consumer, AE2M.getResource("cells/standard/" + cell.id().getPath() + "_with_housing"));
    }

}