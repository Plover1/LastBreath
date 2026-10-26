package me.vlad.lastbreath;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Item-owned progression: survives drops, trades and world saves. */
public record SoulGearData(int level, int branch, int charge, long readyAt) {
    public static final SoulGearData EMPTY = new SoulGearData(0,0,0,0);
    public static final Codec<SoulGearData> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.intRange(0,3).optionalFieldOf("level",0).forGetter(SoulGearData::level),
        Codec.intRange(0,2).optionalFieldOf("branch",0).forGetter(SoulGearData::branch),
        Codec.intRange(0,30000).optionalFieldOf("charge",0).forGetter(SoulGearData::charge),
        Codec.LONG.optionalFieldOf("ready_at",0L).forGetter(SoulGearData::readyAt)
    ).apply(i,SoulGearData::new));
    public SoulGearData spend(int amount,long until) {return new SoulGearData(level,branch,charge-amount,until);}
}
