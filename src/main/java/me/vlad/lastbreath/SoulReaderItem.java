package me.vlad.lastbreath;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class SoulReaderItem extends DescribedItem {
    public SoulReaderItem(Properties properties) { super(properties,"lastbreath.tooltip.reader"); }
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer server) {
            if(player.isShiftKeyDown()){
                var p=server.getData(LastBreath.PROGRESS);server.sendOverlayMessage(Component.translatable("lastbreath.souls.three",p.souls(),p.red(),p.purple()));
                if(hand==InteractionHand.OFF_HAND)SoulService.list(server);
            }else TalentService.activate(server);
        }
        return InteractionResult.SUCCESS;
    }
}
