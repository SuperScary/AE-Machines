package ae2m.core.registries;

import ae2m.core.AE2M;
import appeng.block.AEBaseBlock;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import ae2m.block.BaseBlock;
import ae2m.block.DecorativeBlock;
import ae2m.block.machine.FurnaceBlock;
import ae2m.core.Tab;
import ae2m.item.BaseBlockItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class AE2MBlocks {

    public static final DeferredRegister.Blocks DR = DeferredRegister.createBlocks(AE2M.MOD_ID);

    public static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    // REGISTER BLOCKS HERE
    public static final BlockDefinition<DecorativeBlock> STEEL_BLOCK = reg("Steel Block", DecorativeBlock::new);

    /**
     * MACHINES
     */
    public static final BlockDefinition<FurnaceBlock> FURNACE = reg("Furnace", () -> new FurnaceBlock(AEBaseBlock.metalProps().strength(6.f).noOcclusion()));

    public static List<BlockDefinition<?>> getBlocks () {
        return Collections.unmodifiableList(BLOCKS);
    }

    public static <T extends Block> BlockDefinition<T> reg (final String name, final Supplier<T> supplier) {
        return reg(name, AE2M.getResource(name.toLowerCase().replaceAll("\\s+", "_")), supplier, null, true);
    }

    public static <T extends Block> BlockDefinition<T> reg (final String name, ResourceLocation id, final Supplier<T> supplier, boolean addToTab) {
        return reg(name, id, supplier, null, addToTab);
    }

    public static <T extends Block> BlockDefinition<T> reg (final String name, ResourceLocation id, final Supplier<T> supplier, @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory, boolean addToTab) {
        var deferredBlock = DR.register(id.getPath(), supplier);
        var deferredItem = AE2MItems.DR.register(id.getPath(), () -> {
            var block = deferredBlock.get();
            var itemProperties = new Item.Properties();
            if (itemFactory != null) {
                var item = itemFactory.apply(block, itemProperties);
                if (item == null) {
                    throw new IllegalArgumentException("BlockItem factory for " + id + " returned null.");
                }
                return item;
            } else if (block instanceof BaseBlock) {
                return new BaseBlockItem(block, itemProperties);
            } else {
                return new BlockItem(block, itemProperties);
            }
        });
        var itemDef = new ItemDefinition<>(name, deferredItem);
        Tab.add(itemDef);
        BlockDefinition<T> definition = new BlockDefinition<>(name, deferredBlock, itemDef);
        BLOCKS.add(definition);
        return definition;
    }

}
