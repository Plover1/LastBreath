package me.vlad.lastbreath;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class MobSoulItem extends DescribedItem {
    private final int type;
    public MobSoulItem(Properties properties) { this(properties,0); }
    public MobSoulItem(Properties properties,int type) { super(properties,"lastbreath.tooltip.mob_soul");this.type=type; }
    @Override public int getUseDuration(net.minecraft.world.item.ItemStack stack,net.minecraft.world.entity.LivingEntity entity){return Config.RITUAL_SECONDS.get()*20;}
    @Override public net.minecraft.world.item.ItemUseAnimation getUseAnimation(net.minecraft.world.item.ItemStack stack){return net.minecraft.world.item.ItemUseAnimation.BOW;}
    @Override public void onUseTick(Level level,net.minecraft.world.entity.LivingEntity entity,net.minecraft.world.item.ItemStack stack,int remaining){if(entity instanceof ServerPlayer p)RitualAccess.channel(p,remaining);}
    @Override public net.minecraft.world.item.ItemStack finishUsingItem(net.minecraft.world.item.ItemStack stack,Level level,net.minecraft.world.entity.LivingEntity entity){if(entity instanceof ServerPlayer p)RitualAccess.finish(p);return stack;}
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer server && player.isAlive() && !player.isSpectator()) {
            var progress=player.getData(LastBreath.PROGRESS);
            if (progress.balance(type)>=SoulProgress.CAP) { server.sendOverlayMessage(Component.translatable("lastbreath.souls.full")); return InteractionResult.FAIL; }
            player.getItemInHand(hand).shrink(1);
            player.setData(LastBreath.PROGRESS,progress.absorb(type));
            server.sendOverlayMessage(Component.translatable("lastbreath.souls.absorbed",progress.balance(type)+1));
            AdvancedSkills.burst(server, type);
            RitualFeedback.sound(server.level(),player.position(),net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME,.3f,1.4f);
        }
        return InteractionResult.SUCCESS;
    }
}
