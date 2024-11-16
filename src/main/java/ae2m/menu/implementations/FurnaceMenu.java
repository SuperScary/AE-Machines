package ae2m.menu.implementations;

import ae2m.block.machine.blockentity.FurnaceBlockEntity;
import ae2m.init.AE2MMenuTypes;
import appeng.api.config.YesNo;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.OutputSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

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

    public FurnaceMenu (int id, Inventory ip, FurnaceBlockEntity host) {
        super(AE2MMenuTypes.FURNACE_MENU, id, ip, host);

        var inv = host.getInternalInventory();

        var inputSlot = new AppEngSlot(inv, 0);
        this.inputSlot = this.addSlot(inputSlot, SlotSemantics.MACHINE_INPUT);

        var output = new OutputSlot(inv, 1, null);
        this.addSlot(output, SlotSemantics.MACHINE_OUTPUT);
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
