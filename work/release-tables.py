from pathlib import Path
import json,zipfile,shutil
r=Path.cwd();p=r/'README.md';s=p.read_text(encoding='utf-8').replace('# Last Breath 0.10.3','# Last Breath 0.10.4',1)
if '## Столи 0.10.4' not in s:s+='\n## Столи 0.10.4\n\nНові моделі з ванільними текстурами: дубовий ритуальний стіл із деревом вузлів та кам’яний вівтар із заглибленням. Чотири сині вставки вівтаря показують кількість зарядів (0–4). Форми зіткнення підлаштовані під нові корпуси. Рецепти, відродження й початковий 2D-тотем не змінено.\n'
p.write_text(s,encoding='utf-8')
jar=r/'build/libs/lastbreath-0.10.4.jar'
with zipfile.ZipFile(jar) as z:
 assert not any('GameTests' in n or 'ClientStartupTest' in n for n in z.namelist())
 assert json.loads(z.read('assets/lastbreath/models/item/return_heart.json'))['parent']=='minecraft:item/generated'
 for charge in range(5):
  n='return_altar'+('_'+str(charge) if charge else '')+'.json'
  m=json.loads(z.read('assets/lastbreath/models/block/'+n))
  assert sum(e['name']=='Soul rune' and e['faces']['up']['texture']=='#cyan_concrete' for e in m['elements'])==charge
  for e in m['elements']:
   for f in e['faces'].values():assert f['texture'][1:] in m['textures']
 assert json.loads(z.read('assets/lastbreath/models/block/ritual_table.json'))==json.loads((r/'art/table-preview/ritual_table.json').read_text(encoding='utf-8'))
out=Path(r'C:\Users\Vlad\Documents\Codex\2026-10-18\z\outputs')
shutil.copy2(jar,out/jar.name)
print('Verified all five altar states, table model, 2D totem, no test classes. Copied release:',out/jar.name)
