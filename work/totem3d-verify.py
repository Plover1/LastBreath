from pathlib import Path
import json,zipfile
folder=Path('art/return_totem');m=json.loads((folder/'return_heart.json').read_text(encoding='utf-8'));b=json.loads((folder/'return_totem.bbmodel').read_text(encoding='utf-8'))
assert len(m['elements'])==len(b['elements'])==31
for a,c in zip(m['elements'],b['elements']):
 assert a['from']==c['from'] and a['to']==c['to']
 for face in a['faces']:assert [v*4 for v in a['faces'][face]['uv']]==c['faces'][face]['uv']
jar=Path('build/libs/lastbreath-0.10.1.jar')
with zipfile.ZipFile(jar) as z:
 assert not any('GameTests' in n or 'ClientStartupTest' in n for n in z.namelist())
 assert json.loads(z.read('assets/lastbreath/models/item/return_heart.json'))==m
 assert 'Siris102' in z.read('META-INF/neoforge.mods.toml').decode()
print('Verified matching Blockbench/game geometry, all UVs, release model and no test classes.')
(folder/'README.md').write_text('''# Тотем повернення — 3D

Автор мода: Siris102. Версія інтеграції: Last Breath 0.10.1.

- `return_totem.bbmodel` — редагована модель Blockbench з вбудованою текстурою (Java Block/Item, 31 кубоїд).
- `return_heart.json` — ідентична геометрія для Minecraft, уже підключена в моді.
- `return_heart.png` — оригінальна палітра тотема; нові растрові текстури для цього оновлення не генерувалися.
- `preview.png` / `preview.svg` — проєкції геометрії спереду, під кутом і ззаду; це окреме прев’ю, не скриншот гри.

Відкрийте `.bbmodel` у Blockbench через File → Open Model. Текстура вбудована, додатково шукати її не потрібно. Зміни моделі можна експортувати як Minecraft Java Block/Item Model. У JSON мод використовує текстуру `lastbreath:item/return_heart`.

Предмет зберігає ID `lastbreath:return_heart`, рецепт, заряджання вівтаря та механіку відродження. Відмінність версії 0.10.1 — об’ємний вигляд предмета й налаштування його положення в руках, інвентарі, рамці та на землі. Сяйво кристала передане кольором текстури; модель не є джерелом освітлення. Модель статична, ефекти існуючих ритуалів зберігаються.

Перевірено відповідність геометрії/UV між файлами, завантаження ресурсів клієнтом і складання релізного JAR. Файл Blockbench підготовлений, але інтерактивне відкриття саме в редакторі не перевірялося.
''',encoding='utf-8')
p=Path('README.md');s=p.read_text(encoding='utf-8').replace('# Last Breath 0.10.0','# Last Breath 0.10.1',1);s+='\n## Об’ємний тотем 0.10.1\n\nТотем повернення має стандартну Java-модель із 31 кубоїда: дерев’яний корпус, золоті обідки, об’ємні очі й кристал. Редагований файл Blockbench з вбудованою текстурою та прев’ю — `art/return_totem/`. Ідентифікатор, рецепт і механіка тотема збережені.\n';p.write_text(s,encoding='utf-8')
