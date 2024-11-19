package ae2m.core.util;

import ae2m.core.AE2M;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * Compatability layer for inter-mod communication.
 */
public class CompatabilityLayer {

    private static final List<String> REQUIRED = List.of(
            "ae2",
            "neoforge",
            "minecraft"
    );

    /**
     * Checks if all required mods are loaded.
     */
    public static boolean checkRequiredMods () {
        AtomicBoolean holder = new AtomicBoolean(true);
        REQUIRED.forEach(modid -> {
            if (!isModLoaded(modid)) {
                AE2M.getLogger().error("Missing required mod: {}", modid);
                holder.set(false);
            } else
                AE2M.getLogger().info("Found required mod: {} ({}) v{}", getModName(modid), modid, getModVersion(modid));
        });
        return holder.get();
    }

    /**
     * Checks if a mod is loaded.
     */
    public static boolean isModLoaded (String modid) {
        return ModList.get().isLoaded(modid);
    }

    public static String getModName (String modid) {
        if (!isModLoaded(modid)) return "Unknown";
        else
            return ModList.get().getModContainerById(modid).map(container -> container.getModInfo().getDisplayName()).orElse("Unknown");
    }

    public static String getModVersion (String modid) {
        if (!isModLoaded(modid)) return "0.0.0";
        else
            return ModList.get().getModContainerById(modid).map(container -> container.getModInfo().getVersion().toString()).orElse("0.0.0");
    }

    /**
     * Sends a compatibility message to another mod from AE2M.
     */
    public static boolean sendCompatibilityMessage (String modid, String method, Supplier<?> message) {
        return sendCompatibilityMessage(AE2M.MOD_ID, modid, method, message);
    }

    /**
     * Sends a compatibility message to another mod from a specified mod.
     */
    public static boolean sendCompatibilityMessage (String senderModid, String modid, String method, Supplier<?> message) {
        if (isModLoaded(modid)) return false;
        else return InterModComms.sendTo(senderModid, modid, method, message);
    }

}
