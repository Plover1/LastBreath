from pathlib import Path
root=Path('src/main/java/me/vlad/lastbreath')
def edit(p,a,b):
 p=root/p;s=p.read_text(encoding='utf-8');assert a in s,(p,a);p.write_text(s.replace(a,b),encoding='utf-8')
edit('TalentService.java','public static boolean has(Player p,int id)', 'public static int legacy(Player p,int rank){int[] ids={16,8,13,2,5,11,4,9,14};return rank>=0&&rank<ids.length&&enabled(ids[rank])?p.getData(LastBreath.PROGRESS).rank(rank):0;}\n    public static boolean has(Player p,int id)')
for name in ['SoulHarvest.java','SoulService.java','ReturnHeartItem.java']:
 p=root/name;s=p.read_text(encoding='utf-8')
 for who in ['player','rescuer','target','serverPlayer']:
  for field,rank in [('harvest',0),('vitality',1),('sacrifice',2),('ward',5)]:
   s=s.replace(f'{who}.getData(LastBreath.PROGRESS).{field}()',f'TalentService.legacy({who},{rank})')
 p.write_text(s,encoding='utf-8')
edit('AdvancedSkills.java','!Config.BORROW.get()','!TalentService.enabled(9)')
edit('AdvancedSkills.java','!Config.BOND.get()','!TalentService.enabled(14)')
edit('SoulTreeScreen.java' if False else 'client/SoulTreeScreen.java','public void showEquipment(boolean value){equipment=value;','public void showEquipment(boolean value){equipment=value;living=false;')
# Correct expectation: the sender was already alive at this point of the transaction test.
p=Path('src/gameTest/java/me/vlad/lastbreath/LastBreathGameTests.java');s=p.read_text(encoding='utf-8').replace('!SoulService.ghost(other)&&SoulService.ghost(target)','!SoulService.ghost(other)&&!SoulService.ghost(target)');p.write_text(s,encoding='utf-8')
