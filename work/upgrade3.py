exec(open(__file__.replace('upgrade3.py','upgrade.py'),encoding='utf-8').read().split("p=root/'gradle.properties'")[0])
edit('LastBreath.java','craftRemainder(EMPTY_SOUL_JAR.get())','craftRemainder(LastBreath.EMPTY_SOUL_JAR.get())')
edit('SoulHarvest.java','mob.getType().is(','mob.getType().builtInRegistryHolder().is(')
edit('AdvancedSkills.java','stand.setInvulnerable(true);','stand.addTag("lastbreath_echo");')
edit('AdvancedSkills.java',' @SubscribeEvent public static void logout',''' @SubscribeEvent public static void invulnerable(net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event){if(event.getEntity().getTags().contains("lastbreath_echo"))event.setCanceled(true);}
 @SubscribeEvent public static void load(net.neoforged.neoforge.event.entity.EntityJoinLevelEvent event){if(event.loadedFromDisk()&&event.getEntity().getTags().contains("lastbreath_echo")){event.setCanceled(true);event.getEntity().discard();}}
 @SubscribeEvent public static void logout''')
write('client/SoulTreeScreen.java','''package me.vlad.lastbreath.client;
import me.vlad.lastbreath.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
@EventBusSubscriber(modid=LastBreath.MODID,value=Dist.CLIENT)
public final class SoulTreeScreen extends AbstractContainerScreen<SoulTreeMenu> {
 private final Button[] nodes=new Button[9];
 private final int[] ranks=new int[9];
 private final long[] learned=new long[9];
 public SoulTreeScreen(SoulTreeMenu m,Inventory i,Component title){super(m,i,title,330,236);}
 @SubscribeEvent public static void register(RegisterMenuScreensEvent e){e.register(LastBreath.SOUL_TREE.get(),SoulTreeScreen::new);}
 private int nx(int i){return 60+(i%3)*105;}
 private int ny(int i){return 82+(i/3)*51;}
 @Override protected void init(){
  super.init();
  for(int i=0;i<9;i++){
   final int id=i;
   nodes[i]=addRenderableWidget(new Button(Button.builder(Component.translatable("lastbreath.skill."+i),b->{if(minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,id);}).bounds(leftPos+nx(i)-13,topPos+ny(i)-13,26,26)){
    @Override protected void extractContents(GuiGraphicsExtractor g,int mx,int my,float delta){
     var p=menu.progress();int color=p.rank(id)>0?0xff48bde0:p.unlocked(id)?0xffc5a358:0xff655646;
     if(isHoveredOrFocused())color=0xfff0d39a;
     g.fill(getX()-1,getY()-1,getX()+27,getY()+27,color);g.fill(getX()+1,getY()+1,getX()+25,getY()+25,0xff211b18);
     g.fakeItem(icon(id),getX()+5,getY()+4);
     if(p.rank(id)>0)g.fill(getX()+3,getY()+23,getX()+3+20*p.rank(id)/SoulProgress.max(id),getY()+24,0xff4bd9fa);
    }
   });
   ranks[i]=menu.progress().rank(i);
  }updateButtons();
 }
 private ItemStack icon(int id){return new ItemStack(switch(id){case 0->LastBreath.EXECUTIONER_SWORD.get();case 1->Items.APPLE;case 2->LastBreath.RETURN_HEART.get();case 3->Items.ENDER_EYE;case 4->Items.FEATHER;case 5->Items.SHIELD;case 6->Items.ARMOR_STAND;case 7->LastBreath.PURPLE_SOUL.get();default->Items.LEAD;});}
 private void updateButtons(){var p=menu.progress();for(int i=0;i<9;i++)if(nodes[i]!=null){
  if(p.rank(i)>ranks[i])learned[i]=System.currentTimeMillis();ranks[i]=p.rank(i);
  nodes[i].active=p.unlocked(i)&&p.rank(i)<SoulProgress.max(i)&&p.affordable(i);
  nodes[i].setTooltip(Tooltip.create(Component.translatable("lastbreath.skill."+i).append("\\n").append(Component.translatable("lastbreath.skill."+i+".desc")).append("\\n").append(Component.translatable("lastbreath.tree.price",p.cost(i),p.redCost(i),p.purpleCost(i))).append("\\n").append(Component.translatable(p.rank(i)>=SoulProgress.max(i)?"lastbreath.tree.max":p.unlocked(i)?"lastbreath.tree.buy":"lastbreath.tree.requirements"))));
 }}
 @Override protected void containerTick(){updateButtons();}
 private void line(GuiGraphicsExtractor g,int ax,int ay,int bx,int by,int color){int steps=Math.max(Math.abs(bx-ax),Math.abs(by-ay));for(int j=0;j<=steps;j++){int x=ax+(bx-ax)*j/Math.max(1,steps),y=ay+(by-ay)*j/Math.max(1,steps);g.fill(x,y,x+2,y+2,color);}}
 @Override public void extractContents(GuiGraphicsExtractor g,int mx,int my,float delta){
  int x=leftPos,y=topPos;g.fill(x-2,y-2,x+332,y+238,0xffa98745);g.fill(x,y,x+330,y+236,0xff211b17);
  for(int row=0;row<6;row++){g.fill(x+4,y+4+row*38,x+326,y+40+row*38,row%2==0?0xff35271c:0xff302319);g.fill(x+4,y+40+row*38,x+326,y+41+row*38,0xff1a1411);}
  for(int i=0;i<9;i++){
   int ax=i<3?165:nx(i),ay=i<3?45:ny(i)-51;
   line(g,x+ax,y+ay,x+nx(i),y+ny(i),menu.progress().rank(i)>0?0xff318fa8:0xff77603c);
   if(Config.ANIMATIONS.get()&&(menu.progress().rank(i)>0||System.currentTimeMillis()-learned[i]<1000)){
    double t=(System.currentTimeMillis()%1800)/1800.0;int px=x+ax+(int)((nx(i)-ax)*t),py=y+ay+(int)((ny(i)-ay)*t);g.fill(px-1,py-1,px+3,py+3,0xff81e7ff);
   }
  }
  g.fill(x+160,y+40,x+172,y+52,0xffbf9950);g.fill(x+163,y+43,x+169,y+49,0xff55cdf2);
  super.extractContents(g,mx,my,delta);
 }
 @Override protected void extractLabels(GuiGraphicsExtractor g,int mx,int my){
  g.text(font,title,9,8,0xffedd4a0);var p=menu.progress();g.text(font,Component.translatable("lastbreath.souls.three",p.souls(),p.red(),p.purple()),9,23,0xffa5d9ed);
  for(int i=0;i<3;i++)g.centeredText(font,Component.translatable("lastbreath.branch."+i),nx(i),56,0xffd6bc8d);
  for(int i=0;i<9;i++)g.centeredText(font,p.rank(i)+"/"+SoulProgress.max(i),nx(i),ny(i)+16,0xffc9b994);
  g.text(font,Component.translatable("lastbreath.tree.hint"),9,220,0xffc2b095);
 }
}
''')
