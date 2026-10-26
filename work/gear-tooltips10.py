from pathlib import Path
p=Path('src/main/java/me/vlad/lastbreath/SoulGear.java');s=p.read_text();at='    public static int chargeValue(int type)';insert='''    public static net.minecraft.network.chat.Component details(int type,int branch){
        String key="lastbreath.gear.details."+type+"."+branch;
        if(type==1&&branch==1)return net.minecraft.network.chat.Component.translatable(key,Math.round(Config.value(Config.GEAR_HARVEST_CHANCE)*100),Config.value(Config.GEAR_HARVEST_COST));
        if(type==1&&branch==2)return net.minecraft.network.chat.Component.translatable(key,Config.value(Config.GEAR_HEAL),Config.value(Config.GEAR_HEAL_COST),Config.value(Config.GEAR_HEAL_COOLDOWN));
        if(type==2&&branch==1)return net.minecraft.network.chat.Component.translatable(key,Math.round(Config.value(Config.GEAR_GUARD_REDUCTION)*100),Config.value(Config.GEAR_GUARD_THRESHOLD),Config.value(Config.GEAR_GUARD_COST),Config.value(Config.GEAR_CLOAK_COOLDOWN));
        if(type==2&&branch==2)return net.minecraft.network.chat.Component.translatable(key,Config.value(Config.GEAR_SHROUD_HEALTH),Config.value(Config.GEAR_SHROUD_SECONDS),Config.value(Config.GEAR_SHROUD_COST),Config.value(Config.GEAR_CLOAK_COOLDOWN));
        return net.minecraft.network.chat.Component.empty();
    }
''';s=s.replace(at,insert+at);p.write_text(s)
p=Path('src/main/java/me/vlad/lastbreath/client/SoulTreeScreen.java');s=p.read_text().replace('SoulGear.branchKey(type,choice)+".desc").append("\\n")','SoulGear.branchKey(type,choice)+".desc").append("\\n").append(SoulGear.details(type,choice)).append("\\n")');p.write_text(s)
p=Path('src/main/java/me/vlad/lastbreath/SoulGearItem.java');s=p.read_text().replace('        lines.accept(Component.translatable("lastbreath.gear.tooltip")', '        if(data.branch()>0)lines.accept(SoulGear.details(SoulGear.type(stack),data.branch()).withStyle(ChatFormatting.GRAY));\n        lines.accept(Component.translatable("lastbreath.gear.tooltip")');p.write_text(s)
import json
for lang,lines in [('uk_ua',['Додаткова спроба: %s%% · %s заряду','Здоров’я: +%s · %s заряду · %s с','Захист: %s%% від удару ≥%s · %s заряду · %s с','Здоров’я ≤%s · приманка %s с · %s заряду · %s с']),('en_us',['Extra roll: %s%% · %s charge','Heal: +%s health · %s charge · %s s','Guard: %s%% on hits ≥%s · %s charge · %s s','Health ≤%s · decoy %s s · %s charge · %s s'])]:
 p=Path('src/main/resources/assets/lastbreath/lang')/(lang+'.json');d=json.loads(p.read_text());d.update(dict(zip(['lastbreath.gear.details.1.1','lastbreath.gear.details.1.2','lastbreath.gear.details.2.1','lastbreath.gear.details.2.2'],lines)));p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
