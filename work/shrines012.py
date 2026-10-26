from pathlib import Path
b=Path('src/main/java/me/vlad/lastbreath')
p=b/'KnowledgeScrolls.java';s=p.read_text().replace('p.drop(fragments,false)','p.level().addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(p.level(),p.getX(),p.getY(),p.getZ(),fragments))');p.write_text(s)
(b/'MemoryStoneBlock.java').write_text('''package me.vlad.lastbreath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.*;
import net.minecraft.world.phys.BlockHitResult;
public final class MemoryStoneBlock extends BaseEntityBlock {
 public static final IntegerProperty BRANCH=IntegerProperty.create("branch",0,3),STAGE=IntegerProperty.create("stage",0,2);
 public MemoryStoneBlock(Properties p){super(p);registerDefaultState(stateDefinition.any().setValue(BRANCH,0).setValue(STAGE,0));}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState>b){b.add(BRANCH,STAGE);}
 @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new MemoryStoneEntity(p,s);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l,BlockState s,BlockEntityType<T> t){return l.isClientSide()?null:createTickerHelper(t,LastBreath.MEMORY_ENTITY.get(),MemoryStoneEntity::tick);}
 @Override protected InteractionResult useItemOn(ItemStack stack,BlockState s,Level l,BlockPos pos,Player p,InteractionHand hand,BlockHitResult hit){return useWithoutItem(s,l,pos,p,hit);}
 @Override protected InteractionResult useWithoutItem(BlockState s,Level l,BlockPos pos,Player p,BlockHitResult hit){if(p instanceof net.minecraft.server.level.ServerPlayer sp&&l.getBlockEntity(pos) instanceof MemoryStoneEntity stone)stone.start(sp);return InteractionResult.SUCCESS;}
}
''')
(b/'MemoryStoneEntity.java').write_text('''package me.vlad.lastbreath;
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
  if(!(level instanceof ServerLevel server)||!p.isAlive()||p.isSpectator()||SoulService.ghost(p)||!near(p)||!level.dimension().equals(Level.NETHER))return;
  int stage=getBlockState().getValue(MemoryStoneBlock.STAGE);
  if(stage==2||participant!=null){p.sendOverlayMessage(Component.translatable("lastbreath.shrine.busy"));return;}
  if(stage==1&&!offering(p)){p.sendOverlayMessage(Component.translatable("lastbreath.shrine.offering"));return;}
  if(server.getDifficulty()==net.minecraft.world.Difficulty.PEACEFUL){p.sendOverlayMessage(Component.translatable("lastbreath.shrine.peaceful"));return;}
  participant=p;elapsed=0;
  for(int n=0;n<(stage==0?3:5);n++){
   var safe=SoulService.findSafe(server,worldPosition.offset(n%2==0?5:-5,0,n-2),p,3,3);if(safe==null){cancel();return;}
   Mob mob=(stage==0?EntityType.SKELETON:EntityType.WITHER_SKELETON).create(server,EntitySpawnReason.EVENT);if(mob==null){cancel();return;}
   mob.setPos(safe.getX()+.5,safe.getY(),safe.getZ()+.5);mob.addTag(GUARD);mob.setPersistenceRequired();
   mob.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(net.minecraft.world.item.Items.STONE_SWORD));
   // Shrine identity allows orphan guardians to be removed safely after restart/chunk unload.
   mob.getPersistentData().putLong("lastbreath_shrine",worldPosition.asLong());
   if(!server.addFreshEntity(mob)){cancel();return;}mob.setTarget(p);guards.add(mob);
  }
  p.sendSystemMessage(Component.translatable("lastbreath.shrine.started"));
 }
 private void cancel(){guards.forEach(m->{if(m.isAlive())m.discard();});guards.clear();participant=null;elapsed=0;}
 public boolean owns(Mob mob){return participant!=null&&guards.contains(mob);}
 public static void tick(Level l,BlockPos pos,BlockState s,MemoryStoneEntity stone){
  if(!(l instanceof ServerLevel server)||stone.participant==null)return;
  var p=stone.participant;
  if(p.hasDisconnected()||!p.isAlive()||SoulService.ghost(p)||!stone.near(p)||++stone.elapsed>2400||server.getDifficulty()==net.minecraft.world.Difficulty.PEACEFUL){stone.cancel();return;}
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
''')
(b/'MemoryGuardEvents.java').write_text('''package me.vlad.lastbreath;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
@EventBusSubscriber(modid=LastBreath.MODID)
public final class MemoryGuardEvents {
 @SubscribeEvent public static void tick(net.neoforged.neoforge.event.tick.EntityTickEvent.Post e){
  if(e.getEntity() instanceof net.minecraft.world.entity.Mob m&&!m.level().isClientSide()&&m.getTags().contains(MemoryStoneEntity.GUARD)&&m.tickCount%20==0){
   var pos=net.minecraft.core.BlockPos.of(m.getPersistentData().getLongOr("lastbreath_shrine",0L));
   if(!(m.level().getBlockEntity(pos) instanceof MemoryStoneEntity stone)||!stone.owns(m))m.discard();
  }
 }
 @SubscribeEvent public static void drops(net.neoforged.neoforge.event.entity.living.LivingDropsEvent e){if(e.getEntity().getTags().contains(MemoryStoneEntity.GUARD))e.getDrops().clear();}
 @SubscribeEvent public static void xp(net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent e){if(e.getEntity().getTags().contains(MemoryStoneEntity.GUARD))e.setDroppedExperience(0);}
}
''')
(b/'MemoryShrineFeature.java').write_text('''package me.vlad.lastbreath;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.block.Blocks;
/** Small ruins placed only on an unobstructed soul-sand/soul-soil floor. */
public final class MemoryShrineFeature implements Feature {
 public static final MapCodec<MemoryShrineFeature> CODEC=MapCodec.unit(new MemoryShrineFeature());
 @Override public MapCodec<? extends Feature> codec(){return CODEC;}
 @Override public boolean place(WorldGenLevel level,ChunkGenerator generator,RandomSource random,BlockPos origin){
  BlockPos floor=null;
  for(int y=Math.min(110,origin.getY());y>=32;y--){var p=new BlockPos(origin.getX(),y,origin.getZ());var s=level.getBlockState(p);if((s.is(Blocks.SOUL_SAND)||s.is(Blocks.SOUL_SOIL))&&level.getBlockState(p.above()).isAir()&&level.getBlockState(p.above(3)).isAir()){floor=p;break;}}
  if(floor==null)return false;
  for(int x=-3;x<=3;x++)for(int z=-3;z<=3;z++){var p=floor.offset(x,0,z);if(!level.getBlockState(p).isSolid()||!level.getBlockState(p.above()).isAir()||!level.getBlockState(p.above(2)).isAir())return false;}
  for(int x=-3;x<=3;x++)for(int z=-3;z<=3;z++){var p=floor.offset(x,0,z);level.setBlock(p,(random.nextBoolean()?Blocks.POLISHED_BLACKSTONE_BRICKS:Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS).defaultBlockState(),2);}
  for(int x:new int[]{-3,3})for(int z:new int[]{-3,3})for(int y=1;y<=2+random.nextInt(2);y++)level.setBlock(floor.offset(x,y,z),Blocks.POLISHED_BASALT.defaultBlockState(),2);
  level.setBlock(floor.above(),LastBreath.MEMORY_STONE.get().defaultBlockState().setValue(MemoryStoneBlock.BRANCH,random.nextInt(4)),2);
  for(int x:new int[]{-2,2})level.setBlock(floor.offset(x,1,0),Blocks.SOUL_LANTERN.defaultBlockState(),2);
  return true;
 }
}
''')
p=b/'LastBreath.java';s=p.read_text();mark='    private static final DeferredRegister<CreativeModeTab> TABS';insert='''    private static final DeferredRegister<com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.levelgen.feature.Feature>> FEATURES=DeferredRegister.create(Registries.FEATURE_TYPE,MODID);
    public static final Supplier<com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.levelgen.feature.Feature>> MEMORY_FEATURE=FEATURES.register("memory_shrine",()->MemoryShrineFeature.CODEC);
    public static final net.neoforged.neoforge.registries.DeferredBlock<MemoryStoneBlock> MEMORY_STONE=BLOCKS.registerBlock("memory_stone",MemoryStoneBlock::new,p->p.strength(-1,3600000).noLootTable().lightLevel(s->5));
    public static final DeferredItem<net.minecraft.world.item.BlockItem> MEMORY_STONE_ITEM=ITEMS.registerSimpleBlockItem(MEMORY_STONE);
    public static final Supplier<net.minecraft.world.level.block.entity.BlockEntityType<MemoryStoneEntity>> MEMORY_ENTITY=BLOCK_ENTITIES.register("memory_stone",()->new net.minecraft.world.level.block.entity.BlockEntityType<>(MemoryStoneEntity::new,MEMORY_STONE.get()));
''';s=s.replace(mark,insert+mark).replace('COMPONENTS.register(bus);','FEATURES.register(bus); COMPONENTS.register(bus);').replace('output.accept(RETURN_HEART.get());','output.accept(MEMORY_STONE_ITEM.get());output.accept(RETURN_HEART.get());');p.write_text(s)
p=b/'SoulHarvest.java';s=p.read_text().replace('int type=category(mob);','if(mob.getTags().contains(MemoryStoneEntity.GUARD))return;\n  int type=category(mob);');p.write_text(s)
import json
r=Path('src/main/resources');data={
'data/lastbreath/worldgen/feature/memory_shrine.json':{'type':'lastbreath:memory_shrine'},
'data/lastbreath/worldgen/placed_feature/memory_shrine.json':{'feature':'lastbreath:memory_shrine','placement':[{'type':'minecraft:rarity_filter','chance':24},{'type':'minecraft:in_square'},{'type':'minecraft:height_range','height':{'type':'minecraft:constant','value':{'absolute':110}}},{'type':'minecraft:biome'}]},
'data/lastbreath/neoforge/biome_modifier/memory_shrine.json':{'type':'neoforge:add_features','biomes':'minecraft:soul_sand_valley','features':'lastbreath:memory_shrine','step':'surface_structures'},
'assets/lastbreath/blockstates/memory_stone.json':{'variants':{'':{'model':'lastbreath:block/memory_stone'}}},
'assets/lastbreath/models/block/memory_stone.json':{'parent':'minecraft:block/cube_bottom_top','textures':{'side':'minecraft:block/chiseled_polished_blackstone','top':'minecraft:block/lodestone_top','bottom':'minecraft:block/polished_blackstone'}},
'assets/lastbreath/items/memory_stone.json':{'model':{'type':'minecraft:model','model':'lastbreath:block/memory_stone'}}}
for n,d in data.items():p=r/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,indent=2))
for lang in ['uk_ua','en_us']:
 p=r/'assets/lastbreath/lang'/f'{lang}.json';d=json.loads(p.read_text(encoding='utf8'));pairs={'block.lastbreath.memory_stone':('Камінь пам’яті','Memory Stone'),'lastbreath.shrine.busy':('Камінь зайнятий або вже віддав усі знання','The stone is busy or has shared all its knowledge'),'lastbreath.shrine.offering':('Давнє випробування: 4 уламки знань і 1 фіолетова душа. Оплата після перемоги.','Ancient trial: 4 knowledge fragments and 1 purple soul, paid on victory.'),'lastbreath.shrine.peaceful':('Для випробування потрібна складність вище мирної','Trials require a difficulty above Peaceful'),'lastbreath.shrine.started':('Здолайте вартових! Залишайтеся в межах 24 блоків від каменя.','Defeat the guardians! Stay within 24 blocks of the stone.'),'lastbreath.shrine.complete':('Пам’ять звільнено. Ви отримали сувій.','Memory released. You received a scroll.')}
 for k,v in pairs.items():d[k]=v[0 if lang=='uk_ua' else 1]
 p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n',encoding='utf8')
