from pathlib import Path
p=Path('src/gameTest/java/me/vlad/lastbreath/ClientStartupTest.java');s=p.read_text(encoding='utf-8').replace('if (previewTicks++ == 0) {','if (previewTicks++ == 0) {\n            WandererPreview.export();');p.write_text(s,encoding='utf-8')
p=Path('src/gameTest/java/me/vlad/lastbreath/LastBreathGameTests.java');s=p.read_text(encoding='utf-8');needle='            var savedSword=SoulGear.data(sword);'
new='''            var trousers=new ItemStack(LastBreath.SOUL_TROUSERS.get());var boots=new ItemStack(LastBreath.SOUL_BOOTS.get());
            check(SoulGear.purchase(p,trousers,25)&&SoulGear.purchase(p,boots,25),"New armor accepts upgrades");
            SoulGear.write(cloak,new SoulGearData(3,1,100,0));SoulGear.write(trousers,new SoulGearData(3,1,100,0));SoulGear.write(boots,new SoulGearData(3,1,100,0));
            p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS,trousers);p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET,boots);p.doTick();
            check(p.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR)==18,"All three armor upgrades apply in correct slots without ID collisions");
            p.setHealth(20);p.setInvulnerableTime(0);p.damageCooldownTime=0;p.hurtServer(level,level.damageSources().generic(),15);
            check(SoulGear.data(cloak).charge()==85&&SoulGear.data(trousers).charge()==100&&SoulGear.data(boots).charge()==100,"Only one piece pays for defensive activation");
            check(SoulGear.data(trousers).readyAt()==SoulGear.data(cloak).readyAt()&&SoulGear.data(boots).readyAt()==SoulGear.data(cloak).readyAt(),"Set shares defensive cooldown");
            var savedTrousers=SoulGear.data(trousers);var savedBoots=SoulGear.data(boots);
'''
s=s.replace(needle,new+needle)
s=s.replace('            h.succeed();\n        }finally{pig.discard();server.getPlayerList().remove(p);}', '''            check(SoulGear.data(p.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS)).equals(savedTrousers)&&SoulGear.data(p.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET)).equals(savedBoots),"New armor survives disk reload");
            h.succeed();
        }finally{pig.discard();server.getPlayerList().remove(p);}''',1)
p.write_text(s,encoding='utf-8')
