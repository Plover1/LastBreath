package me.vlad.lastbreath;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class DescribedItem extends Item {
    private final String tooltip;
    public DescribedItem(Properties properties, String tooltip) { super(properties); this.tooltip = tooltip; }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        lines.accept(Component.translatable(tooltip).withStyle(ChatFormatting.AQUA));
    }
}
