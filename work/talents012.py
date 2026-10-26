from pathlib import Path
p=Path('src/main/java/me/vlad/lastbreath/LastBreath.java');s=p.read_text(encoding='utf-8');needle='    public static final Supplier<AttachmentType<SoulProgress>> PROGRESS'
i=s.index(needle);s=s[:i]+'''    public static final Supplier<AttachmentType<SpiritTalents>> TALENTS = ATTACHMENTS.register("talents",()->AttachmentType.builder(()->SpiritTalents.EMPTY).serialize(SpiritTalents.CODEC.fieldOf("talents")).copyOnDeath().sync(net.minecraft.network.codec.ByteBufCodecs.fromCodec(SpiritTalents.CODEC)).build());
'''+s[i:];p.write_text(s,encoding='utf-8')
