package com.example.examplemod.core;

import com.example.examplemod.block.BaseBlock;
import com.example.examplemod.core.definitions.ItemDefinition;
import com.example.examplemod.core.registries.ModItems;
import com.example.examplemod.item.BaseBlockItem;
import com.example.examplemod.item.BaseItem;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.ArrayList;
import java.util.List;

public class Tab {

    private static final Multimap<ResourceKey<CreativeModeTab>, ItemDefinition<?>> externalItemDefs = HashMultimap.create();
    private static final List<ItemDefinition<?>> itemDefs = new ArrayList<>();
    public static final ResourceKey<CreativeModeTab> MAIN = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Mod.getResource("main"));

    public static void init (Registry<CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + Mod.MOD_ID))
                .icon(ModItems.EXAMPLE_ITEM::stack)
                .displayItems(Tab::buildDisplayItems)
                .build();
        Registry.register(registry, MAIN, tab);
    }

    public static void initExternal (BuildCreativeModeTabContentsEvent contents) {
        for (var itemDefinition : externalItemDefs.get(contents.getTabKey())) {
            contents.accept(itemDefinition);
        }
    }

    public static void add (ItemDefinition<?> itemDef) {
        itemDefs.add(itemDef);
    }

    public static void addExternal (ResourceKey<CreativeModeTab> tab, ItemDefinition<?> itemDef) {
        externalItemDefs.put(tab, itemDef);
    }

    private static void buildDisplayItems (CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        for (var itemDef : itemDefs) {
            var item = itemDef.asItem();
            if (item instanceof BaseBlockItem baseItem && baseItem.getBlock() instanceof BaseBlock baseBlock) {
                baseBlock.addToCreativeTab(output);
            } else if (item instanceof BaseItem baseItem) {
                baseItem.addToCreativeTab(output);
            } else {
                output.accept(itemDef);
            }
        }
    }

}
