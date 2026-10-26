package me.vlad.lastbreath;
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
