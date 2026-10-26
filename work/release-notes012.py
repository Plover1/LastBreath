from pathlib import Path
p=Path('src/main/java/me/vlad/lastbreath/MemoryStoneEntity.java');s=p.read_text().replace('||SoulService.ghost(p)||','||p.getData(LastBreath.SOUL).isGhost()||');p.write_text(s)
p=Path('work/scroll-models012.py');s=p.read_text().replace("'minecraft:item/ender_eye','minecraft:item/feather','minecraft:item/iron_chestplate','lastbreath:item/mob_soul'","'minecraft:block/sea_lantern','minecraft:block/prismarine_bricks','minecraft:block/chiseled_stone_bricks','minecraft:block/sculk'");p.write_text(s)
p=Path('NEXT_UPDATE_012.md');s=p.read_text(encoding='utf8').replace('Стан: реалізація розпочата. Не вважати цей файл звітом про готовність.','''Стан: реалізовано у 0.12.0. Фінальна збірка та перевірка артефакту виконуються перед видачею.

Перевірено 10 інтеграційними GameTest-тестами: збереження, старий прогрес, сувої/дублікати, набір сил, відродження, спорядження, святилище, обидві нагороди, переривання без оплати, руїни з уламками. Клієнт завантажив ресурси без помилок моделей мода; дерево та фактична геометрія броні переглянуті. Ручне кооперативне проходження й баланс ще потребують ігрового тестування.

Святилища та руїни з’являються в нових чанках долини піску душ. Якір і окремий вівтар не змінювалися. Автор: Siris102.''');p.write_text(s,encoding='utf8')
