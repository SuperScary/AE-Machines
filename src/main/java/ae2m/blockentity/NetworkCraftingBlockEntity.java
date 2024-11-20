package ae2m.blockentity;

import appeng.api.config.Actionable;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.implementations.blockentities.ICrankable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import appeng.me.storage.DelegatingMEInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

/**
 * Base class for all network crafting block entities. All entities supply their own crafting handling.
 */
public abstract class NetworkCraftingBlockEntity extends AENetworkedPoweredBlockEntity implements IGridTickable, IUpgradeableObject, IPowerChannelState, IConfigurableObject {

    @Nullable
    private CraftingBlockInventory localInvHandler;

    public NetworkCraftingBlockEntity (BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
        this.setInternalPublicPowerStorage(true);
    }

    /**
     * Allows connections on all sides except the front.
     */
    @Override
    public Set<Direction> getGridConnectableSides (BlockOrientation orientation) {
        return EnumSet.complementOf(EnumSet.of(orientation.getSide(RelativeSide.FRONT)));
    }

    /**
     * Called when the block orientation changes.
     * Updates the power sides to match the new orientation.
     */
    @Override
    protected void onOrientationChanged (BlockOrientation orientation) {
        super.onOrientationChanged(orientation);
        this.setPowerSides(getGridConnectableSides(orientation));
    }

    /**
     * Allows block to be powered by a {@link appeng.blockentity.grid.AENetworkedPoweredBlockEntity.Crankable} block
     *
     * @see appeng.blockentity.grid.AENetworkedPoweredBlockEntity.Crankable
     */
    @Nullable
    public ICrankable getCrankable (Direction direction) {
        if (direction != getFront()) {
            return new Crankable();
        }
        return null;
    }

    @Override
    public void onReady () {
        super.onReady();
    }

    @Override
    public boolean isPowered () {
        return getMainNode().isPowered();
    }

    @Override
    public @Nullable IEnergyStorage getEnergyStorage (@Nullable Direction side) {
        return super.getEnergyStorage(side);
    }

    public MEStorage getInventory () {
        return getLocalInventory();
    }

    /**
     * TODO: Implement to automatically add to the AE network
     *
     * @see appeng.blockentity.misc.InterfaceBlockEntity
     * @see InterfaceLogic
     * @see InterfaceLogicHost
     * @see appeng.util.ConfigInventory
     */
    public MEStorage getLocalInventory () {
        if (localInvHandler == null) {
            localInvHandler = new CraftingBlockInventory(getMainNode().getGrid().getStorageService().getInventory());
        }
        return localInvHandler;
    }

    /**
     * Wrapper for interfacing with the ME network.
     */
    private class CraftingBlockInventory extends DelegatingMEInventory {

        CraftingBlockInventory (MEStorage delegate) {
            super(delegate);
        }

        @Override
        public long extract (AEKey what, long amount, Actionable mode, IActionSource source) {
            return super.extract(what, amount, mode, source);
        }

    }

}
