package ae2m.client.gui.implementations;

import appeng.api.config.PowerUnit;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import ae2m.menu.implementations.FurnaceMenu;

public class FurnaceScreen extends UpgradeableScreen<FurnaceMenu> {

    private final ProgressBar pb;
    private final SettingToggleButton<YesNo> separateSidesBtn;
    private final SettingToggleButton<YesNo> autoExportBtn;

    public FurnaceScreen (FurnaceMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
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

        int progress = (int) (menu.getCurrentPower() * 100 / menu.getMaxPower());
        this.pb.setFullMsg(Component.literal(progress + "% " + PowerUnit.AE.symbolName));

        int craftProgress = menu.getCurrentProgress() * 100 / menu.getMaxProgress();

        this.separateSidesBtn.set(getMenu().getSeparateSides());
        this.autoExportBtn.set(getMenu().getAutoExport());
    }
}
