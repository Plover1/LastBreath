package me.vlad.lastbreath.client;
import me.vlad.lastbreath.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid=LastBreath.MODID,value=Dist.CLIENT)
public final class TalentWheel {
 private static final Identifier[] ICONS=java.util.stream.IntStream.range(0,SpiritTalents.COUNT).mapToObj(i->Identifier.fromNamespaceAndPath("lastbreath","textures/gui/abilities/"+i+".png")).toArray(Identifier[]::new);
 private static int lastSelected=-1;private static long selectedAt;
 private static final KeyMapping.Category CATEGORY=new KeyMapping.Category(Identifier.fromNamespaceAndPath("lastbreath","abilities"));
 public static final KeyMapping KEY=new KeyMapping("key.lastbreath.wheel",InputConstants.KEY_LALT,CATEGORY);
 @SubscribeEvent public static void keys(RegisterKeyMappingsEvent e){e.registerCategory(CATEGORY);e.register(KEY);}
 @SubscribeEvent public static void tick(ClientTickEvent.Post e){var mc=Minecraft.getInstance();if(mc.player!=null&&mc.gui.screen()==null&&KEY.consumeClick()&&KEY.isDown()&&TalentService.reader(mc.player))mc.gui.setScreen(new Wheel());}
 public static Identifier icon(int id){return ICONS[Math.clamp(id,0,SpiritTalents.COUNT-1)];}
 public static int color(int id){return SpiritTalents.gold(id)?0xffe7bb50:SpiritTalents.active(id)?0xff41c9e8:0xff6967cf;}
 @SubscribeEvent public static void hud(RegisterGuiLayersEvent e){e.registerAboveAll(Identifier.fromNamespaceAndPath("lastbreath","ability"),(g,delta)->{
  var mc=Minecraft.getInstance();if(mc.player==null||mc.gui.hud.isHidden()||!TalentService.reader(mc.player))return;var t=mc.player.getData(LastBreath.TALENTS);int id=t.selected();if(!t.worn(id))return;
  if(id!=lastSelected){lastSelected=id;selectedAt=System.currentTimeMillis();}
  int x=mc.getWindow().getGuiScaledWidth()-43,y=mc.getWindow().getGuiScaledHeight()-51;
  g.fill(x-3,y-3,x+31,y+31,0xee30231b);g.fill(x-2,y+29,x+30,y+31,color(id));g.blit(icon(id),x+4,y+3,x+24,y+23,0,1,0,1);
  if(System.currentTimeMillis()-selectedAt<2500){var name=Component.translatable("lastbreath.talent."+id);g.text(mc.font,name,x+30-mc.font.width(name),y-14,0xffe9dec7);}
  long remaining=Math.max(0,t.cooldown()-mc.level.getGameTime());if(remaining>0)g.text(mc.font,Component.literal(""+((remaining+19)/20)),x+8,y+11,0xffffffff);
  else if(!TalentService.enabled(id)||!TalentService.form(mc.player,id))g.text(mc.font,Component.literal("×"),x+22,y,0xffff7777);
 });}
 public static final class Wheel extends Screen {
  private final java.util.List<Integer> skills=new java.util.ArrayList<>();private int hover=-1;private boolean canceled;
  public Wheel(){super(Component.translatable("lastbreath.wheel.title"));}
  @Override protected void init(){skills.clear();if(minecraft.player!=null){var t=minecraft.player.getData(LastBreath.TALENTS);for(int i=0;i<SpiritTalents.COUNT;i++)if(t.worn(i)&&SpiritTalents.active(i))skills.add(i);}}
  @Override public boolean isPauseScreen(){return false;}
  @Override public boolean keyPressed(KeyEvent e){if(e.key()==InputConstants.KEY_ESCAPE){canceled=true;onClose();return true;}return super.keyPressed(e);}
  private void choose(){if(!canceled&&hover>=0)net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(new TalentNetwork.Select(hover));onClose();}
  @Override public boolean keyReleased(KeyEvent e){if(KEY.matches(e)){choose();return true;}return super.keyReleased(e);}
  @Override public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent e){if(KEY.matchesMouse(e)){choose();return true;}return super.mouseReleased(e);}
  @Override public void tick(){if(minecraft.player==null||!TalentService.reader(minecraft.player)){canceled=true;onClose();return;}if(KEY.getKey().getType()==InputConstants.Type.KEYBOARD&&!InputConstants.isKeyDown(KEY.getKey().getValue()))choose();}
  @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float delta){
   int cx=width/2,cy=height/2;g.fill(0,0,width,height,0x66101920);hover=-1;
   double dx=mx-cx,dy=my-cy;if(dx*dx+dy*dy>625&&dx*dx+dy*dy<11664&&!skills.isEmpty()){
    double a=Math.atan2(dy,dx)+Math.PI/2;if(a<0)a+=Math.PI*2;int n=(int)Math.floor((a+Math.PI/skills.size())/(Math.PI*2/skills.size()))%skills.size();hover=skills.get(n);
   }
   for(int row=-108;row<=108;row++){int outer=(int)Math.sqrt(108*108-row*row);g.fill(cx-outer,cy+row,cx+outer+1,cy+row+1,0xffb29150);if(Math.abs(row)<=104){int inner=(int)Math.sqrt(104*104-row*row);g.fill(cx-inner,cy+row,cx+inner+1,cy+row+1,((row+108)/12)%2==0?0xff38291d:0xff302219);}}
   for(int row=-38;row<=38;row++){int edge=(int)Math.sqrt(38*38-row*row);g.fill(cx-edge,cy+row,cx+edge+1,cy+row+1,0xff171b1c);}
   for(int n=0;n<skills.size();n++){int id=skills.get(n);double a=n*Math.PI*2/skills.size()-Math.PI/2;int x=cx+(int)(Math.cos(a)*75),y=cy+(int)(Math.sin(a)*75);g.fill(x-22,y-22,x+22,y+22,id==hover?color(id):0xffa88748);g.fill(x-19,y-19,x+19,y+19,0xff241d17);g.blit(icon(id),x-11,y-11,x+11,y+11,0,1,0,1);if(!TalentService.enabled(id)||!TalentService.form(minecraft.player,id))g.fill(x-19,y-19,x+19,y+19,0x99171e27);}
   g.centeredText(font,Component.translatable(skills.isEmpty()?"lastbreath.talent.select":"lastbreath.wheel.cancel"),cx,cy-4,0xffc6d4df);
   if(hover>=0){g.centeredText(font,Component.translatable("lastbreath.talent."+hover),cx,cy+117,color(hover));if(!TalentService.form(minecraft.player,hover))g.centeredText(font,Component.translatable("lastbreath.ability.form"),cx,cy+132,0xffff9999);}
  }
 }
}
