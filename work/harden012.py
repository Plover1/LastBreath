from pathlib import Path
b=Path('src/main/java/me/vlad/lastbreath')
p=b/'client/TalentWheel.java';s=p.read_text().replace('mc.player==null||!TalentService.reader(mc.player)','mc.player==null||mc.gui.hud.isHidden()||!TalentService.reader(mc.player)');s=s.replace('private static final KeyMapping.Category','private static int lastSelected=-1;private static long selectedAt;\n private static final KeyMapping.Category');s=s.replace('int x=mc.getWindow()', 'if(id!=lastSelected){lastSelected=id;selectedAt=System.currentTimeMillis();}\n  int x=mc.getWindow()');s=s.replace('long remaining=Math.max','if(System.currentTimeMillis()-selectedAt<2500){var name=Component.translatable("lastbreath.talent."+id);g.text(mc.font,name,x+30-mc.font.width(name),y-14,0xffe9dec7);}\n  long remaining=Math.max');s=s.replace('g.blit(icon(id),x-11,y-11,x+11,y+11,0,1,0,id/5==3?.25f:1);','g.blit(icon(id),x-11,y-11,x+11,y+11,0,1,0,id/5==3?.25f:1);if(!TalentService.form(minecraft.player,id))g.fill(x-19,y-19,x+19,y+19,0x99171e27);');# rebind release supports mouse and keyboard via KeyMapping API isDown duringGUI not reliable use type
p.write_text(s)
p=b/'SoulEvents.java';s=p.read_text().replace('if (SoulService.ghost(event.getPlayer()) && (','if (LastBreath.KNOWLEDGE_SCROLLS.stream().anyMatch(scroll->event.getItemEntity().getItem().is(scroll.get()))) return;\n        if (SoulService.ghost(event.getPlayer()) && (');p.write_text(s)
p=b/'Config.java';s=p.read_text().replace('    public static final ModConfigSpec SPEC = B.build();','''    public static final ModConfigSpec.IntValue TALENT_COOLDOWN=B.defineInRange("talentCooldownSeconds",25,1,600);
    public static final ModConfigSpec.IntValue GOLD_COOLDOWN=B.defineInRange("goldTalentCooldownSeconds",60,1,1200);
    public static final ModConfigSpec.IntValue SHRINE_SECONDS=B.defineInRange("shrineTimeLimitSeconds",120,20,1200);
    public static final ModConfigSpec.IntValue SHRINE_GUARDS=B.defineInRange("shrineGuardCount",3,1,12);
    public static final ModConfigSpec.IntValue ANCIENT_GUARDS=B.defineInRange("ancientShrineGuardCount",5,1,16);
    public static final ModConfigSpec SPEC = B.build();''');p.write_text(s)
p=b/'TalentService.java';s=p.read_text().replace('SpiritTalents.gold(id)?60:25','SpiritTalents.gold(id)?Config.GOLD_COOLDOWN.get():Config.TALENT_COOLDOWN.get()');p.write_text(s)
p=b/'MemoryStoneEntity.java';s=p.read_text().replace('stage==0?3:5','stage==0?Config.SHRINE_GUARDS.get():Config.ANCIENT_GUARDS.get()').replace('++stone.elapsed>2400','++stone.elapsed>Config.SHRINE_SECONDS.get()*20');p.write_text(s)
p=Path('src/gameTest/java/me/vlad/lastbreath/LastBreathGameTests.java');s=p.read_text().replace('    @SubscribeEvent\n    public static void functions','    private static final Identifier SHRINE=Identifier.fromNamespaceAndPath(LastBreath.MODID,"memory_shrine");\n    @SubscribeEvent\n    public static void functions',1);s=s.replace('        event.register(Registries.TEST_FUNCTION, helper -> helper.register(GEAR','        event.register(Registries.TEST_FUNCTION, helper -> helper.register(SHRINE,(Consumer<GameTestHelper>)LastBreathGameTests::shrine));\n        event.register(Registries.TEST_FUNCTION, helper -> helper.register(GEAR',1);s=s.replace('        event.registerTest(GEAR,','        event.registerTest(SHRINE,new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION,SHRINE),new TestData<>(environment,Identifier.fromNamespaceAndPath(LastBreath.MODID,"empty"),200,0,true)));\n        event.registerTest(GEAR,',1)
pos=s.index('    private static void skills(')
s=s[:pos]+'''    private static void shrine(GameTestHelper helper){
        var server=helper.getLevel().getServer();var level=server.getLevel(net.minecraft.world.level.Level.NETHER);
        var p=connect(helper,UUID.randomUUID(),"LBMemory");var origin=new BlockPos(12320,65,12320);
        var guards=new java.util.ArrayList<net.minecraft.world.entity.Mob>();
        try{
            for(var at:BlockPos.betweenClosed(origin.offset(-12,-1,-12),origin.offset(12,5,12)))level.setBlockAndUpdate(at,(at.getY()==64?Blocks.SOUL_SOIL:Blocks.AIR).defaultBlockState());
            check(new MemoryShrineFeature().place(level,level.getChunkSource().getGenerator(),net.minecraft.util.RandomSource.create(12),origin.above(10)),"Shrine generates on an open soul-soil floor");
            check(level.getBlockState(origin).is(LastBreath.MEMORY_STONE.get()),"Worldgen places a real memory stone");
            p.teleportTo(level,origin.getX()+1.5,65,origin.getZ()+.5,Set.of(),0,0,true);p.setGameMode(GameType.SURVIVAL);
            var stone=(MemoryStoneEntity)level.getBlockEntity(origin);int branch=level.getBlockState(origin).getValue(MemoryStoneBlock.BRANCH);
            stone.start(p);
            guards.addAll(level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class,p.getBoundingBox().inflate(24),m->m.getPersistentData().getBooleanOr(MemoryStoneEntity.GUARD,false)));
            check(guards.size()==Config.SHRINE_GUARDS.get(),"Trial spawns configured guardians");
            stone.start(p);
            check(level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class,p.getBoundingBox().inflate(24),m->m.getPersistentData().getBooleanOr(MemoryStoneEntity.GUARD,false)).size()==guards.size(),"Second start cannot duplicate a trial");
            for(var m:guards)m.setHealth(0);
            MemoryStoneEntity.tick(level,origin,level.getBlockState(origin),stone);
            check(level.getBlockState(origin).getValue(MemoryStoneBlock.STAGE)==1&&p.getInventory().countItem(LastBreath.KNOWLEDGE_SCROLLS.get(branch).get())==1,"Victory spends stage and gives exactly one matching scroll");
            MemoryStoneEntity.tick(level,origin,level.getBlockState(origin),stone);
            check(p.getInventory().countItem(LastBreath.KNOWLEDGE_SCROLLS.get(branch).get())==1,"Repeated tick cannot duplicate reward");
            stone.start(p);check(!stone.owns(guards.getFirst()),"Ancient trial requires offerings");
            p.getInventory().add(new ItemStack(LastBreath.PURPLE_SOUL.get(),1));p.getInventory().add(new ItemStack(LastBreath.KNOWLEDGE_FRAGMENT.get(),4));
            guards.forEach(net.minecraft.world.entity.Entity::discard);guards.clear();stone.start(p);
            guards.addAll(level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class,p.getBoundingBox().inflate(24),m->m.getPersistentData().getBooleanOr(MemoryStoneEntity.GUARD,false)));
            check(guards.size()==Config.ANCIENT_GUARDS.get(),"Ancient trial spawns stronger wave");
            p.snapTo(origin.getX()+40,65,origin.getZ());MemoryStoneEntity.tick(level,origin,level.getBlockState(origin),stone);
            check(p.getInventory().countItem(LastBreath.PURPLE_SOUL.get())==1&&p.getInventory().countItem(LastBreath.KNOWLEDGE_FRAGMENT.get())==4,"Interrupted ritual does not consume offerings");
            check(guards.stream().noneMatch(net.minecraft.world.entity.Mob::isAlive),"Interrupted ritual cleans guardians");
            helper.succeed();
        }finally{guards.forEach(net.minecraft.world.entity.Entity::discard);server.getPlayerList().remove(p);}
    }

'''+s[pos:];p.write_text(s)
