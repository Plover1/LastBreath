package me.vlad.lastbreath;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
@EventBusSubscriber(modid=LastBreath.MODID)
public final class MemoryGuardEvents {
 @SubscribeEvent public static void tick(net.neoforged.neoforge.event.tick.EntityTickEvent.Post e){
  if(e.getEntity() instanceof net.minecraft.world.entity.Mob m&&!m.level().isClientSide()&&m.getPersistentData().getBooleanOr(MemoryStoneEntity.GUARD,false)&&m.tickCount%20==0){
   var pos=net.minecraft.core.BlockPos.of(m.getPersistentData().getLongOr("lastbreath_shrine",0L));
   if(!(m.level().getBlockEntity(pos) instanceof MemoryStoneEntity stone)||!stone.owns(m))m.discard();
  }
 }
 @SubscribeEvent public static void drops(net.neoforged.neoforge.event.entity.living.LivingDropsEvent e){if(e.getEntity().getPersistentData().getBooleanOr(MemoryStoneEntity.GUARD,false))e.getDrops().clear();}
 @SubscribeEvent public static void xp(net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent e){if(e.getEntity().getPersistentData().getBooleanOr(MemoryStoneEntity.GUARD,false))e.setDroppedExperience(0);}
}
