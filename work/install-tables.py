from pathlib import Path
import json,copy
r=Path.cwd();assets=r/'src/main/resources/assets/lastbreath/models/block';preview=r/'art/table-preview'
table=json.loads((preview/'ritual_table.json').read_text(encoding='utf-8'))
(assets/'ritual_table.json').write_text(json.dumps(table,indent=2)+'\n',encoding='utf-8')
altar=json.loads((preview/'return_altar.json').read_text(encoding='utf-8'));altar['textures']['particle']='minecraft:block/polished_deepslate'
for charge in range(5):
 m=copy.deepcopy(altar);index=0
 for e in m['elements']:
  if e['name']=='Soul rune':
   if index>=charge:
    for f in e['faces'].values():f['texture']='#polished_deepslate'
   index+=1
 name='return_altar'+('_'+str(charge) if charge else '')+'.json'
 (assets/name).write_text(json.dumps(m,indent=2)+'\n',encoding='utf-8')
 assert sum(e['name']=='Soul rune' and e['faces']['up']['texture']=='#cyan_concrete' for e in m['elements'])==charge
p=r/'gradle.properties';p.write_text(p.read_text(encoding='utf-8').replace('mod_version=0.10.3','mod_version=0.10.4'),encoding='utf-8')
print('Installed table and five altar charge variants.')
