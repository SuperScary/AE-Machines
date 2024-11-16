package ae2m.core;

import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = AE2M.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class AE2MServer extends AE2MBase {

    public AE2MServer (IEventBus eventBus) {
        super(eventBus);
    }

    @Override
    public Level getClientLevel () {
        return null;
    }

}
