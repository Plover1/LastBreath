package me.vlad.lastbreath;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.Set;
import java.util.UUID;

public final class SoulService {
    // Only used on the server thread, scoped with try/finally around mod-owned transitions.
    private static final ThreadLocal<Boolean> TRANSITION = ThreadLocal.withInitial(() -> false);
    private SoulService() {}
    public static boolean transitioning() { return TRANSITION.get(); }
    public static boolean spiritItem(ItemStack stack) {
        return stack.is(LastBreath.RETURN_HEART.get()) || stack.getItem() instanceof MobSoulItem || stack.is(LastBreath.SOUL_READER.get());
    }
    public static boolean ghost(net.minecraft.world.entity.Entity entity) {
        return entity instanceof net.minecraft.world.entity.player.Player player && player.getData(LastBreath.SOUL).isGhost() && player.getData(LastBreath.BORROWED)<=player.level().getGameTime();
    }

    /** Use vanilla's complete respawn handshake, but our mode event prevents Hardcore's spectator switch. */
    public static void finishDeaths(net.minecraft.server.MinecraftServer server) {
        for (ServerPlayer player : java.util.List.copyOf(server.getPlayerList().getPlayers())) {
            if (ghost(player) && player.isDeadOrDying() && !player.hasDisconnected()) {
                player.connection.handleClientCommand(new net.minecraft.network.protocol.game.ServerboundClientCommandPacket(
                        net.minecraft.network.protocol.game.ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
            }
        }
    }

    public static void markDead(ServerPlayer player) {
        boolean borrowed = player.getData(LastBreath.BORROWED) > 0;
        if (borrowed) player.setData(LastBreath.BORROWED, 0L);
        if ((ghost(player) && !borrowed) || (!borrowed && (!Config.SPIRIT_DEATHS.get() || (Config.HARDCORE_ONLY.get() && !Config.SURVIVAL_SPIRITS.get() && !player.level().getServer().isHardcore())))) return;
        ServerLevel level = player.level();
        BlockPos anchor = findSafe(level, player.blockPosition(), player, 8, 16);
        if (anchor == null) {
            // No destructible soul item: lava and void deaths always have a recoverable anchor.
            level = level.getServer().overworld();
            anchor = level.getRespawnData().pos();
            BlockPos safe = findSafe(level, anchor, player, 8, 16);
            if (safe != null) anchor = safe;
            anchor = new BlockPos(anchor.getX(), Math.clamp(anchor.getY(), level.getMinY() + 1, level.getMaxY() - 2), anchor.getZ());
        }
        player.setData(LastBreath.SOUL, player.getData(LastBreath.SOUL).died(GlobalPos.of(level.dimension(), anchor)));
        player.level().getServer().getPlayerList().broadcastSystemMessage(Component.translatable("lastbreath.death", player.getDisplayName(), anchor.getX(), anchor.getY(), anchor.getZ(), level.dimension().identifier().toString()), false);
    }

    public static void enforce(ServerPlayer player) {
        SoulState soul = player.getData(LastBreath.SOUL);
        if (!ghost(player) || !player.isAlive()) return;
        player.setGameMode(GameType.ADVENTURE);
        var abilities = player.getAbilities();
        if (!abilities.invulnerable || abilities.mayfly || abilities.flying || abilities.mayBuild) {
            abilities.invulnerable = true;
            abilities.mayfly = false;
            abilities.flying = false;
            abilities.mayBuild = false;
            player.onUpdateAbilities();
        }
        player.stopUsingItem();
        player.clearFire();
        player.fallDistance = 0;
        player.setAirSupply(player.getMaxAirSupply());
        player.getFoodData().setFoodLevel(20);
        if (player.getCamera() != player) player.setCamera(player);
        if (Config.GHOST_PARTICLES.get() && player.tickCount % 30 == 0) {
            player.level().sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                    player.getX(), player.getY() + .9, player.getZ(), 1, .2, .35, .2, .005);
        }
        if (player.tickCount % 60 == 0 && !RitualFeedback.showingProgress(player)) player.sendOverlayMessage(Component.translatable("lastbreath.ghost"));
    }

    public static void respawn(ServerPlayer player) {
        if (ghost(player)) {
            GlobalPos anchor = player.getData(LastBreath.SOUL).anchor().orElseThrow();
            ServerLevel level = player.level().getServer().getLevel(anchor.dimension());
            if (level != null) teleport(player, level, Vec3.atBottomCenterOf(anchor.pos()));
        }
        enforce(player);
    }

    public static int becomeGhost(ServerPlayer player) {
        if (!player.isAlive() || ghost(player)) return 0;
        player.closeContainer();
        player.setData(LastBreath.SOUL, player.getData(LastBreath.SOUL).died(GlobalPos.of(player.level().dimension(), player.blockPosition())));
        enforce(player);
        player.level().getServer().getPlayerList().saveAll();
        return 1;
    }

    public static int selfRevive(ServerPlayer player, InteractionHand hand, boolean free) {
        return selfRevive(player, hand, free, player.blockPosition());
    }

    public static int administrativeRevive(ServerPlayer player) {
        if (player.getData(LastBreath.BORROWED) > 0) {
            player.setData(LastBreath.BORROWED, 0L);
            enforce(player);
        }
        return selfRevive(player, InteractionHand.MAIN_HAND, true);
    }

    static int reviveAtAltar(ServerPlayer player, BlockPos altar) {
        if(!Config.ALTAR_REVIVAL.get())return error(player,"lastbreath.feature.disabled");
        return selfRevive(player, InteractionHand.MAIN_HAND, true, altar);
    }

    private static int selfRevive(ServerPlayer player, InteractionHand hand, boolean free, BlockPos origin) {
        if(!free&&!Config.SELF_REVIVAL.get())return error(player,"lastbreath.feature.disabled");
        if (!ghost(player)) return error(player, "lastbreath.not_ghost");
        if (!player.isAlive()) return error(player, "lastbreath.death_screen");
        ItemStack heart = player.getItemInHand(hand);
        if (!free && !heart.is(LastBreath.RETURN_HEART.get())) return error(player, "lastbreath.no_heart");
        BlockPos safe = findSafe(player.level(), origin, player, 3, 3);
        if (safe == null) return error(player, "lastbreath.unsafe");
        boolean previous = TRANSITION.get();
        TRANSITION.set(true);
        try {
            if (!teleport(player, player.level(), Vec3.atBottomCenterOf(safe)) || (player.gameMode()!=GameType.SURVIVAL && !player.setGameMode(GameType.SURVIVAL))) return error(player, "lastbreath.blocked");
            player.closeContainer();
            player.setData(LastBreath.SOUL, player.getData(LastBreath.SOUL).revived());
            if (!free) heart.shrink(1);
            applyReturnEffects(player);
            player.level().getServer().getPlayerList().saveAll();
            player.sendSystemMessage(Component.translatable("lastbreath.self_revived"));
            return 1;
        } finally { TRANSITION.set(previous); }
    }

    private static boolean teleport(ServerPlayer player, ServerLevel level, Vec3 pos) {
        boolean previous = TRANSITION.get();
        TRANSITION.set(true);
        try {
            return player.teleportTo(level, pos.x, pos.y, pos.z, Set.of(), player.getYRot(), player.getXRot(), true);
        } finally { TRANSITION.set(previous); }
    }

    public static int list(ServerPlayer rescuer) {
        rescuer.sendSystemMessage(Component.translatable("lastbreath.list_header").withStyle(ChatFormatting.GOLD));
        int count = 0;
        for (ServerPlayer target : rescuer.level().getServer().getPlayerList().getPlayers()) {
            SoulState soul = target.getData(LastBreath.SOUL);
            if (!soul.isGhost()) continue;
            GlobalPos anchor = GlobalPos.of(target.level().dimension(), target.blockPosition());
            double cost = Math.max(0, RevivalRules.cost(Config.HEALTH_COST.get(), Config.REPEAT_COST.get(), soul.revivals()) - TalentService.legacy(rescuer,2));
            Component entry = Component.translatable("lastbreath.list_entry", target.getDisplayName(), anchor.pos().getX(), anchor.pos().getY(), anchor.pos().getZ(), anchor.dimension().identifier().toString(), cost / 2.0)
                    .withStyle(style -> style.withColor(ChatFormatting.AQUA));
            rescuer.sendSystemMessage(entry);
            count++;
        }
        if (count == 0) rescuer.sendSystemMessage(Component.translatable("lastbreath.no_souls"));
        return count;
    }

    public static int revive(ServerPlayer rescuer, String selector) {
        InteractionHand hand = rescuer.getMainHandItem().is(LastBreath.RETURN_HEART.get()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return revive(rescuer, selector, hand);
    }

    static int revive(ServerPlayer rescuer, String selector, InteractionHand hand) {
        if(!Config.ALLY_REVIVAL.get())return error(rescuer,"lastbreath.feature.disabled");
        ServerPlayer target = rescuer.level().getServer().getPlayerList().getPlayerByName(selector);
        if (target == null) {
            try { target = rescuer.level().getServer().getPlayerList().getPlayer(UUID.fromString(selector)); }
            catch (IllegalArgumentException ignored) { /* User-facing error below. */ }
        }
        if (!rescuer.isAlive() || ghost(rescuer) || rescuer.isSpectator()) return error(rescuer, "lastbreath.not_alive");
        if (target == null) return error(rescuer, "lastbreath.offline");
        if (target == rescuer) return error(rescuer, "lastbreath.self");
        SoulState soul = target.getData(LastBreath.SOUL);
        if (!soul.isGhost()) return error(rescuer, "lastbreath.not_ghost");
        if (!target.isAlive()) return error(rescuer, "lastbreath.death_screen");
        if (rescuer.level() != target.level() || rescuer.distanceToSqr(target) > Math.pow(Config.REVIVE_RADIUS.get(), 2)) return error(rescuer, "lastbreath.too_far");
        ItemStack heart = rescuer.getItemInHand(hand);
        if (heart.isEmpty() || !heart.is(LastBreath.RETURN_HEART.get())) return error(rescuer, "lastbreath.no_heart");
        double cost = Math.max(0, RevivalRules.cost(Config.HEALTH_COST.get(), Config.REPEAT_COST.get(), soul.revivals()) - TalentService.legacy(rescuer,2));
        if (!RevivalRules.canPay(rescuer.getHealth(), cost)) return error(rescuer, "lastbreath.low_health");
        BlockPos safe = findSafe(rescuer.level(), rescuer.blockPosition(), target, 3, 3);
        if (safe == null) return error(rescuer, "lastbreath.unsafe");

        // Commands execute serially on the server thread. No asynchronous work or payment before validation.
        boolean previous = TRANSITION.get();
        TRANSITION.set(true);
        try {
            if (!teleport(target, rescuer.level(), Vec3.atBottomCenterOf(safe))) return error(rescuer, "lastbreath.blocked");
            if (target.gameMode()!=GameType.SURVIVAL && !target.setGameMode(GameType.SURVIVAL)) return error(rescuer, "lastbreath.blocked");
            target.closeContainer();
            target.setData(LastBreath.SOUL, soul.revived());
            heart.shrink(1); // The cooperative rescue always consumes a heart.
            rescuer.setData(LastBreath.PROGRESS,rescuer.getData(LastBreath.PROGRESS).trial(2));
            rescuer.setHealth((float)(rescuer.getHealth() - cost));
            weaken(rescuer, Config.RESCUER_WEAKNESS.get());
            if (TalentService.legacy(rescuer,5)>0) {
                rescuer.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,200,1));
                rescuer.addEffect(new MobEffectInstance(MobEffects.REGENERATION,100,0));
            }
            applyReturnEffects(target);
            rescuer.level().getServer().getPlayerList().saveAll();
            rescuer.level().getServer().getPlayerList().broadcastSystemMessage(Component.translatable("lastbreath.revived", target.getDisplayName(), rescuer.getDisplayName()), false);
            return 1;
        } finally { TRANSITION.set(previous); }
    }

    private static void applyReturnEffects(ServerPlayer target) {
        target.setData(LastBreath.BORROWED,0L);
        AdvancedSkills.returning(target);
        RitualFeedback.sound(target.level(), target.position(), net.minecraft.sounds.SoundEvents.ENCHANTMENT_TABLE_USE, .65f, 1.1f);
        target.setHealth(RevivalRules.returnedHealth(Config.RETURN_HEALTH.get()+2*TalentService.legacy(target,1), target.getMaxHealth()));
        target.clearFire();
        target.fallDistance = 0;
        target.setDeltaMovement(Vec3.ZERO);
        target.setAirSupply(target.getMaxAirSupply());
        weaken(target, Config.RETURN_WEAKNESS.get());
        if (Config.GRACE.get() > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, Config.GRACE.get() * 20, 4));
            target.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, Config.GRACE.get() * 20, 0));
        }
    }

    private static void weaken(ServerPlayer player, int seconds) {
        if (seconds > 0) player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, seconds * 20, 0));
    }
    private static int error(ServerPlayer player, String key) {
        player.sendSystemMessage(Component.translatable(key).withStyle(ChatFormatting.RED));
        return 0;
    }

    static BlockPos findSafe(ServerLevel level, BlockPos origin, ServerPlayer target, int radius, int vertical) {
        for (int r = 0; r <= radius; r++) {
            for (int dy = 0; dy <= vertical; dy++) {
                for (int sign : dy == 0 ? new int[]{1} : new int[]{1, -1}) {
                    for (int dx = -r; dx <= r; dx++) for (int dz = -r; dz <= r; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz)) != r) continue;
                        BlockPos pos = origin.offset(dx, dy * sign, dz);
                        if (safe(level, pos, target)) return pos;
                    }
                }
            }
        }
        return null;
    }

    private static boolean safe(ServerLevel level, BlockPos pos, ServerPlayer target) {
        if (!level.isInsideBuildHeight(pos) || !level.isInsideBuildHeight(pos.above()) || !level.hasChunkAt(pos) || !level.getWorldBorder().isWithinBounds(pos)) return false;
        if (!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) return false;
        AABB box = new AABB(pos.getX() + .2, pos.getY(), pos.getZ() + .2, pos.getX() + .8, pos.getY() + 1.8, pos.getZ() + .8);
        if (!level.noCollision(null, box)) return false;
        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 2, 1))) {
            var state = level.getBlockState(p);
            if (!state.getFluidState().isEmpty() || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE) || state.is(Blocks.LAVA) || state.is(Blocks.MAGMA_BLOCK) || state.is(Blocks.CACTUS) || state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE) || state.is(Blocks.SWEET_BERRY_BUSH) || state.is(Blocks.WITHER_ROSE) || state.is(Blocks.POWDER_SNOW) || state.is(Blocks.NETHER_PORTAL) || state.is(Blocks.END_PORTAL)) return false;
        }
        return true;
    }
}
