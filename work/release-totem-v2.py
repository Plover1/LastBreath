from pathlib import Path
import json,zipfile,shutil
root=Path.cwd()
folder=root/'art/return_totem'
m=json.loads((folder/'return_heart.json').read_text(encoding='utf-8'))
b=json.loads((folder/'return_totem.bbmodel').read_text(encoding='utf-8'))
assert len(m['elements'])==len(b['elements'])==85
for a,c in zip(m['elements'],b['elements']):
 assert a['from']==c['from'] and a['to']==c['to']
 assert all(x<y for x,y in zip(a['from'],a['to']))
 for face in a['faces']:
  assert [v*4 for v in a['faces'][face]['uv']]==c['faces'][face]['uv']
  assert all(0<=v<=16 for v in a['faces'][face]['uv'])
jar=root/'build/libs/lastbreath-0.10.2.jar'
with zipfile.ZipFile(jar) as z:
 assert not any('GameTests' in n or 'ClientStartupTest' in n for n in z.namelist())
 assert json.loads(z.read('assets/lastbreath/models/item/return_heart.json'))==m
 assert 'Siris102' in z.read('META-INF/neoforge.mods.toml').decode()
text='''# Тотем повернення — 0.10.2

Автор мода: Siris102.

Витягнутий різьблений корпус, заглиблені сині очі, тонкі золоті деталі та ступінчастий кристал у формі серця за погодженим концептом. Геометрію адаптовано до кубоїдів Minecraft; концепт не є скриншотом готової моделі.

- return_totem.bbmodel — редагована модель Blockbench, 85 кубоїдів, вбудована текстура.
- return_heart.json — ідентична ігрова геометрія.
- return_heart.png — наявна палітра тотема, використана через UV-розгортку.
- preview.png — окремий рендер фактичної моделі з перевіркою глибини; не скриншот гри. PNG є основним прев’ю, SVG — допоміжна проєкція.
- approved-concept.png — погоджений художній концепт.

Відкрити bbmodel у Blockbench через File → Open Model. Перевірено геометрію, UV, завантаження ресурсів клієнтом і релізний JAR; відкриття у самому Blockbench не перевірялося.

ID lastbreath:return_heart, рецепти й механіка відродження збережені. Попередня модель збережена в art/return_totem_v0101. Це статична модель; світлі кольори кристала не створюють освітлення.
'''
(folder/'README.md').write_text(text,encoding='utf-8')
concept=Path(r'C:\Users\Vlad\.codex\generated_images\01a14e41-fd25-7831-a996-501ab25ed225\exec-0064c709-ba3b-487d-bab4-910374fb5119.png')
shutil.copy2(concept,folder/'approved-concept.png')
p=root/'README.md';s=p.read_text(encoding='utf-8').replace('# Last Breath 0.10.1','# Last Breath 0.10.2',1)
if '## Тотем 0.10.2' not in s:s+='\n## Тотем 0.10.2\n\nНовий витягнутий тотем за погодженим концептом: 85 кубоїдів, синє серце, різьблена маска та золоті вставки. Модель, Blockbench-проєкт і прев’ю містяться в `art/return_totem/`; старий варіант — `art/return_totem_v0101/`. Механіка не змінена.\n'
p.write_text(s,encoding='utf-8')
out=Path(r'C:\Users\Vlad\Documents\Codex\2026-10-18\z\outputs');out.mkdir(parents=True,exist_ok=True)
for src,name in [(jar,jar.name),(folder/'return_totem.bbmodel','LastBreath-ReturnTotem.bbmodel'),(folder/'preview.png','LastBreath-ReturnTotem-preview.png'),(folder/'README.md','LastBreath-ReturnTotem-README.md')]:shutil.copy2(src,out/name)
print('Validated 85 cuboids, all UVs, embedded model, author and test-free release. Published',jar.name)
