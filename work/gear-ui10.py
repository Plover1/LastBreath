from pathlib import Path
p=Path('src/main/java/me/vlad/lastbreath/client/SoulTreeScreen.java');s=p.read_text().replace(' private final Button[] nodes',' private boolean equipment;\n private final java.util.List<Button> gearButtons=new java.util.ArrayList<>();\n private final Button[] nodes',1)
s=s.replace('  super.init();','''  super.init();gearButtons.clear();
  addRenderableWidget(Button.builder(Component.translatable("lastbreath.gear.tree"),b->{equipment=false;updateButtons();}).bounds(leftPos+164,topPos+5,75,16).build());
  addRenderableWidget(Button.builder(Component.translatable("lastbreath.gear.tab"),b->{equipment=true;updateButtons();}).bounds(leftPos+241,topPos+5,81,16).build());
  gearButton(20,224,44,98,"lastbreath.gear.next");
  for(int c=0;c<3;c++)gearButton(22+c,12+c*104,128,98,"lastbreath.gear.charge."+c);
  gearButton(25,12,155,306,"lastbreath.gear.upgrade");
  gearButton(26,12,183,150,"lastbreath.gear.unbound");gearButton(27,168,183,150,"lastbreath.gear.unbound");''')
s=s.replace(' private net.minecraft.resources.Identifier icon', ''' private void gearButton(int id,int x,int y,int width,String key){var b=addRenderableWidget(Button.builder(Component.translatable(key),button->{if(minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,id);}).bounds(leftPos+x,topPos+y,width,20).build());gearButtons.add(b);}
 private void updateGear(){
  int type=menu.gear(0),level=menu.gear(1),branch=menu.gear(2),charge=menu.gear(3);var p=menu.progress();
  for(int i=0;i<gearButtons.size();i++){
   var b=gearButtons.get(i);b.visible=equipment;b.active=equipment&&type>0&&Config.value(Config.GEAR_ENABLED);
   if(i==0){b.active=equipment;continue;}
   if(i<=3){int c=i-1;b.active&=charge+SoulGear.chargeValue(c)<=Config.value(Config.GEAR_CAPACITY)&&(c==0?p.souls():c==1?p.red():p.purple())>0;
    b.setTooltip(Tooltip.create(Component.translatable("lastbreath.gear.charge.tip",SoulGear.chargeValue(c))));
   }else if(i==4){int[] cost=SoulGear.price(level);b.active&=level<3&&p.souls()>=cost[0]&&p.red()>=cost[1]&&p.purple()>=cost[2];
    b.setMessage(Component.translatable(level>=3?"lastbreath.tree.max":"lastbreath.gear.upgrade"));b.setTooltip(Tooltip.create(Component.translatable("lastbreath.tree.price",cost[0],cost[1],cost[2]).append("\\n").append(Component.translatable("lastbreath.gear.upgrade.tip"))));
   }else{int choice=i-4;int price=(int)Math.ceil(5*Config.value(Config.GEAR_COST));b.setMessage(Component.translatable(SoulGear.branchKey(type,choice)));b.active&=level>0&&branch==0&&p.red()>=price;
    b.setTooltip(Tooltip.create(Component.translatable(SoulGear.branchKey(type,choice)+".desc").append("\\n").append(Component.translatable("lastbreath.gear.branch.price",price))));}
  }
 }
 private net.minecraft.resources.Identifier icon''')
s=s.replace(' private void updateButtons(){var p=', ' private void updateButtons(){updateGear();var p=')
s=s.replace('  nodes[i].active=p.unlocked', '  nodes[i].visible=!equipment;nodes[i].active=!equipment&&p.unlocked')
s=s.replace('  for(int i=0;i<9;i++){\n   int ax=', '  if(!equipment)for(int i=0;i<9;i++){\n   int ax=')
s=s.replace('  g.fill(x+160,y+40', '  if(!equipment){g.fill(x+160,y+40')
s=s.replace('y+49,0xff55cdf2);','y+49,0xff55cdf2);}\n  else if(menu.gear(0)>0){g.blit(net.minecraft.resources.Identifier.parse("lastbreath:textures/item/"+(menu.gear(0)==1?"soul_devourer":"soul_cloak")+".png"),x+20,y+68,x+60,y+108,0,1,0,1);g.fill(x+76,y+110,x+310,y+115,0xff141821);g.fill(x+76,y+110,x+76+234*Math.min(menu.gear(3),Config.value(Config.GEAR_CAPACITY))/Config.value(Config.GEAR_CAPACITY),y+115,0xff45c7e4);}')
s=s.replace('  for(int i=0;i<3;i++)g.centeredText', '''  if(equipment){
   int type=menu.gear(0);g.text(font,Component.translatable(type==0?"lastbreath.gear.empty":"item.lastbreath."+(type==1?"soul_devourer":"soul_cloak")),12,49,0xffecd2a2);
   if(type>0){g.text(font,Component.translatable("lastbreath.gear.level",menu.gear(1),menu.gear(4)),76,70,0xffd8c8a0);g.text(font,Component.translatable(SoulGear.branchKey(type,menu.gear(2))),76,84,0xffd6b568);g.text(font,Component.translatable("lastbreath.gear.charge",menu.gear(3),Config.value(Config.GEAR_CAPACITY)),76,98,0xff81dbef);}
   g.text(font,Component.translatable("lastbreath.gear.hint"),12,220,0xffc2b095);return;
  }
  for(int i=0;i<3;i++)g.centeredText''')
p.write_text(s)
p=Path('src/main/java/me/vlad/lastbreath/SoulGear.java');s=p.read_text().replace('||e.getNewDamage()<=0)return;\n        var s=p.getItemBySlot(EquipmentSlot.CHEST);int cost=Config.GEAR_SHROUD_COST', '||e.getDamageContainer().getNewDamage()<=0)return;\n        var s=p.getItemBySlot(EquipmentSlot.CHEST);int cost=Config.GEAR_SHROUD_COST');p.write_text(s)
