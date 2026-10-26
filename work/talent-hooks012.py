from pathlib import Path
p=Path('src/main/java/me/vlad/lastbreath/SoulEvents.java');s=p.read_text(encoding='utf-8').replace('AdvancedSkills.tickPlayer(player);','TalentService.tick(player); AdvancedSkills.tickPlayer(player);');p.write_text(s,encoding='utf-8')
p=Path('src/main/java/me/vlad/lastbreath/AdvancedSkills.java');s=p.read_text(encoding='utf-8').replace('private static boolean bond(','public static boolean bond(').replace('q.getData(LastBreath.PROGRESS).bond()>0','(q.getData(LastBreath.PROGRESS).bond()>0||TalentService.get(q).worn(14))');p.write_text(s,encoding='utf-8')
p=Path('src/main/java/me/vlad/lastbreath/SoulHarvest.java');s=p.read_text(encoding='utf-8').replace('boolean normal=','if(type!=2)chance+=TalentService.harvestBonus(player,mob);\n  boolean normal=');p.write_text(s,encoding='utf-8')
p=Path('src/main/java/me/vlad/lastbreath/SoulReaderItem.java');s=p.read_text(encoding='utf-8');a=s.index('            if (player.isShiftKeyDown())');b=s.index('\n        }\n        return',a);s=s[:a]+'''            if(player.isShiftKeyDown()){
                var p=server.getData(LastBreath.PROGRESS);server.sendOverlayMessage(Component.translatable("lastbreath.souls.three",p.souls(),p.red(),p.purple()));
                if(hand==InteractionHand.OFF_HAND)SoulService.list(server);
            }else TalentService.activate(server);'''+s[b:];p.write_text(s,encoding='utf-8')
