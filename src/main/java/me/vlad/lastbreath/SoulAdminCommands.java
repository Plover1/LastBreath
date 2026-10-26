package me.vlad.lastbreath;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

/** Test helpers: admin-only, use the same persistent balance attachment as absorption. */
public final class SoulAdminCommands {
    private SoulAdminCommands() {}
    public static LiteralArgumentBuilder<CommandSourceStack> tree(){
        var root=Commands.literal("souls").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));
        for(String operation:new String[]{"add","set","give"}){
            var op=Commands.literal(operation);var target=Commands.argument("players",EntityArgument.players());
            for(int type=0;type<3;type++){
                final int category=type;
                target.then(Commands.literal(new String[]{"blue","red","purple"}[type])
                    .then(Commands.argument("amount",IntegerArgumentType.integer(operation.equals("set")?0:1,operation.equals("give")?64:SoulProgress.CAP)).executes(c->{
                        int amount=IntegerArgumentType.getInteger(c,"amount"),changed=0;
                        for(var player:EntityArgument.getPlayers(c,"players")){
                            if(operation.equals("give")){
                                var item=category==0?LastBreath.MOB_SOUL.get():category==1?LastBreath.RED_SOUL.get():LastBreath.PURPLE_SOUL.get();
                                var stack=new net.minecraft.world.item.ItemStack(item,amount);
                                player.getInventory().add(stack);if(!stack.isEmpty())player.level().addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(player.level(),player.getX(),player.getY()+.5,player.getZ(),stack));
                                player.inventoryMenu.broadcastChanges();
                                c.getSource().sendSuccess(()->Component.translatable("lastbreath.command.soul_items",player.getName(),amount,item.getName(item.getDefaultInstance())),true);
                                changed++;continue;
                            }
                            var before=player.getData(LastBreath.PROGRESS);int old=before.balance(category);
                            int value=operation.equals("set")?amount:Math.min(SoulProgress.CAP,old+amount);
                            var next=before.balances(category==0?value:before.souls(),category==1?value:before.red(),category==2?value:before.purple());
                            player.setData(LastBreath.PROGRESS,next);player.containerMenu.broadcastChanges();
                            c.getSource().sendSuccess(()->Component.translatable("lastbreath.command.souls",player.getName(),new String[]{"blue","red","purple"}[category],old,value),true);
                            changed++;
                        }return changed;
                    })));
            }op.then(target);root.then(op);
        }return root;
    }
}
