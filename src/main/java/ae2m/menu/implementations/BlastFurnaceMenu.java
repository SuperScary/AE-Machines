package ae2m.menu.implementations;

import ae2m.blockentity.machine.BlastFurnaceBlockEntity;
import ae2m.client.gui.implementations.BlastFurnaceScreen;
import ae2m.core.recipe.FurnaceRecipes;
import ae2m.init.AE2MMenuTypes;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.core.localization.Side;
import appeng.core.localization.Tooltips;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.OutputSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * @see BlastFurnaceScreen
 */
public class BlastFurnaceMenu extends UpgradeableMenu<BlastFurnaceBlockEntity> implements IProgressProvider {

    private final Slot inputSlot;

    @GuiSync(2)
    public int maxProcessingTime = -1;

    @GuiSync(3)
    public int processingTime = -1;

    @GuiSync(7)
    public YesNo separateSides = YesNo.NO;
    @GuiSync(8)
    public YesNo autoExport = YesNo.NO;

    public BlastFurnaceMenu (int id, Inventory ip, BlastFurnaceBlockEntity host) {
        super(AE2MMenuTypes.BLAST_FURNACE_MENU, id, ip, host);

        var inv = host.getInternalInventory();

        var inputSlot = new AppEngSlot(inv, 0);
        inputSlot.setEmptyTooltip(() -> separateSides == YesNo.YES ? Tooltips.inputSlot(Side.LEFT, Side.RIGHT, Side.BACK, Side.FRONT)
                : Tooltips.inputSlot(Side.ANY));
        this.inputSlot = this.addSlot(inputSlot, SlotSemantics.MACHINE_INPUT);

        var output = new OutputSlot(inv, 1, null);
        output.setEmptyTooltip(() -> separateSides == YesNo.YES ? Tooltips.outputSlot(Side.LEFT, Side.RIGHT, Side.BACK, Side.FRONT)
                : Tooltips.outputSlot(Side.ANY));
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
        }
        super.standardDetectAndSendChanges();
    }

    @Override
    public boolean isValidForSlot (Slot s, ItemStack i) {
        final ItemStack stack = i.copy();
        var level = getHost().getLevel();

        if (s == this.inputSlot) {
            var recipe = FurnaceRecipes.findRecipe(level, stack);
            return recipe != null;
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

    public YesNo getSeparateSides () {
        return separateSides;
    }

    public YesNo getAutoExport () {
        return autoExport;
    }

}
