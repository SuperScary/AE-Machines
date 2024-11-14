package com.example.examplemod.core.registries;

import com.example.examplemod.block.BaseBlock;
import com.example.examplemod.block.DecorativeBlock;
import com.example.examplemod.core.Mod;
import com.example.examplemod.core.Tab;
import com.example.examplemod.core.definitions.BlockDefinition;
import com.example.examplemod.core.definitions.ItemDefinition;
import com.example.examplemod.item.BaseBlockItem;
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

public class ModBlocks {

    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(Mod.MOD_ID);

    public static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    // REGISTER BLOCKS HERE
    public static final BlockDefinition<DecorativeBlock> EXAMPLE_BLOCK = reg("example_block", DecorativeBlock::new);

    public static List<BlockDefinition<?>> getBlocks () {
        return Collections.unmodifiableList(BLOCKS);
    }

    public static <T extends Block> BlockDefinition<T> reg (final String name, final Supplier<T> supplier) {
        return reg(name, Mod.getResource(name), supplier, null, true);
    }

    public static <T extends Block> BlockDefinition<T> reg (final String name, ResourceLocation id, final Supplier<T> supplier, boolean addToTab) {
        return reg(name, id, supplier, null, addToTab);
    }

    public static <T extends Block> BlockDefinition<T> reg (final String name, ResourceLocation id, final Supplier<T> supplier, @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory, boolean addToTab) {
        var deferredBlock = REGISTRY.register(id.getPath(), supplier);
        var deferredItem = ModItems.REGISTRY.register(id.getPath(), () -> {
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
