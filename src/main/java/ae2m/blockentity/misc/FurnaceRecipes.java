package ae2m.blockentity.misc;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

public class FurnaceRecipes {

    public static Iterable<RecipeHolder<SmeltingRecipe>> getRecipes (Level level) {
        return level.getRecipeManager().byType(RecipeType.SMELTING);
    }

    public static SmeltingRecipe findRecipes (Level level, ItemStack input) {
        for (var holder : getRecipes(level)) {
            var recipe = holder.value();
            if (recipe.getIngredients().stream().anyMatch(e -> e.test(input))) {
                return recipe;
            }
        }
        return null;
    }

}
