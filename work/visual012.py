from pathlib import Path
p=Path('src/main/java/me/vlad/lastbreath/client/SoulCloakRendering.java');s=p.read_text(encoding='utf-8');s='\n'.join(l for l in s.splitlines() if 'body.addOrReplaceChild("front_tail_' not in l)+'\n';a=s.index('            for(int sign:new int[]{-1,1}){',s.index('@Override public void setupAnim'));b=s.index('            var gem=',a);s=s[:a]+s[b:];p.write_text(s,encoding='utf-8')
p=Path('work/fix-item-sides.cjs');s=p.read_text(encoding='utf-8');s=s.replace('uv:[0,0,16,16]},north:{texture:\'#soul\',uv:[16,0,0,16]}','uv:[2,4,14,16]},north:{texture:\'#soul\',uv:[14,4,2,16]}')
needle="const jar=await sprite('empty_soul_jar');"
addition="""for(const n of ['executioner_sword','soul_devourer','soul_reader','soul_cloak','soul_trousers','soul_boots']){const m=await sprite(n);if(n.includes('sword')||n==='soul_devourer'){m.display.thirdperson_righthand={rotation:[0,-90,55],translation:[0,4,.5],scale:[.85,.85,.85]};m.display.firstperson_righthand={rotation:[0,-90,25],translation:[1.13,3.2,1.13],scale:[.68,.68,.68]};}fs.writeFileSync(base+'models/item/'+n+'.json',JSON.stringify(m,null,2));}
"""
s=s.replace(needle,addition+needle);p.write_text(s,encoding='utf-8')
