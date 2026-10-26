package me.vlad.lastbreath.mixin;

import me.vlad.lastbreath.GhostInteractions;
import me.vlad.lastbreath.SoulService;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class SpiritGameModeMixin {
    @Shadow @Final protected ServerPlayer player;
    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void lastbreath$break(BlockPos pos, CallbackInfoReturnable<Boolean> ci) {
        if (SoulService.ghost(player)) ci.setReturnValue(false);
    }
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void lastbreath$block(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> ci) {
        if (SoulService.ghost(player)) ci.setReturnValue(SoulService.spiritItem(stack) && !level.getBlockState(hit.getBlockPos()).is(me.vlad.lastbreath.LastBreath.RETURN_ALTAR.get()) && !level.getBlockState(hit.getBlockPos()).is(me.vlad.lastbreath.LastBreath.RITUAL_TABLE.get()) ? stack.getItem().use(level, player, hand) : GhostInteractions.useBlock(player, level, hand, hit));
    }
    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    private void lastbreath$item(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> ci) {
        if (SoulService.ghost(player) && !SoulService.spiritItem(stack)) ci.setReturnValue(InteractionResult.FAIL);
    }
}
