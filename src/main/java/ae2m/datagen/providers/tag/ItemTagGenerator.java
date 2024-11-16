package ae2m.datagen.providers.tag;

import net.neoforged.neoforge.common.Tags;
import ae2m.core.AE2M;
import ae2m.core.registries.AE2MItems;
import ae2m.datagen.IDataProvider;
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
        super(packOutput, future, completableFuture, AE2M.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags (HolderLookup.@NotNull Provider provider) {
        // Add item tags here
        this.tag(Tags.Items.INGOTS)
                .add(AE2MItems.STEEL_INGOT.asItem());

        this.tag(ae2m.core.definitions.Tags.STEEL_INGOTS)
                .add(AE2MItems.STEEL_INGOT.asItem());
    }

    @Override
    public @NotNull String getName () {
        return "Mod ItemTags";
    }

}