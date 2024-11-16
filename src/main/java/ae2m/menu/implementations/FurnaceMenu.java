package ae2m.menu.implementations;

import ae2m.blockentity.machine.FurnaceBlockEntity;
import ae2m.core.recipe.FurnaceRecipes;
import ae2m.init.AE2MMenuTypes;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.OutputSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FurnaceMenu extends UpgradeableMenu<FurnaceBlockEntity> implements IProgressProvider {

    private final Slot inputSlot;

    @GuiSync(2)
    public int maxProcessingTime = -1;

    @GuiSync(3)
    public int processingTime = -1;

    @GuiSync(7)
    public YesNo separateSides = YesNo.NO;
    @GuiSync(8)
    public YesNo autoExport = YesNo.NO;

    @GuiSync(9)
    public double currentPower = -1;
    @GuiSync(10)
    public double maxPower = -1;

    public FurnaceMenu (int id, Inventory ip, FurnaceBlockEntity host) {
        super(AE2MMenuTypes.FURNACE_MENU, id, ip, host);

        var inv = host.getInternalInventory();

        this.currentPower = host.getAECurrentPower();
        this.maxPower = host.getAEMaxPower();

        var inputSlot = new AppEngSlot(inv, 0);
        this.inputSlot = this.addSlot(inputSlot, SlotSemantics.MACHINE_INPUT);

        var output = new OutputSlot(inv, 1, null);
        this.addSlot(output, SlotSemantics.MACHINE_OUTPUT);
    }

    @Override
    protected void loadSettingsFromHost (IConfigManager cm) {
        this.separateSides = getHost().getConfigManager().getSetting(Settings.INSCRIBER_SEPARATE_SIDES);
        this.autoExport = getHost().getConfigManager().getSetting(Settings.AUTO_EXPORT);
    }

    @Override
    protected void standardDetectAndSendChanges () {
        if (isServerSide()) {
            this.maxProcessingTime = getHost().getMaxProcessingTime();
            this.processingTime = getHost().getProcessingTime();
            this.currentPower = getHost().getAECurrentPower();
            this.maxPower = getHost().getAEMaxPower();
        }
        super.standardDetectAndSendChanges();
    }

    @Override
    public boolean isValidForSlot (Slot s, ItemStack i) {
        final ItemStack stack = i.copy();

        if (s == this.inputSlot) {
            return FurnaceRecipes.findRecipe(getHost().getLevel(), stack) != null;
        }
        return true;
    }

    @Override
    public int getCurrentProgress () {
        return this.processingTime;
    }

    @Override
    public int getMaxProgress () {
        return this.maxProcessingTime;
    }

    public double getCurrentPower () {
        return this.currentPower;
    }

    public double getMaxPower () {
        return this.maxPower;
    }

    public YesNo getSeparateSides () {
        return separateSides;
    }

    public YesNo getAutoExport () {
        return autoExport;
    }

}
