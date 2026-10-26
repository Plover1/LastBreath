package me.vlad.lastbreath;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public final class SoulGear {
    private static final Identifier UPGRADE=Identifier.fromNamespaceAndPath("lastbreath","gear_upgrade");
    public static int type(ItemStack stack){return stack.is(LastBreath.SOUL_DEVOURER.get())?1:stack.is(LastBreath.SOUL_CLOAK.get())?2:stack.is(LastBreath.SOUL_TROUSERS.get())?3:stack.is(LastBreath.SOUL_BOOTS.get())?4:0;}
    public static String itemName(int type){return switch(type){case 1->"soul_devourer";case 3->"soul_trousers";case 4->"soul_boots";default->"soul_cloak";};}
    public static SoulGearData data(ItemStack stack){return stack.getOrDefault(LastBreath.GEAR_DATA.get(),SoulGearData.EMPTY);}
    public static String branchKey(int type,int branch){return branch==0?"lastbreath.gear.unbound":"lastbreath.gear.branch."+(type>2?2:type)+"."+branch;}
    public static net.minecraft.network.chat.MutableComponent details(int type,int branch){
        type=type>2?2:type;
        String key="lastbreath.gear.details."+type+"."+branch;
        if(type==1&&branch==1)return net.minecraft.network.chat.Component.translatable(key,Math.round(Config.value(Config.GEAR_HARVEST_CHANCE)*100),Config.value(Config.GEAR_HARVEST_COST));
        if(type==1&&branch==2)return net.minecraft.network.chat.Component.translatable(key,Config.value(Config.GEAR_HEAL),Config.value(Config.GEAR_HEAL_COST),Config.value(Config.GEAR_HEAL_COOLDOWN));
        if(type==2&&branch==1)return net.minecraft.network.chat.Component.translatable(key,Math.round(Config.value(Config.GEAR_GUARD_REDUCTION)*100),Config.value(Config.GEAR_GUARD_THRESHOLD),Config.value(Config.GEAR_GUARD_COST),Config.value(Config.GEAR_CLOAK_COOLDOWN));
        if(type==2&&branch==2)return net.minecraft.network.chat.Component.translatable(key,Config.value(Config.GEAR_SHROUD_HEALTH),Config.value(Config.GEAR_SHROUD_SECONDS),Config.value(Config.GEAR_SHROUD_COST),Config.value(Config.GEAR_CLOAK_COOLDOWN));
        return net.minecraft.network.chat.Component.empty();
    }
    public static int chargeValue(int type){return Config.value(type==0?Config.GEAR_BLUE: type==1?Config.GEAR_RED:Config.GEAR_PURPLE);}
    public static int[] price(int level){int n=level+1;double scale=Config.value(Config.GEAR_COST);return new int[]{(int)Math.ceil(10*n*scale),(int)Math.ceil(2*n*scale),level==2?(int)Math.ceil(scale):0};}
    public static void write(ItemStack stack,SoulGearData data){
        stack.set(LastBreath.GEAR_DATA.get(),data);
        // Replace just our modifier, keeping enchantments and other mods' attributes intact.
        var attrs=stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS,ItemAttributeModifiers.EMPTY);
        boolean sword=type(stack)==1;
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS,attrs.withModifierAdded(sword?Attributes.ATTACK_DAMAGE:Attributes.ARMOR,
            new AttributeModifier(type(stack)>2?Identifier.fromNamespaceAndPath("lastbreath","gear_upgrade_"+type(stack)):UPGRADE,data.level(),AttributeModifier.Operation.ADD_VALUE),sword?EquipmentSlotGroup.MAINHAND:type(stack)==3?EquipmentSlotGroup.LEGS:type(stack)==4?EquipmentSlotGroup.FEET:EquipmentSlotGroup.CHEST));
    }
    /** Only called by the server menu after distance, identity and ritual access checks. */
    static boolean purchase(ServerPlayer player,ItemStack stack,int action){
        if(!Config.GEAR_ENABLED.get()||type(stack)==0||stack.getCount()!=1)return false;
        var d=data(stack);var p=player.getData(LastBreath.PROGRESS);int blue=0,red=0,purple=0;
        SoulGearData next;
        if(action>=22&&action<=24){
            int currency=action-22,amount=(int)Math.ceil(chargeValue(currency)*(TalentService.has(player,18)?1.1:1)),capacity=Config.GEAR_CAPACITY.get();
            // Do not silently waste the remainder of a valuable soul.
            if(d.charge()+chargeValue(currency)>capacity)return false;
            amount=Math.min(amount,capacity-d.charge());
            blue=currency==0?1:0;red=currency==1?1:0;purple=currency==2?1:0;
            next=new SoulGearData(d.level(),d.branch(),d.charge()+amount,d.readyAt());
        }else if(action==25){
            if(d.level()>=3)return false;int[] cost=price(d.level());blue=cost[0];red=cost[1];purple=cost[2];
            next=new SoulGearData(d.level()+1,d.branch(),d.charge(),d.readyAt());
        }else if(action==26||action==27){
            if(d.level()<1||d.branch()!=0)return false;red=(int)Math.ceil(5*Config.GEAR_COST.get());
            next=new SoulGearData(d.level(),action-25,d.charge(),d.readyAt());
        }else return false;
        if(p.souls()<blue||p.red()<red||p.purple()<purple)return false;
        player.setData(LastBreath.PROGRESS,p.balances(p.souls()-blue,p.red()-red,p.purple()-purple));
        write(stack,next);player.getInventory().setChanged();player.inventoryMenu.broadcastChanges();
        AdvancedSkills.burst(player,0);RitualFeedback.sound(player.level(),player.position(),net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME,.4f,1.2f);
        return true;
    }
    private static boolean ready(ServerPlayer p,ItemStack stack,int branch,int cost){
        var d=data(stack);return Config.GEAR_ENABLED.get()&&p.isAlive()&&!SoulService.ghost(p)&&!p.isSpectator()&&d.branch()==branch&&d.charge()>=cost&&d.readyAt()<=p.level().getServer().overworld().getGameTime();
    }
    private static void consume(ServerPlayer p,ItemStack stack,int cost,int seconds){
        write(stack,data(stack).spend(cost,p.level().getServer().overworld().getGameTime()+20L*seconds));
        p.getInventory().setChanged();AdvancedSkills.burst(p,0);
    }
    /** Extra roll has its own payment: ordinary drops never consume charge. */
    static boolean extraSoul(ServerPlayer p,int category,float roll){
        var stack=p.getMainHandItem();int cost=Config.GEAR_HARVEST_COST.get();
        if(category==2||!stack.is(LastBreath.SOUL_DEVOURER.get())||!ready(p,stack,1,cost)||roll>=Config.GEAR_HARVEST_CHANCE.get())return false;
        consume(p,stack,cost,0);return true;
    }
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void kill(LivingDeathEvent e){
        if(!(e.getSource().getEntity() instanceof ServerPlayer p)||e.getSource().getDirectEntity()!=p||!e.getSource().is(net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK)||SoulHarvest.category(e.getEntity())<0||p.getHealth()>=p.getMaxHealth())return;
        var s=p.getMainHandItem();int cost=Config.GEAR_HEAL_COST.get();
        if(s.is(LastBreath.SOUL_DEVOURER.get())&&ready(p,s,2,cost)){
            float before=p.getHealth();p.heal(Config.GEAR_HEAL.get().floatValue());
            if(p.getHealth()>before)consume(p,s,cost,Config.GEAR_HEAL_COOLDOWN.get());
        }
    }
    private static ItemStack defensiveStack(ServerPlayer p,int branch,int cost){
        for(var slot:new EquipmentSlot[]{EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}){
            var stack=p.getItemBySlot(slot);
            if(type(stack)>=2&&ready(p,stack,branch,cost))return stack;
        }
        return ItemStack.EMPTY;
    }
    // A set gets one defensive activation, not three effects on the same hit.
    private static void consumeArmor(ServerPlayer p,ItemStack selected,int cost){
        consume(p,selected,cost,Config.GEAR_CLOAK_COOLDOWN.get());
        long until=data(selected).readyAt();
        for(var slot:new EquipmentSlot[]{EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}){
            var other=p.getItemBySlot(slot);if(other==selected||type(other)<2)continue;
            var d=data(other);write(other,new SoulGearData(d.level(),d.branch(),d.charge(),Math.max(d.readyAt(),until)));
        }
    }
    @SubscribeEvent(priority=EventPriority.LOW)
    public static void protect(LivingDamageEvent.Pre e){
        if(!(e.getEntity() instanceof ServerPlayer p)||e.getNewDamage()<=0||e.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY))return;
        int cost=Config.GEAR_GUARD_COST.get();var s=defensiveStack(p,1,cost);
        if(!s.isEmpty()&&Config.GEAR_GUARD_REDUCTION.get()>0&&e.getNewDamage()>=Config.GEAR_GUARD_THRESHOLD.get()&&ready(p,s,1,cost)){
            e.setNewDamage(e.getNewDamage()*(1-Config.GEAR_GUARD_REDUCTION.get().floatValue()));consumeArmor(p,s,cost);
        }
    }
    @SubscribeEvent public static void shroud(LivingDamageEvent.Post e){
        if(!(e.getEntity() instanceof ServerPlayer p)||!p.isAlive()||p.getHealth()>Config.GEAR_SHROUD_HEALTH.get()||e.getHealthDamage()<=0)return;
        int cost=Config.GEAR_SHROUD_COST.get();var s=defensiveStack(p,2,cost);
        if(!s.isEmpty()&&ready(p,s,2,cost)){
            if(AdvancedSkills.spawnDecoy(p,Config.GEAR_SHROUD_SECONDS.get())){
                consumeArmor(p,s,cost);
                p.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.SPEED,60,0));
            }
        }
    }
}


