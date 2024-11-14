package com.example.examplemod.core;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;

@net.neoforged.fml.common.Mod(Mod.MOD_ID)
public final class ModBootstrap {

    public ModBootstrap (IEventBus modEventBus) {
        switch (FMLEnvironment.dist) {
            case CLIENT -> new ModClient(modEventBus);
            case DEDICATED_SERVER -> new ModServer(modEventBus);
        }
    }

}
