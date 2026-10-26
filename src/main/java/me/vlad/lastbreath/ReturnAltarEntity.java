package me.vlad.lastbreath;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class ReturnAltarEntity extends BlockEntity {
    // Intentionally transient: unloading/restarting cancels the ritual without charging anything.
    private UUID participant;
    private int progress;
    private boolean refunded;
    public static final int RITUAL_TICKS = 200;

    public ReturnAltarEntity(BlockPos pos, BlockState state) { super(LastBreath.ALTAR_ENTITY.get(), pos, state); }

    public void start(ServerPlayer player) {
        if(!Config.ALTAR_REVIVAL.get()){player.sendOverlayMessage(Component.translatable("lastbreath.feature.disabled"));return;}
        if (!SoulService.ghost(player) || !player.isAlive() || player.level() != level || !near(player)) return;
        if (getBlockState().getValue(ReturnAltarBlock.CHARGES) == 0) {
            player.sendSystemMessage(Component.translatable("lastbreath.altar.empty"));
        } else if (participant != null) {
            player.sendSystemMessage(Component.translatable("lastbreath.altar.busy"));
        } else {
            participant = player.getUUID();
            progress = 0;
            player.sendSystemMessage(Component.translatable("lastbreath.altar.started"));
            RitualFeedback.sound(player.level(), Vec3.atCenterOf(worldPosition), net.minecraft.sounds.SoundEvents.BEACON_ACTIVATE, .35f, 1.2f);
        }
    }

    private boolean near(ServerPlayer player) {
        return player.position().distanceToSqr(Vec3.atCenterOf(worldPosition)) <= 16;
    }

    public void withdraw(ServerPlayer player) {
        if (SoulService.ghost(player) || !player.isAlive() || player.level() != level || !near(player)) return;
        if (participant != null) {
            player.sendOverlayMessage(Component.translatable("lastbreath.altar.busy"));
            return;
        }
        int charges = getBlockState().getValue(ReturnAltarBlock.CHARGES);
        if (charges == 0) {
            player.sendOverlayMessage(Component.translatable("lastbreath.altar.empty"));
            return;
        }
        ItemStack heart = new ItemStack(LastBreath.RETURN_HEART.get());
        if (player.getInventory().getFreeSlot() < 0 || !player.getInventory().add(heart)) {
            player.sendOverlayMessage(Component.translatable("lastbreath.altar.inventory_full"));
            return;
        }
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ReturnAltarBlock.CHARGES, charges - 1));
        player.sendOverlayMessage(Component.translatable("lastbreath.altar.withdrawn", charges - 1));
        RitualFeedback.sound(player.level(), Vec3.atCenterOf(worldPosition), net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_HIT, .5f, .8f);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ReturnAltarEntity altar) {
        if (!(level instanceof ServerLevel server) || altar.participant == null) return;
        ServerPlayer player = server.getServer().getPlayerList().getPlayer(altar.participant);
        if (!Config.ALTAR_REVIVAL.get() || player == null || player.hasDisconnected() || player.level() != level || !player.isAlive()
                || !SoulService.ghost(player) || !altar.near(player) || state.getValue(ReturnAltarBlock.CHARGES) == 0) {
            altar.cancel(player);
            return;
        }
        if (altar.progress % 5 == 0) {
            RitualFeedback.progress(player, Component.translatable("lastbreath.ritual.altar"), altar.progress, RITUAL_TICKS);
            RitualFeedback.ring(server, Vec3.atBottomCenterOf(pos).add(0, 1.05, 0), altar.progress * .12, .6);
        }
        if (altar.progress % 40 == 0) {
            RitualFeedback.sound(server, Vec3.atCenterOf(pos), net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME, .25f, .7f + altar.progress / 200f);
        }
        if (++altar.progress < RITUAL_TICKS) return;
        // Server ticks serialize completion. Reserve this altar until the single revival returns.
        if (SoulService.reviveAtAltar(player, pos) == 1) {
            level.setBlockAndUpdate(pos, state.setValue(ReturnAltarBlock.CHARGES, state.getValue(ReturnAltarBlock.CHARGES) - 1));
            if (Config.RITUAL_PARTICLES.get()) server.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1, player.getZ(), 12, .4, .6, .4, .02);
        }
        altar.participant = null;
        altar.progress = 0;
    }

    private void cancel(ServerPlayer player) {
        if (player != null) player.sendSystemMessage(Component.translatable("lastbreath.altar.cancelled"));
        participant = null;
        progress = 0;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel server && !refunded) {
            refunded = true;
            cancel(participant == null ? null : server.getServer().getPlayerList().getPlayer(participant));
            for (int i = 0; i < getBlockState().getValue(ReturnAltarBlock.CHARGES); i++) {
                Block.popResource(level, worldPosition, new ItemStack(LastBreath.RETURN_HEART.get()));
            }
        }
        super.preRemoveSideEffects(pos, state);
    }
}
