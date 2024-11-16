package ae2m.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class BaseItem extends Item {

    public BaseItem () {
        this(new Item.Properties());
    }

    public BaseItem (Item.Properties properties) {
        super(properties);
    }

    @Nullable
    public ResourceLocation getRegistryName () {
        var id = BuiltInRegistries.ITEM.getKey(this);
        return id != BuiltInRegistries.ITEM.getDefaultKey() ? id : null;
    }

    public void addToCreativeTab (CreativeModeTab.Output output) {
        output.accept(this);
    }

    @Override
    public String toString () {
        String regName = this.getRegistryName() != null ? this.getRegistryName().getPath() : "unregistered";
        return this.getClass().getSimpleName() + "[" + regName + "]";
    }

}