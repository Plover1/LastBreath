from pathlib import Path
import zipfile,json,shutil
jar=Path('build/libs/lastbreath-0.11.1.jar')
with zipfile.ZipFile(jar) as z:
 assert not any(any(s in n for s in ['GameTests','ClientStartupTest','WandererPreview','WandererDetail']) for n in z.namelist())
 assert 'me/vlad/lastbreath/SoulAdminCommands.class' in z.namelist()
 for n in z.namelist():
  if n.endswith('.json'):json.loads(z.read(n))
 for name in ['soul_jar','red_soul_jar','purple_soul_jar']:
  model=json.loads(z.read('assets/lastbreath/models/item/'+name+'.json'))
  soul=next(e for e in model['elements'] if e.get('name')=='Soul inside glass')
  assert soul['from'][:2]==[5.5,4.75] and soul['to'][:2]==[11,10]
  assert any(e.get('name')=='Foreground jar rim' and e['to'][2]>soul['to'][2] for e in model['elements'])
 model=json.loads(z.read('assets/lastbreath/models/item/return_heart.json'))
 assert all(e['from'][2]==7.5 and e['to'][2]==8 for e in model['elements'])
 assert model['textures']['sprite']=='lastbreath:item/return_heart'
out=Path(r'C:\Users\Vlad\Documents\Codex\2026-10-18\z\outputs')
shutil.copy2(jar,out/jar.name);shutil.copy2('art/item-fixes-preview.png',out/'LastBreath-item-fixes-preview.png')
print('Validated thin 2D totem, bounded soul quads behind rims, production commands, JSON resources and test-free JAR.')
