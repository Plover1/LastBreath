package me.vlad.lastbreath;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.BlockHitResult;

/** Empty-hand block interactions only: never forward a ghost's held item to a block or item. */
public final class GhostInteractions {
    private GhostInteractions() {}
    public static InteractionResult useBlock(ServerPlayer player, Level level, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.FAIL;
        var event = net.neoforged.neoforge.common.CommonHooks.onRightClickBlock(player, hand, hit.getBlockPos(), hit);
        if (event.isCanceled()) return event.getCancellationResult();
        if (event.getUseBlock().isFalse()) return InteractionResult.FAIL;
        var state = level.getBlockState(hit.getBlockPos());
        var block = state.getBlock();
        boolean allowed = state.getMenuProvider(level, hit.getBlockPos()) != null
                || block instanceof DoorBlock || block instanceof TrapDoorBlock
                || block instanceof FenceGateBlock || block instanceof ButtonBlock
                || block instanceof LeverBlock || block instanceof BellBlock || block instanceof ReturnAltarBlock;
        // Beds/respawn anchors, flower pots, buckets, tools and explosives are not safe block interactions.
        return allowed ? state.useWithoutItem(level, player, hit) : InteractionResult.FAIL;
    }
}
