from pathlib import Path
import json
root=Path(__file__).resolve().parent.parent
r=root/'src/main/resources'
def save(path,obj):
 p=r/path;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(obj,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
for name in ['red_soul','purple_soul','red_soul_jar','purple_soul_jar']:
 save(f'assets/lastbreath/items/{name}.json',{'model':{'type':'minecraft:model','model':f'lastbreath:item/{name}'}})
 textures={'layer0':f'lastbreath:item/{name}'} if not name.endswith('jar') else {'layer0':'lastbreath:item/empty_soul_jar','layer1':f'lastbreath:item/{name.replace("_jar","_overlay")}'}
 save(f'assets/lastbreath/models/item/{name}.json',{'parent':'minecraft:item/generated','textures':textures})
for name in ['mob_soul','red_soul','purple_soul','mob_soul_overlay','red_soul_overlay','purple_soul_overlay']:
 save(f'assets/lastbreath/textures/item/{name}.png.mcmeta',{'animation':{'frametime':6,'interpolate':False}})
save('assets/lastbreath/models/item/soul_jar.json',{'parent':'minecraft:item/generated','textures':{'layer0':'lastbreath:item/empty_soul_jar','layer1':'lastbreath:item/mob_soul_overlay'}})
for name in ['return_heart','ritual_table']:
 p=r/f'data/lastbreath/recipe/{name}.json';d=json.loads(p.read_text());d['key']['J']='lastbreath:red_soul_jar' if name=='return_heart' else 'lastbreath:soul_jar';save(str(p.relative_to(r)),d)
# Optional crafting uses colored souls without consuming a boss soul in mandatory early progression.
for soul in ['mob_soul','red_soul','purple_soul']:
 jar='soul_jar' if soul=='mob_soul' else soul+'_jar'
 save(f'data/lastbreath/recipe/{jar}.json',{'type':'minecraft:crafting_shapeless','category':'misc','ingredients':['lastbreath:empty_soul_jar','lastbreath:'+soul],'result':{'id':'lastbreath:'+jar,'count':1}})
# Native block models: plain vanilla wood at native UV scale; no stretched ornate legs.
for p in (r/'assets/lastbreath/models/block').glob('*.json'):
 d=json.loads(p.read_text());d.setdefault('textures',{})['wood']='minecraft:block/dark_oak_log';d['textures']['plank']='minecraft:block/dark_oak_planks'
 for index,e in enumerate(d.get('elements',[])):
  a=e['from'];b=e['to'];dx=b[0]-a[0];dy=b[1]-a[1];dz=b[2]-a[2]
  for face,f in e['faces'].items():
   # Preserve top glyph and charge gems; remap side wood with proportional UVs.
   if f['texture']=='#side':
    f['texture']='#wood' if dy>dx else '#plank'
    f['uv']=[0,0,dx if face in ['north','south','up','down'] else dz,dz if face in ['up','down'] else dy]
   if p.stem=='ritual_table' and index>0 and face in ['up','down']:f['texture']='#wood';f['uv']=[0,0,dx,dz]
 save(str(p.relative_to(r)),d)
uk={
'item.lastbreath.mob_soul':'Мирна душа','item.lastbreath.red_soul':'Люта душа','item.lastbreath.purple_soul':'Велична душа',
'item.lastbreath.soul_jar':'Банка з мирною душею','item.lastbreath.red_soul_jar':'Банка з лютою душею','item.lastbreath.purple_soul_jar':'Банка з величною душею',
'lastbreath.souls.three':'Сині: %s  Червоні: %s  Фіолетові: %s',
'lastbreath.table.hint':'Дух: ПКМ. Живий: утримуйте ПКМ лютою душею біля стола.',
'lastbreath.table.channel':'Вхід у ритуал',
'lastbreath.ability.selected':'Обрано: %s','lastbreath.ability.form':'Ця здібність недоступна у вашій поточній формі.',
'lastbreath.borrow.start':'Позичений час: %s с. Ви вразливі!','lastbreath.borrow.end':'Позичений час вичерпано. Ви знову дух.',
'lastbreath.bond.active':'Зв’язок душ із %s активний. Ви ділите шкоду.',
'lastbreath.bond.none':'Потрібен вільний живий друг поруч із вивченим Зв’язком душ.',
'lastbreath.bond.request':'%s пропонує ділити шкоду. Для згоди активуйте Зв’язок душ протягом 20 с.',
'lastbreath.bond.sent':'Пропозицію зв’язку надіслано. Очікуємо згоди друга.',
'lastbreath.mark':'%s позначає небезпеку: %s, %s, %s',
'lastbreath.tree.price':'Ціна — сині: %s; червоні: %s; фіолетові: %s',
'lastbreath.tree.buy':'Натисніть для придбання.',
'lastbreath.tree.requirements':'Потрібен попередній вузол. Фінальні: перемога над босом; Зв’язок також порятунок друга. Перевірте налаштування здібностей.',
'lastbreath.branch.0':'Мисливець','lastbreath.branch.1':'Дух','lastbreath.branch.2':'Хранитель',
'lastbreath.skill.6':'Відлуння','lastbreath.skill.7':'Позичений час','lastbreath.skill.8':'Зв’язок душ','lastbreath.skill.9':'Позначити небезпеку',
'lastbreath.skill.6.desc':'Після Погляду за завісу. Створює тимчасову приманку, що відволікає монстрів поруч. Працює в обох формах.',
'lastbreath.skill.7.desc':'Після Кроку духа. Тимчасово повертає духа до вразливого тіла. Інвентар залишається звичайним: без копій і відкату. Вихід завершує дію.',
'lastbreath.skill.8.desc':'Після Спільного захисту. Двоє живих власників уміння добровільно ділять шкоду. Віддалення, смерть або вихід розривають зв’язок.',
'lastbreath.tooltip.reader':'ПКМ: запас. Друга рука + ПКМ: список духів. Shift+ПКМ униз: вибір здібності; вперед: активація.',
'lastbreath.tooltip.soul_jar':'Кольорова душа для крафту. Порожня банка повертається.',
'lastbreath.tooltip.executioner_sword':'Добивайте мобів для душ. Колір залежить від істоти; шанс — від налаштувань і Жнеця.',
'lastbreath.list_header':'Духи онлайн — для порятунку утримуйте ПКМ тотемом поруч:',
'lastbreath.list_entry':'%s — %s, %s, %s (%s). Ціна: %s сердець.',
'lastbreath.death':'%s став духом. Якір душі: %s, %s, %s (%s). Список — Оберіг душ у другій руці.',
}
en={
'item.lastbreath.mob_soul':'Peaceful Soul','item.lastbreath.red_soul':'Fierce Soul','item.lastbreath.purple_soul':'Sovereign Soul',
'item.lastbreath.soul_jar':'Peaceful Soul Jar','item.lastbreath.red_soul_jar':'Fierce Soul Jar','item.lastbreath.purple_soul_jar':'Sovereign Soul Jar',
'lastbreath.souls.three':'Blue: %s  Red: %s  Purple: %s',
'lastbreath.table.hint':'Spirit: use table. Living: hold use with a Fierce Soul near the table.',
'lastbreath.table.channel':'Entering the ritual',
'lastbreath.ability.selected':'Selected: %s','lastbreath.ability.form':'This ability requires a different form.',
'lastbreath.borrow.start':'Borrowed Time: %s s. You are vulnerable!','lastbreath.borrow.end':'Borrowed Time expired. You are a spirit again.',
'lastbreath.bond.active':'Soul Bond with %s active. You share damage.',
'lastbreath.bond.none':'A nearby living friend with Soul Bond must be available.',
'lastbreath.bond.request':'%s offers to share damage. Activate Soul Bond within 20 s to accept.',
'lastbreath.bond.sent':'Bond offered. Waiting for consent.',
'lastbreath.mark':'%s marks danger: %s, %s, %s',
'lastbreath.tree.price':'Cost — blue: %s; red: %s; purple: %s',
'lastbreath.tree.buy':'Click to purchase.',
'lastbreath.tree.requirements':'Requires preceding node. Final powers: defeat a boss; Soul Bond also requires rescuing a friend. Check ability configuration.',
'lastbreath.branch.0':'Hunter','lastbreath.branch.1':'Spirit','lastbreath.branch.2':'Guardian',
'lastbreath.skill.6':'Echo','lastbreath.skill.7':'Borrowed Time','lastbreath.skill.8':'Soul Bond','lastbreath.skill.9':'Mark Danger',
'lastbreath.skill.6.desc':'Requires Veil Sight. Creates a temporary decoy that distracts nearby monsters. Available in both forms.',
'lastbreath.skill.7.desc':'Requires Spirit Step. Temporarily restores a vulnerable body. Inventory stays real, with no copies or rollback. Logout ends it.',
'lastbreath.skill.8.desc':'Requires Shared Ward. Two living owners voluntarily share damage. Distance, death or logout ends the bond.',
'lastbreath.tooltip.reader':'Use: balance. Offhand use: spirits. Sneak-use looking down: select ability; looking forward: activate.',
'lastbreath.tooltip.soul_jar':'Colored soul for crafting. The empty jar is returned.',
'lastbreath.tooltip.executioner_sword':'Killing blows harvest souls. Color depends on the creature; chance on configuration and Reaper.',
'lastbreath.list_header':'Online spirits — hold use with a Totem near them to rescue:',
'lastbreath.list_entry':'%s — %s, %s, %s (%s). Cost: %s hearts.',
'lastbreath.death':'%s became a spirit. Soul anchor: %s, %s, %s (%s). Use an offhand Soul Reader to list spirits.',
}
for lang,updates in [('uk_ua',uk),('en_us',en)]:
 p=r/f'assets/lastbreath/lang/{lang}.json';d=json.loads(p.read_text(encoding='utf-8'));d.update(updates)
 if lang=='uk_ua':d={k:v.replace('Тотемм','Тотемом').replace('Тотем залишилося','Тотем залишився') for k,v in d.items()}
 # Prices are now dynamic, avoid stale fixed costs in tooltips.
 for i in range(6):
  k=f'lastbreath.skill.{i}.desc';d[k]=d[k].split(' Вартість:')[0].split(' Cost:')[0]
 save(str(p.relative_to(r)),d)
