package me.vlad.lastbreath;

import java.util.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;

public final class AdvancedSkills {
 /** Server-only targeting behavior; clients receive the ordinary invisible armor-stand representation. */
 private static final class Decoy extends ArmorStand {
  Decoy(net.minecraft.server.level.ServerLevel level,double x,double y,double z){super(level,x,y,z);}
  @Override public boolean canBeSeenByAnyone(){return isAlive()&&!isRemoved();}
  @Override public boolean canBeSeenAsEnemy(){return canBeSeenByAnyone();}
 }
 private record Echo(ArmorStand entity,long end,boolean gear) {}
 private record Bond(ServerPlayer partner,long end) {}
 private record Request(ServerPlayer from,long end) {}
 private static final List<Echo> ECHOES=new ArrayList<>();
 private static final Map<ServerPlayer,Bond> BONDS=new WeakHashMap<>();
 private static final Map<ServerPlayer,Request> REQUESTS=new WeakHashMap<>();
 private static boolean sharing;
 private static final Map<ServerPlayer,Integer> RETURNING=new WeakHashMap<>();
 public static void returning(ServerPlayer p){RETURNING.put(p,p.tickCount+30);burst(p,0);}
 public static void burst(ServerPlayer p,int type){
  if(!Config.ANIMATIONS.get()||!Config.RITUAL_PARTICLES.get())return;
  p.level().sendParticles(type==0?ParticleTypes.SOUL:type==1?ParticleTypes.FLAME:ParticleTypes.PORTAL,p.getX(),p.getY()+1,p.getZ(),12,.25,.45,.25,.025);
 }
 public static void cycle(ServerPlayer p){
  var s=p.getData(LastBreath.PROGRESS);int id=s.selected();
  for(int i=0;i<10;i++){id=(id+1)%10;if(id==9||(id>=3&&id!=5&&s.rank(id)>0&&s.enabled(id)))break;}
  p.setData(LastBreath.PROGRESS,s.select(id));p.sendOverlayMessage(Component.translatable("lastbreath.ability.selected",Component.translatable("lastbreath.skill."+id)));
 }
 public static void activate(ServerPlayer p){
  if(!p.isAlive()||p.isSpectator())return;
  var s=p.getData(LastBreath.PROGRESS);int id=s.selected();
  if(id==9){mark(p);return;}
  if(s.rank(id)==0||!s.enabled(id)){p.sendOverlayMessage(Component.translatable("lastbreath.skill.locked"));return;}
  long now=p.level().getServer().overworld().getGameTime();
  if(s.cooldown()>now){p.sendOverlayMessage(Component.translatable("lastbreath.skill.cooldown",(s.cooldown()-now+19)/20));return;}
  boolean ghost=SoulService.ghost(p);
  if((id==4||id==7)&&!ghost|| (id==3||id==8)&&p.getData(LastBreath.SOUL).isGhost()){p.sendOverlayMessage(Component.translatable("lastbreath.ability.form"));return;}
  if(id==3||id==4){SoulSkills.activate(p);return;}
  if(id==6){
   var safe=SoulService.findSafe(p.level(),p.blockPosition().relative(p.getDirection(),3),p,1,2);if(safe==null){p.sendOverlayMessage(Component.translatable("lastbreath.unsafe"));return;}
   var stand=new Decoy(p.level(),safe.getX()+.5,safe.getY(),safe.getZ()+.5);stand.setInvisible(true);stand.setPermanentlyInvulnerable(true);stand.addTag("lastbreath_echo");stand.setNoGravity(true);p.level().addFreshEntity(stand);
   ECHOES.add(new Echo(stand,p.level().getGameTime()+Config.ECHO_SECONDS.get()*20L,false));
  } else if(id==7){
   p.closeContainer();p.setData(LastBreath.BORROWED,p.level().getGameTime()+Config.BORROW_SECONDS.get()*20L);
   if(!p.setGameMode(GameType.SURVIVAL)){p.setData(LastBreath.BORROWED,0L);SoulService.enforce(p);return;}
   p.setHealth(Math.min(p.getMaxHealth(),8));p.sendOverlayMessage(Component.translatable("lastbreath.borrow.start",Config.BORROW_SECONDS.get()));
  } else if(id==8){if(!bond(p,now))return;}
  else return;
  p.setData(LastBreath.PROGRESS,p.getData(LastBreath.PROGRESS).cooldown(now+Config.ABILITY_COOLDOWN.get()*20L));burst(p,2);
 }
 public static boolean spawnDecoy(ServerPlayer p,int seconds){return spawnDecoy(p,seconds,true);}
 public static boolean spawnDecoy(ServerPlayer p,int seconds,boolean gear){
  var safe=SoulService.findSafe(p.level(),p.blockPosition(),p,2,2);if(safe==null)return false;
  var stand=new Decoy(p.level(),safe.getX()+.5,safe.getY(),safe.getZ()+.5);stand.setInvisible(true);stand.setPermanentlyInvulnerable(true);stand.addTag("lastbreath_echo");stand.setNoGravity(true);
  if(!p.level().addFreshEntity(stand))return false;
  ECHOES.add(new Echo(stand,p.level().getGameTime()+seconds*20L,gear));return true;
 }
 public static boolean bond(ServerPlayer p,long now){
  var request=REQUESTS.get(p);
  if(request!=null&&request.end>now&&eligible(p,request.from)&&Math.max(request.from.getData(LastBreath.PROGRESS).cooldown(),TalentService.get(request.from).cooldown())<=now&&!BONDS.containsKey(p)&&!BONDS.containsKey(request.from)){
   var other=request.from;other.setData(LastBreath.TALENTS,TalentService.get(other).cooldown(now+Config.GOLD_COOLDOWN.get()*20L));long end=now+Config.BOND_SECONDS.get()*20L;
   BONDS.put(p,new Bond(other,end));BONDS.put(other,new Bond(p,end));REQUESTS.remove(p);
   other.setData(LastBreath.PROGRESS,other.getData(LastBreath.PROGRESS).cooldown(now+Config.ABILITY_COOLDOWN.get()*20L));
   p.sendSystemMessage(Component.translatable("lastbreath.bond.active",other.getDisplayName()));other.sendSystemMessage(Component.translatable("lastbreath.bond.active",p.getDisplayName()));return true;
  }
  if(REQUESTS.values().stream().anyMatch(r->r.from==p&&r.end>now)){p.sendOverlayMessage(Component.translatable("lastbreath.bond.sent"));return false;}
  var other=p.level().getServer().getPlayerList().getPlayers().stream().filter(q->q!=p&&eligible(p,q)&&(q.getData(LastBreath.PROGRESS).bond()>0||TalentService.get(q).worn(14))&&!BONDS.containsKey(q)).min(Comparator.comparingDouble(p::distanceToSqr)).orElse(null);
  if(other==null||BONDS.containsKey(p)){p.sendOverlayMessage(Component.translatable("lastbreath.bond.none"));return false;}
  REQUESTS.put(other,new Request(p,now+400));other.sendSystemMessage(Component.translatable("lastbreath.bond.request",p.getDisplayName()));p.sendOverlayMessage(Component.translatable("lastbreath.bond.sent"));return false;
 }
 private static boolean eligible(ServerPlayer a,ServerPlayer b){return a!=b&&!a.hasDisconnected()&&!b.hasDisconnected()&&a.isAlive()&&b.isAlive()&&!a.isSpectator()&&!b.isSpectator()&&!a.getData(LastBreath.SOUL).isGhost()&&!b.getData(LastBreath.SOUL).isGhost()&&a.level()==b.level()&&a.distanceToSqr(b)<=Config.BOND_RADIUS.get()*Config.BOND_RADIUS.get();}
 private static void mark(ServerPlayer p){
  if(!SoulService.ghost(p)){p.sendOverlayMessage(Component.translatable("lastbreath.ability.form"));return;}
  long now=p.level().getServer().overworld().getGameTime();var progress=p.getData(LastBreath.PROGRESS);if(progress.cooldown()>now)return;
  var hit=p.pick(24,0,false);var pos=hit.getLocation();
  for(var friend:p.level().getServer().getPlayerList().getPlayers())if(friend.level()==p.level()&&friend.distanceToSqr(p)<4096)friend.sendOverlayMessage(Component.translatable("lastbreath.mark",p.getDisplayName(),(int)pos.x,(int)pos.y,(int)pos.z));
  if(Config.ANIMATIONS.get())p.level().sendParticles(ParticleTypes.END_ROD,pos.x,pos.y+.5,pos.z,20,.2,.6,.2,.02);
  p.setData(LastBreath.PROGRESS,progress.cooldown(now+100));
 }
 public static void tickPlayer(ServerPlayer p){
  var end=RETURNING.get(p);if(end!=null){if(p.tickCount>=end)RETURNING.remove(p);else if(Config.ANIMATIONS.get()&&p.tickCount%3==0)RitualFeedback.ring(p.level(),p.position().add(0,2-(end-p.tickCount)/15.0,0),p.tickCount*.3,.45);}
  long until=p.getData(LastBreath.BORROWED);
  if(until!=0&&(until<=p.level().getGameTime()||!TalentService.enabled(9)||!p.getData(LastBreath.SOUL).isGhost())){
   p.setData(LastBreath.BORROWED,0L);if(p.getData(LastBreath.SOUL).isGhost()){p.closeContainer();SoulService.enforce(p);p.sendOverlayMessage(Component.translatable("lastbreath.borrow.end"));burst(p,0);}
  }
  var b=BONDS.get(p);if(b!=null){if(!TalentService.enabled(14)||b.end<=p.level().getServer().overworld().getGameTime()||!eligible(p,b.partner)){BONDS.remove(p);BONDS.remove(b.partner);}else if(Config.ANIMATIONS.get()&&p.tickCount%20==0)RitualFeedback.link(p,b.partner);}
 }
 @SubscribeEvent public static void tick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event){
  var it=ECHOES.iterator();while(it.hasNext()){
   var e=it.next();var a=e.entity;if(a.isRemoved()||(!e.gear&&(!Config.ECHO.get()||!Config.TALENTS_ENABLED.get()))||(e.gear&&!Config.GEAR_ENABLED.get())||a.level().getGameTime()>=e.end){a.discard();it.remove();continue;}
   if(a.tickCount%5!=0)continue;
   for(var m:a.level().getEntitiesOfClass(Mob.class,a.getBoundingBox().inflate(12),m->m instanceof Enemy)){m.setTarget(a);m.getNavigation().moveTo(a,1.1);}
   if(Config.ANIMATIONS.get()&&Config.RITUAL_PARTICLES.get()&&a.level() instanceof net.minecraft.server.level.ServerLevel level){
    for(int i=0;i<6;i++){double y=i*.28;level.sendParticles(ParticleTypes.SOUL,a.getX()+Math.sin(a.tickCount*.2+i)*.15,a.getY()+y,a.getZ(),1,.05,.02,.05,0);}
   }
  }
 }
 @SubscribeEvent public static void damage(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre event){
  if(sharing||!(event.getEntity() instanceof ServerPlayer p))return;
  var b=BONDS.get(p);if(b==null||!eligible(p,b.partner)||b.end<=p.level().getServer().overworld().getGameTime()||!TalentService.enabled(14)||event.getNewDamage()<=0)return;
  float half=event.getNewDamage()*.5f;sharing=true;
  try{if(b.partner.hurtServer(b.partner.level(),event.getSource(),half)){event.setNewDamage(half);if(Config.ANIMATIONS.get())RitualFeedback.link(p,b.partner);}}finally{sharing=false;}
 }
 @SubscribeEvent public static void invulnerable(net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event){if(event.getEntity().entityTags().contains("lastbreath_echo"))event.setCanceled(true);}
 @SubscribeEvent public static void load(net.neoforged.neoforge.event.entity.EntityJoinLevelEvent event){if(event.loadedFromDisk()&&event.getEntity().entityTags().contains("lastbreath_echo")){event.setCanceled(true);event.getEntity().discard();}}
 @SubscribeEvent public static void logout(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event){if(event.getEntity() instanceof ServerPlayer p){RitualAccess.cancel(p);var b=BONDS.remove(p);if(b!=null)BONDS.remove(b.partner);REQUESTS.remove(p);REQUESTS.entrySet().removeIf(e->e.getValue().from==p);p.setData(LastBreath.BORROWED,0L);SoulService.enforce(p);}}
 @SubscribeEvent public static void stop(net.neoforged.neoforge.event.server.ServerStoppedEvent event){ECHOES.clear();BONDS.clear();REQUESTS.clear();}
}
