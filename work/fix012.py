from pathlib import Path
b=Path('src/main/java/me/vlad/lastbreath')
for name in ['MemoryGuardEvents.java','SoulHarvest.java']:
 p=b/name;s=p.read_text().replace('.getTags().contains(MemoryStoneEntity.GUARD)','.getPersistentData().getBooleanOr(MemoryStoneEntity.GUARD,false)');p.write_text(s)
p=b/'MemoryStoneEntity.java';s=p.read_text().replace('EntityType.SKELETON','EntityTypes.SKELETON').replace('EntityType.WITHER_SKELETON','EntityTypes.WITHER_SKELETON').replace('mob.addTag(GUARD);','mob.addTag(GUARD);mob.getPersistentData().putBoolean(GUARD,true);');p.write_text(s)
p=b/'client/SoulTreeScreen.java';s=p.read_text().replace('"lastbreath.skill."+i','"lastbreath.talent."+i');p.write_text(s)
