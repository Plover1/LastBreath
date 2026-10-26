package me.vlad.lastbreath;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;

/** Vanilla action-bar, particle and sound packets keep feedback available without custom networking. */
public final class RitualFeedback {
    private static final Map<ServerPlayer, Long> HINT_UNTIL = new WeakHashMap<>();
    private RitualFeedback() {}

    public static void progress(ServerPlayer player, Component label, int elapsed, int total) {
        int filled = Math.clamp(elapsed * 12 / total, 0, 12);
        player.sendOverlayMessage(Component.empty().append(label.copy().withStyle(ChatFormatting.WHITE))
                .append("  ").append(Component.literal("▰".repeat(filled)).withStyle(ChatFormatting.AQUA))
                .append(Component.literal("▱".repeat(12 - filled)).withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal("  " + Math.max(0, (total - elapsed + 19) / 20) + " s").withStyle(ChatFormatting.GRAY)));
        HINT_UNTIL.put(player, player.level().getGameTime() + 30);
    }

    public static boolean showingProgress(ServerPlayer player) {
        return HINT_UNTIL.getOrDefault(player, Long.MIN_VALUE) > player.level().getGameTime();
    }

    public static void sound(ServerLevel level, Vec3 pos, SoundEvent sound, float volume, float pitch) {
        if (Config.RITUAL_VOLUME.get() > 0)
            level.playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.BLOCKS, volume * Config.RITUAL_VOLUME.get().floatValue(), pitch);
    }

    public static void ring(ServerLevel level, Vec3 center, double phase, double radius) {
        if (!Config.RITUAL_PARTICLES.get()) return;
        for (int i = 0; i < 4; i++) {
            double angle = phase + i * Math.PI / 2;
            level.sendParticles(ParticleTypes.END_ROD, center.x + Math.cos(angle) * radius, center.y,
                    center.z + Math.sin(angle) * radius, 1, 0, .02, 0, .002);
        }
    }

    public static void link(ServerPlayer rescuer, ServerPlayer target) {
        if (!Config.RITUAL_PARTICLES.get()) return;
        Vec3 start = rescuer.position().add(0, 1, 0);
        Vec3 end = target.position().add(0, 1, 0);
        for (int i = 1; i <= 6; i++) {
            Vec3 point = start.lerp(end, i / 7.0);
            rescuer.level().sendParticles(ParticleTypes.END_ROD, point.x, point.y, point.z, 1, .015, .015, .015, 0);
        }
    }
}
