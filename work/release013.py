from pathlib import Path
import json,re
root=Path('src/main/java/me/vlad/lastbreath')
p=root/'client/TalentWheel.java';s=p.read_text(encoding='utf-8');s=s.replace('private static int lastSelected=-1;','private static final Identifier[] ICONS=java.util.stream.IntStream.range(0,SpiritTalents.COUNT).mapToObj(i->Identifier.fromNamespaceAndPath("lastbreath","textures/gui/abilities/"+i+".png")).toArray(Identifier[]::new);\n private static int lastSelected=-1;');s=s.replace('return Identifier.fromNamespaceAndPath("lastbreath","textures/gui/abilities/"+Math.clamp(id,0,SpiritTalents.COUNT-1)+".png");','return ICONS[Math.clamp(id,0,SpiritTalents.COUNT-1)];').replace('dx*dx+dy*dy<22500','dx*dx+dy*dy<11664').replace('else if(!TalentService.form(mc.player,id))','else if(!TalentService.enabled(id)||!TalentService.form(mc.player,id))');p.write_text(s,encoding='utf-8')
p=root/'AdvancedSkills.java';s=p.read_text(encoding='utf-8').replace('(!e.gear&&!Config.ECHO.get())','(!e.gear&&(!Config.ECHO.get()||!Config.TALENTS_ENABLED.get()))');p.write_text(s,encoding='utf-8')
p=root/'client/SoulTreeScreen.java';s=p.read_text(encoding='utf-8').replace('t.has(i)?"lastbreath.talent.equip":t.unlocked(i)?','!TalentService.enabled(i)?"lastbreath.talent.disabled":t.has(i)?"lastbreath.talent.equip":t.unlocked(i)?');p.write_text(s,encoding='utf-8')
p=Path('README.md');s=p.read_text(encoding='utf-8').replace('# Last Breath 0.12.1','# Last Breath 0.13.0').replace('За замовчуванням нові смерті стають духами лише в Hardcore.','За замовчуванням нові смерті стають духами у звичайному виживанні та Hardcore.').replace('| `hardcoreOnly` | true | Нові смерті стають духами лише у Hardcore |','| `hardcoreOnly` | true | Старий параметр; обмежує смерті до Hardcore лише коли `enableSurvivalSpirits=false` |').replace('Залишено адміністративні `/lastbreath ghost` і `/lastbreath alive`.','Адміністративні `/lastbreath ghost [гравці]` і `/lastbreath alive [гравці]` приймають нік або селектор. Без аргументу діють на виконавця; консоль повинна вказати ціль.')
s+='''
## 0.13.0 — пам’ять, сили живих та налаштування

Автор: **Siris102**. Мови: українська, англійська, російська (включно з назвами параметрів налаштувань).

- «Уламок пам’яті» має власну текстуру розбитої руни. Реєстровий ID `knowledge_fragment` збережено для сумісності світів.
- 28 власних іконок для дерева та оберега; власні печатки сувоїв, емблема моду, дерев’яно-золоті кнопки й колесо Alt.
- Нову сторінку «Живі» відкривають ті самі свитки: чотири верхні вузли відповідають чотирьом гілкам знань. Три пасивні вузли потребують відповідного верхнього. Золотий вартовий потребує захисту від жару, пильності та стародавнього сувою Вартового.
- ID 20 — відштовхування монстрів у 4 блоках; 21 — лікування на 2 серця за 3 сині душі; 22 — вогнестійкість 8 с; 23 — швидкість II та стрибучість 10 с.
- ID 24 — дихання під водою; 25 — +10% опору відкиданню; 26 — підсвічування видимих монстрів у 6 блоках. Ці пасиви працюють в обох формах.
- ID 27 — Останній вартовий: опір II 6 с та поглинання II 10 с, перезарядка 90 с. Активні нові сили працюють живим. Зберігається загальний ліміт 3 звичайні сили + 1 золота.

Команди для тестування (OP/чити):

```text
/lastbreath ghost Siris102
/lastbreath alive Siris102
/lastbreath ghost @a
/lastbreath souls add Siris102 blue 100
```

Кнопка «Налаштування» в списку модів відкриває стандартний редактор NeoForge. Серверні параметри визначає власник світу/сервера; звичайний клієнт не може обійти їх. Файл світу: `serverconfig/lastbreath-server.toml`.

| Параметр | Дія |
|---|---|
| `enableSpiritDeaths` | Перехід у духа після нових смертей |
| `enableSurvivalSpirits` | Дозволити це у звичайному виживанні; типово true, також для старих конфігів |
| `enableTotemSelfRevival` | Самовідродження тотемом |
| `enableAllyRevival` | Порятунок іншого гравця |
| `enableAltarRevival` | Ритуал вівтаря; вимкнення не забирає вкладені тотеми |
| `enableSoulDrops` | Випадання нових душ |
| `enableTalents` | Сили й пасивні бонуси персонажа без видалення вивченого прогресу |
| `disabledTalents` | Номери окремо вимкнених умінь, наприклад `[21, 27]` |
| `enableMemoryTrials` | Випробування каменів пам’яті |
| `generateMemoryRuins` | Генерація руїн у нових чанках |

Усі нові перемикачі типово увімкнені. Вже застосовані короткі зіллєві ефекти завершуються за своєю тривалістю; пасивні атрибути прибираються наступного секундного оновлення. Старі духи залишаються духами при вимкненні нових смертей. Адміністративні команди залишаються доступними.

Перевірки та їхні межі: `work/validation013.md`. Це тестова версія перед 1.0.0, не заява про відсутність усіх можливих багів.
''';p.write_text(s,encoding='utf-8')
p=Path('ASSET_SOURCES.md');p.write_text(p.read_text(encoding='utf-8')+'''
## 0.13.0 — memory and ability artwork

Built-in imagegen generated `art/update013/glyphs-source.png` and `art/update013/logo-source.png`. The 6×5 glyph sheet is mechanically cropped and resized by `work/assets013.cjs`: 28 GUI icons, memory fragment and Soul Reader sprites, and four branch seals used in the block texture atlas by scroll models. `work/new-sides013.cjs` derives closed sidewall geometry from the item alpha masks. `lastbreath.png` is a 256×256 square mod icon. Existing swords, armor and the approved 2D return totem are unchanged. Wood/brass buttons and the wheel background are native GUI rendering code.
''',encoding='utf-8')
# Verify every locale key and formatted argument, every JSON resource, all custom icon files.
base=Path('src/main/resources/assets/lastbreath');en=json.loads((base/'lang/en_us.json').read_text(encoding='utf-8'))
for lang in ['uk_ua','ru_ru']:
 d=json.loads((base/f'lang/{lang}.json').read_text(encoding='utf-8'));assert d.keys()==en.keys(),(lang,en.keys()-d.keys(),d.keys()-en.keys())
 for k,v in en.items():assert v.count('%s')==d[k].count('%s'),(lang,k)
for p in Path('src/main/resources').rglob('*.json'):json.loads(p.read_text(encoding='utf-8'))
for i in range(28):assert (base/f'textures/gui/abilities/{i}.png').exists()
print(f'Validated JSON, 28 icons and {len(en)} matching keys in all three languages.')
