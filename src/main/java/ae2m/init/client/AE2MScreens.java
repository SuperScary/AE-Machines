package ae2m.init.client;

import ae2m.client.gui.implementations.BlastFurnaceScreen;
import ae2m.client.gui.implementations.FurnaceScreen;
import ae2m.init.AE2MMenuTypes;
import appeng.init.client.InitScreens;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class AE2MScreens {

    public static void init (RegisterMenuScreensEvent event) {
        InitScreens.register(event, AE2MMenuTypes.FURNACE_MENU, FurnaceScreen::new, "/screens/furnace.json");
        InitScreens.register(event, AE2MMenuTypes.BLAST_FURNACE_MENU, BlastFurnaceScreen::new, "/screens/blast_furnace.json");
    }

}
