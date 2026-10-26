from pathlib import Path
p=Path('src/gameTest/java/me/vlad/lastbreath/LastBreathGameTests.java');s=p.read_text();needle='            check(guards.stream().noneMatch(net.minecraft.world.entity.Mob::isAlive),"Interrupted ritual cleans guardians");';s=s.replace(needle,needle+'''
            p.snapTo(origin.getX()+1.5,65,origin.getZ()+.5);guards.clear();stone.start(p);guards.addAll(stone.guardians());
            check(guards.size()==Config.ANCIENT_GUARDS.get(),"Canceled trial can be retried");
            for(var m:guards)m.setHealth(0);MemoryStoneEntity.tick(level,origin,level.getBlockState(origin),stone);
            check(level.getBlockState(origin).getValue(MemoryStoneBlock.STAGE)==2&&p.getInventory().countItem(LastBreath.KNOWLEDGE_SCROLLS.get(branch+4).get())==1,"Ancient victory awards matching gold scroll");
            check(p.getInventory().countItem(LastBreath.PURPLE_SOUL.get())==0&&p.getInventory().countItem(LastBreath.KNOWLEDGE_FRAGMENT.get())==0,"Ancient offering is consumed once on success");
            stone.start(p);check(stone.guardians().isEmpty(),"Completed shrine cannot be farmed again");
            var cache=origin.offset(32,0,0);
            for(var at:BlockPos.betweenClosed(cache.offset(-4,-1,-4),cache.offset(4,4,4)))level.setBlockAndUpdate(at,(at.getY()==64?Blocks.SOUL_SOIL:Blocks.AIR).defaultBlockState());
            check(new FragmentRuinFeature().place(level,level.getChunkSource().getGenerator(),net.minecraft.util.RandomSource.create(4),cache.above(10)),"Fragment ruin generates");
            check(level.getBlockEntity(cache) instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chest&&chest.getItem(13).is(LastBreath.KNOWLEDGE_FRAGMENT.get()),"Fragment cache contains knowledge rather than a repeatable trial");''');p.write_text(s)
p=Path('gradle.properties');s=p.read_text().replace('mod_version=0.11.1','mod_version=0.12.0');p.write_text(s)
p=Path('README.md');s=p.read_text(encoding='utf8').replace('# Last Breath 0.11.1','# Last Breath 0.12.0');a=s.index('## Оберіг і здібності');z=s.index('## Ритуальний стіл і дерево',a);s=s[:a]+'''## Оберіг і здібності

- Тримайте Оберіг душ в одній із рук. **Утримуйте лівий Alt**, наведіть на силу й відпустіть: вибір без витрат. Центр або Escape скасовує вибір. Клавішу можна змінити в керуванні.
- **ПКМ:** застосувати вибрану силу. Унизу праворуч — її значок, недоступність у поточній формі та секунди відновлення. F1 приховує індикатор.
- **Shift+ПКМ:** три запаси душ. Друга рука + Shift+ПКМ також показує духів.
- Набір — до трьох звичайних активних сил та однієї золотої. Споряджання/зняття доступне натисканням на вивчений вузол біля стола. Пасивні бонуси працюють після вивчення без слотів.

Активні здібності використовують спільне відновлення, яке зберігається після виходу. Деякі сили мають довше відновлення; вибір іншої не скидає таймер.

'''+s[z:];a=s.index('Дев’ять вузлів');z=s.index('Позичений час не є справжнім',a);s=s[:a]+'''**20 вузлів, чотири гілки:** Відлуння, Мандрівник, Охоронець, Жнець. Бірюзові вузли — дії духа, темно-сині — пасивні властивості, золоті — особливі сили. Опис і ціна показуються при наведенні. Сервер перевіряє оплату, передумови та відстань.

Перший вузол кожної гілки доступний за сині душі без сувою. Подальший розвиток потребує знань відповідної гілки; золота вершина — її давнього сувою. Приклади: приманка, ривок, підсвічування ворога, допомога другові, послаблення монстра, чуття душ, тимчасове тіло, добровільний поділ шкоди.

### Знання Незеру

У **нових чанках долини піску душ** генеруються святилища та покинуті руїни. Руїни містять скриню з 1–3 уламками знань. Камінь пам’яті у святилищі відкриває одну з чотирьох гілок:

1. Живий гравець натискає ПКМ на камені. Треба здолати 3 вартових за 120 с, залишаючись у межах 24 блоків. Мирна складність не запускає випробування.
2. Перемога дає гарантований сувій учаснику, який почав ритуал. Друзі можуть допомагати. Перша нагорода одноразова.
3. Повторний, давній ритуал потребує **4 уламки знань і 1 фіолетову душу як предмети**. Після перемоги над 5 сильнішими вартовими ресурси списуються, а гравець отримує давній сувій цієї гілки.
4. Вартові не дають досвіду, предметів чи душ. Вихід, смерть, відхід або розвантаження/перезапуск скасовують незавершене випробування без списання пожертви. Завершені стадії зберігаються у світі.

Кнопка **«Вивчити сувій»** біля ритуального стола бере перший сувій з інвентарю. Дух може підбирати сувої. Дублікат звичайного сувою повертає 1 уламок, давнього — 3. Душі оплачують самі вміння; сувій лише відкриває знання. Базове відродження сувоїв не потребує.

Зв’язок душ вимагає згоди обох живих учасників. Позичений час духу дає тимчасове тіло, живому — коротку швидкість. Жнива душ коштують 2 червоні душі та тимчасово збільшують шанс здобуття синіх/червоних душ.

'''+s[z:];s=s.replace('Старі ID `return_heart`','Старі рівні та баланси збережено, відповідні вузли нового дерева відкриваються під час перенесення прогресу.\n\nСтарі ID `return_heart`');s+='''
## Зміни 0.12.0 та перевірка

Передні поли плаща прибрано, задні залишено. Душі в банках збільшено в межах скла. Обидва мечі отримали нові текстури. Додано явні бічні грані статичним предметам і кожному пікселю анімованих душ. Сувої мають об’ємні валики й різні печатки.

Додаткові параметри: `talentCooldownSeconds`, `goldTalentCooldownSeconds`, `shrineTimeLimitSeconds`, `shrineGuardCount`, `ancientShrineGuardCount`. Частота генерації змінюється datapack-файлами `worldgen/placed_feature/memory_shrine.json` та `fragment_ruin.json`.

Перевірки: окремий GameTest-світ, завантаження ресурсів клієнтом, знімки дерева та експорт фактичної геометрії броні. Автоматичний запуск клієнта не замінює ручне проходження й перевірку балансу з друзями.
''';p.write_text(s,encoding='utf8')
p=Path('MOD_DESCRIPTION.md');s=p.read_text(encoding='utf8').replace('три шляхи — Мисливця, Духа й Хранителя','чотири шляхи — Відлуння, Мандрівника, Охоронця та Женця');s=s.replace('Створіть Пожирача душ', 'Знаходьте святилища у долині піску душ, долайте вартових і відкривайте знання стародавніх сувоїв. Дерево містить 20 умінь; вибирайте споряджені сили через меню Alt.\n\nСтворіть Пожирача душ');s+='\nExplore Nether soul-sand-valley shrines, defeat guardians and unlock four branches of ancient knowledge. Twenty talents, a three-plus-one active loadout and an Alt ability wheel support different play styles.\n';p.write_text(s,encoding='utf8')
p=Path('ASSET_SOURCES.md');p.write_text(p.read_text(encoding='utf8')+'''\n## 0.12.0 sword redesign and native models

Both sword sprites originate from the built-in imagegen sheet `exec-6f85b3b3-b8df-44bf-a1c0-c5436ae3a01c.png`, retained as `art/update012/swords-source.png`. Mechanical crop and nearest-neighbor packing: `work/swords012.cjs`. Palette: dark steel, brown leather and brass; cyan soul crack on the Soul Devourer.

Scrolls and memory stones use native JSON geometry with references to Minecraft block textures. No Minecraft raster files are redistributed. Sidewalls for existing item sprites are geometry derived from their alpha masks; animated souls use pixel prisms so frame changes retain matching edge faces. These operations do not repaint the source textures.
''',encoding='utf8')
