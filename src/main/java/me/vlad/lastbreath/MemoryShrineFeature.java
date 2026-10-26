package me.vlad.lastbreath;
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
  if(!Config.value(Config.RUINS))return false;
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
