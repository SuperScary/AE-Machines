package ae2m.core.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class FurnaceRecipes {

    public static Iterable<RecipeHolder<SmeltingRecipe>> getRecipes (Level level) {
        return level.getRecipeManager().byType(RecipeType.SMELTING);
    }

    @Nullable
    public static SmeltingRecipe findRecipe (Level level, ItemStack input) {
        for (var holder : getRecipes(level)) {
            var recipe = holder.value();
            if (recipe.getIngredients().get(0).test(input)) {
                return recipe;
            }
        }

        return null;
    }

}
