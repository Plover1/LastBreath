from pathlib import Path
p=Path(__file__).resolve().parent.parent/'src/gameTest/java/me/vlad/lastbreath/LastBreathGameTests.java'
s=p.read_text(encoding='utf-8')
s=s.replace('new SoulProgress(100,0,0,0,0,0,0,0)','new SoulProgress(100,0,0,0,0,0,0,0).balances(100,100,5)')
s=s.replace('drop.getItem().is(LastBreath.MOB_SOUL.get())','drop.getItem().getItem() instanceof MobSoulItem')
s=s.replace('new ItemStack(Items.GHAST_TEAR), new ItemStack(LastBreath.SOUL_JAR.get()), new ItemStack(Items.GHAST_TEAR)','new ItemStack(Items.GHAST_TEAR), new ItemStack(LastBreath.RED_JAR.get()), new ItemStack(Items.GHAST_TEAR)')
s=s.replace('    private static final Identifier SKILLS', '    private static final Identifier ADVANCED = Identifier.fromNamespaceAndPath(LastBreath.MODID,"advanced_rituals");\n    private static final Identifier SKILLS')
s=s.replace('    public static void functions(RegisterEvent event) {','    public static void functions(RegisterEvent event) {\n        event.register(Registries.TEST_FUNCTION, helper -> helper.register(ADVANCED, (Consumer<GameTestHelper>)LastBreathGameTests::advanced));')
s=s.replace('        event.registerTest(SKILLS,','        event.registerTest(ADVANCED, new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, ADVANCED), new TestData<>(environment, Identifier.fromNamespaceAndPath(LastBreath.MODID,"empty"), 200, 0, true)));\n        event.registerTest(SKILLS,')
pos=s.index('    private static void skills(')
s=s[:pos]+'''    private static void advanced(GameTestHelper helper) {
        var level=helper.getLevel();var server=level.getServer();var origin=helper.absolutePos(new BlockPos(4,2,4));
        var a=connect(helper,UUID.randomUUID(),"LBRitual");var b=connect(helper,UUID.randomUUID(),"LBBond");
        var pig=helper.spawn(net.minecraft.world.entity.EntityTypes.PIG,new BlockPos(2,2,2));
        var zombie=helper.spawn(net.minecraft.world.entity.EntityTypes.ZOMBIE,new BlockPos(3,2,2));
        var wither=net.minecraft.world.entity.EntityType.WITHER.create(level,net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        try {
            for(var pos:BlockPos.betweenClosed(origin.offset(-10,-1,-10),origin.offset(10,4,10)))level.setBlockAndUpdate(pos,(pos.getY()==origin.getY()-1?Blocks.STONE:Blocks.AIR).defaultBlockState());
            a.setGameMode(GameType.SURVIVAL);b.setGameMode(GameType.SURVIVAL);a.snapTo(origin.getX()+.5,origin.getY(),origin.getZ()+.5);b.snapTo(a.getX()+2,a.getY(),a.getZ());
            check(SoulHarvest.category(pig)==0&&SoulHarvest.category(zombie)==1&&SoulHarvest.category(wither)==2,"Three entity categories are distinct");
            a.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(LastBreath.EXECUTIONER_SWORD.get()));
            check(harvestDrops(wither,level.damageSources().playerAttack(a),0)==1,"Boss drop is guaranteed with executioner sword");
            check((a.getData(LastBreath.PROGRESS).trials()&1)!=0,"Boss trial records actual kill attribution");
            for(var item:java.util.List.of(LastBreath.RED_SOUL.get(),LastBreath.PURPLE_SOUL.get())){a.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(item));a.gameMode.useItem(a,level,a.getMainHandItem(),InteractionHand.MAIN_HAND);}
            check(a.getData(LastBreath.PROGRESS).red()==1&&a.getData(LastBreath.PROGRESS).purple()==1&&a.getData(LastBreath.PROGRESS).souls()==0,"Absorption keeps independent currencies");
            var table=origin.offset(1,0,1);level.setBlockAndUpdate(table,LastBreath.RITUAL_TABLE.get().defaultBlockState());
            a.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(LastBreath.RED_SOUL.get(),3));
            check(RitualAccess.start(a,table,InteractionHand.MAIN_HAND),"Living channel starts with red soul");
            for(int i=0;i<10;i++)a.doTick();a.releaseUsingItem();RitualAccess.tick(a);
            check(!RitualAccess.allowed(a,table)&&a.getMainHandItem().getCount()==3,"Early release does not charge");
            check(RitualAccess.start(a,table,InteractionHand.MAIN_HAND),"Can restart ritual");
            for(int i=0;i<Config.RITUAL_SECONDS.get()*20;i++)a.doTick();
            check(a.containerMenu instanceof SoulTreeMenu&&RitualAccess.allowed(a,table)&&a.getMainHandItem().getCount()==2,"Full channel opens living tree and charges once");
            a.hurtServer(level,level.damageSources().generic(),1);
            check(!RitualAccess.allowed(a,table)&&!(a.containerMenu instanceof SoulTreeMenu),"Damage closes living ritual");
            var all=new SoulProgress(100,3,3,3,1,1,1,0,100,5,1,1,1,3,8);
            a.setData(LastBreath.PROGRESS,all);b.setData(LastBreath.PROGRESS,all);a.setHealth(20);b.setHealth(20);a.invulnerableTime=0;b.invulnerableTime=0;
            AdvancedSkills.activate(a);
            a.hurtServer(level,level.damageSources().generic(),4);
            check(b.getHealth()==20,"Unaccepted bond cannot harm another player");
            a.setHealth(20);a.invulnerableTime=0;AdvancedSkills.activate(b);
            a.hurtServer(level,level.damageSources().generic(),4);
            check(a.getHealth()==18&&b.getHealth()==18,"Mutual bond splits damage without recursion");
            b.snapTo(a.getX()+40,a.getY(),a.getZ());AdvancedSkills.tickPlayer(a);a.invulnerableTime=0;a.hurtServer(level,level.damageSources().generic(),2);
            check(b.getHealth()==18,"Distance breaks bond");
            SoulService.becomeGhost(a);a.setData(LastBreath.PROGRESS,all.select(7));
            a.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.EMERALD,3));AdvancedSkills.activate(a);
            check(!SoulService.ghost(a)&&a.getData(LastBreath.SOUL).isGhost()&&!a.getAbilities().invulnerable,"Borrowed Time is temporary and vulnerable");
            a.setData(LastBreath.BORROWED,Math.max(1,level.getGameTime()-1));AdvancedSkills.tickPlayer(a);
            check(SoulService.ghost(a)&&a.getMainHandItem().getCount()==3,"Expiry restores spirit without copying inventory");
            var noTrial=new SoulProgress(100,3,3,3,1,1,1,0).balances(100,100,5);
            check(noTrial.buy(6).equals(noTrial),"Final power requires trial");
            check(noTrial.trial(1).buy(6).echo()==1,"Boss trial unlocks final power");
            helper.succeed();
        } finally {pig.discard();zombie.discard();wither.discard();server.getPlayerList().remove(a);server.getPlayerList().remove(b);}
    }

'''+s[pos:]
p.write_text(s,encoding='utf-8')
