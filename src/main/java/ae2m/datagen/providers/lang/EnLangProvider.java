package ae2m.datagen.providers.lang;

import ae2m.core.AE2M;
import ae2m.core.registries.AE2MBlocks;
import ae2m.core.registries.AE2MItems;
import ae2m.datagen.IDataProvider;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EnLangProvider extends LanguageProvider implements IDataProvider {

    public EnLangProvider (DataGenerator generator) {
        super(generator.getPackOutput(), AE2M.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations () {
        add("itemGroup." + AE2M.MOD_ID, AE2M.NAME);
        types();
        items();
        blocks();
    }

    protected void types () {
        for (var type : AE2MItems.CellBuilder.Type.values()) {
            add(type.getTranslationKey(), type.getTranslation());
        }
    }

    protected void items () {
        for (var item : AE2MItems.getItems()) {
            add(item.asItem(), item.getEnglishName());
        }
    }

    protected void blocks () {
        for (var block : AE2MBlocks.getBlocks()) {
            add(block.block(), block.getEnglishName());
        }
    }

}