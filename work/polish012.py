from pathlib import Path
import json
b=Path('src/main/resources/assets/lastbreath')
for i in range(8):
 p=b/'models/item'/f'knowledge_scroll_{i}.json';d=json.loads(p.read_text());d['textures'].pop('paper',None);d['textures']['seal']=['minecraft:block/sea_lantern','minecraft:block/prismarine_bricks','minecraft:block/chiseled_stone_bricks','minecraft:block/sculk'][i%4];p.write_text(json.dumps(d,indent=2))
p=b/'equipment/soul_trousers.json';d=json.loads(p.read_text());d['layers'].pop('humanoid_leggings_baby',None);p.write_text(json.dumps(d,indent=2))
j=Path('src/main/java/me/vlad/lastbreath')
p=j/'client/TalentWheel.java';s=p.read_text();a=s.index(' public static Identifier icon(');z=s.index(' public static int color(',a);s=s[:a]+''' public static Identifier icon(int id){String[] names={"armor_stand","amethyst_shard","ender_eye","golden_carrot","totem_of_undying","ender_pearl","sugar","feather","apple","clock_00","golden_apple","iron_chestplate","fermented_spider_eye","rabbit_foot","lead","compass_16","iron_sword","ghast_tear","echo_shard","nether_star"};return Identifier.parse("minecraft:textures/item/"+names[Math.clamp(id,0,19)]+".png");}
'''+s[z:];s=s.replace('id/5==3?.25f:1','1');p.write_text(s)
p=j/'client/SoulTreeScreen.java';s=p.read_text().replace('int color=t.has(id)?TalentWheel.color(id):t.unlocked(id)?0xff9b8b72:0xff45444b;','int color=TalentWheel.color(id);');a=s.index('     float v=id/5==3?');z=s.index('\n     if(t.worn(id))',a);s=s[:a]+'''     g.blit(icon(id),getX()+5,getY()+4,getX()+21,getY()+20,0,1,0,1);
     if(!t.has(id)&&!t.unlocked(id))g.fill(getX()+1,getY()+1,getX()+25,getY()+25,0x88171b22);'''+s[z:];p.write_text(s)
p=j/'AdvancedSkills.java';s=p.read_text().replace('request.from.getData(LastBreath.PROGRESS).cooldown()<=now&&','Math.max(request.from.getData(LastBreath.PROGRESS).cooldown(),TalentService.get(request.from).cooldown())<=now&&');s=s.replace('var other=request.from;long end=', 'var other=request.from;other.setData(LastBreath.TALENTS,TalentService.get(other).cooldown(now+Config.GOLD_COOLDOWN.get()*20L));long end=');p.write_text(s)
# A second feature reuses the ruin floor but leaves knowledge fragments rather than a trial.
(j/'FragmentRuinFeature.java').write_text('''package me.vlad.lastbreath;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.block.Blocks;
public final class FragmentRuinFeature implements Feature {
 public static final MapCodec<FragmentRuinFeature> CODEC=MapCodec.unit(new FragmentRuinFeature());
 @Override public MapCodec<? extends Feature> codec(){return CODEC;}
 @Override public boolean place(WorldGenLevel level,ChunkGenerator generator,RandomSource random,BlockPos origin){
  if(!new MemoryShrineFeature().place(level,generator,random,origin))return false;
  for(int y=Math.min(111,origin.getY()+1);y>=33;y--){var p=new BlockPos(origin.getX(),y,origin.getZ());if(level.getBlockState(p).is(LastBreath.MEMORY_STONE.get())){
   level.setBlock(p,Blocks.CHEST.defaultBlockState(),2);
   if(level.getBlockEntity(p) instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chest)chest.setItem(13,new net.minecraft.world.item.ItemStack(LastBreath.KNOWLEDGE_FRAGMENT.get(),1+random.nextInt(3)));
   // Broken corner pillars distinguish the abandoned fragment cache from an intact shrine.
   for(int x:new int[]{-3,3})for(int z:new int[]{-3,3})for(int h=1;h<=3;h++)level.setBlock(p.offset(x,h,z),Blocks.AIR.defaultBlockState(),2);
   return true;
  }}return false;
 }
}
''')
p=j/'LastBreath.java';s=p.read_text().replace('    public static final Supplier<com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.levelgen.feature.Feature>> MEMORY_FEATURE=', '    public static final Supplier<com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.levelgen.feature.Feature>> FRAGMENT_FEATURE=FEATURES.register("fragment_ruin",()->FragmentRuinFeature.CODEC);\n    public static final Supplier<com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.levelgen.feature.Feature>> MEMORY_FEATURE=');p.write_text(s)
r=Path('src/main/resources/data/lastbreath')
for folder in ['worldgen/feature','worldgen/placed_feature','neoforge/biome_modifier']:
 p=r/folder/'memory_shrine.json';s=p.read_text().replace('memory_shrine','fragment_ruin').replace('"chance": 24','"chance": 32');(r/folder/'fragment_ruin.json').write_text(s)
