package me.vlad.lastbreath;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class RitualTableBlock extends Block {
    public RitualTableBlock(Properties properties) { super(properties); }
    private static final net.minecraft.world.phys.shapes.VoxelShape SHAPE=net.minecraft.world.phys.shapes.Shapes.or(
            Block.box(0,10,0,16,13,16),Block.box(1,0,1,4,10,4),Block.box(12,0,1,15,10,4),
            Block.box(1,0,12,4,10,15),Block.box(12,0,12,15,10,15),
            Block.box(3,3,2,13,5,4),Block.box(3,3,12,13,5,14),Block.box(2,13,2,14,13.55,14));
    @Override protected net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state,net.minecraft.world.level.BlockGetter level,BlockPos pos,net.minecraft.world.phys.shapes.CollisionContext context) { return SHAPE; }
    @Override
    protected MenuProvider getMenuProvider(BlockState state,Level level,BlockPos pos) {
        return new SimpleMenuProvider((id,inventory,player)->new SoulTreeMenu(id,inventory,ContainerLevelAccess.create(level,pos)),Component.translatable("lastbreath.tree.title"));
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if (!level.isClientSide()) {
            if (RitualAccess.allowed(player,pos)) player.openMenu(getMenuProvider(state,level,pos));
            else if(player instanceof net.minecraft.server.level.ServerPlayer sp) RitualAccess.hint(sp);
        }
        return InteractionResult.SUCCESS;
    }
}
