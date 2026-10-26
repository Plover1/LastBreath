package me.vlad.lastbreath;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/** Charges live in the block state, so vanilla chunk saves and client updates persist them. */
public final class ReturnAltarBlock extends BaseEntityBlock {
    public static final IntegerProperty CHARGES = IntegerProperty.create("charges", 0, 4);
    private static final net.minecraft.world.phys.shapes.VoxelShape SHAPE = net.minecraft.world.phys.shapes.Shapes.or(
            Block.box(0, 0, 0, 16, 2, 16), Block.box(2, 2, 2, 14, 8, 14), Block.box(1, 8, 1, 15, 10, 15),
            Block.box(1, 10, 1, 15, 12, 4), Block.box(1, 10, 12, 15, 12, 15),
            Block.box(1, 10, 4, 4, 12, 12), Block.box(12, 10, 4, 15, 12, 12),
            Block.box(4, 10, 4, 12, 10.2, 12), Block.box(6, 10.2, 6, 10, 10.35, 10));

    @Override
    protected net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        return SHAPE;
    }

    public ReturnAltarBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(CHARGES, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGES);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ReturnAltarEntity(pos, state); }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, LastBreath.ALTAR_ENTITY.get(), ReturnAltarEntity::tick);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (SoulService.ghost(player)) return useWithoutItem(state, level, pos, player, hit);
        if (stack.is(LastBreath.RETURN_HEART.get())) {
            if (!level.isClientSide()) {
                int charges = state.getValue(CHARGES);
                if (charges == 4) player.sendSystemMessage(Component.translatable("lastbreath.altar.full"));
                else {
                    level.setBlockAndUpdate(pos, state.setValue(CHARGES, charges + 1));
                    stack.shrink(1);
                    ((ServerPlayer)player).sendOverlayMessage(Component.translatable("lastbreath.altar.charges", charges + 1));
                    RitualFeedback.sound((net.minecraft.server.level.ServerLevel)level, net.minecraft.world.phys.Vec3.atCenterOf(pos), net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME, .6f, 1.0f + charges * .12f);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof ReturnAltarEntity altar) {
            if (SoulService.ghost(player)) altar.start(serverPlayer);
            else if (player.isShiftKeyDown() && player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty()) altar.withdraw(serverPlayer);
            else serverPlayer.sendOverlayMessage(Component.translatable("lastbreath.altar.charges", state.getValue(CHARGES)));
        }
        return InteractionResult.SUCCESS;
    }
}
