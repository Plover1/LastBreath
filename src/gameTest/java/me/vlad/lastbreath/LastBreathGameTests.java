package me.vlad.lastbreath;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@EventBusSubscriber(modid = LastBreath.MODID)
public final class LastBreathGameTests {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(LastBreath.MODID, "revival_transaction");
    private static final Identifier RESTART = Identifier.fromNamespaceAndPath(LastBreath.MODID, "restart_persistence");
    private static final Identifier ALTAR = Identifier.fromNamespaceAndPath(LastBreath.MODID, "altar_ritual");
    private static final Identifier CHANNEL = Identifier.fromNamespaceAndPath(LastBreath.MODID, "heart_channel");
    private static final Identifier HARVEST = Identifier.fromNamespaceAndPath(LastBreath.MODID, "soul_harvest");
    private static final Identifier ADVANCED = Identifier.fromNamespaceAndPath(LastBreath.MODID,"advanced_rituals");
    private static final Identifier GEAR = Identifier.fromNamespaceAndPath(LastBreath.MODID,"soul_equipment");
    private static final Identifier SKILLS = Identifier.fromNamespaceAndPath(LastBreath.MODID, "soul_progression");
    private static final Identifier SHRINE=Identifier.fromNamespaceAndPath(LastBreath.MODID,"memory_shrine");
    @SubscribeEvent
    public static void functions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, helper -> helper.register(SHRINE,(Consumer<GameTestHelper>)LastBreathGameTests::shrine));
        event.register(Registries.TEST_FUNCTION, helper -> helper.register(GEAR, (Consumer<GameTestHelper>)LastBreathGameTests::gear));
        event.register(Registries.TEST_FUNCTION, helper -> helper.register(ADVANCED, (Consumer<GameTestHelper>)LastBreathGameTests::advanced));
        event.register(Registries.TEST_FUNCTION, helper -> helper.register(SKILLS, (Consumer<GameTestHelper>)LastBreathGameTests::skills));
        event.register(Registries.TEST_FUNCTION, helper -> helper.register(HARVEST, (Consumer<GameTestHelper>)LastBreathGameTests::harvest));
        event.register(Registries.TEST_FUNCTION, helper -> helper.register(CHANNEL, (Consumer<GameTestHelper>)LastBreathGameTests::channel));
        event.register(Registries.TEST_FUNCTION, helper -> helper.register(ALTAR, (Consumer<GameTestHelper>)LastBreathGameTests::altar));
        event.register(Registries.TEST_FUNCTION, helper -> helper.register(ID, (Consumer<GameTestHelper>)LastBreathGameTests::test));
        event.register(Registries.TEST_FUNCTION, helper -> helper.register(RESTART, (Consumer<GameTestHelper>)LastBreathGameTests::restart));
    }
    @SubscribeEvent
    public static void tests(RegisterGameTestsEvent event) {
        var environment = event.registerEnvironment(ID, new TestEnvironmentDefinition.AllOf(java.util.List.of()));
        event.registerTest(SHRINE,new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION,SHRINE),new TestData<>(environment,Identifier.fromNamespaceAndPath(LastBreath.MODID,"empty"),200,0,true)));
        event.registerTest(GEAR,new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION,GEAR),new TestData<>(environment,Identifier.fromNamespaceAndPath(LastBreath.MODID,"empty"),200,0,true)));
        event.registerTest(ADVANCED, new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, ADVANCED), new TestData<>(environment, Identifier.fromNamespaceAndPath(LastBreath.MODID,"empty"), 200, 0, true)));
        event.registerTest(SKILLS, new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, SKILLS),
                new TestData<>(environment, Identifier.fromNamespaceAndPath(LastBreath.MODID, "empty"), 200, 0, true)));
        event.registerTest(HARVEST, new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, HARVEST),
                new TestData<>(environment, Identifier.fromNamespaceAndPath(LastBreath.MODID, "empty"), 200, 0, true)));
        event.registerTest(CHANNEL, new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, CHANNEL),
                new TestData<>(environment, Identifier.fromNamespaceAndPath(LastBreath.MODID, "empty"), 200, 0, true)));
        event.registerTest(ALTAR, new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, ALTAR),
                new TestData<>(environment, Identifier.fromNamespaceAndPath(LastBreath.MODID, "empty"), 200, 0, true)));
        event.registerTest(ID, new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, ID),
                new TestData<>(environment, Identifier.fromNamespaceAndPath(LastBreath.MODID, "empty"), 200, 0, true)));
        event.registerTest(RESTART, new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, RESTART),
                new TestData<>(environment, Identifier.fromNamespaceAndPath(LastBreath.MODID, "empty"), 200, 0, true)));
    }

    private static void gear(GameTestHelper h) {
        var level=h.getLevel();var server=level.getServer();var pos=h.absolutePos(new BlockPos(4,2,4));
        var p=connect(h,UUID.randomUUID(),"LBEquipment");var pig=h.spawn(net.minecraft.world.entity.EntityTypes.PIG,new BlockPos(2,2,2));
        try {
            for(var b:BlockPos.betweenClosed(pos.offset(-4,-1,-4),pos.offset(4,4,4)))level.setBlockAndUpdate(b,(b.getY()==pos.getY()-1?Blocks.STONE:Blocks.AIR).defaultBlockState());
            p.snapTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5);p.setGameMode(GameType.SURVIVAL);
            var table=pos.offset(1,0,0);level.setBlockAndUpdate(table,LastBreath.RITUAL_TABLE.get().defaultBlockState());
            var sword=new ItemStack(LastBreath.SOUL_DEVOURER.get());var cloak=new ItemStack(LastBreath.SOUL_CLOAK.get());
            p.setItemInHand(InteractionHand.MAIN_HAND,sword);p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST,cloak);
            p.setData(LastBreath.PROGRESS,SoulProgress.EMPTY.balances(200,100,5));
            var menu=new SoulTreeMenu(1,p.getInventory(),net.minecraft.world.inventory.ContainerLevelAccess.create(level,table));
            check(!menu.clickMenuButton(p,25),"Living player without ritual cannot upgrade");
            p.setData(LastBreath.SOUL,SoulState.ALIVE.died(GlobalPos.of(level.dimension(),pos)));
            check(menu.clickMenuButton(p,25),"Spirit can purchase equipment upgrade at table");
            check(SoulGear.data(sword).level()==1&&p.getData(LastBreath.PROGRESS).souls()==190&&p.getData(LastBreath.PROGRESS).red()==98,"Exact tier price charged once");
            check(menu.clickMenuButton(p,26)&&!menu.clickMenuButton(p,27),"Paths are mutually exclusive");
            check(menu.clickMenuButton(p,24)&&SoulGear.data(sword).charge()==100,"Purple soul fills empty item");
            var bank=p.getData(LastBreath.PROGRESS);check(!menu.clickMenuButton(p,22)&&bank.equals(p.getData(LastBreath.PROGRESS)),"Full item does not consume souls");
            check(menu.clickMenuButton(p,25)&&menu.clickMenuButton(p,25)&&!menu.clickMenuButton(p,25),"Three permanent tiers only");
            double damage=sword.get(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS).compute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,1,net.minecraft.world.entity.EquipmentSlot.MAINHAND);
            check(damage==10,"Maximum sword upgrade gives actual attack attribute 10");
            p.snapTo(pos.getX()+30,pos.getY(),pos.getZ());check(!menu.clickMenuButton(p,22),"Distance check rejects remote gear actions");p.snapTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5);
            p.setData(LastBreath.SOUL,SoulState.ALIVE);
            check(!SoulGear.extraSoul(p,2,0)&&SoulGear.data(sword).charge()==100,"Boss souls do not consume harvest charge");
            check(!SoulGear.extraSoul(p,0,.99f)&&SoulGear.data(sword).charge()==100,"Failed bonus roll is free");
            check(SoulGear.extraSoul(p,0,0)&&SoulGear.data(sword).charge()==95,"Successful harvest spends charge exactly once");
            SoulGear.write(sword,new SoulGearData(3,2,100,0));p.setHealth(10);
            pig.hurtServer(level,level.damageSources().playerAttack(p),100);
            check(p.getHealth()==12&&SoulGear.data(sword).charge()==90,"Actual sword kill heals and spends charge");
            var cooldown=SoulGear.data(sword).readyAt();check(cooldown>level.getGameTime(),"Hunger has persistent item cooldown");
            SoulGear.write(cloak,new SoulGearData(3,1,100,0));p.doTick();p.setHealth(20);p.setInvulnerableTime(0);p.damageCooldownTime=0;
            p.hurtServer(level,level.damageSources().generic(),10);
            float protectedDamage=20-p.getHealth();check(SoulGear.data(cloak).charge()==85&&protectedDamage>0,"Strong hit triggers charged cloak protection");
            p.setHealth(20);p.setInvulnerableTime(0);p.damageCooldownTime=0;p.hurtServer(level,level.damageSources().generic(),10);
            check(20-p.getHealth()>protectedDamage&&SoulGear.data(cloak).charge()==85,"Cooldown blocks repeated protection");
            SoulGear.write(cloak,new SoulGearData(3,2,100,0));p.setHealth(7);p.setInvulnerableTime(0);p.damageCooldownTime=0;p.hurtServer(level,level.damageSources().generic(),3);
            check(p.isAlive()&&SoulGear.data(cloak).charge()==80&&p.hasEffect(net.minecraft.world.effect.MobEffects.SPEED),"Survived low-health hit triggers shroud");
            var trousers=new ItemStack(LastBreath.SOUL_TROUSERS.get());var boots=new ItemStack(LastBreath.SOUL_BOOTS.get());
            check(SoulGear.purchase(p,trousers,25)&&SoulGear.purchase(p,boots,25),"New armor accepts upgrades");
            SoulGear.write(cloak,new SoulGearData(3,1,100,0));SoulGear.write(trousers,new SoulGearData(3,1,100,0));SoulGear.write(boots,new SoulGearData(3,1,100,0));
            p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS,trousers);p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET,boots);p.doTick();
            check(p.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR)==18,"All three armor upgrades apply in correct slots without ID collisions");
            p.setHealth(20);p.setInvulnerableTime(0);p.damageCooldownTime=0;p.hurtServer(level,level.damageSources().generic(),15);
            check(SoulGear.data(cloak).charge()==85&&SoulGear.data(trousers).charge()==100&&SoulGear.data(boots).charge()==100,"Only one piece pays for defensive activation");
            check(SoulGear.data(trousers).readyAt()==SoulGear.data(cloak).readyAt()&&SoulGear.data(boots).readyAt()==SoulGear.data(cloak).readyAt(),"Set shares defensive cooldown");
            var savedTrousers=SoulGear.data(trousers);var savedBoots=SoulGear.data(boots);
            var savedSword=SoulGear.data(sword);var savedCloak=SoulGear.data(cloak);var uuid=p.getUUID();
            server.getPlayerList().remove(p);p=connect(h,uuid,"LBEquipment");
            check(SoulGear.data(p.getMainHandItem()).equals(savedSword)&&SoulGear.data(p.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST)).equals(savedCloak),"Equipment levels, paths, charge and cooldown survive disk reload");
            check(SoulGear.data(p.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS)).equals(savedTrousers)&&SoulGear.data(p.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET)).equals(savedBoots),"New armor survives disk reload");
            h.succeed();
        }finally{pig.discard();server.getPlayerList().remove(p);}
    }

    private static void advanced(GameTestHelper helper) {
        var level=helper.getLevel();var server=level.getServer();var origin=helper.absolutePos(new BlockPos(4,2,4));
        var a=connect(helper,UUID.randomUUID(),"LBRitual");var b=connect(helper,UUID.randomUUID(),"LBBond");
        var pig=helper.spawn(net.minecraft.world.entity.EntityTypes.PIG,new BlockPos(2,2,2));
        var zombie=helper.spawn(net.minecraft.world.entity.EntityTypes.ZOMBIE,new BlockPos(3,2,2));
        var wither=net.minecraft.world.entity.EntityTypes.WITHER.create(level,net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        try {
            for(var pos:BlockPos.betweenClosed(origin.offset(-10,-1,-10),origin.offset(10,4,10)))level.setBlockAndUpdate(pos,(pos.getY()==origin.getY()-1?Blocks.STONE:Blocks.AIR).defaultBlockState());
            a.setGameMode(GameType.SURVIVAL);b.setGameMode(GameType.SURVIVAL);a.snapTo(origin.getX()+.5,origin.getY(),origin.getZ()+.5);b.snapTo(a.getX()+2,a.getY(),a.getZ());
            check(SoulHarvest.category(pig)==0&&SoulHarvest.category(zombie)==1&&SoulHarvest.category(wither)==2,"Three entity categories are distinct");
            a.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(LastBreath.EXECUTIONER_SWORD.get()));
            check(harvestDrops(wither,level.damageSources().playerAttack(a),0)==1,"Boss drop is guaranteed with executioner sword");
            check((a.getData(LastBreath.PROGRESS).trials()&1)!=0,"Boss trial records actual kill attribution");
            for(var item:java.util.List.of(LastBreath.RED_SOUL.get(),LastBreath.PURPLE_SOUL.get())){a.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(item));a.gameMode.useItem(a,level,a.getMainHandItem(),InteractionHand.MAIN_HAND);}
            check(a.getData(LastBreath.PROGRESS).red()==1&&a.getData(LastBreath.PROGRESS).purple()==1&&a.getData(LastBreath.PROGRESS).souls()==0,"Absorption keeps independent currencies");
            var table=origin.offset(1,0,1);level.setBlockAndUpdate(table,LastBreath.RITUAL_TABLE.get().defaultBlockState());
            a.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(LastBreath.RED_SOUL.get(),3));
            check(RitualAccess.start(a,table,InteractionHand.MAIN_HAND),"Living channel starts with red soul");
            for(int i=0;i<10;i++)a.doTick();a.releaseUsingItem();RitualAccess.tick(a);
            check(!RitualAccess.allowed(a,table)&&a.getMainHandItem().getCount()==3,"Early release does not charge");
            check(RitualAccess.start(a,table,InteractionHand.MAIN_HAND),"Can restart ritual");
            for(int i=0;i<Config.RITUAL_SECONDS.get()*20;i++)a.doTick();
            check(a.containerMenu instanceof SoulTreeMenu&&RitualAccess.allowed(a,table)&&a.getMainHandItem().getCount()==2,"Full channel opens living tree and charges once");
            a.setInvulnerableTime(0);a.damageCooldownTime=0;
            check(a.hurtServer(level,level.damageSources().generic(),1),"Living ritual player takes real damage");
            check(!RitualAccess.allowed(a,table)&&!(a.containerMenu instanceof SoulTreeMenu),"Damage closes living ritual");
            var all=new SoulProgress(100,3,3,3,1,1,1,0,100,5,1,1,1,3,8);
            a.setData(LastBreath.PROGRESS,all);b.setData(LastBreath.PROGRESS,all);a.setHealth(20);b.setHealth(20);a.setInvulnerableTime(0);a.damageCooldownTime=0;b.setInvulnerableTime(0);b.damageCooldownTime=0;
            AdvancedSkills.activate(a);
            a.hurtServer(level,level.damageSources().generic(),4);
            check(b.getHealth()==20,"Unaccepted bond cannot harm another player");
            a.setHealth(20);a.setInvulnerableTime(0);a.damageCooldownTime=0;AdvancedSkills.activate(b);
            a.hurtServer(level,level.damageSources().generic(),4);
            check(a.getHealth()==18&&b.getHealth()==18,"Mutual bond splits damage without recursion");
            b.snapTo(a.getX()+40,a.getY(),a.getZ());AdvancedSkills.tickPlayer(a);a.setInvulnerableTime(0);a.damageCooldownTime=0;a.hurtServer(level,level.damageSources().generic(),2);
            check(b.getHealth()==18,"Distance breaks bond");
            SoulService.becomeGhost(a);a.setData(LastBreath.PROGRESS,all.select(7));
            a.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.EMERALD,3));AdvancedSkills.activate(a);
            check(!SoulService.ghost(a)&&a.getData(LastBreath.SOUL).isGhost()&&!a.getAbilities().invulnerable,"Borrowed Time is temporary and vulnerable");
            a.setData(LastBreath.BORROWED,Math.max(1,level.getGameTime()-1));AdvancedSkills.tickPlayer(a);
            check(SoulService.ghost(a)&&a.getMainHandItem().getCount()==3,"Expiry restores spirit without copying inventory");
            a.setData(LastBreath.PROGRESS,all.select(6));AdvancedSkills.activate(a);
            var echoes=level.getEntitiesOfClass(net.minecraft.world.entity.decoration.ArmorStand.class,a.getBoundingBox().inflate(10),e->e.entityTags().contains("lastbreath_echo"));
            check(echoes.size()==1,"Echo creates exactly one decoy");
            var decoy=echoes.getFirst();decoy.tickCount=5;
            zombie.snapTo(decoy.getX()+2,decoy.getY(),decoy.getZ());
            AdvancedSkills.tick(new net.neoforged.neoforge.event.tick.ServerTickEvent.Post(()->true,server));
            check(zombie.getTarget()==decoy,"Hostile mob targets echo instead of spirit");
            decoy.discard();
            a.setData(LastBreath.PROGRESS,all.select(7));AdvancedSkills.activate(a);
            var uuid=a.getUUID();a.setHealth(0);a.die(level.damageSources().genericKill());
            check(a.getData(LastBreath.BORROWED)==0&&SoulService.ghost(a),"Death ends Borrowed Time immediately");
            SoulService.finishDeaths(server);a=server.getPlayerList().getPlayer(uuid);
            check(a!=null&&a.isAlive()&&SoulService.ghost(a),"Borrowed death completes the vanilla respawn handshake");
            a.setData(LastBreath.PROGRESS,all.select(7));AdvancedSkills.activate(a);
            server.getPlayerList().remove(a);a=connect(helper,uuid,"LBRitual");
            check(SoulService.ghost(a)&&a.getData(LastBreath.BORROWED)==0,"Relog cannot turn Borrowed Time into permanent life");
            var noTrial=new SoulProgress(100,3,3,3,1,1,1,0).balances(100,100,5);
            check(noTrial.buy(6).equals(noTrial),"Final power requires trial");
            check(noTrial.trial(1).buy(6).echo()==1,"Boss trial unlocks final power");
            helper.succeed();
        } finally {pig.discard();zombie.discard();wither.discard();server.getPlayerList().remove(a);server.getPlayerList().remove(b);}
    }

    private static void shrine(GameTestHelper helper){
        var server=helper.getLevel().getServer();var level=server.getLevel(net.minecraft.world.level.Level.NETHER);
        var p=connect(helper,UUID.randomUUID(),"LBMemory");var origin=new BlockPos(12320,65,12320);
        var guards=new java.util.ArrayList<net.minecraft.world.entity.Mob>();
        try{
            for(var at:BlockPos.betweenClosed(origin.offset(-12,-1,-12),origin.offset(12,5,12)))level.setBlockAndUpdate(at,(at.getY()==64?Blocks.SOUL_SOIL:Blocks.AIR).defaultBlockState());
            check(new MemoryShrineFeature().place(level,level.getChunkSource().getGenerator(),net.minecraft.util.RandomSource.create(12),origin.above(10)),"Shrine generates on an open soul-soil floor");
            check(level.getBlockState(origin).is(LastBreath.MEMORY_STONE.get()),"Worldgen places a real memory stone");
            p.teleportTo(level,origin.getX()+1.5,65,origin.getZ()+.5,Set.of(),0,0,true);p.setGameMode(GameType.SURVIVAL);
            var stone=(MemoryStoneEntity)level.getBlockEntity(origin);int branch=level.getBlockState(origin).getValue(MemoryStoneBlock.BRANCH);
            stone.start(p);
            guards.addAll(stone.guardians());
            check(guards.size()==Config.SHRINE_GUARDS.get(),"Trial spawns configured guardians: actual="+guards.size()+", dimension="+p.level().dimension()+", difficulty="+level.getDifficulty());
            stone.start(p);
            check(stone.guardians().size()==guards.size(),"Second start cannot duplicate a trial");
            for(var m:guards)m.setHealth(0);
            MemoryStoneEntity.tick(level,origin,level.getBlockState(origin),stone);
            check(level.getBlockState(origin).getValue(MemoryStoneBlock.STAGE)==1&&p.getInventory().countItem(LastBreath.KNOWLEDGE_SCROLLS.get(branch).get())==1,"Victory spends stage and gives exactly one matching scroll");
            MemoryStoneEntity.tick(level,origin,level.getBlockState(origin),stone);
            check(p.getInventory().countItem(LastBreath.KNOWLEDGE_SCROLLS.get(branch).get())==1,"Repeated tick cannot duplicate reward");
            stone.start(p);check(!stone.owns(guards.getFirst()),"Ancient trial requires offerings");
            p.getInventory().add(new ItemStack(LastBreath.PURPLE_SOUL.get(),1));p.getInventory().add(new ItemStack(LastBreath.KNOWLEDGE_FRAGMENT.get(),4));
            guards.forEach(net.minecraft.world.entity.Entity::discard);guards.clear();stone.start(p);
            guards.addAll(stone.guardians());
            check(guards.size()==Config.ANCIENT_GUARDS.get(),"Ancient trial spawns stronger wave");
            p.snapTo(origin.getX()+40,65,origin.getZ());MemoryStoneEntity.tick(level,origin,level.getBlockState(origin),stone);
            check(p.getInventory().countItem(LastBreath.PURPLE_SOUL.get())==1&&p.getInventory().countItem(LastBreath.KNOWLEDGE_FRAGMENT.get())==4,"Interrupted ritual does not consume offerings");
            check(guards.stream().noneMatch(net.minecraft.world.entity.Mob::isAlive),"Interrupted ritual cleans guardians");
            p.snapTo(origin.getX()+1.5,65,origin.getZ()+.5);guards.clear();stone.start(p);guards.addAll(stone.guardians());
            check(guards.size()==Config.ANCIENT_GUARDS.get(),"Canceled trial can be retried");
            for(var m:guards)m.setHealth(0);MemoryStoneEntity.tick(level,origin,level.getBlockState(origin),stone);
            check(level.getBlockState(origin).getValue(MemoryStoneBlock.STAGE)==2&&p.getInventory().countItem(LastBreath.KNOWLEDGE_SCROLLS.get(branch+4).get())==1,"Ancient victory awards matching gold scroll");
            check(p.getInventory().countItem(LastBreath.PURPLE_SOUL.get())==0&&p.getInventory().countItem(LastBreath.KNOWLEDGE_FRAGMENT.get())==0,"Ancient offering is consumed once on success");
            stone.start(p);check(stone.guardians().isEmpty(),"Completed shrine cannot be farmed again");
            var cache=origin.offset(32,0,0);
            for(var at:BlockPos.betweenClosed(cache.offset(-4,-1,-4),cache.offset(4,4,4)))level.setBlockAndUpdate(at,(at.getY()==64?Blocks.SOUL_SOIL:Blocks.AIR).defaultBlockState());
            check(new FragmentRuinFeature().place(level,level.getChunkSource().getGenerator(),net.minecraft.util.RandomSource.create(4),cache.above(10)),"Fragment ruin generates");
            check(level.getBlockEntity(cache) instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chest&&chest.getItem(13).is(LastBreath.KNOWLEDGE_FRAGMENT.get()),"Fragment cache contains knowledge rather than a repeatable trial");
            helper.succeed();
        }finally{guards.forEach(net.minecraft.world.entity.Entity::discard);server.getPlayerList().remove(p);}
    }

    private static void skills(GameTestHelper helper) {
        var level=helper.getLevel(); var server=level.getServer();
        var origin=helper.absolutePos(new BlockPos(4,2,4));
        ServerPlayer player=connect(helper,UUID.randomUUID(),"LBAdept");
        var zombie=helper.spawn(net.minecraft.world.entity.EntityTypes.ZOMBIE,new BlockPos(3,2,3));
        try {
            var beforeTalents=player.getData(LastBreath.TALENTS);var beforeBank=player.getData(LastBreath.PROGRESS);
            boolean talentsEnabled=Config.TALENTS_ENABLED.get();var disabled=Config.DISABLED_TALENTS.get();
            try {
                var talents=new SpiritTalents(SpiritTalents.MASK,15,15,1<<21|1<<27,21,0,true);
                var encoded=SpiritTalents.CODEC.encodeStart(com.mojang.serialization.JsonOps.INSTANCE,talents).getOrThrow();
                check(SpiritTalents.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE,encoded).getOrThrow().equals(talents),"All 28 talent bits round trip");
                player.setData(LastBreath.TALENTS,talents);player.setData(LastBreath.PROGRESS,SoulProgress.EMPTY.balances(10,10,2));
                player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(LastBreath.SOUL_READER.get()));player.setHealth(10);
                Config.DISABLED_TALENTS.set(java.util.List.of(21));TalentService.activate(player);
                check(player.getHealth()==10&&player.getData(LastBreath.PROGRESS).souls()==10,"Disabled living ability has no effect or cost");
                Config.DISABLED_TALENTS.set(java.util.List.of());TalentService.activate(player);
                check(player.getHealth()==14&&player.getData(LastBreath.PROGRESS).souls()==7,"Second Wind heals exactly two hearts for three blue souls");
                TalentService.activate(player);check(player.getData(LastBreath.PROGRESS).souls()==7,"Cooldown prevents repeated payment");
                for(int id:new int[]{20,22,23,27}){
                    player.setData(LastBreath.TALENTS,new SpiritTalents(SpiritTalents.MASK,15,15,1<<id,id,0,true));
                    TalentService.activate(player);
                    check(player.getData(LastBreath.TALENTS).cooldown()>server.overworld().getGameTime(),"Living power sets cooldown: "+id);
                }
                check(player.hasEffect(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE)&&player.hasEffect(net.minecraft.world.effect.MobEffects.SPEED)&&player.hasEffect(net.minecraft.world.effect.MobEffects.RESISTANCE),"Living defensive and movement powers apply effects");
                player.removeAllEffects();
                player.tickCount=20;TalentService.tick(player);
                check(player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE).getModifier(Identifier.fromNamespaceAndPath("lastbreath","wanderer_anchor"))!=null,"Passive anchor grants knockback resistance");
                Config.TALENTS_ENABLED.set(false);player.tickCount=20;TalentService.tick(player);
                check(player.getData(LastBreath.TALENTS).has(27)&&!TalentService.has(player,27),"Disabling preserves progression while removing bonuses");
                check(player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE).getModifier(Identifier.fromNamespaceAndPath("lastbreath","wanderer_anchor"))==null,"Disabling removes passive attribute modifiers");
            } finally {Config.TALENTS_ENABLED.set(talentsEnabled);Config.DISABLED_TALENTS.set(disabled);player.setData(LastBreath.TALENTS,beforeTalents);player.setData(LastBreath.PROGRESS,beforeBank);player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);}

            for(var pos:BlockPos.betweenClosed(origin.offset(-10,-1,-10),origin.offset(10,4,10)))
                level.setBlockAndUpdate(pos,(pos.getY()==origin.getY()-1?Blocks.STONE:Blocks.AIR).defaultBlockState());
            player.setGameMode(GameType.SURVIVAL);
            player.snapTo(origin.getX()+.5,origin.getY(),origin.getZ()+.5);
            player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(LastBreath.MOB_SOUL.get(),2));
            player.gameMode.useItem(player,level,player.getMainHandItem(),InteractionHand.MAIN_HAND);
            check(player.getData(LastBreath.PROGRESS).souls()==1 && player.getMainHandItem().getCount()==1,"Absorbing consumes one real soul");
            var table=origin.offset(2,0,0);
            level.setBlockAndUpdate(table,LastBreath.RITUAL_TABLE.get().defaultBlockState());
            var hit=new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(table),net.minecraft.core.Direction.UP,table,false);
            player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);
            player.gameMode.useItemOn(player,level,ItemStack.EMPTY,InteractionHand.MAIN_HAND,hit);
            check(!(player.containerMenu instanceof SoulTreeMenu),"Living players cannot open tree");
            SoulService.becomeGhost(player);
            var readerDrop=new net.minecraft.world.entity.item.ItemEntity(level,player.getX(),player.getY(),player.getZ(),new ItemStack(LastBreath.SOUL_READER.get()));
            readerDrop.setNoPickUpDelay();
            readerDrop.playerTouch(player);
            check(player.getMainHandItem().is(LastBreath.SOUL_READER.get()),"Spirit can collect and equip reader after death");
            readerDrop.discard();
            player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);
            player.setData(LastBreath.PROGRESS,new SoulProgress(100,0,0,0,0,0,0,0).balances(100,100,5));
            player.gameMode.useItemOn(player,level,ItemStack.EMPTY,InteractionHand.MAIN_HAND,hit);
            check(player.containerMenu instanceof SoulTreeMenu,"Spirit opens ritual table through real interaction");
            var menu=(SoulTreeMenu)player.containerMenu;
            check(!menu.clickMenuButton(player,3) && !menu.clickMenuButton(player,99),"Reject prerequisite bypass and invalid IDs");
            check(menu.clickMenuButton(player,100) && player.getData(LastBreath.PROGRESS).souls()==95,"New purchase spends exact cost");
            check(!menu.clickMenuButton(player,102),"Branch knowledge is required");
            player.getInventory().setItem(1,new ItemStack(LastBreath.KNOWLEDGE_SCROLLS.get(0).get(),2));
            check(menu.clickMenuButton(player,40)&&TalentService.get(player).knowledge()==1,"Study unlocks the matching branch");
            check(menu.clickMenuButton(player,40)&&player.getInventory().countItem(LastBreath.KNOWLEDGE_FRAGMENT.get())==1,"Duplicate yields one fragment");
            check(menu.clickMenuButton(player,102),"Knowledge unlocks a dependent skill");
            var bank=player.getData(LastBreath.PROGRESS);
            check(menu.clickMenuButton(player,102)&&player.getData(LastBreath.PROGRESS).equals(bank),"Learned active toggles without charging");
            check(!menu.clickMenuButton(player,104),"Gold requires ancient knowledge and both parents");
            var testTalents=SpiritTalents.EMPTY.knowledge(0,true);
            for(int id:new int[]{0,2,5,7,4,9})testTalents=testTalents.learn(id).toggle(id);
            check(testTalents.worn(0)&&testTalents.worn(2)&&testTalents.worn(5)&&!testTalents.worn(7)&&testTalents.worn(4)&&!testTalents.worn(9),"Loadout enforces three regular and one gold");
            check(testTalents.select(19).equals(testTalents)&&testTalents.select(-1).equals(testTalents),"Forged selection cannot equip unlearned skills");
            var migrated=SpiritTalents.migrate(new SoulProgress(100,1,1,0,1,1,1,0));
            check(migrated.has(16)&&migrated.has(8)&&migrated.has(2)&&migrated.has(5)&&migrated.has(11),"Legacy unlocks migrate");
            // Retain dedicated coverage for old saved skills as well as the new menu.
            player.setData(LastBreath.PROGRESS,new SoulProgress(100,1,1,0,1,1,0,0));
            var before=player.getData(LastBreath.PROGRESS);
            player.snapTo(origin.getX()+30,origin.getY(),origin.getZ());
            check(!menu.clickMenuButton(player,0) && player.getData(LastBreath.PROGRESS).equals(before),"Reject remote menu purchase");
            player.closeContainer();
            player.snapTo(origin.getX()+.5,origin.getY(),origin.getZ()+.5);
            player.setYRot(0);player.setXRot(0);
            var start=player.position();
            SoulSkills.activate(player);
            check(player.distanceToSqr(start)>2 && player.distanceToSqr(start)<=64,"Spirit Step moves to safe nearby ground");
            var after=player.position();SoulSkills.activate(player);
            check(player.position().equals(after),"Cooldown blocks repeated teleport");
            player.snapTo(origin.getX()+.5,origin.getY(),origin.getZ()+.5);
            player.setData(LastBreath.PROGRESS,before.cooldown(0));
            for(var pos:BlockPos.betweenClosed(origin.offset(-3,0,2),origin.offset(3,4,2))) level.setBlockAndUpdate(pos,Blocks.STONE.defaultBlockState());
            SoulSkills.activate(player);
            check(player.getZ()<origin.getZ()+2,"Spirit Step cannot cross solid wall");
            check(SoulService.selfRevive(player,InteractionHand.MAIN_HAND,true)==1 && player.getHealth()==10,"Vitality increases revival health");
            player.setData(LastBreath.PROGRESS,before.cooldown(0));
            zombie.snapTo(player.getX()+2,player.getY(),player.getZ());
            SoulSkills.activate(player);
            check(zombie.hasEffect(net.minecraft.world.effect.MobEffects.GLOWING) && zombie.hasEffect(net.minecraft.world.effect.MobEffects.SLOWNESS),"Veil Sight reveals and slows hostile mob");
            var saved=player.getData(LastBreath.PROGRESS);var savedTalents=TalentService.get(player);var uuid=player.getUUID();
            server.getPlayerList().remove(player);player=connect(helper,uuid,"LBAdept");
            check(player.getData(LastBreath.PROGRESS).equals(saved),"Balance, skills and cooldown persist after relog");
            check(TalentService.get(player).equals(savedTalents),"Knowledge, loadout and learned talents persist after relog");
            boolean old=Config.HARDCORE_ONLY.get();Config.HARDCORE_ONLY.set(false);
            try { player.setHealth(0);player.die(level.damageSources().genericKill());SoulService.finishDeaths(server);player=server.getPlayerList().getPlayer(uuid); }
            finally { Config.HARDCORE_ONLY.set(old); }
            check(player.getData(LastBreath.PROGRESS).equals(saved),"Progress survives real death and respawn");
            player.setData(LastBreath.PROGRESS,SoulProgress.EMPTY);
            check(SoulProgress.EMPTY.buy(0).equals(SoulProgress.EMPTY),"Cannot purchase without souls");
            helper.succeed();
        } finally { zombie.discard();server.getPlayerList().remove(player); }
    }

    private static void harvest(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = connect(helper, UUID.randomUUID(), "LBHunter");
        var pig = helper.spawn(net.minecraft.world.entity.EntityTypes.PIG, new BlockPos(2,2,2));
        var zombie = helper.spawn(net.minecraft.world.entity.EntityTypes.ZOMBIE, new BlockPos(3,2,2));
        try {
            player.setGameMode(GameType.SURVIVAL);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(LastBreath.EXECUTIONER_SWORD.get()));
            check(SoulHarvest.successfulRoll(0) && SoulHarvest.successfulRoll(.04999f) && !SoulHarvest.successfulRoll(.05f) && !SoulHarvest.successfulRoll(.99f), "Chance boundary must be exactly five percent");
            long seed = 0;
            while (net.minecraft.util.RandomSource.create(seed).nextFloat() >= .05f) seed++;
            check(harvestDrops(pig, level.damageSources().playerAttack(player), seed) == 1, "Passive mobs can drop a soul");
            check(harvestDrops(zombie, level.damageSources().playerAttack(player), seed) == 1, "Hostile mobs can drop a soul");
            check(harvestDrops(pig, level.damageSources().onFire(), seed) == 0, "Environmental damage does not qualify");
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_SWORD));
            check(harvestDrops(pig, level.damageSources().playerAttack(player), seed) == 0, "Other swords cannot harvest souls");
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(LastBreath.EXECUTIONER_SWORD.get()));
            long failSeed = 0;
            while (net.minecraft.util.RandomSource.create(failSeed).nextFloat() < .05f) failSeed++;
            check(harvestDrops(pig, level.damageSources().playerAttack(player), failSeed) == 0, "Failed roll produces no soul");
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(LastBreath.EMPTY_SOUL_JAR.get()));
            LastBreath.EMPTY_SOUL_JAR.get().use(level, player, InteractionHand.MAIN_HAND);
            check(player.getMainHandItem().is(LastBreath.EMPTY_SOUL_JAR.get()), "No soul leaves jar empty");
            for (int slot = 1; slot < 36; slot++) player.getInventory().setItem(slot, new ItemStack(Items.STONE, 64));
            player.getInventory().setItem(1, new ItemStack(LastBreath.MOB_SOUL.get(), 2));
            LastBreath.EMPTY_SOUL_JAR.get().use(level, player, InteractionHand.MAIN_HAND);
            check(player.getMainHandItem().is(LastBreath.SOUL_JAR.get()) && player.getInventory().getItem(1).getCount() == 1, "Full inventory capture replaces jar and consumes one soul");
            player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(LastBreath.EMPTY_SOUL_JAR.get()));
            LastBreath.EMPTY_SOUL_JAR.get().use(level, player, InteractionHand.OFF_HAND);
            check(player.getOffhandItem().is(LastBreath.SOUL_JAR.get()) && player.getInventory().getItem(1).isEmpty(), "Offhand jar also works");
            SoulService.becomeGhost(player);
            player.getInventory().setItem(1, new ItemStack(LastBreath.MOB_SOUL.get()));
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(LastBreath.EMPTY_SOUL_JAR.get()));
            LastBreath.EMPTY_SOUL_JAR.get().use(level, player, InteractionHand.MAIN_HAND);
            check(player.getMainHandItem().is(LastBreath.EMPTY_SOUL_JAR.get()) && player.getInventory().getItem(1).getCount() == 1, "Ghost cannot bottle souls");
            var grid = new java.util.ArrayList<ItemStack>(java.util.List.of(
                    new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.DIAMOND), new ItemStack(Items.GOLD_INGOT),
                    new ItemStack(Items.GHAST_TEAR), new ItemStack(LastBreath.RED_JAR.get()), new ItemStack(Items.GHAST_TEAR),
                    new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.DIAMOND), new ItemStack(Items.GOLD_INGOT)));
            var input = net.minecraft.world.item.crafting.CraftingInput.of(3,3,grid);
            var recipe = level.getServer().getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING, input, level).orElseThrow().value();
            check(recipe.assemble(input).is(LastBreath.RETURN_HEART.get()), "Soul jar recipe makes Return Heart");
            check(recipe.getRemainingItems(input).get(4).is(LastBreath.EMPTY_SOUL_JAR.get()), "Crafting returns empty jar");
            grid.set(4, new ItemStack(Items.TOTEM_OF_UNDYING));
            check(level.getServer().getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING, net.minecraft.world.item.crafting.CraftingInput.of(3,3,grid), level).isEmpty(), "Old totem recipe must not match");
            helper.succeed();
        } finally {
            pig.discard(); zombie.discard(); level.getServer().getPlayerList().remove(player);
        }
    }

    private static int harvestDrops(net.minecraft.world.entity.LivingEntity mob, net.minecraft.world.damagesource.DamageSource source, long seed) {
        mob.getRandom().setSeed(seed);
        var drops = new java.util.ArrayList<net.minecraft.world.entity.item.ItemEntity>();
        var event = new net.neoforged.neoforge.event.entity.living.LivingDropsEvent(mob, source, drops, true);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
        return drops.stream().filter(drop -> drop.getItem().getItem() instanceof MobSoulItem).mapToInt(drop -> drop.getItem().getCount()).sum();
    }

    private static void channel(GameTestHelper helper) {
        var level = helper.getLevel();
        var server = level.getServer();
        var origin = helper.absolutePos(new BlockPos(4, 2, 4));
        ServerPlayer rescuer = connect(helper, UUID.randomUUID(), "LBChannel");
        ServerPlayer near = connect(helper, UUID.randomUUID(), "LBNear");
        ServerPlayer far = connect(helper, UUID.randomUUID(), "LBFar");
        try {
            for (BlockPos p : BlockPos.betweenClosed(origin.offset(-5,-1,-5), origin.offset(5,4,5)))
                level.setBlockAndUpdate(p, (p.getY() == origin.getY()-1 ? Blocks.STONE : Blocks.AIR).defaultBlockState());
            rescuer.setGameMode(GameType.SURVIVAL);
            rescuer.snapTo(origin.getX()+.5, origin.getY(), origin.getZ()+.5);
            near.snapTo(origin.getX()+2.5, origin.getY(), origin.getZ()+.5);
            far.snapTo(origin.getX()+4.5, origin.getY(), origin.getZ()+.5);
            SoulService.becomeGhost(near);
            SoulService.becomeGhost(far);
            rescuer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(LastBreath.RETURN_HEART.get()));
            rescuer.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(LastBreath.RETURN_HEART.get()));
            rescuer.gameMode.useItem(rescuer, level, rescuer.getOffhandItem(), InteractionHand.OFF_HAND);
            for (int i = 0; i < 40; i++) rescuer.doTick();
            rescuer.releaseUsingItem();
            check(SoulService.ghost(near) && rescuer.getHealth() == 20 && !rescuer.getOffhandItem().isEmpty(), "Releasing early spends nothing");
            rescuer.gameMode.useItem(rescuer, level, rescuer.getOffhandItem(), InteractionHand.OFF_HAND);
            near.snapTo(origin.getX()+50, origin.getY(), origin.getZ());
            for (int i = 0; i < 100; i++) rescuer.doTick();
            check(SoulService.ghost(far) && !rescuer.getOffhandItem().isEmpty() && !rescuer.isUsingItem(), "Leaving range cancels without switching target");
            near.snapTo(origin.getX()+2.5, origin.getY(), origin.getZ()+.5);
            rescuer.gameMode.useItem(rescuer, level, rescuer.getOffhandItem(), InteractionHand.OFF_HAND);
            for (int i = 0; i < 99; i++) rescuer.doTick();
            check(SoulService.ghost(near) && !rescuer.getOffhandItem().isEmpty(), "Hold requires 100 real item-use ticks");
            rescuer.doTick();
            check(!SoulService.ghost(near) && SoulService.ghost(far), "Only the nearest locked target is revived");
            check(rescuer.getOffhandItem().isEmpty() && !rescuer.getMainHandItem().isEmpty() && rescuer.getHealth() == 14, "Payment uses exactly the active hand and health price");
            rescuer.gameMode.useItem(rescuer, level, rescuer.getMainHandItem(), InteractionHand.MAIN_HAND);
            rescuer.setHealth(6);
            rescuer.getFoodData().setFoodLevel(10); // Disable natural regeneration while checking completion payment.
            for (int i = 0; i < 100; i++) rescuer.doTick();
            check(SoulService.ghost(far) && !rescuer.getMainHandItem().isEmpty(), "Insufficient health at completion spends no heart");
            rescuer.setHealth(20);
            rescuer.gameMode.useItem(rescuer, level, rescuer.getMainHandItem(), InteractionHand.MAIN_HAND);
            rescuer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            for (int i = 0; i < 100; i++) rescuer.doTick();
            check(SoulService.ghost(far), "Changing the held item cancels revival");
            helper.succeed();
        } finally {
            server.getPlayerList().remove(rescuer);
            server.getPlayerList().remove(near);
            server.getPlayerList().remove(far);
        }
    }

    private static void altar(GameTestHelper helper) {
        var level = helper.getLevel();
        var server = level.getServer();
        BlockPos pos = helper.absolutePos(new BlockPos(4, 2, 4));
        ServerPlayer living = connect(helper, UUID.randomUUID(), "LBKeeper");
        ServerPlayer spirit = connect(helper, UUID.randomUUID(), "LBSeeker");
        ServerPlayer other = connect(helper, UUID.randomUUID(), "LBWaiting");
        try {
            for (BlockPos p : BlockPos.betweenClosed(pos.offset(-5,-1,-5), pos.offset(5,5,5)))
                level.setBlockAndUpdate(p, (p.getY() == pos.getY()-1 ? Blocks.STONE : Blocks.AIR).defaultBlockState());
            level.setBlockAndUpdate(pos, LastBreath.RETURN_ALTAR.get().defaultBlockState());
            var altar = (ReturnAltarEntity) level.getBlockEntity(pos);
            living.setGameMode(GameType.SURVIVAL);
            for (var player : java.util.List.of(living, spirit, other))
                player.teleportTo(level, pos.getX()+2.5, pos.getY(), pos.getZ()+.5, Set.of(), 0, 0, true);
            SoulService.becomeGhost(spirit);
            SoulService.becomeGhost(other);
            var hit = new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(pos), net.minecraft.core.Direction.UP, pos, false);
            altar.start(spirit);
            altarTicks(level, pos, altar, 200);
            check(SoulService.ghost(spirit), "Empty altar cannot revive");
            for (int i = 0; i < 4; i++) {
                living.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(LastBreath.RETURN_HEART.get()));
                living.gameMode.useItemOn(living, level, living.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
                check(living.getMainHandItem().isEmpty(), "Charging consumes one Heart");
            }
            living.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(LastBreath.RETURN_HEART.get()));
            living.gameMode.useItemOn(living, level, living.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
            check(!living.getMainHandItem().isEmpty() && level.getBlockState(pos).getValue(ReturnAltarBlock.CHARGES) == 4, "Full altar does not consume Hearts");
            living.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            living.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            living.setShiftKeyDown(true);
            living.gameMode.useItemOn(living, level, ItemStack.EMPTY, InteractionHand.MAIN_HAND, hit);
            living.setShiftKeyDown(false);
            check(level.getBlockState(pos).getValue(ReturnAltarBlock.CHARGES) == 3 && living.getInventory().contains(new ItemStack(LastBreath.RETURN_HEART.get())), "Sneak use retrieves exactly one charge");
            living.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(LastBreath.RETURN_HEART.get()));
            living.gameMode.useItemOn(living, level, living.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
            check(level.getBlockState(pos).getValue(ReturnAltarBlock.CHARGES) == 4, "Retrieved Heart can recharge altar");
            for (int slot = 0; slot < 36; slot++) living.getInventory().setItem(slot, new ItemStack(Items.STONE, 64));
            altar.withdraw(living);
            check(level.getBlockState(pos).getValue(ReturnAltarBlock.CHARGES) == 4, "Full survival inventory preserves charge");
            living.setGameMode(GameType.CREATIVE);
            altar.withdraw(living);
            check(level.getBlockState(pos).getValue(ReturnAltarBlock.CHARGES) == 4, "Full creative inventory must not discard a charged Heart");
            living.setGameMode(GameType.SURVIVAL);
            for (int slot = 0; slot < 36; slot++) living.getInventory().setItem(slot, ItemStack.EMPTY);
            altar.withdraw(spirit);
            check(level.getBlockState(pos).getValue(ReturnAltarBlock.CHARGES) == 4, "Ghosts cannot withdraw charges");
            spirit.gameMode.useItemOn(spirit, level, ItemStack.EMPTY, InteractionHand.MAIN_HAND, hit);
            altar.withdraw(living);
            check(level.getBlockState(pos).getValue(ReturnAltarBlock.CHARGES) == 4, "Active ritual locks withdrawals");
            altarTicks(level, pos, altar, 50);
            spirit.snapTo(pos.getX()+10, pos.getY(), pos.getZ());
            altarTicks(level, pos, altar, 1);
            check(level.getBlockState(pos).getValue(ReturnAltarBlock.CHARGES) == 4, "Leaving cancels without charge");
            spirit.snapTo(pos.getX()+2.5, pos.getY(), pos.getZ()+.5);
            altarTicks(level, pos, altar, 200);
            check(SoulService.ghost(spirit), "Returning does not restart a cancelled ritual");
            // Vanilla block state and block entity save/load preserve charges but cancel the ritual.
            altar.start(spirit);
            var state = level.getBlockState(pos);
            var encoded = net.minecraft.world.level.block.state.BlockState.CODEC.encodeStart(com.mojang.serialization.JsonOps.INSTANCE, state).getOrThrow();
            var decoded = net.minecraft.world.level.block.state.BlockState.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, encoded).getOrThrow();
            check(decoded.getValue(ReturnAltarBlock.CHARGES) == 4, "Charges serialize in block state");
            var restored = (ReturnAltarEntity) net.minecraft.world.level.block.entity.BlockEntity.loadStatic(pos, decoded, altar.saveWithFullMetadata(level.registryAccess()), level.registryAccess());
            restored.setLevel(level);
            altarTicks(level, pos, restored, 200);
            check(SoulService.ghost(spirit), "Loaded altar does not resume an interrupted ritual");
            // Cancel old ritual by moving away, then start via the real ghost interaction path.
            spirit.snapTo(pos.getX()+10, pos.getY(), pos.getZ());
            altarTicks(level, pos, altar, 1);
            spirit.snapTo(pos.getX()+2.5, pos.getY(), pos.getZ()+.5);
            spirit.gameMode.useItemOn(spirit, level, ItemStack.EMPTY, InteractionHand.MAIN_HAND, hit);
            altar.start(other);
            altarTicks(level, pos, altar, 199);
            check(SoulService.ghost(spirit), "Ritual must wait all 200 ticks");
            altarTicks(level, pos, altar, 1);
            check(!SoulService.ghost(spirit) && spirit.getHealth() == 8 && spirit.hasEffect(net.minecraft.world.effect.MobEffects.WEAKNESS), "Completed ritual revives with penalties");
            check(SoulService.ghost(other) && level.getBlockState(pos).getValue(ReturnAltarBlock.CHARGES) == 3, "Only one participant and one charge per ritual");
            // Obstruct every safe position, including the top of the altar.
            for (BlockPos p : BlockPos.betweenClosed(pos.offset(-4,0,-4), pos.offset(4,5,4)))
                if (!p.equals(pos)) level.setBlockAndUpdate(p, Blocks.STONE.defaultBlockState());
            altar.start(other);
            altarTicks(level, pos, altar, 200);
            check(SoulService.ghost(other) && level.getBlockState(pos).getValue(ReturnAltarBlock.CHARGES) == 3, "Unsafe revival keeps charges");
            level.destroyBlock(pos, true);
            var drops = level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, new net.minecraft.world.phys.AABB(pos).inflate(3));
            check(drops.stream().filter(e -> e.getItem().is(LastBreath.RETURN_HEART.get())).mapToInt(e -> e.getItem().getCount()).sum() == 3, "Breaking refunds all remaining Hearts once");
            helper.succeed();
        } finally {
            server.getPlayerList().remove(living);
            server.getPlayerList().remove(spirit);
            server.getPlayerList().remove(other);
        }
    }

    private static void altarTicks(net.minecraft.server.level.ServerLevel level, BlockPos pos, ReturnAltarEntity altar, int ticks) {
        for (int i = 0; i < ticks; i++) ReturnAltarEntity.tick(level, pos, level.getBlockState(pos), altar);
    }

    private static void restart(GameTestHelper helper) {
        var server = helper.getLevel().getServer();
        UUID id = UUID.fromString("d6be8d18-46c7-4f11-a037-3871f0291c0a");
        // GameTest deletes its universe on startup. Preserve only this test player's vanilla save outside it.
        var fixture = java.nio.file.Path.of("lastbreath-restart-player.dat");
        var playerFile = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.PLAYER_DATA_DIR).resolve(id + ".dat");
        boolean existing = java.nio.file.Files.exists(fixture);
        try {
            if (existing) java.nio.file.Files.copy(fixture, playerFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (java.io.IOException e) { throw new IllegalStateException(e); }
        ServerPlayer player = connect(helper, id, "LBPersistence");
        try {
            if (existing) {
                check(SoulService.ghost(player) && player.gameMode() == GameType.ADVENTURE, "Spirit must survive restart and migrate out of Spectator");
                com.mojang.logging.LogUtils.getLogger().info("LASTBREATH: persisted spirit verified after server restart");
            } else {
                player.setData(LastBreath.SOUL, SoulState.ALIVE.died(GlobalPos.of(helper.getLevel().dimension(), helper.absolutePos(new BlockPos(4, 2, 4)))));
                SoulService.enforce(player);
                com.mojang.logging.LogUtils.getLogger().info("LASTBREATH: restart persistence fixture saved; run suite again to verify");
            }
            helper.succeed();
        } finally {
            server.getPlayerList().remove(player);
            try { java.nio.file.Files.copy(playerFile, fixture, java.nio.file.StandardCopyOption.REPLACE_EXISTING); }
            catch (java.io.IOException e) { throw new IllegalStateException(e); }
        }
    }

    private static ServerPlayer connect(GameTestHelper helper, UUID uuid, String name) {
        var server = helper.getLevel().getServer();
        var cookie = CommonListenerCookie.createInitial(new GameProfile(uuid, name), false);
        var player = new ServerPlayer(server, helper.getLevel(), cookie.gameProfile(), cookie.clientInformation());
        server.getPlayerList().loadPlayerData(player.nameAndId()).ifPresent(tag ->
                player.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, server.registryAccess(), tag)));
        var connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        server.getPlayerList().placeNewPlayer(connection, player, cookie);
        player.connection.markClientLoaded();
        return player;
    }
    private static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
    private static void test(GameTestHelper helper) {
        var level = helper.getLevel();
        var server = level.getServer();
        boolean hardcoreOnly = Config.HARDCORE_ONLY.get();
        ServerPlayer rescuer = null;
        ServerPlayer target = null;
        ServerPlayer other = null;
        try {
            Config.HARDCORE_ONLY.set(false);
            BlockPos origin = helper.absolutePos(new BlockPos(4, 2, 4));
            for (BlockPos p : BlockPos.betweenClosed(origin.offset(-5, -1, -5), origin.offset(5, 3, 5))) {
                level.setBlockAndUpdate(p, (p.getY() == origin.getY() - 1 ? Blocks.STONE : Blocks.AIR).defaultBlockState());
            }
            rescuer = connect(helper, UUID.randomUUID(), "LBRescuer");
            target = connect(helper, UUID.randomUUID(), "LBFallen");
            other = connect(helper, UUID.randomUUID(), "LBOther");
            rescuer.setGameMode(GameType.SURVIVAL);
            target.setGameMode(GameType.SURVIVAL);
            other.setGameMode(GameType.SURVIVAL);
            rescuer.teleportTo(level, origin.getX()+.5, origin.getY(), origin.getZ()+.5, Set.of(), 0, 0, true);
            target.teleportTo(level, origin.getX()+2.5, origin.getY(), origin.getZ()+.5, Set.of(), 0, 0, true);
            other.teleportTo(level, origin.getX()-.5, origin.getY(), origin.getZ()+.5, Set.of(), 0, 0, true);
            target.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND, 3));
            target.setHealth(0);
            target.die(level.damageSources().genericKill());
            check(SoulService.ghost(target), "Real death must create a persisted soul");
            UUID deadId = target.getUUID();
            SoulService.finishDeaths(server);
            target = server.getPlayerList().getPlayer(deadId);
            SoulService.enforce(target);
            check(!target.isSpectator() && target.gameMode() == GameType.ADVENTURE && SoulService.ghost(target), "Automatic respawn must create a walking spirit");
            check(target.getAbilities().invulnerable && !target.getAbilities().flying && !target.getAbilities().mayfly, "Spirit walks without creative flight and is protected");
            check(!target.getInventory().contains(new ItemStack(Items.DIAMOND)), "Death must not restore dropped items");
            check(!target.setGameMode(GameType.SURVIVAL), "Spirit cannot change mode");
            check(target.teleportTo(level, origin.getX()+1000, origin.getY(), origin.getZ(), Set.of(), 0, 0, true), "Spirit teleport must be allowed");
            target.teleportTo(level, origin.getX()+2.5, origin.getY(), origin.getZ()+.5, Set.of(), 0, 0, true);
            target.setCamera(rescuer);
            check(target.getCamera() == target, "Spirit camera must not follow other entities");
            check(target.openMenu(new SimpleMenuProvider((id, inventory, player) -> ChestMenu.threeRows(id, inventory), Component.literal("test"))).isPresent(), "Spirit must open containers");
            var chest = target.containerMenu;
            chest.getSlot(0).set(new ItemStack(Items.DIAMOND, 2));
            for (var input : net.minecraft.world.inventory.ContainerInput.values()) {
                chest.clicked(0, 0, input, target);
                check(chest.getSlot(0).getItem().getCount() == 2 && chest.getCarried().isEmpty(), "All container click types must remain read-only: " + input);
            }
            SoulService.enforce(target);
            check(target.containerMenu == chest, "Open chest must remain open during ghost ticks");
            target.closeContainer();
            testSpiritActions(helper, target, origin);

            // Save/remove/reload uses actual vanilla player files and NeoForge attachment serialization.
            UUID targetId = target.getUUID();
            SoulState beforeSave = target.getData(LastBreath.SOUL);
            target.snapTo(origin.getX()+100, origin.getY(), origin.getZ());
            var savedPosition = target.position();
            server.getPlayerList().remove(target);
            target = connect(helper, targetId, "LBFallen");
            SoulService.enforce(target);
            check(target.getData(LastBreath.SOUL).equals(beforeSave) && target.gameMode() == GameType.ADVENTURE, "Relog must preserve spirit and anchor");
            check(target.position().equals(savedPosition), "Relog must preserve free spirit position");
            target.snapTo(origin.getX() + 100, origin.getY(), origin.getZ());
            SoulService.enforce(target);
            check(target.getX() == origin.getX() + 100, "Spirit must not be tethered");
            check(SoulService.revive(rescuer, targetId.toString()) == 0, "Rescue must use current position, not death anchor");
            target.snapTo(origin.getX()+2.5, origin.getY(), origin.getZ()+.5);

            check(SoulService.revive(rescuer, targetId.toString()) == 0 && rescuer.getHealth() == 20, "Missing item must not charge health");
            rescuer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(LastBreath.RETURN_HEART.get()));
            other.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(LastBreath.RETURN_HEART.get()));
            rescuer.setHealth(6);
            check(SoulService.revive(rescuer, targetId.toString()) == 0, "Lethal price must be refused");
            check(rescuer.getHealth() == 6 && !rescuer.getMainHandItem().isEmpty(), "Failed payment must be free");
            rescuer.setHealth(20);
            check(SoulService.revive(target, targetId.toString()) == 0, "Self revival must fail");
            // Remove the entire safe search floor: no charge when space is unsafe.
            for (BlockPos p : BlockPos.betweenClosed(origin.offset(-5,-4,-5), origin.offset(5,-1,5))) level.setBlockAndUpdate(p, Blocks.LAVA.defaultBlockState());
            check(SoulService.revive(rescuer, targetId.toString()) == 0, "Unsafe revival must fail");
            check(rescuer.getHealth() == 20 && !rescuer.getMainHandItem().isEmpty(), "Unsafe revival must not spend resources");
            for (BlockPos p : BlockPos.betweenClosed(origin.offset(-5,-4,-5), origin.offset(5,-1,5))) level.setBlockAndUpdate(p, Blocks.STONE.defaultBlockState());
            check(SoulService.revive(rescuer, targetId.toString()) == 1, "Valid revival must succeed");
            check(!SoulService.ghost(target) && target.gameMode() == GameType.SURVIVAL, "Target must return to survival");
            check(!target.getAbilities().invulnerable && target.getAbilities().mayBuild && target.canBeSeenAsEnemy(), "Revival must restore normal abilities and mob targeting");
            check(target.getHealth() == 8 && rescuer.getHealth() == 14, "Health balance must be 4 hearts / cost 3 hearts");
            check(rescuer.getMainHandItem().isEmpty(), "One heart must be consumed");
            check(SoulService.revive(other, targetId.toString()) == 0 && !other.getMainHandItem().isEmpty() && other.getHealth() == 20, "Second rescuer must not pay for the same soul");
            check(target.getData(LastBreath.SOUL).revivals() == 1, "Revival count must increment once");
            check(target.hasEffect(net.minecraft.world.effect.MobEffects.WEAKNESS), "Revived target must receive weakness");
            server.getPlayerList().remove(target);
            target = connect(helper, targetId, "LBFallen");
            check(!SoulService.ghost(target) && target.getData(LastBreath.SOUL).revivals() == 1, "Completed revival must survive relog");
            // Free commands require OP; item self-rescue remains available without OP.
            var dispatcher = server.getCommands().getDispatcher();
            var source = target.createCommandSourceStack();
            try {
                var originalProgress=target.getData(LastBreath.PROGRESS);
                var admin=source.withPermission(net.minecraft.server.permissions.PermissionSet.ALL_PERMISSIONS);
                dispatcher.execute("lastbreath ghost LBOther",admin);
                check(SoulService.ghost(other),"Targeted ghost command affects named player");
                dispatcher.execute("lastbreath alive LBOther",admin);
                check(!SoulService.ghost(other)&&!SoulService.ghost(target),"Targeted revival does not change command sender");
                check(dispatcher.execute("lastbreath souls set @s blue 29999",admin)==1,"Admin can set soul balance");
                dispatcher.execute("lastbreath souls add @s blue 10",admin);
                check(target.getData(LastBreath.PROGRESS).souls()==30000,"Soul balance clamps at cap");
                dispatcher.execute("lastbreath souls set @s red 12",admin);dispatcher.execute("lastbreath souls set @s purple 3",admin);
                check(target.getData(LastBreath.PROGRESS).red()==12&&target.getData(LastBreath.PROGRESS).purple()==3,"All three currencies supported");
                boolean soulsDenied=false;try{dispatcher.execute("lastbreath souls add @s blue 1",source.withPermission(net.minecraft.server.permissions.PermissionSet.NO_PERMISSIONS));}catch(com.mojang.brigadier.exceptions.CommandSyntaxException expected){soulsDenied=true;}
                check(soulsDenied,"Non-admin soul commands denied");
                boolean negative=false;try{dispatcher.execute("lastbreath souls set @s blue -1",admin);}catch(com.mojang.brigadier.exceptions.CommandSyntaxException expected){negative=true;}
                check(negative,"Negative balances rejected");
                int itemsBefore=target.getInventory().countItem(LastBreath.RED_SOUL.get());
                dispatcher.execute("lastbreath souls give @s red 2",admin);
                check(target.getInventory().countItem(LastBreath.RED_SOUL.get())==itemsBefore+2,"Physical soul command gives items");
                target.setData(LastBreath.PROGRESS,originalProgress);
                check(dispatcher.execute("lastbreath ghost", source.withPermission(net.minecraft.server.permissions.PermissionSet.ALL_PERMISSIONS)) == 1, "OP ghost command");
                boolean denied = false;
                try { dispatcher.execute("lastbreath alive", source.withPermission(net.minecraft.server.permissions.PermissionSet.NO_PERMISSIONS)); }
                catch (com.mojang.brigadier.exceptions.CommandSyntaxException expected) { denied = true; }
                check(denied && SoulService.ghost(target), "Non-OP cannot freely revive");
                check(dispatcher.execute("lastbreath alive", source.withPermission(net.minecraft.server.permissions.PermissionSet.ALL_PERMISSIONS)) == 1, "OP alive command");
            } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) { throw new IllegalStateException(e); }
            check(SoulService.becomeGhost(target) == 1, "Convert without death");
            target.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIRT));
            target.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            var heartDrop = new net.minecraft.world.entity.item.ItemEntity(level, target.getX(), target.getY(), target.getZ(), new ItemStack(LastBreath.RETURN_HEART.get()));
            heartDrop.setNoPickUpDelay();
            heartDrop.playerTouch(target);
            check(target.getOffhandItem().is(LastBreath.RETURN_HEART.get()) && heartDrop.isRemoved(), "Dropped heart must move into free offhand");
            target.snapTo(origin.getX()+100, origin.getY()+100, origin.getZ());
            check(SoulService.selfRevive(target, InteractionHand.OFF_HAND, false) == 0 && !target.getOffhandItem().isEmpty(), "Unsafe self-rescue keeps heart");
            target.snapTo(origin.getX()+.5, origin.getY(), origin.getZ()+.5);
            target.gameMode.useItem(target, level, target.getOffhandItem(), InteractionHand.OFF_HAND);
            check(!SoulService.ghost(target) && target.getOffhandItem().isEmpty() && target.getHealth() == 8, "Offhand right click must consume heart and self-revive");
            check(target.hasEffect(net.minecraft.world.effect.MobEffects.WEAKNESS), "Self-rescue applies weakness");
            SoulService.becomeGhost(target);
            target.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(LastBreath.RETURN_HEART.get()));
            var heartHit = new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(origin.below()), net.minecraft.core.Direction.UP, origin.below(), false);
            target.gameMode.useItemOn(target, level, target.getMainHandItem(), InteractionHand.MAIN_HAND, heartHit);
            check(!SoulService.ghost(target) && target.getMainHandItem().isEmpty(), "Heart must also work when looking at a block");
            Config.HARDCORE_ONLY.set(true);
            SoulService.markDead(target);
            check(SoulService.ghost(target), "Survival deaths become spirits by default even with legacy hardcoreOnly");
            SoulService.administrativeRevive(target);
            boolean survival=Config.SURVIVAL_SPIRITS.get();
            try {Config.SURVIVAL_SPIRITS.set(false);SoulService.markDead(target);check(!SoulService.ghost(target),"Survival toggle restores Hardcore-only policy");}
            finally {Config.SURVIVAL_SPIRITS.set(survival);}
            boolean enabled=Config.SPIRIT_DEATHS.get();
            try {Config.SPIRIT_DEATHS.set(false);SoulService.markDead(target);check(!SoulService.ghost(target),"Disabled spirit deaths leave player state unchanged");}
            finally {Config.SPIRIT_DEATHS.set(enabled);}
            Config.HARDCORE_ONLY.set(false);
            target.teleportTo(level, origin.getX(), level.getMinY() - 200, origin.getZ(), Set.of(), 0, 0, true);
            SoulService.markDead(target);
            check(SoulService.ghost(target) && target.getData(LastBreath.SOUL).anchor().orElseThrow().pos().getY() > level.getMinY(), "Void death must have a recoverable anchor");
            check(!RevivalRules.canPay(6, 6) && RevivalRules.canPay(7, 6), "Payment boundary must leave half a heart");
            check(RevivalRules.cost(6, 2, 3) == 12, "Repeated revival price");
            check(RevivalRules.returnedHealth(8, 4) == 4, "Return health must respect max health");
            helper.succeed();
        } finally {
            Config.HARDCORE_ONLY.set(hardcoreOnly);
            if (rescuer != null) server.getPlayerList().remove(rescuer);
            if (target != null) server.getPlayerList().remove(target);
            if (other != null) server.getPlayerList().remove(other);
        }
    }

    private static void testSpiritActions(GameTestHelper helper, ServerPlayer spirit, BlockPos origin) {
        var level = helper.getLevel();
        var zombie = helper.spawn(net.minecraft.world.entity.EntityTypes.ZOMBIE, new BlockPos(2, 2, 2));
        zombie.setTarget(spirit);
        check(zombie.getTarget() == null && !spirit.canBeSeenAsEnemy(), "Monsters must not target spirits");
        float hp = spirit.getHealth();
        spirit.hurtServer(level, level.damageSources().mobAttack(zombie), 10);
        check(spirit.getHealth() == hp, "Monster damage must not harm spirits");
        float mobHp = zombie.getHealth();
        spirit.attack(zombie);
        check(zombie.getHealth() == mobHp, "Spirit cannot attack monsters");
        zombie.discard();

        BlockPos stone = origin.offset(2, 0, 2);
        level.setBlockAndUpdate(stone, Blocks.STONE.defaultBlockState());
        check(!spirit.gameMode.destroyBlock(stone) && level.getBlockState(stone).is(Blocks.STONE), "Spirit cannot break blocks even through the server game mode API");
        spirit.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIRT, 2));
        var hit = new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(stone), net.minecraft.core.Direction.UP, stone, false);
        spirit.gameMode.useItemOn(spirit, level, spirit.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
        check(level.getBlockState(stone.above()).isAir() && spirit.getMainHandItem().getCount() == 2, "Spirit cannot place or consume held blocks");
        spirit.drop(false);
        check(spirit.getMainHandItem().getCount() == 2, "Dropping items must be blocked before removal from inventory");
        spirit.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

        var dropped = new net.minecraft.world.entity.item.ItemEntity(level, spirit.getX(), spirit.getY(), spirit.getZ(), new ItemStack(Items.EMERALD));
        dropped.setNoPickUpDelay();
        dropped.playerTouch(spirit);
        check(!dropped.getItem().isEmpty() && !spirit.getInventory().contains(new ItemStack(Items.EMERALD)), "Spirit cannot pick up items");
        dropped.discard();

        level.setBlockAndUpdate(stone, Blocks.CHEST.defaultBlockState());
        spirit.gameMode.useItemOn(spirit, level, ItemStack.EMPTY, InteractionHand.MAIN_HAND, hit);
        check(spirit.containerMenu instanceof ChestMenu, "A real chest block must open for spirits");
        spirit.closeContainer();
        level.setBlockAndUpdate(stone, Blocks.LEVER.defaultBlockState().setValue(net.minecraft.world.level.block.LeverBlock.POWERED, false));
        spirit.gameMode.useItemOn(spirit, level, ItemStack.EMPTY, InteractionHand.MAIN_HAND, hit);
        check(level.getBlockState(stone).getValue(net.minecraft.world.level.block.LeverBlock.POWERED), "Spirit must be able to operate a lever");
        level.setBlockAndUpdate(stone, Blocks.AIR.defaultBlockState());
    }
}
