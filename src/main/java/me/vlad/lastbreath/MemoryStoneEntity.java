package me.vlad.lastbreath;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
/** Only completed stages persist in block state. Unloading cancels trials without consuming offerings. */
public final class MemoryStoneEntity extends BlockEntity {
 public static final String GUARD="lastbreath_memory_guard";
 private ServerPlayer participant;private final List<Mob> guards=new ArrayList<>();private int elapsed;
 public MemoryStoneEntity(BlockPos p,BlockState s){super(LastBreath.MEMORY_ENTITY.get(),p,s);}
 private boolean near(ServerPlayer p){return p.level()==level&&p.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(worldPosition))<=24*24;}
 private int count(ServerPlayer p,net.minecraft.world.item.Item item){int count=0;for(int i=0;i<p.getInventory().getContainerSize();i++){var s=p.getInventory().getItem(i);if(s.is(item))count+=s.getCount();}return count;}
 private boolean offering(ServerPlayer p){return count(p,LastBreath.PURPLE_SOUL.get())>=1&&count(p,LastBreath.KNOWLEDGE_FRAGMENT.get())>=4;}
 private void consume(ServerPlayer p,net.minecraft.world.item.Item item,int n){for(int i=0;i<p.getInventory().getContainerSize()&&n>0;i++){var s=p.getInventory().getItem(i);if(s.is(item)){int k=Math.min(n,s.getCount());s.shrink(k);n-=k;}}}
 public void start(ServerPlayer p){
  if(!Config.SHRINES.get()){p.sendOverlayMessage(Component.translatable("lastbreath.feature.disabled"));return;}
  if(!(level instanceof ServerLevel server)||!p.isAlive()||p.isSpectator()||p.getData(LastBreath.SOUL).isGhost()||!near(p)||!level.dimension().equals(Level.NETHER))return;
  int stage=getBlockState().getValue(MemoryStoneBlock.STAGE);
  if(stage==2||participant!=null){p.sendOverlayMessage(Component.translatable("lastbreath.shrine.busy"));return;}
  if(stage==1&&!offering(p)){p.sendOverlayMessage(Component.translatable("lastbreath.shrine.offering"));return;}
  if(server.getDifficulty()==net.minecraft.world.Difficulty.PEACEFUL){p.sendOverlayMessage(Component.translatable("lastbreath.shrine.peaceful"));return;}
  participant=p;elapsed=0;
  for(int n=0;n<(stage==0?Config.SHRINE_GUARDS.get():Config.ANCIENT_GUARDS.get());n++){
   var safe=SoulService.findSafe(server,worldPosition.offset(n%2==0?5:-5,0,n-2),p,3,3);if(safe==null){p.sendOverlayMessage(Component.translatable("lastbreath.shrine.blocked"));cancel();return;}
   Mob mob=(stage==0?EntityTypes.SKELETON:EntityTypes.WITHER_SKELETON).create(server,EntitySpawnReason.EVENT);if(mob==null){cancel();return;}
   mob.setPos(safe.getX()+.5,safe.getY(),safe.getZ()+.5);mob.addTag(GUARD);mob.getPersistentData().putBoolean(GUARD,true);mob.setPersistenceRequired();
   mob.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(net.minecraft.world.item.Items.STONE_SWORD));
   // Shrine identity allows orphan guardians to be removed safely after restart/chunk unload.
   mob.getPersistentData().putLong("lastbreath_shrine",worldPosition.asLong());
   if(!server.addFreshEntity(mob)){cancel();return;}mob.setTarget(p);guards.add(mob);
  }
  p.sendSystemMessage(Component.translatable("lastbreath.shrine.started"));
 }
 private void cancel(){guards.forEach(m->{if(m.isAlive())m.discard();});guards.clear();participant=null;elapsed=0;}
 java.util.List<Mob> guardians(){return java.util.List.copyOf(guards);}
 public boolean owns(Mob mob){return participant!=null&&guards.contains(mob);}
 public static void tick(Level l,BlockPos pos,BlockState s,MemoryStoneEntity stone){
  if(!(l instanceof ServerLevel server)||stone.participant==null)return;
  var p=stone.participant;
  if(!Config.SHRINES.get()||p.hasDisconnected()||!p.isAlive()||p.getData(LastBreath.SOUL).isGhost()||!stone.near(p)||++stone.elapsed>Config.SHRINE_SECONDS.get()*20||server.getDifficulty()==net.minecraft.world.Difficulty.PEACEFUL){p.sendOverlayMessage(Component.translatable("lastbreath.shrine.cancel"));stone.cancel();return;}
  if(stone.guards.stream().anyMatch(m->m.isRemoved()&&m.getHealth()>0)){stone.cancel();return;}
  if(stone.elapsed%20==0)RitualFeedback.ring(server,net.minecraft.world.phys.Vec3.atCenterOf(pos).add(0,1,0),stone.elapsed*.1,1.4);
  if(stone.guards.stream().anyMatch(Mob::isAlive))return;
  int stage=s.getValue(MemoryStoneBlock.STAGE);if(stage==1&&!stone.offering(p)){stone.cancel();return;}
  // Persist the spent stage before awarding anything; simultaneous clicks cannot pay twice.
  l.setBlockAndUpdate(pos,s.setValue(MemoryStoneBlock.STAGE,stage+1));
  if(stage==1){stone.consume(p,LastBreath.PURPLE_SOUL.get(),1);stone.consume(p,LastBreath.KNOWLEDGE_FRAGMENT.get(),4);}
  var reward=new ItemStack(LastBreath.KNOWLEDGE_SCROLLS.get(s.getValue(MemoryStoneBlock.BRANCH)+stage*4).get());
  if(!p.getInventory().add(reward))server.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(server,p.getX(),p.getY(),p.getZ(),reward));
  p.sendSystemMessage(Component.translatable("lastbreath.shrine.complete"));stone.cancel();
 }
}
