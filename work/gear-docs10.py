from pathlib import Path
p=Path('README.md');s=p.read_text().replace('# Last Breath 0.9.0','# Last Breath 0.10.0').replace('**Мечем палача**','**Мечем палача або Пожирачем душ**');section='''## Спорядження душ (0.10.0)

На ритуальному столі відкрийте вкладку **Спорядження**. Кнопка **Наступний** перебирає мечі й плащі у вашому інвентарі, включно з надягненим плащем. Предмети не перекладаються й не копіюються. Доступ до стола залишається тим самим: духам — одразу, живим — після ритуалу з червоною душею.

- **Пожирач душ**: базова шкода 7, швидкість 1.6; добуває душі як Меч палача. Три рівні дають по +1 шкоди, максимум 10 без зачарувань.
- **Плащ сутінків**: слот нагрудника, базовий захист 4; три рівні дають по +1 захисту, максимум 7. Має об’ємну тканину за спиною з легкою анімацією. Плащ і елітри займають один слот.
- Рівні коштують відповідно **10/20/30 синіх + 2/4/6 червоних** душ; останній також потребує **1 фіолетову**. Покращення постійні та не потребують заряду.
- Починаючи з рівня 1 можна за **5 червоних** вибрати один із двох напрямів. Вибір постійний для цього предмета; другу гілку одночасно взяти не можна.
- Заряджання витрачає **поглинуті душі з особистого запасу**: синя +10, червона +25, фіолетова +100; місткість типово 100. Якщо душа не вміститься повністю, оплата не відбувається.
- Рівень, напрям, заряд і перезарядка зберігаються на предметі, зокрема після передачі або перепідключення. Зачарування зберігаються при покращенні на столі. Без заряду базові характеристики та рівні продовжують працювати.

| Предмет / напрям | Типова дія | Заряд / відновлення |
|---|---|---|
| Меч — Жнива | Додаткова спроба з шансом 10%, якщо звичайна душа не випала; не для босів | 5 лише за успішну додаткову душу |
| Меч — Голод | Пряме вбивство моба відновлює 2 здоров’я, якщо ви поранені | 10 / 10 с |
| Плащ — Примарна тканина | Удар від 4 пунктів після звичайного захисту послаблюється на 40% | 15 / 30 с |
| Плащ — Останній покров | Після пережитого удару при здоров’ї до 6 створює приманку на 5 с і дає швидкість на 3 с | 20 / 30 с |

Покров не скасовує смертельний удар. Ефекти спорядження не працюють у формі духа. Звичайні успішні випадання душ, невдалі додаткові спроби та повне здоров’я не витрачають заряд.

Рецепти:
- Пожирач: ` R /NSN/ O `, де R — червона банка душі, N — уламок незериту, S — Меч палача, O — обсидіан. Крафт створює новий меч; зачарування старого Меча палача не переносяться.
- Плащ: `WGW/WBW/W W`, де W — чорна вовна, G — золотий злиток, B — синя банка душі. Банки повертаються порожніми.

У конфігурації: `enableSoulGearPowers` (здібності й операції стола для спорядження), `gearChargeCapacity`, `gearBlueSoulCharge`, `gearRedSoulCharge`, `gearPurpleSoulCharge`, `gearUpgradeCostMultiplier`; параметри `gearHarvest*`, `gearHeal*`, `gearGuard*`, `gearCloakCooldownSeconds`, `gearShroud*` регулюють шанси, ефекти, пороги, витрати й перезарядки. Анімація плаща керується `animateRitualsAndSkills`.

Капелюх та активні здібності самого спорядження залишені для наступного етапу; ця версія додає чотири альтернативні пасивні ефекти.

''';s=s.replace('## Налаштування збірок',section+'## Налаштування збірок');p.write_text(s,encoding='utf-8')
p=Path('MOD_DESCRIPTION.md');s=p.read_text().replace('Мод має налаштування','Створіть Пожирача душ і Плащ сутінків. Заряджайте їх поглинутими душами, підвищуйте шкоду та захист і вибирайте особливу силу для кожного предмета.\n\nМод має налаштування').replace('Configurable soul categories','Craft a Soul Devourer and Twilight Cloak, charge them with souls and choose permanent equipment paths. Configurable soul categories');p.write_text(s,encoding='utf-8')
p=Path('NEXT_STEPS.md');s=p.read_text();p.write_text('''# Оновлення 0.10.0 — спорядження душ

Реалізовано Пожирача душ і Плащ сутінків, вкладку спорядження, заряджання трьома типами душ, три рівні характеристик і по два взаємовиключні пасивні напрями. Плащ має окрему модель та анімацію. Капелюх і активні здібності спорядження — можливий наступний етап, ще не реалізовані. Вівтар і якір залишені.

Поточні правила та параметри — README.md. Нижче історія попередніх домовленостей, а не список невиконаної роботи.

'''+s,encoding='utf-8')
p=Path('ASSET_SOURCES.md');s=p.read_text();s+='''
## Equipment 0.10.0 — built-in imagegen, 2026-10-20

Generated with the built-in imagegen tool. Originals retained under `C:/Users/Vlad/.codex/generated_images/01a14e41-fd25-7831-a996-501ab25ed225/`. Mechanical nearest-neighbor scaling to 32×32 performed by `work/gear-textures10.ps1`; original alpha preserved for inventory icons. Project destinations are under `src/main/resources/assets/lastbreath/`.

### textures/item/soul_devourer.png
Source: `exec-a5d6c65f-8183-4c7a-b74d-5aeb66d9a171.png`
Prompt: Use case: stylized-concept. Asset type: Minecraft inventory item sprite, Soul Devourer sword. One isolated sword diagonally from bottom left handle to top right point. True coarse pixel art on a strict 32 by 32 conceptual pixel grid enlarged with crisp square pixels. Dark charcoal netherite blade with a thin cyan soul channel, a small blue soul gemstone in antique gold crossguard, dark brown wrapped handle. Mysterious but restrained vanilla Minecraft appearance. Entire sword fits with 2 pixel padding. No letters, no text, no frame, no glow outside silhouette, no tiny ornament, no antialiasing. Transparent background.

### textures/item/soul_cloak.png
Source: `exec-d644fd4c-0109-4d02-affb-2267dc6d653e.png`
Prompt: Use case: stylized-concept. Asset type: single Minecraft inventory item sprite for a mysterious soul cloak. Front view, empty wearable shoulder cape cloak without person and without hood or hat. Coarse crisp 32x32 conceptual pixel art. Charcoal purple cloth, two muted cyan seams running down its edges, small antique gold clasp with blue gem at neck, broad shoulders tapered flowing hem. Simple readable vanilla Minecraft style, limited colors, no tiny decorative details. Entire isolated item centered with padding. No text, no frame, no antialiasing, no exterior bloom. Transparent background.

### textures/entity/equipment/humanoid/soul_cloak.png
Source: `exec-daf36f11-c61c-4855-a583-72ffdb5ca8a1.png`
Prompt: Use case: stylized-concept. Asset type: flat seamless pixel-art fabric texture tile for a Minecraft dark magical cloak model, NOT an item or garment illustration. Entire square filled edge to edge with dark charcoal muted purple cloth in large crisp pixels on a conceptual 32x32 grid. Low contrast woven mottling, a few sparse very subdued desaturated blue stitches. No central motif, no borders, no objects, no text, no transparency, no lighting gradients. Vanilla Minecraft texture density, flat tileable cloth material.
''';p.write_text(s,encoding='utf-8')
