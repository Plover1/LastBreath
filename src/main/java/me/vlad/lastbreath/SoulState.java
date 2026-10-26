package me.vlad.lastbreath;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;
import java.util.Optional;

/** Immutable, serialized on the player and copied by NeoForge during respawn. */
public record SoulState(Optional<GlobalPos> anchor, int revivals) {
    public static final SoulState ALIVE = new SoulState(Optional.empty(), 0);
    public static final MapCodec<SoulState> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            GlobalPos.CODEC.optionalFieldOf("anchor").forGetter(SoulState::anchor),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("revivals", 0).forGetter(SoulState::revivals)
    ).apply(i, SoulState::new));
    public boolean isGhost() { return anchor.isPresent(); }
    public SoulState died(GlobalPos position) { return new SoulState(Optional.of(position), revivals); }
    public SoulState revived() { return new SoulState(Optional.empty(), Math.min(Integer.MAX_VALUE - 1, revivals) + 1); }
}
