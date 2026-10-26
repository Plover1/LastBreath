package me.vlad.lastbreath;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import java.util.*;

public final class TalentService {
    private static final Map<ServerPlayer,Long> HARVEST=new WeakHashMap<>();
    private static final Map<Mob,Long> MARKED=new WeakHashMap<>();
    public static SpiritTalents get(Player p){
        var t=p.getData(LastBreath.TALENTS);
        if(!t.migrated()&&p instanceof ServerPlayer){t=SpiritTalents.migrate(p.getData(LastBreath.PROGRESS));p.setData(LastBreath.TALENTS,t);}
        return t;
    }
    public static boolean enabled(int id){return id>=0&&id<SpiritTalents.COUNT&&Config.value(Config.TALENTS_ENABLED)&&!Config.value(Config.DISABLED_TALENTS).contains(id)&&((id!=0&&id!=4)||Config.value(Config.ECHO))&&(id!=9||Config.value(Config.BORROW))&&(id!=14||Config.value(Config.BOND));}
    public static int legacy(Player p,int rank){int[] ids={16,8,13,2,5,11,4,9,14};return rank>=0&&rank<ids.length&&enabled(ids[rank])?p.getData(LastBreath.PROGRESS).rank(rank):0;}
    public static boolean has(Player p,int id){return enabled(id)&&get(p).has(id);}
    public static boolean reader(Player p){return p.getMainHandItem().is(LastBreath.SOUL_READER.get())||p.getOffhandItem().is(LastBreath.SOUL_READER.get());}
    public static int[] cost(int id){int step=id>=20?(id==27?4:2):id%5;double scale=Config.value(Config.COST_SCALE);return new int[]{(int)Math.ceil(new int[]{5,8,12,15,30}[step]*scale),(int)Math.ceil(new int[]{0,2,3,5,10}[step]*scale),step==4?1:0};}
    public static boolean buy(ServerPlayer p,int id){
        var t=get(p);if(!enabled(id)||t.has(id)||!t.unlocked(id))return false;
        int[] cost=cost(id);var bank=p.getData(LastBreath.PROGRESS);
        if(bank.souls()<cost[0]||bank.red()<cost[1]||bank.purple()<cost[2])return false;
        p.setData(LastBreath.PROGRESS,bank.balances(bank.souls()-cost[0],bank.red()-cost[1],bank.purple()-cost[2]));
        t=t.learn(id);if(SpiritTalents.active(id))t=t.toggle(id);p.setData(LastBreath.TALENTS,t);return true;
    }
    public static boolean form(Player p,int id){boolean ghost=SoulService.ghost(p);if(id>=20)return !ghost;return SpiritTalents.gold(id)?id==14||id==19?!ghost:true:ghost;}
    public static void select(ServerPlayer p,int id){if(!p.isAlive()||p.isSpectator()||!reader(p)||!enabled(id))return;var old=get(p);var next=old.select(id);if(next!=old)p.setData(LastBreath.TALENTS,next);}
    private static Mob enemy(ServerPlayer p){return p.level().getEntitiesOfClass(Mob.class,p.getBoundingBox().inflate(20),m->m instanceof Enemy&&m.isAlive()&&p.hasLineOfSight(m)&&m.getEyePosition().subtract(p.getEyePosition()).normalize().dot(p.getLookAngle())>.92).stream().min(Comparator.comparingDouble(p::distanceToSqr)).orElse(null);}
    private static ServerPlayer friend(ServerPlayer p){return p.level().getServer().getPlayerList().getPlayers().stream().filter(q->q!=p&&q.level()==p.level()&&q.isAlive()&&!SoulService.ghost(q)&&q.distanceToSqr(p)<=256&&p.hasLineOfSight(q)).min(Comparator.comparingDouble(p::distanceToSqr)).orElse(null);}
    public static void activate(ServerPlayer p){
        if(!p.isAlive()||p.isSpectator()||!reader(p))return;
        var t=get(p);int id=t.selected();long now=p.level().getServer().overworld().getGameTime();
        if(!t.worn(id)||!SpiritTalents.active(id)){p.sendOverlayMessage(Component.translatable("lastbreath.talent.select"));return;}
        if(!enabled(id)){p.sendOverlayMessage(Component.translatable("lastbreath.talent.disabled"));return;}
        if(!form(p,id)){p.sendOverlayMessage(Component.translatable("lastbreath.ability.form"));return;}
        if(Math.max(t.cooldown(),p.getData(LastBreath.PROGRESS).cooldown())>now){p.sendOverlayMessage(Component.translatable("lastbreath.skill.cooldown",(Math.max(t.cooldown(),p.getData(LastBreath.PROGRESS).cooldown())-now+19)/20));return;}
        int seconds=SpiritTalents.gold(id)?Config.GOLD_COOLDOWN.get():Config.TALENT_COOLDOWN.get();
        switch(id){
            case 0,4 -> {if(!AdvancedSkills.spawnDecoy(p,id==4?12:7,false))return;}
            case 2,12,17 -> {var mob=enemy(p);if(mob==null){p.sendOverlayMessage(Component.translatable("lastbreath.talent.target"));return;}mob.addEffect(new MobEffectInstance(id==12?MobEffects.WEAKNESS:MobEffects.GLOWING,200,id==12?1:0));if(id==17)MARKED.put(mob,now+200);}
            case 5 -> {var old=p.getData(LastBreath.PROGRESS);p.setData(LastBreath.PROGRESS,new SoulProgress(old.souls(),old.harvest(),old.vitality(),old.sacrifice(),old.sight(),Math.max(1,old.step()),old.ward(),old.cooldown(),old.red(),old.purple(),old.echo(),old.borrow(),old.bond(),old.trials(),4));try{SoulSkills.activate(p);}finally{var end=p.getData(LastBreath.PROGRESS).cooldown();p.setData(LastBreath.PROGRESS,old.cooldown(end));}if(p.getData(LastBreath.PROGRESS).cooldown()<=now)return;}
            case 7 -> p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,300,0));
            case 9 -> {if(!SoulService.ghost(p))p.addEffect(new MobEffectInstance(MobEffects.SPEED,160,1));else{p.closeContainer();p.setData(LastBreath.BORROWED,p.level().getGameTime()+Config.BORROW_SECONDS.get()*20L);if(!p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL)){p.setData(LastBreath.BORROWED,0L);return;}p.setHealth(Math.min(p.getMaxHealth(),8));}}
            case 10 -> {var other=friend(p);if(other==null){p.sendOverlayMessage(Component.translatable("lastbreath.talent.target"));return;}other.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,120,0));seconds=45;}
            case 14 -> {if(!AdvancedSkills.bond(p,now))return;}
            case 15 -> {var items=p.level().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,p.getBoundingBox().inflate(32),e->e.getItem().is(LastBreath.MOB_SOUL.get())||e.getItem().is(LastBreath.RED_SOUL.get())||e.getItem().is(LastBreath.PURPLE_SOUL.get()));for(var item:items)p.level().sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL,item.getX(),item.getY()+.5,item.getZ(),6,.1,.2,.1,.01);p.sendOverlayMessage(Component.translatable("lastbreath.talent.found",items.size()));}
            case 19 -> {var b=p.getData(LastBreath.PROGRESS);if(b.red()<2){p.sendOverlayMessage(Component.translatable("lastbreath.talent.harvest_cost"));return;}p.setData(LastBreath.PROGRESS,b.balances(b.souls(),b.red()-2,b.purple()));HARVEST.put(p,now+400);}
            case 20 -> {for(var mob:p.level().getEntitiesOfClass(Mob.class,p.getBoundingBox().inflate(4),m->m instanceof Enemy&&m.isAlive()&&p.hasLineOfSight(m))){var v=p.position().subtract(mob.position());mob.knockback(1.2,v.x,v.z,p.damageSources().playerAttack(p),0,true);}}
            case 21 -> {if(p.getHealth()>=p.getMaxHealth())return;var b=p.getData(LastBreath.PROGRESS);if(b.souls()<3){p.sendOverlayMessage(Component.translatable("lastbreath.talent.blue_cost",3));return;}p.setData(LastBreath.PROGRESS,b.balances(b.souls()-3,b.red(),b.purple()));p.heal(4);seconds=60;}
            case 22 -> {p.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,160,0));seconds=60;}
            case 23 -> {p.addEffect(new MobEffectInstance(MobEffects.SPEED,200,1));p.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST,200,0));}
            case 27 -> {p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,120,1));p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,200,1));seconds=90;}
            default -> {return;}
        }
        long end=now+seconds*20L;if(has(p,1))end=now+(long)(seconds*18L);
        p.setData(LastBreath.TALENTS,get(p).cooldown(Math.max(end,p.getData(LastBreath.PROGRESS).cooldown())));AdvancedSkills.burst(p,0);
    }
    public static double harvestBonus(ServerPlayer p,net.minecraft.world.entity.LivingEntity victim){long now=p.level().getServer().overworld().getGameTime();return (has(p,16)&&p.getData(LastBreath.PROGRESS).harvest()==0?.025:0)+(enabled(19)&&HARVEST.getOrDefault(p,0L)>now?.15:0)+(enabled(17)&&victim instanceof Mob m&&MARKED.getOrDefault(m,0L)>now?.10:0);}
    public static void tick(ServerPlayer p){
        var t=get(p);if(p.tickCount%20!=0)return;
        attribute(p,Attributes.MOVEMENT_SPEED,"wanderer_speed",has(p,6)?.015:0);
        attribute(p,Attributes.MAX_HEALTH,"wanderer_health",has(p,8)&&p.getData(LastBreath.PROGRESS).vitality()==0?2:0);
        attribute(p,Attributes.ARMOR,"wanderer_armor",has(p,11)&&p.getData(LastBreath.PROGRESS).ward()==0?2:0);
        if(has(p,3))p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,240,0,false,false));
        if(has(p,13))p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,40,0,false,false));
        attribute(p,Attributes.KNOCKBACK_RESISTANCE,"wanderer_anchor",has(p,25)?.1:0);
        if(has(p,24)&&p.isUnderWater())p.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING,40,0,false,false));
        if(has(p,26)&&p.tickCount%40==0)for(var mob:p.level().getEntitiesOfClass(Mob.class,p.getBoundingBox().inflate(6),m->m instanceof Enemy&&m.isAlive()&&p.hasLineOfSight(m)))mob.addEffect(new MobEffectInstance(MobEffects.GLOWING,60,0));
        MARKED.entrySet().removeIf(e->!e.getKey().isAlive()||e.getValue()<p.level().getServer().overworld().getGameTime());
    }
    private static void attribute(ServerPlayer p,net.minecraft.core.Holder<Attribute> type,String name,double amount){var a=p.getAttribute(type);if(a==null)return;var key=Identifier.fromNamespaceAndPath("lastbreath",name);var old=a.getModifier(key);if(old!=null&&old.amount()==amount)return;a.removeModifier(key);if(amount!=0)a.addTransientModifier(new AttributeModifier(key,amount,AttributeModifier.Operation.ADD_VALUE));}
}
