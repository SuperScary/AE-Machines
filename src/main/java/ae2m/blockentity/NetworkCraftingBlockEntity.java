package ae2m.blockentity;

import appeng.api.implementations.IPowerChannelState;
import appeng.api.implementations.blockentities.ICrankable;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class NetworkCraftingBlockEntity extends AENetworkedPoweredBlockEntity implements IGridTickable, IUpgradeableObject, IPowerChannelState, IConfigurableObject {

    public NetworkCraftingBlockEntity (BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Nullable
    public ICrankable getCrankable (Direction direction) {
        if (direction != getFront()) {
            return new Crankable();
        }
        return null;
    }

    @Override
    public boolean isPowered () {
        return getInternalCurrentPower() > 0;
    }

}
