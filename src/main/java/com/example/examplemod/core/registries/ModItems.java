package com.example.examplemod.core.registries;

import com.example.examplemod.core.Mod;
import com.example.examplemod.core.Tab;
import com.example.examplemod.core.definitions.ItemDefinition;
import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ModItems {

    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(Mod.MOD_ID);

    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    // REGISTER ITEMS HERE
    public static final ItemDefinition<Item> EXAMPLE_ITEM = item("example_item", Item::new);

    public static List<ItemDefinition<?>> getItems () {
        return Collections.unmodifiableList(ITEMS);
    }

    static <T extends Item> ItemDefinition<T> item (String name, Function<Item.Properties, T> factory) {
        return item(name, Mod.getResource(name), factory, Tab.MAIN);
    }

    static <T extends Item> ItemDefinition<T> item (String name, ResourceLocation id, Function<Item.Properties, T> factory) {
        return item(name, id, factory, Tab.MAIN);
    }

    static <T extends Item> ItemDefinition<T> item (String name, ResourceLocation id, Function<Item.Properties, T> factory, @Nullable ResourceKey<CreativeModeTab> group) {
        Preconditions.checkArgument(id.getNamespace().equals(Mod.MOD_ID), "Can only register items in " + Mod.MOD_ID + " namespace");
        var definition = new ItemDefinition<>(name, REGISTRY.registerItem(id.getPath(), factory));

        if (Objects.equals(group, Tab.MAIN)) {
            Tab.add(definition);
        } else if (group != null) {
            Tab.addExternal(group, definition);
        }

        ITEMS.add(definition);
        return definition;
    }

}
