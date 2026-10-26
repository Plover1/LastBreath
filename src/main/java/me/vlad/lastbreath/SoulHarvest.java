package me.vlad.lastbreath;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
public final class SoulHarvest {
 static boolean successfulRoll(float roll){return roll>=0&&roll<.05f;}
 public static int category(LivingEntity mob){
  String id=net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
  if(Config.EXCLUDED.get().contains(id))return -1;
  if(Config.BOSSES.get().contains(id)||mob.getType().builtInRegistryHolder().is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE,net.minecraft.resources.Identifier.fromNamespaceAndPath("lastbreath","bosses"))))return 2;
  if(!(mob instanceof Mob))return -1;
  return mob instanceof net.minecraft.world.entity.monster.Enemy||Config.MONSTERS.get().contains(id)||mob.getType().builtInRegistryHolder().is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE,net.minecraft.resources.Identifier.fromNamespaceAndPath("lastbreath","monsters")))?1:0;
 }
 @SubscribeEvent public static void drops(LivingDropsEvent event){
  var mob=event.getEntity();if(mob.level().isClientSide()||!Config.SOUL_DROPS.get())return;
  if(mob.getPersistentData().getBooleanOr(MemoryStoneEntity.GUARD,false))return;
  int type=category(mob);if(type<0)return;
  var source=event.getSource();
  if(!(source.getEntity() instanceof ServerPlayer player)||!player.isAlive()||SoulService.ghost(player))return;
  if(type==2)player.setData(LastBreath.PROGRESS,player.getData(LastBreath.PROGRESS).trial(1));
  var weapon=source.getWeaponItem();
  if(Config.REQUIRE_SWORD.get()&&(!source.is(net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK)||source.getDirectEntity()!=player||weapon==null||(!weapon.is(LastBreath.EXECUTIONER_SWORD.get())&&!weapon.is(LastBreath.SOUL_DEVOURER.get()))))return;
  double chance=type==2?Config.PURPLE_CHANCE.get():(type==1?Config.RED_CHANCE.get():Config.BLUE_CHANCE.get())+Config.HARVEST_BONUS.get()*TalentService.legacy(player,0);
  if(type!=2)chance+=TalentService.harvestBonus(player,mob);
  boolean normal=mob.getRandom().nextFloat()<Math.min(1,chance);
  boolean extra=!normal&&source.is(net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK)&&source.getDirectEntity()==player&&SoulGear.extraSoul(player,type,mob.getRandom().nextFloat());
  if(normal||extra)event.getDrops().add(new ItemEntity(mob.level(),mob.getX(),mob.getY()+.3,mob.getZ(),new ItemStack(type==0?LastBreath.MOB_SOUL.get():type==1?LastBreath.RED_SOUL.get():LastBreath.PURPLE_SOUL.get())));
 }
}
