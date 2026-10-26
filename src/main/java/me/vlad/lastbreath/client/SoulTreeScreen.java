package me.vlad.lastbreath.client;
import me.vlad.lastbreath.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
@EventBusSubscriber(modid=LastBreath.MODID,value=Dist.CLIENT)
public final class SoulTreeScreen extends AbstractContainerScreen<SoulTreeMenu> {
 private boolean equipment,living;
 private Button study;
 private final java.util.List<Button> gearButtons=new java.util.ArrayList<>();
 private final Button[] nodes=new Button[SpiritTalents.COUNT];
 private final int[] ranks=new int[SpiritTalents.COUNT];
 private final long[] learned=new long[SpiritTalents.COUNT];
 public SoulTreeScreen(SoulTreeMenu m,Inventory i,Component title){super(m,i,title,330,236);}
 @SubscribeEvent public static void register(RegisterMenuScreensEvent e){e.register(LastBreath.SOUL_TREE.get(),SoulTreeScreen::new);}
 public void showLiving(){equipment=false;living=true;updateButtons();}
 public void showEquipment(boolean value){equipment=value;living=false;updateButtons();}
 private int nx(int i){if(i>=20)return 52+(i-20)%4*75;int[] x={38,82,83,126,132};return 165+(i/5%2==0?-1:1)*x[i%5];}
 private int ny(int i){if(i>=20)return i<24?90:170;int[] y={26,15,52,36,77};return 130+(i/5<2?-1:1)*y[i%5];}
 @Override protected void init(){
  super.init();gearButtons.clear();
  study=addRenderableWidget(new SoulButton(Button.builder(Component.translatable("lastbreath.scroll.study"),b->{if(minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,40);}).bounds(leftPos+7,topPos+5,109,16)));
  addRenderableWidget(new SoulButton(Button.builder(Component.translatable("lastbreath.gear.tree"),b->{equipment=false;living=false;updateButtons();}).bounds(leftPos+119,topPos+5,65,16)));
  addRenderableWidget(new SoulButton(Button.builder(Component.translatable("lastbreath.gear.tab"),b->{equipment=true;updateButtons();}).bounds(leftPos+250,topPos+5,73,16)));
  addRenderableWidget(new SoulButton(Button.builder(Component.translatable("lastbreath.tree.living"),b->{equipment=false;living=true;updateButtons();}).bounds(leftPos+187,topPos+5,60,16)));
  gearButton(20,224,44,98,"lastbreath.gear.next");
  for(int c=0;c<3;c++)gearButton(22+c,12+c*104,128,98,"lastbreath.gear.charge."+c);
  gearButton(25,12,155,306,"lastbreath.gear.upgrade");
  gearButton(26,12,183,150,"lastbreath.gear.unbound");gearButton(27,168,183,150,"lastbreath.gear.unbound");
  for(int i=0;i<SpiritTalents.COUNT;i++){
   final int id=i;
   nodes[i]=addRenderableWidget(new Button(Button.builder(Component.translatable("lastbreath.talent."+i),b->{if(minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,100+id);}).bounds(leftPos+nx(i)-13,topPos+ny(i)-13,26,26)){
    @Override protected void extractContents(GuiGraphicsExtractor g,int mx,int my,float delta){
     var t=menu.talents();int color=TalentWheel.color(id);
     if(isHoveredOrFocused())color=0xfff0d39a;
     g.fill(getX()-1,getY()-1,getX()+27,getY()+27,color);g.fill(getX()+1,getY()+1,getX()+25,getY()+25,0xff211b18);
     g.blit(icon(id),getX()+3,getY()+2,getX()+23,getY()+22,0,1,0,1);
     if(!t.has(id)&&!t.unlocked(id))g.fill(getX()+1,getY()+1,getX()+25,getY()+25,0x88171b22);
     if(t.worn(id))g.fill(getX()+3,getY()+23,getX()+23,getY()+24,0xfff7e7a7);
    }
   });
   ranks[i]=menu.talents().has(i)?1:0;
  }updateButtons();
 }
 private void gearButton(int id,int x,int y,int width,String key){var b=addRenderableWidget(new SoulButton(Button.builder(Component.translatable(key),button->{if(minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,id);}).bounds(leftPos+x,topPos+y,width,20)));gearButtons.add(b);}
 private void updateGear(){
  int type=menu.gear(0),level=menu.gear(1),branch=menu.gear(2),charge=menu.gear(3);var p=menu.progress();
  for(int i=0;i<gearButtons.size();i++){
   var b=gearButtons.get(i);b.visible=equipment;b.active=equipment&&type>0&&Config.value(Config.GEAR_ENABLED);
   if(i==0){b.visible=equipment&&type>0;b.active=b.visible;continue;}
   if(i<=3){int c=i-1;b.active&=charge+SoulGear.chargeValue(c)<=Config.value(Config.GEAR_CAPACITY)&&(c==0?p.souls():c==1?p.red():p.purple())>0;
    b.setTooltip(Tooltip.create(Component.translatable("lastbreath.gear.charge.tip",SoulGear.chargeValue(c))));
   }else if(i==4){int[] cost=SoulGear.price(level);b.active&=level<3&&p.souls()>=cost[0]&&p.red()>=cost[1]&&p.purple()>=cost[2];
    b.setMessage(Component.translatable(level>=3?"lastbreath.tree.max":"lastbreath.gear.upgrade"));b.setTooltip(Tooltip.create(Component.translatable("lastbreath.tree.price",cost[0],cost[1],cost[2]).append("\n").append(Component.translatable("lastbreath.gear.upgrade.tip"))));
   }else{int choice=i-4;int price=(int)Math.ceil(5*Config.value(Config.GEAR_COST));b.setMessage(Component.translatable(SoulGear.branchKey(type,choice)));b.active&=level>0&&branch==0&&p.red()>=price;
    b.setTooltip(Tooltip.create(Component.translatable(SoulGear.branchKey(type,choice)+".desc").append("\n").append(SoulGear.details(type,choice)).append("\n").append(Component.translatable("lastbreath.gear.branch.price",price))));}
  }
 }
 private net.minecraft.resources.Identifier icon(int id){return TalentWheel.icon(id);}
 private void updateButtons(){if(study!=null)study.visible=!equipment;updateGear();var t=menu.talents();var bank=menu.progress();for(int i=0;i<SpiritTalents.COUNT;i++)if(nodes[i]!=null){
  int rank=t.has(i)?1:0;if(rank>ranks[i])learned[i]=System.currentTimeMillis();ranks[i]=rank;
  int[] price=TalentService.cost(i);
  nodes[i].visible=!equipment&&(living==(i>=20));nodes[i].active=nodes[i].visible&&TalentService.enabled(i)&&(t.has(i)?SpiritTalents.active(i):t.unlocked(i)&&bank.souls()>=price[0]&&bank.red()>=price[1]&&bank.purple()>=price[2]);
  nodes[i].setTooltip(Tooltip.create(Component.translatable("lastbreath.talent."+i).append("\n").append(Component.translatable("lastbreath.talent."+i+".desc")).append("\n").append(Component.translatable("lastbreath.tree.price",price[0],price[1],price[2])).append("\n").append(Component.translatable(!TalentService.enabled(i)?"lastbreath.talent.disabled":t.has(i)?"lastbreath.talent.equip":t.unlocked(i)?"lastbreath.tree.buy":"lastbreath.talent.locked"))));
 }}
 @Override protected void containerTick(){updateButtons();}
 private void line(GuiGraphicsExtractor g,int ax,int ay,int bx,int by,int color){int steps=Math.max(Math.abs(bx-ax),Math.abs(by-ay));for(int j=0;j<=steps;j++){int x=ax+(bx-ax)*j/Math.max(1,steps),y=ay+(by-ay)*j/Math.max(1,steps);g.fill(x,y,x+2,y+2,color);}}
 @Override public void extractContents(GuiGraphicsExtractor g,int mx,int my,float delta){
  int x=leftPos,y=topPos;g.fill(x-2,y-2,x+332,y+238,0xffa98745);g.fill(x,y,x+330,y+236,0xff211b17);
  for(int row=0;row<6;row++){g.fill(x+4,y+4+row*38,x+326,y+40+row*38,row%2==0?0xff35271c:0xff302319);g.fill(x+4,y+40+row*38,x+326,y+41+row*38,0xff1a1411);}
  if(!equipment&&!living)for(int i=0;i<20;i++){
   int parent=i%5==0?-1:i/5*5+(i%5==4?3:i%5==3?2:0);int ax=parent<0?165:nx(parent),ay=parent<0?130:ny(parent);
   line(g,x+ax,y+ay,x+nx(i),y+ny(i),menu.talents().has(i)?0xff318fa8:0xff77603c);
   if(Config.value(Config.ANIMATIONS)&&(menu.talents().has(i)||System.currentTimeMillis()-learned[i]<1000)){
    double t=(System.currentTimeMillis()%1800)/1800.0;int px=x+ax+(int)((nx(i)-ax)*t),py=y+ay+(int)((ny(i)-ay)*t);g.fill(px-1,py-1,px+3,py+3,0xff81e7ff);
   }
  }
  if(!equipment&&living)for(int i=24;i<27;i++)line(g,x+nx(i-4),y+ny(i-4),x+nx(i),y+ny(i),0xff77603c);
  if(!equipment&&living)line(g,x+nx(26),y+ny(26),x+nx(27),y+ny(27),0xffa88748);
  if(!equipment&&!living){g.fill(x+160,y+125,x+172,y+137,0xffbf9950);g.fill(x+163,y+128,x+169,y+134,0xff55cdf2);}
  else if(equipment&&menu.gear(0)>0){g.blit(net.minecraft.resources.Identifier.parse("lastbreath:textures/item/"+SoulGear.itemName(menu.gear(0))+".png"),x+20,y+68,x+60,y+108,0,1,0,1);g.fill(x+76,y+110,x+310,y+115,0xff141821);g.fill(x+76,y+110,x+76+234*Math.min(menu.gear(3),Config.value(Config.GEAR_CAPACITY))/Config.value(Config.GEAR_CAPACITY),y+115,0xff45c7e4);}
  super.extractContents(g,mx,my,delta);
 }
 @Override protected void extractLabels(GuiGraphicsExtractor g,int mx,int my){
  if(equipment)g.text(font,title,9,8,0xffedd4a0);var p=menu.progress();g.text(font,Component.translatable("lastbreath.souls.three",p.souls(),p.red(),p.purple()),9,23,0xffa5d9ed);
  if(equipment){
   int type=menu.gear(0);g.text(font,Component.translatable(type==0?"lastbreath.gear.empty":"item.lastbreath."+SoulGear.itemName(type)),12,49,0xffecd2a2);
   if(type>0){g.text(font,Component.translatable("lastbreath.gear.level",menu.gear(1),menu.gear(4)),76,70,0xffd8c8a0);g.text(font,Component.translatable(SoulGear.branchKey(type,menu.gear(2))),76,84,0xffd6b568);g.text(font,Component.translatable("lastbreath.gear.charge",menu.gear(3),Config.value(Config.GEAR_CAPACITY)),76,98,0xff81dbef);}
   g.text(font,Component.translatable("lastbreath.gear.hint"),12,220,0xffc2b095);return;
  }
  g.centeredText(font,Component.translatable("lastbreath.talent.loadout"),165,39,0xffd6bc8d);
  g.text(font,Component.translatable("lastbreath.talent.hint"),9,220,0xffc2b095);
 }
}
