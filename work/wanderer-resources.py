from pathlib import Path
import json
r=Path('src/main/resources');a=r/'assets/lastbreath';d=r/'data'
def write(p,v):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(v,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
for name,slot in [('soul_cloak','chest'),('soul_trousers','leg'),('soul_boots','foot')]:
 layer='humanoid_leggings' if slot=='leg' else 'humanoid'
 write(a/'equipment'/f'{name}.json',{'layers':{layer:[{'texture':'lastbreath:soul_cloak'}],layer+'_baby':[{'texture':'lastbreath:soul_cloak'}]}})
 write(a/'items'/f'{name}.json',{'model':{'type':'minecraft:model','model':'lastbreath:item/'+name}})
 write(a/'models/item'/f'{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'lastbreath:item/'+name}})
 if slot=='chest':continue
 for tag in [slot+'_armor','enchantable/'+slot+'_armor','enchantable/armor','enchantable/durability']:
  p=d/'minecraft/tags/item'/f'{tag}.json';v=json.loads(p.read_text(encoding='utf-8')) if p.exists() else {'replace':False,'values':[]}
  if 'lastbreath:'+name not in v['values']:v['values'].append('lastbreath:'+name)
  write(p,v)
 pattern=['LLL','LBL','L L'] if slot=='leg' else ['L L','L L','B G']
 key={'L':'minecraft:leather','B':'lastbreath:soul_jar'}
 if slot=='foot':key['G']='minecraft:gold_ingot'
 write(d/'lastbreath/recipe'/f'{name}.json',{'type':'minecraft:crafting_shaped','category':'equipment','pattern':pattern,'key':key,'result':{'id':'lastbreath:'+name,'count':1}})
for locale,names in [('uk_ua',['Плащ мандрівника душ','Штани мандрівника душ','Чоботи мандрівника душ']),('en_us',['Soul Wanderer Coat','Soul Wanderer Trousers','Soul Wanderer Boots'])]:
 p=a/'lang'/f'{locale}.json';v=json.loads(p.read_text(encoding='utf-8'))
 for n,label in zip(['soul_cloak','soul_trousers','soul_boots'],names):v['item.lastbreath.'+n]=label
 write(p,v)
p=Path('gradle.properties');p.write_text(p.read_text(encoding='utf-8').replace('mod_version=0.10.4','mod_version=0.11.0'),encoding='utf-8')
