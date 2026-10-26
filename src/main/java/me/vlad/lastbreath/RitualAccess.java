package me.vlad.lastbreath;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;

/** Sessions are tied to player instances and expire on logout, damage, movement or menu closure. */
public final class RitualAccess {
 private record Session(GlobalPos table,long end,boolean open) {}
 private static final Map<ServerPlayer,Session> SESSIONS=new WeakHashMap<>();
 private static final Map<ServerPlayer,Long> HINTS=new WeakHashMap<>();
 public static boolean allowed(net.minecraft.world.entity.player.Player player,BlockPos pos) {
  if(SoulService.ghost(player))return true;
  if(!(player instanceof ServerPlayer sp))return false;
  var s=SESSIONS.get(sp);
  return s!=null&&s.open&&s.end>sp.level().getGameTime()&&s.table.dimension().equals(sp.level().dimension())&&s.table.pos().equals(pos);
 }
 public static void hint(ServerPlayer p){long now=p.level().getGameTime();if(HINTS.getOrDefault(p,0L)<=now){p.sendOverlayMessage(Component.translatable("lastbreath.table.hint"));HINTS.put(p,now+40);}}
 public static boolean start(ServerPlayer p,BlockPos pos,InteractionHand hand){
  if(!Config.LIVING_RITUAL.get()||p.isSpectator()||!p.isAlive()||p.getData(LastBreath.SOUL).isGhost()||p.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(pos))>16)return false;
  var stack=p.getItemInHand(hand);
  if(!stack.is(LastBreath.RED_SOUL.get())||stack.getCount()<Config.RITUAL_COST.get())return false;
  SESSIONS.put(p,new Session(GlobalPos.of(p.level().dimension(),pos.immutable()),0,false));p.startUsingItem(hand);return true;
 }
 public static void channel(ServerPlayer p,int remaining){
  var s=SESSIONS.get(p);if(s==null||s.open)return;
  if(!valid(p,s)){cancel(p);return;}
  int total=Config.RITUAL_SECONDS.get()*20;
  if(remaining%5==0){RitualFeedback.progress(p,Component.translatable("lastbreath.table.channel"),total-remaining,total);if(Config.ANIMATIONS.get())RitualFeedback.ring(p.level(),net.minecraft.world.phys.Vec3.atCenterOf(s.table.pos()).add(0,.6,0),p.tickCount*.18,.7);}
 }
 public static void finish(ServerPlayer p){
  var s=SESSIONS.get(p);if(s==null||s.open||!valid(p,s)) {cancel(p);return;}
  var held=p.getUseItem();if(!held.is(LastBreath.RED_SOUL.get())||held.getCount()<Config.RITUAL_COST.get()){cancel(p);return;}
  held.shrink(Config.RITUAL_COST.get());
  SESSIONS.put(p,new Session(s.table,p.level().getGameTime()+Config.RITUAL_SESSION.get()*20L,true));
  p.openMenu(p.level().getBlockState(s.table.pos()).getMenuProvider(p.level(),s.table.pos()));
  AdvancedSkills.burst(p,1);
 }
 private static boolean valid(ServerPlayer p,Session s){return p.isAlive()&&!p.isSpectator()&&s.table.dimension().equals(p.level().dimension())&&p.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(s.table.pos()))<=16&&p.level().getBlockState(s.table.pos()).is(LastBreath.RITUAL_TABLE.get());}
 public static void tick(ServerPlayer p){var s=SESSIONS.get(p);if(s==null)return;if(!valid(p,s)||(s.open?(s.end<=p.level().getGameTime()||!(p.containerMenu instanceof SoulTreeMenu)):!p.isUsingItem()))cancel(p);}
 public static void cancel(ServerPlayer p){if(SESSIONS.remove(p)!=null){p.stopUsingItem();if(p.containerMenu instanceof SoulTreeMenu)p.closeContainer();}}
}
