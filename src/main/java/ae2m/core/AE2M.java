package ae2m.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public interface AE2M {

    String MOD_ID = "ae2machines";
    String NAME = "AE2 Machines";

    Logger LOGGER = LoggerFactory.getLogger(NAME);

    static AE2M instance () {
        return AE2MBase.INSTANCE;
    }

    static ResourceLocation getResource (String name) {
        return custom(MOD_ID, name);
    }

    static ResourceLocation getMinecraftResource (String name) {
        return custom("minecraft", name);
    }

    static ResourceLocation custom (String id, String name) {
        return ResourceLocation.fromNamespaceAndPath(id, name);
    }

    Collection<ServerPlayer> getPlayers ();

    Level getClientLevel ();

    MinecraftServer getCurrentServer ();

    static Logger getLogger () {
        return LOGGER;
    }


}
