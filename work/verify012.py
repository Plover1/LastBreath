from pathlib import Path
import zipfile,json,hashlib,shutil
p=Path('build/libs/lastbreath-0.12.0.jar')
with zipfile.ZipFile(p) as z:
 names=z.namelist()
 assert not any(any(t in n for t in ['GameTests','ClientStartupTest','WandererPreview','WandererDetailExport','WandererDetailModel','test_instance/']) for n in names)
 for n in names:
  if n.endswith('.json'):json.loads(z.read(n))
 meta=z.read('META-INF/neoforge.mods.toml').decode()
 assert '0.12.0' in meta and 'Siris102' in meta
 for n in ['me/vlad/lastbreath/MemoryStoneEntity.class','me/vlad/lastbreath/TalentNetwork.class','me/vlad/lastbreath/client/TalentWheel.class','data/lastbreath/worldgen/feature/memory_shrine.json','data/lastbreath/worldgen/feature/fragment_ruin.json']:
  assert n in names,n
out=Path('C:/Users/Vlad/Documents/Codex/2026-10-18/z/outputs')
shutil.copy2(p,out/p.name)
shutil.copy2('README.md',out/'LastBreath-0.12.0-README.md')
shutil.copy2('run/screenshots/2026-10-26_14.22.13.png',out/'LastBreath-0.12.0-tree.png')
shutil.copy2('art/wanderer/preview.png',out/'LastBreath-0.12.0-armor.png')
shutil.copy2('art/item-fixes-preview.png',out/'LastBreath-0.12.0-items.png')
sha=hashlib.sha256(p.read_bytes()).hexdigest()
(out/'lastbreath-0.12.0.jar.sha256').write_text(sha+'  '+p.name+'\n')
q=Path('NEXT_UPDATE_012.md');s=q.read_text(encoding='utf8').replace('Фінальна збірка та перевірка артефакту виконуються перед видачею.','Чиста збірка успішна; JAR перевірено, тестових класів немає.');q.write_text(s,encoding='utf8')
Path('work/validation012.md').write_text(f'''# Last Breath 0.12.0 validation

- GameTest: all 10 required tests passed (`work/tests012-final.log`).
- Client resource/menu smoke: successful, no mod model/texture errors (`work/client012b.log`). OSHI host performance-counter warnings are unrelated.
- Clean production build: successful (`work/build012-final.log`); no test classes or fixtures included.
- JSON resources parse; release version and author verified.
- Actual tree screenshot, baked armor geometry and jar/sidewall preview inspected.
- SHA256: {sha}

Limits: no manual multiplayer playthrough or balance test. Alt wheel input is implemented and compiles but has not been manually exercised in a multiplayer session. Shrine feature placement and rewards were exercised by GameTest; naturally finding shrines across many seeds was not benchmarked.\n''',encoding='utf8')
print('Verified release:',p,'bytes=',p.stat().st_size,'sha256=',sha)
print('Delivered:',out/p.name)
