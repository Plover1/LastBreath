from pathlib import Path
root=Path('src/main/java/me/vlad/lastbreath')
def edit(name, pairs):
 p=root/name;s=p.read_text(encoding='utf-8')
 for a,b in pairs:
  assert a in s,(name,a)
  s=s.replace(a,b)
 p.write_text(s,encoding='utf-8')
edit('SoulEvents.java', [('import net.minecraft.commands.Commands;', 'import net.minecraft.commands.Commands;\nimport net.minecraft.commands.arguments.EntityArgument;'),
 ('SoulService.becomeGhost(c.getSource().getPlayerOrException())))','SoulService.becomeGhost(c.getSource().getPlayerOrException()))\n                    .then(Commands.argument("players",EntityArgument.players()).executes(c->{int count=0;for(var p:EntityArgument.getPlayers(c,"players"))count+=SoulService.becomeGhost(p);return count;})))'),
 ('SoulService.administrativeRevive(c.getSource().getPlayerOrException()))));','SoulService.administrativeRevive(c.getSource().getPlayerOrException()))\n                    .then(Commands.argument("players",EntityArgument.players()).executes(c->{int count=0;for(var p:EntityArgument.getPlayers(c,"players"))count+=SoulService.administrativeRevive(p);return count;}))));')])
edit('Config.java', [('public static final ModConfigSpec SPEC = B.build();','''public static final ModConfigSpec.BooleanValue SURVIVAL_SPIRITS=B.comment("Allow spirit deaths in ordinary Survival, including worlds with the legacy hardcoreOnly setting enabled.").define("enableSurvivalSpirits",true);
    public static final ModConfigSpec.BooleanValue SPIRIT_DEATHS=B.comment("Controls new deaths only. Existing spirits can still be revived; admin commands remain available.").define("enableSpiritDeaths",true);
    public static final ModConfigSpec.BooleanValue SOUL_DROPS=B.define("enableSoulDrops",true);
    public static final ModConfigSpec.BooleanValue TALENTS_ENABLED=B.define("enableTalents",true);
    public static final ModConfigSpec.ConfigValue<java.util.List<? extends Integer>> DISABLED_TALENTS=B.comment("Talent IDs 0-27. Disabling preserves learned progress and prevents activation and passive bonuses.").defineListAllowEmpty("disabledTalents",java.util.List.<Integer>of(),()->0,o->o instanceof Integer n&&n>=0&&n<28);
    public static final ModConfigSpec SPEC = B.build();''')])
edit('SoulService.java', [('(!borrowed && Config.HARDCORE_ONLY.get() && !player.level().getServer().isHardcore())','(!borrowed && (!Config.SPIRIT_DEATHS.get() || (Config.HARDCORE_ONLY.get() && !Config.SURVIVAL_SPIRITS.get() && !player.level().getServer().isHardcore())))')])
edit('SoulHarvest.java',[('var mob=event.getEntity();if(mob.level().isClientSide())return;','var mob=event.getEntity();if(mob.level().isClientSide()||!Config.SOUL_DROPS.get())return;')])
edit('SpiritTalents.java', [('public static final int MASK=(1<<20)-1;','public static final int COUNT=28;\n    public static final int MASK=(1<<COUNT)-1;'),('Codec.intRange(-1,19)','Codec.intRange(-1,COUNT-1)'),('id<20','id<COUNT'),('id>=20','id>=COUNT'),('n<20','n<COUNT'),
 ('return id>=0&&id<COUNT&&id%5==4;','return id==27||(id>=0&&id<20&&id%5==4);'),
 ('return id>=0&&id<COUNT&&(id%5==0||id%5==2||gold(id));','return id>=20?id<24||id==27:id>=0&&(id%5==0||id%5==2||gold(id));'),
 ('int branch=id/5,step=id%5;','if(id>=20){if(id<24)return (knowledge&(1<<(id-20)))!=0;if(id<27)return has(id-4);return (ancient&4)!=0&&has(22)&&has(26);}\n        int branch=id/5,step=id%5;')])
edit('TalentService.java', [('public static boolean reader(Player p)', '''public static boolean enabled(int id){return id>=0&&id<SpiritTalents.COUNT&&Config.value(Config.TALENTS_ENABLED)&&!Config.value(Config.DISABLED_TALENTS).contains(id)&&((id!=0&&id!=4)||Config.value(Config.ECHO))&&(id!=9||Config.value(Config.BORROW))&&(id!=14||Config.value(Config.BOND));}
    public static boolean has(Player p,int id){return enabled(id)&&get(p).has(id);}
    public static boolean reader(Player p)'''),
 ('int step=id%5;double scale', 'int step=id>=20?(id==27?4:2):id%5;double scale'),
 ('id<0||id>=20||t.has(id)', '!enabled(id)||t.has(id)'),
 ('boolean ghost=SoulService.ghost(p);return', 'boolean ghost=SoulService.ghost(p);if(id>=20)return !ghost;return'),
 ('if(!p.isAlive()||p.isSpectator()||!reader(p))return;var old=get(p);','if(!p.isAlive()||p.isSpectator()||!reader(p)||!enabled(id))return;var old=get(p);'),
 ('if(((id==0||id==4)&&!Config.ECHO.get())||(id==9&&!Config.BORROW.get())||(id==14&&!Config.BOND.get()))','if(!enabled(id))'),
 ('default -> {return;}', '''case 20 -> {for(var mob:p.level().getEntitiesOfClass(Mob.class,p.getBoundingBox().inflate(4),m->m instanceof Enemy&&m.isAlive()&&p.hasLineOfSight(m))){var v=p.position().subtract(mob.position());mob.knockback(1.2,v.x,v.z);}}
            case 21 -> {if(p.getHealth()>=p.getMaxHealth())return;var b=p.getData(LastBreath.PROGRESS);if(b.souls()<3){p.sendOverlayMessage(Component.translatable("lastbreath.talent.blue_cost",3));return;}p.setData(LastBreath.PROGRESS,b.balances(b.souls()-3,b.red(),b.purple()));p.heal(4);seconds=60;}
            case 22 -> {p.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,160,0));seconds=60;}
            case 23 -> {p.addEffect(new MobEffectInstance(MobEffects.SPEED,200,1));p.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST,200,0));}
            case 27 -> {p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,120,1));p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,200,1));seconds=90;}
            default -> {return;}'''),
 ('if(t.has(1))','if(has(p,1))'),('get(p).has(16)','has(p,16)'),
 ('HARVEST.getOrDefault(p,0L)>now','enabled(19)&&HARVEST.getOrDefault(p,0L)>now'),('victim instanceof Mob m&&MARKED','enabled(17)&&victim instanceof Mob m&&MARKED'),
 ('t.has(6)','has(p,6)'),('t.has(8)','has(p,8)'),('t.has(11)','has(p,11)'),('t.has(3)','has(p,3)'),('t.has(13)','has(p,13)'),
 ('MARKED.entrySet().removeIf', '''attribute(p,Attributes.KNOCKBACK_RESISTANCE,"wanderer_anchor",has(p,25)?.1:0);
        if(has(p,24)&&p.isUnderWater())p.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING,40,0,false,false));
        if(has(p,26)&&p.tickCount%40==0)for(var mob:p.level().getEntitiesOfClass(Mob.class,p.getBoundingBox().inflate(6),m->m instanceof Enemy&&m.isAlive()&&p.hasLineOfSight(m)))mob.addEffect(new MobEffectInstance(MobEffects.GLOWING,60,0));
        MARKED.entrySet().removeIf''')])
edit('SoulTreeMenu.java',[('id<120','id<100+SpiritTalents.COUNT'),('int talent=id-100;var old','int talent=id-100;if(!TalentService.enabled(talent))return false;var old')])
edit('client/TalentWheel.java',[('for(int i=0;i<20;i++)','for(int i=0;i<SpiritTalents.COUNT;i++)'),('if(!TalentService.form(minecraft.player,id))','if(!TalentService.enabled(id)||!TalentService.form(minecraft.player,id))')])
p=root/'client/TalentWheel.java';s=p.read_text();a=s.index(' public static Identifier icon(');b=s.index('\n public static int color',a);s=s[:a]+''' public static Identifier icon(int id){return Identifier.fromNamespaceAndPath("lastbreath","textures/gui/abilities/"+Math.clamp(id,0,SpiritTalents.COUNT-1)+".png");}'''+s[b:];p.write_text(s,encoding='utf-8')
