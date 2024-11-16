package ae2m.core.registries;

import appeng.api.stacks.AEKeyType;
import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;
import appeng.items.materials.StorageComponentItem;
import appeng.items.storage.BasicStorageCell;
import appeng.items.storage.StorageTier;
import ae2m.core.AE2M;
import ae2m.core.Tab;
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

public class AE2MItems {

    public static final DeferredRegister.Items DR = DeferredRegister.createItems(AE2M.MOD_ID);

    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    private static final List<CellDefinition> CELLS = new ArrayList<>();

    // REGISTER ITEMS HERE

    /**
     * ITEMS
     */
    public static final ItemDefinition<MaterialItem> STEEL_INGOT = item("Steel Ingot", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> STEEL_PLATE = item("Steel Plate", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> DEEP_STORAGE_PROCESSOR = item("Deep Storage Processor", MaterialItem::new);

    /**
     * HOUSINGS
     */
    public static final ItemDefinition<MaterialItem> STEEL_ITEM_HOUSING = item("Steel Item Housing", MaterialItem::new);

    /**
     * COMPONENTS
     */
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_1M = new ComponentBuilder(1).build();
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_4M = new ComponentBuilder(4).build();
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_16M = new ComponentBuilder(16).build();
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_64M = new ComponentBuilder(64).build();
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_256M = new ComponentBuilder(256).build();
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_1B = new ComponentBuilder(1024).build();

    /**
     * TIERS
     */
    public static final StorageTier TIER_1M = tier(6, CELL_COMPONENT_1M);
    public static final StorageTier TIER_4M = tier(7, CELL_COMPONENT_4M);
    public static final StorageTier TIER_16M = tier(8, CELL_COMPONENT_16M);
    public static final StorageTier TIER_64M = tier(9, CELL_COMPONENT_64M);
    public static final StorageTier TIER_256M = tier(10, CELL_COMPONENT_256M);
    public static final StorageTier TIER_1B = tier(11, CELL_COMPONENT_1B);

    /**
     * CELLS
     */
    public static final ItemDefinition<BasicStorageCell> ITEM_CELL_1M = new CellBuilder(TIER_1M, CELL_COMPONENT_1M).build();
    public static final ItemDefinition<BasicStorageCell> ITEM_CELL_4M = new CellBuilder(TIER_4M, CELL_COMPONENT_4M).build();
    public static final ItemDefinition<BasicStorageCell> ITEM_CELL_16M = new CellBuilder(TIER_16M, CELL_COMPONENT_16M).build();
    public static final ItemDefinition<BasicStorageCell> ITEM_CELL_64M = new CellBuilder(TIER_64M, CELL_COMPONENT_64M).build();
    public static final ItemDefinition<BasicStorageCell> ITEM_CELL_256M = new CellBuilder(TIER_256M, CELL_COMPONENT_256M).build();
    public static final ItemDefinition<BasicStorageCell> ITEM_CELL_1B = new CellBuilder(TIER_1B, CELL_COMPONENT_1B).build();

    /**
     * PRESSES
     */
    public static final ItemDefinition<MaterialItem> INGOT_PRESS = item("Ingot Press", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> BLANK_PRESS = item("Blank Press", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> DEEP_STORAGE_PRESS = item("Deep Storage Press", MaterialItem::new);

    public static List<ItemDefinition<?>> getItems () {
        return Collections.unmodifiableList(ITEMS);
    }

    public static List<CellDefinition> getCells () {
        return Collections.unmodifiableList(CELLS);
    }

    static <T extends Item> ItemDefinition<T> item (String name, Function<Item.Properties, T> factory) {
        return item(name, AE2M.getResource(name.toLowerCase().replaceAll("\\s+", "_")), factory, Tab.MAIN);
    }

    static <T extends Item> ItemDefinition<T> item (String name, ResourceLocation id, Function<Item.Properties, T> factory) {
        return item(name, id, factory, Tab.MAIN);
    }

    static <T extends Item> ItemDefinition<T> item (String name, ResourceLocation id, Function<Item.Properties, T> factory, @Nullable ResourceKey<CreativeModeTab> group) {
        Preconditions.checkArgument(id.getNamespace().equals(AE2M.MOD_ID), "Can only register items in " + AE2M.MOD_ID + " namespace");
        var definition = new ItemDefinition<>(name, DR.registerItem(id.getPath(), factory));

        if (Objects.equals(group, Tab.MAIN)) {
            Tab.add(definition);
        } else if (group != null) {
            Tab.addExternal(group, definition);
        }

        ITEMS.add(definition);
        return definition;
    }

    static StorageTier tier (int index, ItemDefinition<StorageComponentItem> component) {
        var multiplier = (int) Math.pow(4, index - 1);

        // FOR CALCULATING THE NAME BASED ON SIZE
        var prefix = (multiplier / 1024);
        var suffix = "m";
        return new StorageTier(index, prefix + suffix, 1024 * multiplier, 0.5 * index, component::asItem);
    }

    public static class CellBuilder {

        private final StorageTier tier;
        private final ItemDefinition<StorageComponentItem> storageComponent;
        private final Type type;

        public CellBuilder (StorageTier tier, ItemDefinition<StorageComponentItem> storageComponent, Type type) {
            this.tier = tier;
            this.storageComponent = storageComponent;
            this.type = type;
        }

        public CellBuilder (StorageTier tier, ItemDefinition<StorageComponentItem> storageComponent) {
            this(tier, storageComponent, Type.ITEM);
        }

        private ItemDefinition<BasicStorageCell> build () {
            var cell = item(getTier().namePrefix().toUpperCase() + " " + getType().translation + " Storage Cell", AE2M.getResource("item_storage_cell_" + getTier().namePrefix()), p ->
                    new BasicStorageCell(p.stacksTo(1), getTier().componentSupplier().get(), STEEL_ITEM_HOUSING, getTier().idleDrain(), getTier().bytes() / 1024, getTier().bytes() / 128, 63, AEKeyType.items()));
            CELLS.add(new CellDefinition(cell, tier, "item", false));
            return cell;
        }

        private StorageTier getTier () {
            return tier;
        }

        private Type getType () {
            return type;
        }

        private ItemDefinition<StorageComponentItem> getStorageComponent () {
            return storageComponent;
        }

        public enum Type {
            ITEM(AE2M.MOD_ID + ".item", "Item"),
            FLUID(AE2M.MOD_ID + ".fluid", "Fluid"),
            GAS(AE2M.MOD_ID + ".gas", "Gas");

            private final String translationKey;
            private final String translation;
            Type (String translationKey, String translation) {
                this.translationKey = translationKey;
                this.translation = translation;
            }

            public String getTranslation () {
                return translation;
            }

            public String getTranslationKey () {
                return translationKey;
            }

        }

    }

    public static class ComponentBuilder {

        private final int tier;

        public ComponentBuilder (int tier) {
            this.tier = tier;
        }

        private ItemDefinition<StorageComponentItem> build () {
            return item(getTier() + "M Storage Component", AE2M.getResource("cell_component_" + getTier() + "m"), p -> new StorageComponentItem(p, getTier() * 1024));
        }

        private int getTier () {
            return tier;
        }

    }

    public record CellDefinition (ItemDefinition<?> item, StorageTier tier, String keyType, boolean portable) {

    }

}
