package ae2m.client.gui.implementations;

import ae2m.menu.implementations.BlastFurnaceMenu;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BlastFurnaceScreen extends UpgradeableScreen<BlastFurnaceMenu> {

    private final ProgressBar pb;
    private final SettingToggleButton<YesNo> separateSidesBtn;
    private final SettingToggleButton<YesNo> autoExportBtn;

    public BlastFurnaceScreen (BlastFurnaceMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.pb = new ProgressBar(menu, style.getImage("progressBar"), ProgressBar.Direction.VERTICAL);
        widgets.add("progressBar", pb);

        this.separateSidesBtn = new ServerSettingToggleButton<>(Settings.INSCRIBER_SEPARATE_SIDES, YesNo.NO);
        this.addToLeftToolbar(separateSidesBtn);

        this.autoExportBtn = new ServerSettingToggleButton<>(Settings.AUTO_EXPORT, YesNo.NO);
        this.addToLeftToolbar(autoExportBtn);
    }

    @Override
    protected void updateBeforeRender () {
        super.updateBeforeRender();

        int progress = menu.processingTime * 100 / menu.maxProcessingTime;
        this.pb.setFullMsg(Component.literal(progress + "%"));

        this.separateSidesBtn.set(getMenu().getSeparateSides());
        this.autoExportBtn.set(getMenu().getAutoExport());
    }
}
