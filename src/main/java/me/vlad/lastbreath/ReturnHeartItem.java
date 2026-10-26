package me.vlad.lastbreath;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.network.chat.Component;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public final class ReturnHeartItem extends Item {
    public static final int CHANNEL_TICKS = 100;
    // Player instance keys prevent a disconnected player's ritual from surviving a reconnect.
    private final Map<ServerPlayer, UUID> targets = new WeakHashMap<>();
    public ReturnHeartItem(Properties properties) { super(properties); }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display,
            java.util.function.Consumer<Component> lines, net.minecraft.world.item.TooltipFlag flag) {
        lines.accept(Component.translatable("lastbreath.tooltip.heart").withStyle(net.minecraft.ChatFormatting.AQUA));
        lines.accept(Component.translatable("lastbreath.tooltip.heart_self").withStyle(net.minecraft.ChatFormatting.GRAY));
    }
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            targets.remove(serverPlayer);
            if (SoulService.ghost(player)) {
                SoulService.selfRevive(serverPlayer, hand, false);
                return InteractionResult.SUCCESS;
            }
            ServerPlayer nearest = serverPlayer.level().getServer().getPlayerList().getPlayers().stream()
                    .filter(target -> eligible(serverPlayer, target))
                    .min(java.util.Comparator.comparingDouble(target -> target.distanceToSqr(serverPlayer)))
                    .orElse(null);
            if (nearest == null) {
                serverPlayer.sendOverlayMessage(Component.translatable("lastbreath.channel.none"));
                return InteractionResult.FAIL;
            }
            double cost = Math.max(0, RevivalRules.cost(Config.HEALTH_COST.get(), Config.REPEAT_COST.get(), nearest.getData(LastBreath.SOUL).revivals()) - TalentService.legacy(serverPlayer,2));
            if (!RevivalRules.canPay(serverPlayer.getHealth(), cost)) {
                serverPlayer.sendOverlayMessage(Component.translatable("lastbreath.low_health"));
                return InteractionResult.FAIL;
            }
            targets.put(serverPlayer, nearest.getUUID());
            RitualFeedback.sound(serverPlayer.level(), serverPlayer.position(), net.minecraft.sounds.SoundEvents.BEACON_POWER_SELECT, .25f, 1.2f);
        }
        if (SoulService.ghost(player)) return InteractionResult.SUCCESS;
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    private static boolean eligible(ServerPlayer rescuer, ServerPlayer target) {
        return rescuer.isAlive() && !SoulService.ghost(rescuer) && !rescuer.isSpectator()
                && target != rescuer && target.isAlive() && !target.hasDisconnected() && SoulService.ghost(target)
                && target.level() == rescuer.level() && target.distanceToSqr(rescuer) <= Math.pow(Config.REVIVE_RADIUS.get(), 2);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) { return CHANNEL_TICKS; }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.BOW; }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (!(entity instanceof ServerPlayer player)) return;
        UUID id = targets.get(player);
        ServerPlayer target = id == null ? null : player.level().getServer().getPlayerList().getPlayer(id);
        if (target == null || !eligible(player, target)) {
            targets.remove(player);
            player.stopUsingItem();
            player.sendOverlayMessage(Component.translatable("lastbreath.channel.cancelled"));
            return;
        }
        if (remaining % 5 == 0) {
            RitualFeedback.progress(player, Component.translatable("lastbreath.ritual.rescuing", target.getDisplayName()), CHANNEL_TICKS - remaining, CHANNEL_TICKS);
            RitualFeedback.progress(target, Component.translatable("lastbreath.ritual.rescuer", player.getDisplayName()), CHANNEL_TICKS - remaining, CHANNEL_TICKS);
            RitualFeedback.link(player, target);
        }
        if (remaining % 20 == 0) {
            RitualFeedback.sound(player.level(), target.position(), net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME, .2f, 1.5f - remaining / 150f);
        }
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remaining) {
        if (entity instanceof ServerPlayer player && targets.remove(player) != null)
            player.sendOverlayMessage(Component.translatable("lastbreath.channel.cancelled"));
        return false;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            UUID id = targets.remove(player);
            ServerPlayer target = id == null ? null : player.level().getServer().getPlayerList().getPlayer(id);
            if (target != null && eligible(player, target))
                SoulService.revive(player, id.toString(), player.getUsedItemHand());
        }
        return stack;
    }
}
