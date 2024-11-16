package ae2m.core;

import ae2m.block.machine.blockentity.FurnaceBlockEntity;
import ae2m.core.registries.ModBlockEntities;
import ae2m.core.registries.ModBlocks;
import ae2m.core.registries.ModItems;
import appeng.api.AECapabilities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import ae2m.init.AE2MMenuTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public abstract class AE2MBase implements AE2M {

    static AE2MBase INSTANCE;

    public AE2MBase (IEventBus modEventBus) {
        if (INSTANCE != null) {
            throw new IllegalStateException("Already initialized");
        }
        INSTANCE = this;

        ModBlocks.DR.register(modEventBus);
        ModItems.DR.register(modEventBus);
        ModBlockEntities.DR.register(modEventBus);
        AE2MMenuTypes.DR.register(modEventBus);

        modEventBus.addListener(Tab::initExternal);
        modEventBus.addListener(AE2MBase::initCapabilities);
        modEventBus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey() == Registries.CREATIVE_MODE_TAB) {
                registerCreativeTabs();
            }
        });

    }

    private void registerCreativeTabs () {
        Tab.init(BuiltInRegistries.CREATIVE_MODE_TAB);
    }

    private static void initCapabilities (RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(AECapabilities.CRANKABLE, ModBlockEntities.FURNACE.get(), FurnaceBlockEntity::getCrankable);
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
