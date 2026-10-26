package me.vlad.lastbreath.mixin;

import me.vlad.lastbreath.SoulService;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class SpiritMenuMixin {
    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void lastbreath$readOnly(int slot, int button, ContainerInput input, Player player, CallbackInfo ci) {
        if (SoulService.ghost(player)) ci.cancel();
    }
}
