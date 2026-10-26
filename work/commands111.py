from pathlib import Path
import json
p=Path('src/main/java/me/vlad/lastbreath/SoulAdminCommands.java');s=p.read_text(encoding='utf-8').replace('{"add","set"}','{"add","set","give"}').replace('IntegerArgumentType.integer(operation.equals("add")?1:0,SoulProgress.CAP)','IntegerArgumentType.integer(operation.equals("set")?0:1,operation.equals("give")?64:SoulProgress.CAP)')
s=s.replace('                            var before=player.getData', '''                            if(operation.equals("give")){
                                var item=category==0?LastBreath.MOB_SOUL.get():category==1?LastBreath.RED_SOUL.get():LastBreath.PURPLE_SOUL.get();
                                var stack=new net.minecraft.world.item.ItemStack(item,amount);
                                player.getInventory().add(stack);if(!stack.isEmpty())player.drop(stack,false);
                                player.inventoryMenu.broadcastChanges();
                                c.getSource().sendSuccess(()->Component.translatable("lastbreath.command.soul_items",player.getName(),amount,item.getName()),true);
                                changed++;continue;
                            }
                            var before=player.getData''');p.write_text(s,encoding='utf-8')
for loc,vals in [('uk_ua',['Душі %s (%s): %s → %s','Гравцю %s видано %s × %s']),('en_us',['Souls for %s (%s): %s → %s','Gave %s: %s × %s'])]:
 p=Path('src/main/resources/assets/lastbreath/lang')/(loc+'.json');v=json.loads(p.read_text(encoding='utf-8'));v['lastbreath.command.souls']=vals[0];v['lastbreath.command.soul_items']=vals[1];p.write_text(json.dumps(v,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
p=Path('src/gameTest/java/me/vlad/lastbreath/LastBreathGameTests.java');s=p.read_text(encoding='utf-8');needle='                check(dispatcher.execute("lastbreath ghost",'
idx=s.index(needle)
s=s[:idx]+'''                var originalProgress=target.getData(LastBreath.PROGRESS);
                var admin=source.withPermission(net.minecraft.server.permissions.PermissionSet.ALL_PERMISSIONS);
                check(dispatcher.execute("lastbreath souls set @s blue 29999",admin)==1,"Admin can set soul balance");
                dispatcher.execute("lastbreath souls add @s blue 10",admin);
                check(target.getData(LastBreath.PROGRESS).souls()==30000,"Soul balance clamps at cap");
                dispatcher.execute("lastbreath souls set @s red 12",admin);dispatcher.execute("lastbreath souls set @s purple 3",admin);
                check(target.getData(LastBreath.PROGRESS).red()==12&&target.getData(LastBreath.PROGRESS).purple()==3,"All three currencies supported");
                boolean denied=false;try{dispatcher.execute("lastbreath souls add @s blue 1",source.withPermission(net.minecraft.server.permissions.PermissionSet.NO_PERMISSIONS));}catch(com.mojang.brigadier.exceptions.CommandSyntaxException expected){denied=true;}
                check(denied,"Non-admin soul commands denied");
                boolean negative=false;try{dispatcher.execute("lastbreath souls set @s blue -1",admin);}catch(com.mojang.brigadier.exceptions.CommandSyntaxException expected){negative=true;}
                check(negative,"Negative balances rejected");
                int itemsBefore=target.getInventory().countItem(LastBreath.RED_SOUL.get());
                dispatcher.execute("lastbreath souls give @s red 2",admin);
                check(target.getInventory().countItem(LastBreath.RED_SOUL.get())==itemsBefore+2,"Physical soul command gives items");
                target.setData(LastBreath.PROGRESS,originalProgress);
'''+s[idx:];p.write_text(s,encoding='utf-8')
