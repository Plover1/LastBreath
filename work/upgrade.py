from pathlib import Path
import json
root=Path(__file__).resolve().parent.parent
java=root/'src/main/java/me/vlad/lastbreath'
def write(name,s): (java/name).write_text(s,encoding='utf-8')
def edit(name,a,b):
 p=java/name;s=p.read_text(encoding='utf-8');assert a in s,name+': '+a[:40];p.write_text(s.replace(a,b),encoding='utf-8')
p=root/'gradle.properties';s=p.read_text();s=s.replace('mod_version=0.8.0','mod_version=0.9.0').replace('mod_authors=Vlad','mod_authors=Siris102').replace('mod_name=LastBreath','mod_name=Last Breath');s=s[:s.index('mod_description=')]+'mod_description=Death is a new chapter. Become a spirit, rescue friends with Return Totems, harvest three kinds of souls and develop supernatural abilities at a ritual table. Configurable cooperative Hardcore progression by Siris102.\n';p.write_text(s)
edit('Config.java','    public static final ModConfigSpec SPEC = B.build();','''    public static final ModConfigSpec.DoubleValue BLUE_CHANCE = B.defineInRange("peacefulSoulChance", .05, 0, 1);
    public static final ModConfigSpec.DoubleValue RED_CHANCE = B.defineInRange("monsterSoulChance", .05, 0, 1);
    public static final ModConfigSpec.DoubleValue PURPLE_CHANCE = B.defineInRange("bossSoulChance", 1.0, 0, 1);
    public static final ModConfigSpec.DoubleValue HARVEST_BONUS = B.defineInRange("harvestBonusPerRank", .025, 0, 1);
    public static final ModConfigSpec.BooleanValue REQUIRE_SWORD = B.define("requireExecutionerSword", true);
    public static final ModConfigSpec.ConfigValue<java.util.List<? extends String>> BOSSES = B.comment("Entity IDs. Boss category takes priority. Entity type tag lastbreath:bosses also supported.").defineListAllowEmpty("bossEntities", java.util.List.of("minecraft:ender_dragon", "minecraft:wither"), ()->"minecraft:wither", o->o instanceof String);
    public static final ModConfigSpec.ConfigValue<java.util.List<? extends String>> MONSTERS = B.comment("Additional monster entity IDs; vanilla Enemy mobs and lastbreath:monsters tag also count.").defineListAllowEmpty("monsterEntities", java.util.List.<String>of(), ()->"minecraft:zombie", o->o instanceof String);
    public static final ModConfigSpec.ConfigValue<java.util.List<? extends String>> EXCLUDED = B.defineListAllowEmpty("excludedSoulEntities", java.util.List.of("minecraft:armor_stand"), ()->"minecraft:armor_stand", o->o instanceof String);
    public static final ModConfigSpec.DoubleValue COST_SCALE = B.defineInRange("skillCostMultiplier", 1.0, .1, 100);
    public static final ModConfigSpec.BooleanValue TRIALS = B.comment("Final powers require a boss kill; Soul Bond also requires a successful rescue.").define("requireSkillTrials", true);
    public static final ModConfigSpec.BooleanValue LIVING_RITUAL = B.define("allowLivingRitual", true);
    public static final ModConfigSpec.IntValue RITUAL_SECONDS = B.defineInRange("tableChannelSeconds", 4, 1, 30);
    public static final ModConfigSpec.IntValue RITUAL_COST = B.defineInRange("tableRedSoulCost", 1, 0, 100);
    public static final ModConfigSpec.IntValue RITUAL_SESSION = B.defineInRange("tableSessionSeconds", 120, 10, 600);
    public static final ModConfigSpec.IntValue ABILITY_COOLDOWN = B.defineInRange("advancedAbilityCooldownSeconds", 60, 1, 3600);
    public static final ModConfigSpec.IntValue BORROW_SECONDS = B.defineInRange("borrowedTimeSeconds", 15, 1, 120);
    public static final ModConfigSpec.IntValue ECHO_SECONDS = B.defineInRange("echoSeconds", 10, 1, 60);
    public static final ModConfigSpec.IntValue BOND_SECONDS = B.defineInRange("bondSeconds", 30, 1, 300);
    public static final ModConfigSpec.IntValue BOND_RADIUS = B.defineInRange("bondRadius", 16, 2, 64);
    public static final ModConfigSpec.BooleanValue ECHO = B.define("enableEcho", true);
    public static final ModConfigSpec.BooleanValue BORROW = B.define("enableBorrowedTime", true);
    public static final ModConfigSpec.BooleanValue BOND = B.define("enableSoulBond", true);
    public static final ModConfigSpec.BooleanValue ANIMATIONS = B.define("animateRitualsAndSkills", true);
    public static final ModConfigSpec SPEC = B.build();''')
# Keep old eight-field constructor and codec keys for save migration.
fields=[('int','souls',30000),('int','harvest',3),('int','vitality',3),('int','sacrifice',3),('int','sight',1),('int','step',1),('int','ward',1),('long','cooldown',0),('int','red',30000),('int','purple',30000),('int','echo',1),('int','borrow',1),('int','bond',1),('int','trials',3),('int','selected',9)]
codec=',\n'.join(('Codec.LONG' if t=='long' else f'Codec.intRange(0,{cap})')+f'.optionalFieldOf("{n}",'+('0L' if t=='long' else '0')+f').forGetter(SoulProgress::{n})' for t,n,cap in fields)
write('SoulProgress.java','''package me.vlad.lastbreath;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
public record SoulProgress('''+', '.join(t+' '+n for t,n,_ in fields)+''') {
 public static final int CAP=30000;
 public static final SoulProgress EMPTY=new SoulProgress(0,0,0,0,0,0,0,0);
 public SoulProgress(int souls,int harvest,int vitality,int sacrifice,int sight,int step,int ward,long cooldown) { this(souls,harvest,vitality,sacrifice,sight,step,ward,cooldown,0,0,0,0,0,0,9); }
 public static final MapCodec<SoulProgress> CODEC=RecordCodecBuilder.mapCodec(i->i.group('''+codec+''').apply(i,SoulProgress::new));
 public int rank(int id) { return switch(id) {case 0->harvest;case 1->vitality;case 2->sacrifice;case 3->sight;case 4->step;case 5->ward;case 6->echo;case 7->borrow;case 8->bond;default->0;}; }
 public static int max(int id) { return id<3?3:1; }
 public boolean enabled(int id) { return switch(id) {case 6->Config.ECHO.get();case 7->Config.BORROW.get();case 8->Config.BOND.get();default->true;}; }
 public int cost(int id) { int base=switch(id) {case 0->new int[]{5,12,25}[Math.min(harvest,2)];case 1->new int[]{5,10,20}[Math.min(vitality,2)];case 2->new int[]{8,16,24}[Math.min(sacrifice,2)];case 3->12;case 4->15;case 5->25;case 6,7,8->30;default->0;};return (int)Math.ceil(base*Config.COST_SCALE.get()); }
 public int redCost(int id) { return (int)Math.ceil((id<3?rank(id):id<6?3:10)*Config.COST_SCALE.get()); }
 public int purpleCost(int id) { return id>=6?Math.max(1,(int)Math.ceil(Config.COST_SCALE.get())):0; }
 public boolean unlocked(int id) { boolean base=switch(id) {case 0,1,2->true;case 3->harvest>=1;case 4->vitality>=1;case 5->sacrifice>=2;case 6->sight>0;case 7->step>0;case 8->ward>0;default->false;};return enabled(id)&&base&&(id<6||!Config.TRIALS.get()||((trials&1)!=0&&(id!=8||(trials&2)!=0))); }
 public boolean affordable(int id) { return souls>=cost(id)&&red>=redCost(id)&&purple>=purpleCost(id); }
 public SoulProgress buy(int id) {
  if(id<0||id>8||rank(id)>=max(id)||!unlocked(id)||!affordable(id))return this;
  int[] r={harvest,vitality,sacrifice,sight,step,ward,echo,borrow,bond};r[id]++;
  return new SoulProgress(souls-cost(id),r[0],r[1],r[2],r[3],r[4],r[5],cooldown,red-redCost(id),purple-purpleCost(id),r[6],r[7],r[8],trials,selected);
 }
 public int balance(int type){return type==0?souls:type==1?red:purple;}
 public SoulProgress balances(int blue,int red,int purple){return new SoulProgress(blue,harvest,vitality,sacrifice,sight,step,ward,cooldown,red,purple,echo,borrow,bond,trials,selected);}
 public SoulProgress absorb(){return absorb(0);}
 public SoulProgress absorb(int type){return balances(Math.min(CAP,souls+(type==0?1:0)),Math.min(CAP,red+(type==1?1:0)),Math.min(CAP,purple+(type==2?1:0)));}
 public SoulProgress cooldown(long time){return new SoulProgress(souls,harvest,vitality,sacrifice,sight,step,ward,time,red,purple,echo,borrow,bond,trials,selected);}
 public SoulProgress trial(int bit){return new SoulProgress(souls,harvest,vitality,sacrifice,sight,step,ward,cooldown,red,purple,echo,borrow,bond,trials|bit,selected);}
 public SoulProgress select(int id){return new SoulProgress(souls,harvest,vitality,sacrifice,sight,step,ward,cooldown,red,purple,echo,borrow,bond,trials,id);}
}
''')
edit('LastBreath.java','    private static final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> MENUS', '''    public static final DeferredItem<MobSoulItem> RED_SOUL = ITEMS.registerItem("red_soul",p->new MobSoulItem(p,1));
    public static final DeferredItem<MobSoulItem> PURPLE_SOUL = ITEMS.registerItem("purple_soul",p->new MobSoulItem(p,2));
    public static final DeferredItem<DescribedItem> RED_JAR = ITEMS.registerItem("red_soul_jar",p->new DescribedItem(p,"lastbreath.tooltip.soul_jar"),p->p.stacksTo(1).craftRemainder(EMPTY_SOUL_JAR.get()));
    public static final DeferredItem<DescribedItem> PURPLE_JAR = ITEMS.registerItem("purple_soul_jar",p->new DescribedItem(p,"lastbreath.tooltip.soul_jar"),p->p.stacksTo(1).craftRemainder(EMPTY_SOUL_JAR.get()));
    public static final Supplier<AttachmentType<Long>> BORROWED = ATTACHMENTS.register("borrowed_until",()->AttachmentType.builder(()->0L).serialize(com.mojang.serialization.Codec.LONG.fieldOf("until")).sync(net.minecraft.network.codec.ByteBufCodecs.VAR_LONG).build());
    private static final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> MENUS''')
edit('LastBreath.java','output.accept(SOUL_READER.get()); output.accept(RITUAL_TABLE_ITEM.get());','output.accept(SOUL_READER.get()); output.accept(RITUAL_TABLE_ITEM.get()); output.accept(RED_SOUL.get()); output.accept(PURPLE_SOUL.get()); output.accept(RED_JAR.get()); output.accept(PURPLE_JAR.get());')
edit('LastBreath.java','NeoForge.EVENT_BUS.register(SoulHarvest.class);','NeoForge.EVENT_BUS.register(SoulHarvest.class); NeoForge.EVENT_BUS.register(AdvancedSkills.class);')
edit('MobSoulItem.java','    public MobSoulItem(Properties properties) { super(properties,"lastbreath.tooltip.mob_soul"); }','    private final int type;\n    public MobSoulItem(Properties properties) { this(properties,0); }\n    public MobSoulItem(Properties properties,int type) { super(properties,"lastbreath.tooltip.mob_soul");this.type=type; }')
edit('MobSoulItem.java','progress.souls()>=SoulProgress.CAP','progress.balance(type)>=SoulProgress.CAP')
edit('MobSoulItem.java','progress.absorb()','progress.absorb(type)')
edit('MobSoulItem.java','progress.souls()+1','progress.balance(type)+1')
edit('MobSoulItem.java','            RitualFeedback.sound','            AdvancedSkills.burst(server, type);\n            RitualFeedback.sound')
edit('SoulJarItem.java','if (!soul.is(LastBreath.MOB_SOUL.get())) continue;','if (!(soul.getItem() instanceof MobSoulItem)) continue;\n            var filled=soul.is(LastBreath.RED_SOUL.get())?LastBreath.RED_JAR.get():soul.is(LastBreath.PURPLE_SOUL.get())?LastBreath.PURPLE_JAR.get():LastBreath.SOUL_JAR.get();')
edit('SoulJarItem.java','new ItemStack(LastBreath.SOUL_JAR.get())','new ItemStack(filled)')
edit('SoulService.java','stack.is(LastBreath.MOB_SOUL.get())','stack.getItem() instanceof MobSoulItem')
edit('SoulService.java','player.getData(LastBreath.SOUL).isGhost();','player.getData(LastBreath.SOUL).isGhost() && player.getData(LastBreath.BORROWED)<=player.level().getGameTime();')
edit('SoulService.java','if (!soul.isGhost() || !player.isAlive()) return;','if (!ghost(player) || !player.isAlive()) return;')
# Achievements awarded only on successful paid cooperative rescue.
edit('SoulService.java','        heart.shrink(1);','        heart.shrink(1);\n        rescuer.setData(LastBreath.PROGRESS,rescuer.getData(LastBreath.PROGRESS).trial(2));') if False else None
write('SoulHarvest.java','''package me.vlad.lastbreath;
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
  if(Config.BOSSES.get().contains(id)||mob.getType().is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE,net.minecraft.resources.Identifier.fromNamespaceAndPath("lastbreath","bosses"))))return 2;
  if(!(mob instanceof Mob))return -1;
  return mob instanceof net.minecraft.world.entity.monster.Enemy||Config.MONSTERS.get().contains(id)||mob.getType().is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE,net.minecraft.resources.Identifier.fromNamespaceAndPath("lastbreath","monsters")))?1:0;
 }
 @SubscribeEvent public static void drops(LivingDropsEvent event){
  var mob=event.getEntity();if(mob.level().isClientSide())return;
  int type=category(mob);if(type<0)return;
  var source=event.getSource();
  if(!(source.getEntity() instanceof ServerPlayer player)||!player.isAlive()||SoulService.ghost(player))return;
  if(type==2)player.setData(LastBreath.PROGRESS,player.getData(LastBreath.PROGRESS).trial(1));
  var weapon=source.getWeaponItem();
  if(Config.REQUIRE_SWORD.get()&&(!source.is(net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK)||source.getDirectEntity()!=player||weapon==null||!weapon.is(LastBreath.EXECUTIONER_SWORD.get())))return;
  double chance=type==2?Config.PURPLE_CHANCE.get():(type==1?Config.RED_CHANCE.get():Config.BLUE_CHANCE.get())+Config.HARVEST_BONUS.get()*player.getData(LastBreath.PROGRESS).harvest();
  if(mob.getRandom().nextFloat()<Math.min(1,chance))event.getDrops().add(new ItemEntity(mob.level(),mob.getX(),mob.getY()+.3,mob.getZ(),new ItemStack(type==0?LastBreath.MOB_SOUL.get():type==1?LastBreath.RED_SOUL.get():LastBreath.PURPLE_SOUL.get())));
 }
}
''')
