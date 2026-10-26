package me.vlad.lastbreath;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

public final class ReturnAltarItem extends BlockItem {
    public ReturnAltarItem(Properties properties) { super(LastBreath.RETURN_ALTAR.get(), properties); }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        lines.accept(Component.translatable("lastbreath.tooltip.altar").withStyle(ChatFormatting.AQUA));
        lines.accept(Component.translatable("lastbreath.tooltip.altar_withdraw").withStyle(ChatFormatting.GRAY));
    }
}
