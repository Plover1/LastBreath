from pathlib import Path
root=Path('src/main/java/me/vlad/lastbreath')
def edit(p,a,b):
 p=root/p;s=p.read_text(encoding='utf-8');assert a in s;s=s.replace(a,b);p.write_text(s,encoding='utf-8')
edit('Config.java','public static final ModConfigSpec SPEC = B.build();','''public static final ModConfigSpec.BooleanValue SELF_REVIVAL=B.define("enableTotemSelfRevival",true);
    public static final ModConfigSpec.BooleanValue ALLY_REVIVAL=B.define("enableAllyRevival",true);
    public static final ModConfigSpec.BooleanValue ALTAR_REVIVAL=B.define("enableAltarRevival",true);
    public static final ModConfigSpec.BooleanValue SHRINES=B.define("enableMemoryTrials",true);
    public static final ModConfigSpec.BooleanValue RUINS=B.comment("Affects generation in new chunks only; existing ruins are preserved.").define("generateMemoryRuins",true);
    public static final ModConfigSpec SPEC = B.build();''')
edit('SoulService.java','return selfRevive(player, InteractionHand.MAIN_HAND, true, altar);','if(!Config.ALTAR_REVIVAL.get())return error(player,"lastbreath.feature.disabled");\n        return selfRevive(player, InteractionHand.MAIN_HAND, true, altar);')
edit('SoulService.java','if (!ghost(player)) return error(player, "lastbreath.not_ghost");','if(!free&&!Config.SELF_REVIVAL.get())return error(player,"lastbreath.feature.disabled");\n        if (!ghost(player)) return error(player, "lastbreath.not_ghost");')
edit('SoulService.java','static int revive(ServerPlayer rescuer, String selector, InteractionHand hand) {','static int revive(ServerPlayer rescuer, String selector, InteractionHand hand) {\n        if(!Config.ALLY_REVIVAL.get())return error(rescuer,"lastbreath.feature.disabled");')
edit('ReturnAltarEntity.java','public void start(ServerPlayer player) {','public void start(ServerPlayer player) {\n        if(!Config.ALTAR_REVIVAL.get()){player.sendOverlayMessage(Component.translatable("lastbreath.feature.disabled"));return;}')
edit('ReturnAltarEntity.java','if (player == null || player.hasDisconnected()','if (!Config.ALTAR_REVIVAL.get() || player == null || player.hasDisconnected()')
edit('MemoryStoneEntity.java','public void start(ServerPlayer p){','public void start(ServerPlayer p){\n  if(!Config.SHRINES.get()){p.sendOverlayMessage(Component.translatable("lastbreath.feature.disabled"));return;}')
edit('MemoryStoneEntity.java','if(p.hasDisconnected()','if(!Config.SHRINES.get()||p.hasDisconnected()')
edit('MemoryShrineFeature.java','BlockPos floor=null;','if(!Config.value(Config.RUINS))return false;\n  BlockPos floor=null;')
p=Path('src/gameTest/java/me/vlad/lastbreath/LastBreathGameTests.java');s=p.read_text(encoding='utf-8').replace('var before=player.getData(LastBreath.TALENTS);','var beforeTalents=player.getData(LastBreath.TALENTS);').replace('player.setData(LastBreath.TALENTS,before);','player.setData(LastBreath.TALENTS,beforeTalents);');p.write_text(s,encoding='utf-8')
import json
for lang,v in {'en_us':'This feature is disabled in server settings','uk_ua':'Цю можливість вимкнено в налаштуваннях сервера','ru_ru':'Эта возможность отключена в настройках сервера'}.items():
 p=Path('src/main/resources/assets/lastbreath/lang')/(lang+'.json');d=json.loads(p.read_text(encoding='utf-8'));d['lastbreath.feature.disabled']=v;p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
