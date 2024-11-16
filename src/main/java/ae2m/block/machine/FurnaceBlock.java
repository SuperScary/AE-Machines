package ae2m.block.machine;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import ae2m.block.machine.blockentity.FurnaceBlockEntity;
import ae2m.menu.implementations.FurnaceMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FurnaceBlock extends AEBaseEntityBlock<FurnaceBlockEntity> implements SimpleWaterloggedBlock {

    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public FurnaceBlock (Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    public int getLightBlock (BlockState state, BlockGetter level, BlockPos pos) {
        return 2; // FIXME validate this. a) possibly not required because of getShape b) value
        // range. was 2 in 1.10
    }

    @Override
    protected InteractionResult useWithoutItem (BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof FurnaceBlockEntity be) {
            if (!level.isClientSide()) {
                MenuOpener.open(FurnaceMenu.TYPE, player, MenuLocators.forBlockEntity(be));
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy () {
        return OrientationStrategies.full();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement (BlockPlaceContext context) {
        var fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return super.getStateForPlacement(context).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public @NotNull FluidState getFluidState (BlockState blockState) {
        return blockState.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(blockState);
    }

    @Override
    public @NotNull BlockState updateShape (BlockState blockState, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (blockState.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(blockState, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public void animateTick (@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (state.getValue(WATERLOGGED)) return;

        double xPos = (double) pos.getX() + 0.5;
        double yPos = pos.getY();
        double zPos = (double) pos.getZ() + 0.5;

        if (random.nextDouble() < 0.1) {
            level.playLocalSound(xPos, yPos, zPos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }

        Direction direction = state.getValue(BlockStateProperties.FACING);
        Direction.Axis axis = direction.getAxis();

        double defaultOffset = random.nextDouble() * 0.6 - 0.3;
        double xOffsets = axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52 : defaultOffset;
        double yOffsets = random.nextDouble() * 6.0 / 8.0;
        double zOffsets = axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52 : defaultOffset;

        if (level.getBlockEntity(pos) instanceof FurnaceBlockEntity be && be.getAECurrentPower() > 0) {
            level.addParticle(ParticleTypes.FLAME, xPos + xOffsets, yPos + yOffsets, zPos + zOffsets, 0d, 0d, 0d);
        }

        if (level.getBlockEntity(pos) instanceof FurnaceBlockEntity be && !be.getInternalInventory().isEmpty() && be.isCooking()) {
            var stack = be.getInternalInventory().getStackInSlot(0);
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), xPos + xOffsets, yPos + yOffsets, zPos + zOffsets, 0d, 0d, 0d);
        }

        super.animateTick(state, level, pos, random);

    }
}