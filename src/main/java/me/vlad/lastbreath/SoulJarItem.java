package me.vlad.lastbreath;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import java.util.function.Consumer;

public final class SoulJarItem extends Item {
    public SoulJarItem(Properties properties) { super(properties); }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.SUCCESS;
        if (!player.isAlive() || player.isSpectator() || SoulService.ghost(player)) return InteractionResult.FAIL;
        ItemStack jar = player.getItemInHand(hand);
        if (!jar.is(this) || jar.getCount() != 1) return InteractionResult.FAIL;
        for (int slot = -1; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack soul = slot == -1 ? player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND) : player.getInventory().getItem(slot);
            if (!(soul.getItem() instanceof MobSoulItem)) continue;
            var filled=soul.is(LastBreath.RED_SOUL.get())?LastBreath.RED_JAR.get():soul.is(LastBreath.PURPLE_SOUL.get())?LastBreath.PURPLE_JAR.get():LastBreath.SOUL_JAR.get();
            soul.shrink(1);
            player.setItemInHand(hand, new ItemStack(filled));
            serverPlayer.inventoryMenu.broadcastChanges();
            serverPlayer.sendOverlayMessage(Component.translatable("lastbreath.jar.filled"));
            RitualFeedback.sound(serverPlayer.level(), player.position(), net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME, .5f, 1.3f);
            return InteractionResult.SUCCESS;
        }
        serverPlayer.sendOverlayMessage(Component.translatable("lastbreath.jar.no_soul"));
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        lines.accept(Component.translatable("lastbreath.tooltip.empty_jar").withStyle(ChatFormatting.AQUA));
    }
}
