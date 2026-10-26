package me.vlad.lastbreath;
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
