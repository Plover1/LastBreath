from pathlib import Path
p=Path('src/main/java/me/vlad/lastbreath/TalentService.java');s=p.read_text(encoding='utf-8').replace('mob.knockback(1.2,v.x,v.z)','mob.knockback(1.2,v.x,v.z,p.damageSources().playerAttack(p),0,true)');p.write_text(s,encoding='utf-8')
p=Path('src/main/java/me/vlad/lastbreath/SoulGear.java');s=p.read_text(encoding='utf-8').replace('TalentService.get(player).has(18)','TalentService.has(player,18)');p.write_text(s,encoding='utf-8')
p=Path('src/gameTest/java/me/vlad/lastbreath/LastBreathGameTests.java');s=p.read_text(encoding='utf-8')
s=s.replace('check(!SoulService.ghost(target), "Default policy must leave non-Hardcore deaths unchanged");','''check(SoulService.ghost(target), "Survival deaths become spirits by default even with legacy hardcoreOnly");
            SoulService.administrativeRevive(target);
            boolean survival=Config.SURVIVAL_SPIRITS.get();
            try {Config.SURVIVAL_SPIRITS.set(false);SoulService.markDead(target);check(!SoulService.ghost(target),"Survival toggle restores Hardcore-only policy");}
            finally {Config.SURVIVAL_SPIRITS.set(survival);}
            boolean enabled=Config.SPIRIT_DEATHS.get();
            try {Config.SPIRIT_DEATHS.set(false);SoulService.markDead(target);check(!SoulService.ghost(target),"Disabled spirit deaths leave player state unchanged");}
            finally {Config.SPIRIT_DEATHS.set(enabled);}''')
needle='check(dispatcher.execute("lastbreath souls set @s blue 29999",admin)==1,"Admin can set soul balance");'
s=s.replace(needle,'''dispatcher.execute("lastbreath ghost LBOther",admin);
                check(SoulService.ghost(other),"Targeted ghost command affects named player");
                dispatcher.execute("lastbreath alive LBOther",admin);
                check(!SoulService.ghost(other)&&SoulService.ghost(target),"Targeted revival does not change command sender");
                '''+needle)
# Exercise living abilities and high progression bits in the existing connected-player skill test.
needle='private static void skills(GameTestHelper helper) {'
assert needle in s
start=s.index(needle);at=s.index('try {',start)+len('try {')
s=s[:at]+'''
            var before=player.getData(LastBreath.TALENTS);var beforeBank=player.getData(LastBreath.PROGRESS);
            boolean talentsEnabled=Config.TALENTS_ENABLED.get();var disabled=Config.DISABLED_TALENTS.get();
            try {
                var talents=new SpiritTalents(SpiritTalents.MASK,15,15,1<<21|1<<27,21,0,true);
                var encoded=SpiritTalents.CODEC.encodeStart(com.mojang.serialization.JsonOps.INSTANCE,talents).getOrThrow();
                check(SpiritTalents.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE,encoded).getOrThrow().equals(talents),"All 28 talent bits round trip");
                player.setData(LastBreath.TALENTS,talents);player.setData(LastBreath.PROGRESS,SoulProgress.EMPTY.balances(10,10,2));
                player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(LastBreath.SOUL_READER.get()));player.setHealth(10);
                Config.DISABLED_TALENTS.set(java.util.List.of(21));TalentService.activate(player);
                check(player.getHealth()==10&&player.getData(LastBreath.PROGRESS).souls()==10,"Disabled living ability has no effect or cost");
                Config.DISABLED_TALENTS.set(java.util.List.of());TalentService.activate(player);
                check(player.getHealth()==14&&player.getData(LastBreath.PROGRESS).souls()==7,"Second Wind heals exactly two hearts for three blue souls");
                TalentService.activate(player);check(player.getData(LastBreath.PROGRESS).souls()==7,"Cooldown prevents repeated payment");
                Config.TALENTS_ENABLED.set(false);player.tickCount=20;TalentService.tick(player);
                check(player.getData(LastBreath.TALENTS).has(27)&&!TalentService.has(player,27),"Disabling preserves progression while removing bonuses");
            } finally {Config.TALENTS_ENABLED.set(talentsEnabled);Config.DISABLED_TALENTS.set(disabled);player.setData(LastBreath.TALENTS,before);player.setData(LastBreath.PROGRESS,beforeBank);player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);}
''' + s[at:]
p.write_text(s,encoding='utf-8')
p=Path('src/main/java/me/vlad/lastbreath/client/SoulTreeScreen.java');s=p.read_text(encoding='utf-8').replace('public void showEquipment(boolean value)','public void showLiving(){equipment=false;living=true;updateButtons();}\n public void showEquipment(boolean value)');p.write_text(s,encoding='utf-8')
p=Path('src/gameTest/java/me/vlad/lastbreath/ClientStartupTest.java');s=p.read_text(encoding='utf-8').replace('if(previewTicks==25)','if(previewTicks==45)preview.showLiving();\n        if(previewTicks==25)').replace('previewTicks==35)','previewTicks==35||previewTicks==55)').replace('previewTicks < 50','previewTicks < 70');p.write_text(s,encoding='utf-8')
p=Path('gradle.properties');s=p.read_text().replace('mod_version=0.12.1','mod_version=0.13.0').replace('cooperative Hardcore progression','cooperative Survival and Hardcore progression');p.write_text(s,encoding='utf-8')
