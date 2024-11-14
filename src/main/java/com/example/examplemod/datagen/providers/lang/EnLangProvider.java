package com.example.examplemod.datagen.providers.lang;

import com.example.examplemod.core.Mod;
import com.example.examplemod.core.registries.ModBlocks;
import com.example.examplemod.core.registries.ModItems;
import com.example.examplemod.datagen.IDataProvider;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EnLangProvider extends LanguageProvider implements IDataProvider {

    public EnLangProvider (DataGenerator generator) {
        super(generator.getPackOutput(), Mod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations () {
        add("itemGroup." + Mod.MOD_ID, "Example Mod");
        add(ModItems.EXAMPLE_ITEM.asItem(), "Example Item");
        add(ModBlocks.EXAMPLE_BLOCK.block(), "Example Block");
    }

}