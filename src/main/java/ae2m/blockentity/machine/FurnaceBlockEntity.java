package ae2m.blockentity.machine;

import ae2m.blockentity.NetworkCraftingBlockEntity;
import ae2m.core.AE2M;
import appeng.api.config.*;
import appeng.api.implementations.blockentities.ICrankable;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.core.settings.TickRates;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import ae2m.core.recipe.FurnaceRecipes;
import ae2m.core.registries.AE2MBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FurnaceBlockEntity extends NetworkCraftingBlockEntity {

    private static final int MAX_PROCESSING_STEPS = 200;

    private final IUpgradeInventory upgrades;
    private final IConfigManager configManager;
    private int processingTime = 0;
    private int finalStep = 0;

    private boolean cooking = false;

    // Internally visible inventories
    private final IAEItemFilter baseFilter = new FurnaceBlockEntity.BaseFilter();
    private final AppEngInternalInventory mainItemHandler = new AppEngInternalInventory(this, 2, 64, baseFilter);
    // Combined internally visible inventories
    private final InternalInventory inv = new CombinedInternalInventory(this.mainItemHandler);

    // "Hack" to see if active recipe changed.
    private final Map<InternalInventory, ItemStack> lastStacks = new IdentityHashMap<>(Map.of(mainItemHandler, ItemStack.EMPTY));

    // The externally visible inventories (with filters applied)
    private final InternalInventory mainItemHandlerExtern;
    // Combined externally visible inventories
    private final InternalInventory combinedItemHandlerExtern;

    private SmeltingRecipe cachedTask = null;

    public FurnaceBlockEntity (BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);

        this.getMainNode()
                .setIdlePowerUsage(2)
                .addService(IGridTickable.class, this);
        this.setInternalMaxPower(16_000);

        this.upgrades = UpgradeInventories.forMachine(AE2MBlocks.FURNACE, 4, this::saveChanges);
        this.configManager = IConfigManager.builder(this::onConfigChanged)
                .registerSetting(Settings.INSCRIBER_SEPARATE_SIDES, YesNo.NO)
                .registerSetting(Settings.AUTO_EXPORT, YesNo.NO)
                .build();

        var automationFilter = new AutomationFilter();
        this.mainItemHandlerExtern = new FilteredInternalInventory(this.mainItemHandler, automationFilter);

        this.combinedItemHandlerExtern = new CombinedInternalInventory(mainItemHandlerExtern);

        this.setPowerSides(getGridConnectableSides(getOrientation()));
    }

    @Override
    public AECableType getCableConnectionType (Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    public Set<Direction> getGridConnectableSides (BlockOrientation orientation) {
        return EnumSet.complementOf(EnumSet.of(orientation.getSide(RelativeSide.FRONT)));
    }

    @Override
    protected void onOrientationChanged (BlockOrientation orientation) {
        super.onOrientationChanged(orientation);
        this.setPowerSides(getGridConnectableSides(orientation));
    }

    @Override
    public void addAdditionalDrops (Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);

        for (var upgrade : upgrades) {
            drops.add(upgrade);
        }
    }

    @Override
    public void clearContent () {
        super.clearContent();
        upgrades.clear();
    }

    @Override
    public InternalInventory getInternalInventory () {
        return this.inv;
    }

    @Override
    public void onChangeInventory (AppEngInternalInventory inv, int slot) {
        if (slot == 0) {
            boolean sameItemSameTags = ItemStack.isSameItemSameComponents(inv.getStackInSlot(0), lastStacks.get(inv));
            lastStacks.put(inv, inv.getStackInSlot(0).copy());
            if (sameItemSameTags) {
                return; // Don't care if it's just a count change
            }

            // Reset recipe
            this.setProcessingTime(0);
            this.cachedTask = null;
        }

        // Update displayed stacks on the client
        if (!this.isCooking()) {
            this.markForUpdate();
        }

        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    @Override
    public TickingRequest getTickingRequest (IGridNode node) {
        return new TickingRequest(1, 20, !hasAutoExportWork() && !this.hasCraftWork());
    }

    private boolean hasAutoExportWork () {
        return !this.mainItemHandler.getStackInSlot(1).isEmpty() && configManager.getSetting(Settings.AUTO_EXPORT) == YesNo.YES;
    }

    private boolean hasCraftWork () {
        var task = this.getTask();
        if (task != null) {
            // Only process if the result would fit.
            return mainItemHandler.insertItem(1, task.getResultItem(Objects.requireNonNull(getLevel()).registryAccess()).copy(), true).isEmpty();
        }

        this.setProcessingTime(0);
        return this.isCooking();
    }

    @Nullable
    public SmeltingRecipe getTask () {
        if (this.cachedTask == null && level != null) {
            ItemStack input = this.mainItemHandler.getStackInSlot(0);
            if (input.isEmpty()) {
                return null; // No input to handle
            }

            var recipe = Objects.requireNonNull(getLevel()).getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), getLevel());
            AE2M.getLogger().info("Recipe: {}", recipe);
            this.cachedTask = recipe.map(RecipeHolder::value).get();
            AE2M.getLogger().info("Found recipe: {}", this.cachedTask);
        }
        return this.cachedTask;
    }

    @Override
    public TickRateModulation tickingRequest (IGridNode node, int ticksSinceLastCall) {
        Objects.requireNonNull(getLevel()).registryAccess();

        if (this.isCooking()) {
            final SmeltingRecipe out = this.getTask();
            if (out != null) {
                final ItemStack outputCopy = out.getResultItem(getLevel().registryAccess()).copy();

                if (this.mainItemHandler.insertItem(1, outputCopy, false).isEmpty()) {
                    this.setProcessingTime(0);
                    this.mainItemHandler.extractItem(0, 1, false);
                }
            }
            this.saveChanges();
        } else if (this.hasCraftWork()) {
            getMainNode().ifPresent(grid -> {
                IEnergyService eg = grid.getEnergyService();
                IEnergySource src = this;

                final int powerConsumption = 10;
                final double powerThreshold = powerConsumption - 0.01;
                double powerReq = this.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);

                if (powerReq <= powerThreshold) {
                    src = eg;
                    powerReq = eg.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                }

                if (powerReq > powerThreshold) {
                    src.extractAEPower(powerConsumption, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    this.setProcessingTime(this.getProcessingTime());
                }
            });

            if (this.getProcessingTime() > this.getMaxProcessingTime()) {
                this.setProcessingTime(this.getMaxProcessingTime());
                final SmeltingRecipe out = this.getTask();
                if (out != null) {
                    final ItemStack outputCopy = out.getResultItem(getLevel().registryAccess()).copy();
                    if (this.mainItemHandler.insertItem(1, outputCopy, true).isEmpty()) {
                        this.setCooking(true);
                        this.finalStep = 0;
                        this.markForUpdate();
                    }
                }
            }
        }

        if (this.pushOutResult()) {
            return TickRateModulation.URGENT;
        }

        return this.hasCraftWork() ? TickRateModulation.URGENT
                : this.hasAutoExportWork() ? TickRateModulation.SLOWER : TickRateModulation.SLEEP;
    }

    private boolean pushOutResult () {
        if (!this.hasAutoExportWork()) {
            return false;
        }

        var pushSides = EnumSet.allOf(Direction.class);
        if (isSeparateSides()) {
            pushSides.remove(this.getTop());
            pushSides.remove(this.getTop().getOpposite());
        }

        for (var dir : pushSides) {
            assert level != null;
            var target = InternalInventory.wrapExternal(level, getBlockPos().relative(dir), dir.getOpposite());

            if (target != null) {
                int startItems = this.mainItemHandler.getStackInSlot(1).getCount();
                this.mainItemHandler.insertItem(1, target.addItems(this.mainItemHandler.extractItem(1, 64, false)),
                        false);
                int endItems = this.mainItemHandler.getStackInSlot(1).getCount();

                if (startItems != endItems) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isSeparateSides () {
        return this.configManager.getSetting(Settings.INSCRIBER_SEPARATE_SIDES) == YesNo.NO;
    }

    @Override
    public IConfigManager getConfigManager () {
        return configManager;
    }

    private void onConfigChanged (IConfigManager manager, Setting<?> setting) {
        if (setting == Settings.AUTO_EXPORT) {
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        if (setting == Settings.INSCRIBER_SEPARATE_SIDES) {
            // Our exposed inventory changed, invalidate caps!
            invalidateCapabilities();
        }

        saveChanges();
    }

    public boolean isCooking () {
        return this.cooking;
    }

    public void setCooking (boolean cooking) {
        this.cooking = cooking;
    }

    public int getMaxProcessingTime () {
        return MAX_PROCESSING_STEPS;
    }

    public int getProcessingTime () {
        return this.processingTime;
    }

    private void setProcessingTime (int processingTime) {
        this.processingTime = processingTime;
    }

    @Override
    public boolean isActive () {
        return isCooking();
    }

    public class BaseFilter implements IAEItemFilter {
        @Override
        public boolean allowInsert (InternalInventory inv, int slot, ItemStack stack) {
            return true;
        }
    }

    public class AutomationFilter implements IAEItemFilter {
        @Override
        public boolean allowExtract (InternalInventory inv, int slot, int amount) {
            if (slot == 1) {
                return true; // Can always extract from output slot
            }

            if (isCooking()) {
                return false;
            }

            // Can only extract from top and bottom in separated sides mode
            return isSeparateSides();
        }

        @Override
        public boolean allowInsert (InternalInventory inv, int slot, ItemStack stack) {
            if (slot == 1) {
                return false; // No inserting into the output slot
            }
            return !isCooking();
        }
    }

}
