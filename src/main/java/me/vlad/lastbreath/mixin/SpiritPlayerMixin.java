package me.vlad.lastbreath.mixin;

import me.vlad.lastbreath.SoulService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class SpiritPlayerMixin {
    // TAIL runs only after vanilla death/drop processing, not on a cancelled LivingDeathEvent.
    @Inject(method = "die", at = @At("TAIL"))
    private void lastbreath$death(DamageSource source, CallbackInfo ci) {
        SoulService.markDead((ServerPlayer)(Object)this);
    }
    @Inject(method = "setCamera", at = @At("HEAD"), cancellable = true)
    private void lastbreath$camera(Entity entity, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer)(Object)this;
        if (entity != null && entity != player && SoulService.ghost(player)) ci.cancel();
    }
    @Inject(method = "drop(Z)V", at = @At("HEAD"), cancellable = true)
    private void lastbreath$drop(boolean all, CallbackInfo ci) {
        if (SoulService.ghost((ServerPlayer)(Object)this)) ci.cancel();
    }
}
