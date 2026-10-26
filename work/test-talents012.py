from pathlib import Path
p=Path('src/gameTest/java/me/vlad/lastbreath/LastBreathGameTests.java');s=p.read_text();a=s.index('            check(menu.clickMenuButton(player,0) &&');z=s.index('            var before=player.getData(LastBreath.PROGRESS);',a);s=s[:a]+'''            check(menu.clickMenuButton(player,100) && player.getData(LastBreath.PROGRESS).souls()==95,"New purchase spends exact cost");
            check(!menu.clickMenuButton(player,102),"Branch knowledge is required");
            player.getInventory().setItem(1,new ItemStack(LastBreath.KNOWLEDGE_SCROLLS.get(0).get(),2));
            check(menu.clickMenuButton(player,40)&&TalentService.get(player).knowledge()==1,"Study unlocks the matching branch");
            check(menu.clickMenuButton(player,40)&&player.getInventory().countItem(LastBreath.KNOWLEDGE_FRAGMENT.get())==1,"Duplicate yields one fragment");
            check(menu.clickMenuButton(player,102),"Knowledge unlocks a dependent skill");
            var bank=player.getData(LastBreath.PROGRESS);
            check(menu.clickMenuButton(player,102)&&player.getData(LastBreath.PROGRESS).equals(bank),"Learned active toggles without charging");
            check(!menu.clickMenuButton(player,104),"Gold requires ancient knowledge and both parents");
            var testTalents=SpiritTalents.EMPTY.knowledge(0,true);
            for(int id:new int[]{0,2,5,7,4,9})testTalents=testTalents.learn(id).toggle(id);
            check(testTalents.worn(0)&&testTalents.worn(2)&&testTalents.worn(5)&&!testTalents.worn(7)&&testTalents.worn(4)&&!testTalents.worn(9),"Loadout enforces three regular and one gold");
            check(testTalents.select(19).equals(testTalents)&&testTalents.select(-1).equals(testTalents),"Forged selection cannot equip unlearned skills");
            var migrated=SpiritTalents.migrate(new SoulProgress(100,1,1,0,1,1,1,0));
            check(migrated.has(16)&&migrated.has(8)&&migrated.has(2)&&migrated.has(5)&&migrated.has(11),"Legacy unlocks migrate");
            // Retain dedicated coverage for old saved skills as well as the new menu.
            player.setData(LastBreath.PROGRESS,new SoulProgress(100,1,1,0,1,1,0,0));
'''+s[z:];s=s.replace('var saved=player.getData(LastBreath.PROGRESS);var uuid=player.getUUID();','var saved=player.getData(LastBreath.PROGRESS);var savedTalents=TalentService.get(player);var uuid=player.getUUID();');s=s.replace('check(player.getData(LastBreath.PROGRESS).equals(saved),"Balance, skills and cooldown persist after relog");','check(player.getData(LastBreath.PROGRESS).equals(saved),"Balance, skills and cooldown persist after relog");\n            check(TalentService.get(player).equals(savedTalents),"Knowledge, loadout and learned talents persist after relog");');p.write_text(s)
p=Path('src/main/java/me/vlad/lastbreath/SoulGear.java');s=p.read_text().replace('if(d.charge()+amount>capacity)return false;','if(d.charge()+chargeValue(currency)>capacity)return false;\n            amount=Math.min(amount,capacity-d.charge());');p.write_text(s)
