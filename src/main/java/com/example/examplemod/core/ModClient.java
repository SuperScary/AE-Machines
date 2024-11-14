package com.example.examplemod.core;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;

public class ModClient extends ModBase {

    public ModClient (IEventBus eventBus) {
        super(eventBus);
    }

    @Override
    public Level getClientLevel () {
        return Minecraft.getInstance().level;
    }

}
