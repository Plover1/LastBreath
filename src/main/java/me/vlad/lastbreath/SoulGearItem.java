package me.vlad.lastbreath;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public final class SoulGearItem extends Item {
    public SoulGearItem(Properties properties){super(properties);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,TooltipDisplay display,Consumer<Component> lines,TooltipFlag flag){
        var data=SoulGear.data(stack);
        lines.accept(Component.translatable("lastbreath.gear.stats",data.level(),data.charge(),Config.value(Config.GEAR_CAPACITY)).withStyle(ChatFormatting.AQUA));
        lines.accept(Component.translatable(SoulGear.branchKey(SoulGear.type(stack),data.branch())).withStyle(ChatFormatting.GOLD));
        if(data.branch()>0)lines.accept(SoulGear.details(SoulGear.type(stack),data.branch()).withStyle(ChatFormatting.GRAY));
        lines.accept(Component.translatable("lastbreath.gear.tooltip").withStyle(ChatFormatting.GRAY));
    }
    @Override public boolean isBarVisible(ItemStack stack){return SoulGear.data(stack).charge()>0||super.isBarVisible(stack);}
    @Override public int getBarWidth(ItemStack stack){return SoulGear.data(stack).charge()>0?Math.min(13,Math.round(13f*SoulGear.data(stack).charge()/Config.value(Config.GEAR_CAPACITY))):super.getBarWidth(stack);}
    @Override public int getBarColor(ItemStack stack){return SoulGear.data(stack).charge()>0?0x47cbea:super.getBarColor(stack);}
}
