from pathlib import Path
import json,zipfile,hashlib,shutil
p=Path('README.md');s=p.read_text(encoding='utf-8').replace('Файл світу: `serverconfig/lastbreath-server.toml`.','Синхронізований конфіг: `config/lastbreath-synced.toml` у папці гри або сервера.');p.write_text(s,encoding='utf-8')
p=Path('MOD_DESCRIPTION.md');s=p.read_text(encoding='utf-8').replace('кооперативного Hardcore','кооперативного виживання та Hardcore').replace('20 умінь','28 умінь').replace('Twenty talents','Twenty-eight talents').replace('cooperative Hardcore survival','cooperative Survival and Hardcore').replace('Плащ сутінків','Плащ мандрівника').replace('Twilight Cloak','Soul Wanderer Coat');s+='''
## Русский

**Автор: Siris102.** Last Breath добавляет в обычное выживание и Hardcore форму духа, совместное возрождение и развитие через души. Спасайте друзей тотемами, заряжайте алтарь, добывайте синие, красные и фиолетовые души. Исследуйте руины Нижнего мира, побеждайте стражей и изучайте свитки на ритуальном столе. Доступны 28 умений: от призрачной приманки и связи душ до отталкивающего импульса, лечения и защитных сил живого персонажа.

Меч Пожиратель душ и комплект странника усиливаются на ритуальном столе. Выбирайте активные силы через Alt. Настройки позволяют менять стоимость, шансы, эффекты и отключать отдельные механики. Прогресс сохраняется после выхода и перезапуска. Minecraft Java 26.3, NeoForge 26.3.0.90; мод нужен серверу и всем игрокам.
''';p.write_text(s,encoding='utf-8')
jar=Path('build/libs/lastbreath-0.13.0.jar')
with zipfile.ZipFile(jar) as z:
 names=z.namelist()
 assert not any(any(t in n for t in ['GameTests','ClientStartupTest','WandererPreview','WandererDetailExport']) for n in names)
 meta=z.read('META-INF/neoforge.mods.toml').decode();assert '0.13.0' in meta and 'Siris102' in meta and 'iconFile="lastbreath.png"' in meta
 for n in names:
  if n.endswith('.json'):json.loads(z.read(n))
 for i in range(28):assert f'assets/lastbreath/textures/gui/abilities/{i}.png' in names
 assert 'assets/lastbreath/lang/ru_ru.json' in names
out=Path('C:/Users/Vlad/Documents/Codex/2026-10-18/z/outputs');out.mkdir(parents=True,exist_ok=True)
shutil.copy2(jar,out/jar.name)
for src,name in [('2026-10-26_20.53.15.png','lastbreath-0.13.0-tree.png'),('2026-10-26_20.53.16.png','lastbreath-0.13.0-living.png')]:shutil.copy2(Path('run/screenshots')/src,out/name)
digest=hashlib.sha256(jar.read_bytes()).hexdigest()
report=f'''# Last Breath 0.13.0 validation

- `runGameTestServer -PlastbreathTests --offline`: all 10 required integration tests passed (`work/tests013-final.log`). Includes real death/respawn flow, persistence/relog/restart, altar/channel transactions, soul harvesting, gear and memory trials. Expanded assertions cover named-player ghost/alive commands, Survival death policy and its toggles, all new living active effects, healing cost/cooldown, passive modifier removal and 28-bit talent codec round trip.
- `runClient -PlastbreathTests -PlastbreathClientSmoke --offline`: successful (`work/client013-final.log`). Resource/model loading completed without mod errors. Actual equipment, original talent tree and living page screenshots visually inspected. Final metadata uses `iconFile` supported by this loader.
- All JSON parsed; English/Ukrainian/Russian each contain the same 307 keys with matching format placeholders. The 28 custom icons, scroll seals, reader/fragment textures and square mod icon are packaged.
- Clean production build excludes test fixture classes. JAR SHA-256: `{digest}`.

Limits: no session with multiple real network clients or manual Alt-wheel input test in this pass. Long-session multiplayer balance, other-mod compatibility and world-generation density remain playtesting tasks. Short already-applied potion effects expire naturally when a talent is disabled; no unrelated potion effects are forcibly removed. GUI settings registration and resource startup checked; live remote server config editing was not exercised.

Production: {out/jar.name}
'''
Path('work/validation013.md').write_text(report,encoding='utf-8')
shutil.copy2('README.md',out/'lastbreath-0.13.0-README.md')
print(f'Packaged {jar.name}: {jar.stat().st_size} bytes; SHA256 {digest}')
