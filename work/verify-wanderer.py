from pathlib import Path
import json,zipfile,shutil
r=Path.cwd();jar=r/'build/libs/lastbreath-0.11.0.jar'
with zipfile.ZipFile(jar) as z:
 assert not any(any(t in n for t in ['GameTests','ClientStartupTest','WandererPreview']) for n in z.namelist())
 for item in ['soul_cloak','soul_trousers','soul_boots']:
  for path in [f'assets/lastbreath/items/{item}.json',f'assets/lastbreath/models/item/{item}.json',f'assets/lastbreath/equipment/{item}.json',f'data/lastbreath/recipe/{item}.json']:json.loads(z.read(path))
  assert len(z.read(f'assets/lastbreath/textures/item/{item}.png'))>100
 uk=json.loads(z.read('assets/lastbreath/lang/uk_ua.json'));en=json.loads(z.read('assets/lastbreath/lang/en_us.json'));assert uk.keys()==en.keys()
 assert json.loads(z.read('assets/lastbreath/models/item/return_heart.json'))['parent']=='minecraft:item/generated'
 assert 'Siris102' in z.read('META-INF/neoforge.mods.toml').decode()
 for n in z.namelist():
  if n.endswith('.json'):json.loads(z.read(n))
for frame in range(9):
 poses=json.loads((r/f'art/wanderer/pose-{frame}.json').read_text(encoding='utf-8'));assert len(poses)>100
 assert all(0<=v[3]<=16 and 0<=v[4]<=16 for q in poses for v in q)
out=Path(r'C:\Users\Vlad\Documents\Codex\2026-10-18\z\outputs')
shutil.copy2(jar,out/jar.name);shutil.copy2(r/'art/wanderer/preview.png',out/'LastBreath-Wanderer-preview.png');shutil.copy2(r/'art/wanderer/README.md',out/'LastBreath-Wanderer-README.md')
print('Release verified: three armor items, recipes, equipment layers, matching locales, valid pose UVs, 2D totem, no test classes.')
