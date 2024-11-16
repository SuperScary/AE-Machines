package ae2m.core;

import ae2m.client.gui.style.AE2MStyleManager;
import ae2m.init.client.AE2MScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = AE2M.MOD_ID, dist = Dist.CLIENT)
public class AE2MClient extends AE2MBase {

    public AE2MClient (IEventBus eventBus) {
        super(eventBus);

        eventBus.addListener(AE2MScreens::init);

        eventBus.addListener(this::clientSetup);

        INSTANCE = this;
    }

    private void clientSetup (FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            try {
                postClientSetup(minecraft);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void postClientSetup (Minecraft minecraft) {
        AE2MStyleManager.initialize(minecraft.getResourceManager());
    }

    @Override
    public Level getClientLevel () {
        return Minecraft.getInstance().level;
    }

}
