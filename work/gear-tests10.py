from pathlib import Path
p=Path('src/gameTest/java/me/vlad/lastbreath/LastBreathGameTests.java');s=p.read_text();s=s.replace('    private static final Identifier SKILLS', '    private static final Identifier GEAR = Identifier.fromNamespaceAndPath(LastBreath.MODID,"soul_equipment");\n    private static final Identifier SKILLS',1);s=s.replace('        event.register(Registries.TEST_FUNCTION, helper -> helper.register(ADVANCED', '        event.register(Registries.TEST_FUNCTION, helper -> helper.register(GEAR, (Consumer<GameTestHelper>)LastBreathGameTests::gear));\n        event.register(Registries.TEST_FUNCTION, helper -> helper.register(ADVANCED',1);s=s.replace('        event.registerTest(ADVANCED,', '        event.registerTest(GEAR,new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION,GEAR),new TestData<>(environment,Identifier.fromNamespaceAndPath(LastBreath.MODID,"empty"),200,0,true)));\n        event.registerTest(ADVANCED,',1)
method='''    private static void gear(GameTestHelper h) {
        var level=h.getLevel();var server=level.getServer();var pos=h.absolutePos(new BlockPos(4,2,4));
        var p=connect(h,UUID.randomUUID(),"LBEquipment");var pig=h.spawn(net.minecraft.world.entity.EntityTypes.PIG,new BlockPos(2,2,2));
        try {
            for(var b:BlockPos.betweenClosed(pos.offset(-4,-1,-4),pos.offset(4,4,4)))level.setBlockAndUpdate(b,(b.getY()==pos.getY()-1?Blocks.STONE:Blocks.AIR).defaultBlockState());
            p.snapTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5);p.setGameMode(GameType.SURVIVAL);
            var table=pos.offset(1,0,0);level.setBlockAndUpdate(table,LastBreath.RITUAL_TABLE.get().defaultBlockState());
            var sword=new ItemStack(LastBreath.SOUL_DEVOURER.get());var cloak=new ItemStack(LastBreath.SOUL_CLOAK.get());
            p.setItemInHand(InteractionHand.MAIN_HAND,sword);p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST,cloak);
            p.setData(LastBreath.PROGRESS,SoulProgress.EMPTY.balances(200,100,5));
            var menu=new SoulTreeMenu(1,p.getInventory(),net.minecraft.world.inventory.ContainerLevelAccess.create(level,table));
            check(!menu.clickMenuButton(p,25),"Living player without ritual cannot upgrade");
            p.setData(LastBreath.SOUL,SoulState.ALIVE.died(GlobalPos.of(level.dimension(),pos)));
            check(menu.clickMenuButton(p,25),"Spirit can purchase equipment upgrade at table");
            check(SoulGear.data(sword).level()==1&&p.getData(LastBreath.PROGRESS).souls()==190&&p.getData(LastBreath.PROGRESS).red()==98,"Exact tier price charged once");
            check(menu.clickMenuButton(p,26)&&!menu.clickMenuButton(p,27),"Paths are mutually exclusive");
            check(menu.clickMenuButton(p,24)&&SoulGear.data(sword).charge()==100,"Purple soul fills empty item");
            var bank=p.getData(LastBreath.PROGRESS);check(!menu.clickMenuButton(p,22)&&bank.equals(p.getData(LastBreath.PROGRESS)),"Full item does not consume souls");
            check(menu.clickMenuButton(p,25)&&menu.clickMenuButton(p,25)&&!menu.clickMenuButton(p,25),"Three permanent tiers only");
            double damage=sword.get(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS).compute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,1,net.minecraft.world.entity.EquipmentSlot.MAINHAND);
            check(damage==10,"Maximum sword upgrade gives actual attack attribute 10");
            p.snapTo(pos.getX()+30,pos.getY(),pos.getZ());check(!menu.clickMenuButton(p,22),"Distance check rejects remote gear actions");p.snapTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5);
            p.setData(LastBreath.SOUL,SoulState.ALIVE);
            check(!SoulGear.extraSoul(p,2,0)&&SoulGear.data(sword).charge()==100,"Boss souls do not consume harvest charge");
            check(!SoulGear.extraSoul(p,0,.99f)&&SoulGear.data(sword).charge()==100,"Failed bonus roll is free");
            check(SoulGear.extraSoul(p,0,0)&&SoulGear.data(sword).charge()==95,"Successful harvest spends charge exactly once");
            SoulGear.write(sword,new SoulGearData(3,2,100,0));p.setHealth(10);
            pig.hurtServer(level,level.damageSources().playerAttack(p),100);
            check(p.getHealth()==12&&SoulGear.data(sword).charge()==90,"Actual sword kill heals and spends charge");
            var cooldown=SoulGear.data(sword).readyAt();check(cooldown>level.getGameTime(),"Hunger has persistent item cooldown");
            SoulGear.write(cloak,new SoulGearData(3,1,100,0));p.doTick();p.setHealth(20);p.setInvulnerableTime(0);p.damageCooldownTime=0;
            p.hurtServer(level,level.damageSources().generic(),10);
            float protectedDamage=20-p.getHealth();check(SoulGear.data(cloak).charge()==85&&protectedDamage>0,"Strong hit triggers charged cloak protection");
            p.setHealth(20);p.setInvulnerableTime(0);p.damageCooldownTime=0;p.hurtServer(level,level.damageSources().generic(),10);
            check(20-p.getHealth()>protectedDamage&&SoulGear.data(cloak).charge()==85,"Cooldown blocks repeated protection");
            SoulGear.write(cloak,new SoulGearData(3,2,100,0));p.setHealth(7);p.setInvulnerableTime(0);p.damageCooldownTime=0;p.hurtServer(level,level.damageSources().generic(),3);
            check(p.isAlive()&&SoulGear.data(cloak).charge()==80&&p.hasEffect(net.minecraft.world.effect.MobEffects.SPEED),"Survived low-health hit triggers shroud");
            var savedSword=SoulGear.data(sword);var savedCloak=SoulGear.data(cloak);var uuid=p.getUUID();
            server.getPlayerList().remove(p);p=connect(h,uuid,"LBEquipment");
            check(SoulGear.data(p.getMainHandItem()).equals(savedSword)&&SoulGear.data(p.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST)).equals(savedCloak),"Equipment levels, paths, charge and cooldown survive disk reload");
            h.succeed();
        }finally{pig.discard();server.getPlayerList().remove(p);}
    }

'''
s=s.replace('    private static void advanced(GameTestHelper helper)',method+'    private static void advanced(GameTestHelper helper)');p.write_text(s)
