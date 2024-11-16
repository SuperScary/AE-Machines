package ae2m.block.machine;

import ae2m.blockentity.machine.FurnaceBlockEntity;
import ae2m.init.AE2MMenuTypes;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FurnaceBlock extends AEBaseEntityBlock<FurnaceBlockEntity> implements SimpleWaterloggedBlock {

    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public FurnaceBlock (Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    public int getLightBlock (@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 2;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem (BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof FurnaceBlockEntity be) {
            if (!level.isClientSide()) {
                MenuOpener.open(AE2MMenuTypes.FURNACE_MENU, player, MenuLocators.forBlockEntity(be));
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
        return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
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
        double xPos = (double) pos.getX() + 0.5;
        double yPos = pos.getY();
        double zPos = (double) pos.getZ() + 0.5;

        Direction direction = state.getValue(BlockStateProperties.FACING);
        Direction.Axis axis = direction.getAxis();

        double defaultOffset = random.nextDouble() * 0.6 - 0.3;
        double xOffsets = axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52 : defaultOffset;
        double yOffsets = random.nextDouble() * 6.0 / 8.0;
        double zOffsets = axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52 : defaultOffset;

        if (state.getValue(WATERLOGGED)) {
            if (random.nextDouble() < 0.1) {
                level.playLocalSound(xPos, yPos, zPos, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            level.addParticle(ParticleTypes.BUBBLE, xPos + xOffsets, yPos + yOffsets, zPos + zOffsets, 0d, 0d, 0d);
        } else {

            if (level.getBlockEntity(pos) instanceof FurnaceBlockEntity be && be.getAECurrentPower() > 0) {
                level.addParticle(ParticleTypes.SMOKE, xPos + xOffsets, yPos + yOffsets, zPos + zOffsets, 0d, 0d, 0d);

                if (random.nextDouble() < 0.1) {
                    level.playLocalSound(xPos, yPos, zPos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                }
            }

            if (level.getBlockEntity(pos) instanceof FurnaceBlockEntity be && !be.getInternalInventory().isEmpty() && be.isCooking()) {
                var stack = be.getInternalInventory().getStackInSlot(0);
                level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), xPos + xOffsets, yPos + yOffsets, zPos + zOffsets, 0d, 0d, 0d);
            }
        }

        super.animateTick(state, level, pos, random);

    }
}
