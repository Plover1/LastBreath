package me.vlad.lastbreath.mixin;

import me.vlad.lastbreath.SoulService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class SpiritPacketsMixin {
    @Shadow public ServerPlayer player;

    // Run after vanilla moves packet handling to the server thread, before any mutation or client prediction hashes.
    @Inject(method = {"handleContainerClick", "handlePlaceRecipe",
            "handleSetCreativeModeSlot", "handleContainerSlotStateChanged", "handleSetBeaconPacket",
            "handleSelectTrade", "handlePlayerAction"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V", shift = At.Shift.AFTER),
            cancellable = true)
    private void lastbreath$readOnly(CallbackInfo ci) {
        if (SoulService.ghost(player)) {
            player.containerMenu.sendAllDataToRemote();
            ci.cancel();
        }
    }

    @Inject(method = "handleContainerButtonClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V", shift = At.Shift.AFTER), cancellable = true)
    private void lastbreath$skillButton(CallbackInfo ci) {
        if (SoulService.ghost(player) && !(player.containerMenu instanceof me.vlad.lastbreath.SoulTreeMenu)) ci.cancel();
    }
}
