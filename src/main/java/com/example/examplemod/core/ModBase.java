package com.example.examplemod.core;

import com.example.examplemod.core.registries.ModBlocks;
import com.example.examplemod.core.registries.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public abstract class ModBase implements Mod {

    static ModBase INSTANCE;

    public ModBase (IEventBus modEventBus) {
        if (INSTANCE != null) {
            throw new IllegalStateException("Already initialized");
        }
        INSTANCE = this;

        modEventBus.addListener(Tab::initExternal);
        modEventBus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey() == Registries.CREATIVE_MODE_TAB) {
                registerCreativeTabs();
            }
        });

        ModBlocks.REGISTRY.register(modEventBus);
        ModItems.REGISTRY.register(modEventBus);

    }

    private void registerCreativeTabs () {
        Tab.init(BuiltInRegistries.CREATIVE_MODE_TAB);
    }

    @Override
    public Collection<ServerPlayer> getPlayers () {
        var server = getCurrentServer();

        if (server != null) {
            return server.getPlayerList().getPlayers();
        }

        return Collections.emptyList();
    }

    @Nullable
    @Override
    public MinecraftServer getCurrentServer () {
        return ServerLifecycleHooks.getCurrentServer();
    }

}
