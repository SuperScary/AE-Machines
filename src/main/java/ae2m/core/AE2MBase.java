package ae2m.core;

import ae2m.blockentity.machine.BlastFurnaceBlockEntity;
import ae2m.blockentity.machine.FurnaceBlockEntity;
import ae2m.core.registries.AE2MBlockEntities;
import ae2m.core.registries.AE2MBlocks;
import ae2m.core.registries.AE2MItems;
import ae2m.core.util.CompatabilityLayer;
import appeng.api.AECapabilities;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.blockentity.AEBaseInvBlockEntity;
import appeng.blockentity.powersink.AEBasePoweredBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
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
            AE2M.getLogger().error("Already initialized", new IllegalStateException("Already initialized"));
        }
        INSTANCE = this;

        if (!CompatabilityLayer.checkRequiredMods()) {
            AE2M.getLogger().error("Missing required mods. Did you forget to install the requirements?", new IllegalStateException("Missing requirements."));
        }

        AE2MBlocks.DR.register(modEventBus);
        AE2MItems.DR.register(modEventBus);
        AE2MBlockEntities.DR.register(modEventBus);
        AE2MMenuTypes.DR.register(modEventBus);

        modEventBus.addListener(Tab::initExternal);
        modEventBus.addListener(this::initCapabilities);
        modEventBus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey() == Registries.CREATIVE_MODE_TAB) {
                registerCreativeTabs();
            }
        });

    }

    /**
     * Registers the creative tabs
     */
    private void registerCreativeTabs () {
        Tab.init(BuiltInRegistries.CREATIVE_MODE_TAB);
    }

    /**
     * Initializes the capabilities for all blocks and items
     */
    private void initCapabilities (RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(AECapabilities.CRANKABLE, AE2MBlockEntities.FURNACE.get(), FurnaceBlockEntity::getCrankable);
        event.registerBlockEntity(AECapabilities.CRANKABLE, AE2MBlockEntities.BLAST_FURNACE.get(), BlastFurnaceBlockEntity::getCrankable);

        for (var type : AE2MBlockEntities.getSubclassesOf(AEBaseInvBlockEntity.class)) {
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type, AEBaseInvBlockEntity::getExposedItemHandler);
        }

        for (var type : AE2MBlockEntities.getSubclassesOf(AEBasePoweredBlockEntity.class)) {
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, type, AEBasePoweredBlockEntity::getEnergyStorage);
        }

        for (var type : AE2MBlockEntities.getImplementorsOf(IInWorldGridNodeHost.class)) {
            event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, type, (object, context) -> (IInWorldGridNodeHost) object);
        }

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
