exec(Path('work/update013.py').read_text()[:0]) if False else None
from pathlib import Path
p=Path('src/main/java/me/vlad/lastbreath/client/SoulTreeScreen.java');s=p.read_text(encoding='utf-8')
s=s.replace('private boolean equipment;','private boolean equipment,living;').replace('new Button[20]','new Button[SpiritTalents.COUNT]').replace('new int[20]','new int[SpiritTalents.COUNT]').replace('new long[20]','new long[SpiritTalents.COUNT]')
s=s.replace('int[] x={38','if(i>=20)return 52+(i-20)%4*75;int[] x={38').replace('int[] y={26','if(i>=20)return i<24?90:170;int[] y={26')
s=s.replace('equipment=false;updateButtons();','equipment=false;living=false;updateButtons();')
s=s.replace('leftPos+7,topPos+5,150,16','leftPos+7,topPos+5,109,16').replace('leftPos+164,topPos+5,75,16','leftPos+119,topPos+5,65,16').replace('leftPos+241,topPos+5,81,16','leftPos+250,topPos+5,73,16')
needle='  gearButton(20,224,44,98'
s=s.replace(needle,'  addRenderableWidget(new SoulButton(Button.builder(Component.translatable("lastbreath.tree.living"),b->{equipment=false;living=true;updateButtons();}).bounds(leftPos+187,topPos+5,60,16)));\n'+needle)
# All ordinary buttons use the same styled widget; talent nodes retain their custom renderer.
s=s.replace('addRenderableWidget(Button.builder','addRenderableWidget(new SoulButton(Button.builder').replace('.build());','.build());')
import re
s=re.sub(r'(addRenderableWidget\(new SoulButton\(Button\.builder[^\n]+?)\.build\(\)\)',r'\1))',s)
s=s.replace('for(int i=0;i<20;i++){','for(int i=0;i<SpiritTalents.COUNT;i++){').replace('for(int i=0;i<20;i++)if(nodes','for(int i=0;i<SpiritTalents.COUNT;i++)if(nodes')
s=s.replace('nodes[i].visible=!equipment;nodes[i].active=!equipment&&','nodes[i].visible=!equipment&&(living==(i>=20));nodes[i].active=nodes[i].visible&&TalentService.enabled(i)&&')
s=s.replace('if(!equipment)for(int i=0;i<20;i++){','if(!equipment&&!living)for(int i=0;i<20;i++){')
s=s.replace('if(!equipment){g.fill(x+160','if(!equipment&&living)for(int i=24;i<27;i++)line(g,x+nx(i-4),y+ny(i-4),x+nx(i),y+ny(i),0xff77603c);\n  if(!equipment&&!living){g.fill(x+160')
s=s.replace('else if(menu.gear(0)>0)','else if(equipment&&menu.gear(0)>0)')
p.write_text(s,encoding='utf-8')
p=Path('src/main/java/me/vlad/lastbreath/client/TalentWheel.java');s=p.read_text(encoding='utf-8');s=s.replace('for(int n=0;n<skills.size();n++){','''for(int row=-108;row<=108;row++){int outer=(int)Math.sqrt(108*108-row*row);g.fill(cx-outer,cy+row,cx+outer+1,cy+row+1,0xffb29150);if(Math.abs(row)<=104){int inner=(int)Math.sqrt(104*104-row*row);g.fill(cx-inner,cy+row,cx+inner+1,cy+row+1,((row+108)/12)%2==0?0xff38291d:0xff302219);}}
   for(int row=-38;row<=38;row++){int edge=(int)Math.sqrt(38*38-row*row);g.fill(cx-edge,cy+row,cx+edge+1,cy+row+1,0xff171b1c);}
   for(int n=0;n<skills.size();n++){''').replace('0xff34404c','0xffa88748').replace('0xff171e27','0xff241d17').replace('0xaa151b22','0xee30231b');p.write_text(s,encoding='utf-8')
