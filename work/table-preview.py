from pathlib import Path
import json,zipfile
root=Path.cwd();out=root/'art/table-preview';out.mkdir(parents=True,exist_ok=True)
names=['dark_oak_planks','dark_oak_log','polished_deepslate','deepslate_tiles','gold_block','cyan_concrete']
with zipfile.ZipFile(root/'build/moddev/artifacts/minecraft-patched-26.3.0.90-merged.jar') as z:
 for n in names:
  p='assets/minecraft/textures/block/'+n+'.png'
  if p in z.namelist():(out/(n+'.png')).write_bytes(z.read(p))
print([p.name for p in out.glob('*.png')])
textures={n:'minecraft:block/'+n for n in names}
textures['particle']=textures['dark_oak_planks']
def box(name,a,b,mat):
 return dict(name=name,**{'from':a,'to':b},faces={f:dict(texture='#'+mat,uv=uv) for f,uv in {'up':[a[0],a[2],b[0],b[2]],'down':[a[0],a[2],b[0],b[2]],'south':[a[0],16-b[1],b[0],16-a[1]],'north':[a[0],16-b[1],b[0],16-a[1]],'east':[a[2],16-b[1],b[2],16-a[1]],'west':[a[2],16-b[1],b[2],16-a[1]]}.items()})
def save(n,e):
 m=dict(parent='minecraft:block/block',textures=textures,elements=e)
 (out/(n+'.json')).write_text(json.dumps(m,indent=2),encoding='utf-8')
 assert all(all(0<=v<=16 for v in a['from']+a['to']) for a in e)
 assert all(all(x<y for x,y in zip(a['from'],a['to'])) for a in e)
wood='dark_oak_planks';log='dark_oak_log';gold='gold_block';stone='polished_deepslate';cyan='cyan_concrete'
e=[]
for x in [1,12]:
 for z in [1,12]:e.append(box('Sturdy leg',[x,0,z],[x+3,10,z+3],log))
for z in [2,12]:e.append(box('Lower stretcher',[3,3,z],[13,5,z+2],wood))
e.extend([box('Solid tabletop',[0,10,0],[16,13,16],wood),box('Inset dark panel',[2,13,2],[14,13.15,14],log)])
for x in [0,14]:
 for z in [0,14]:e.append(box('Small brass corner',[x,13,z],[x+2,13.2,z+2],gold))
# Thin relief traces a branching tree across the tabletop, three paths.
e.append(box('Tree trunk',[7.7,13.16,5],[8.3,13.24,12],gold))
e.append(box('Tree branch',[4,13.16,8],[12,13.24,8.5],gold))
for x in [4,11.5]:e.append(box('Tree branch stem',[x,13.16,4.5],[x+.5,13.24,8.5],gold))
for x,z in [(7.5,11),(7.5,7.5),(3.75,7.5),(11.25,7.5),(3.75,4),(7.5,4),(11.25,4)]:
 e.append(box('Soul node socket',[x-.15,13.25,z-.15],[x+1.15,13.42,z+1.15],stone))
 e.append(box('Soul node',[x,13.43,z],[x+1,13.55,z+1],cyan))
save('ritual_table',e)
e=[box('Stone foundation',[0,0,0],[16,2,16],stone),box('Stepped stone pedestal',[2,2,2],[14,8,14],'deepslate_tiles'),box('Upper stone slab',[1,8,1],[15,10,15],stone),box('Offering recess floor',[4,10,4],[12,10.2,12],log)]
for a,b in [([1,10,1],[15,12,4]),([1,10,12],[15,12,15]),([1,10,4],[4,12,12]),([12,10,4],[15,12,12])]:e.append(box('Dark wooden rim',a,b,wood))
# A quiet stone-lined central socket, no floating display to imply implemented animation.
e.append(box('Totem resting socket',[6,10.21,6],[10,10.35,10],stone))
for x,z in [(2,2),(12,2),(2,12),(12,12)]:
 e.append(box('Brass rune mount',[x,12,z],[x+2,12.15,z+2],gold))
 e.append(box('Soul rune',[x+.5,12.16,z+.5],[x+1.5,12.3,z+1.5],cyan))
for x in [3,11]:e.append(box('Front brass detail',[x,4,1.85],[x+2,6,2],gold))
save('return_altar',e)
(out/'README.md').write_text('''# Прев’ю столів

Окремі Java block-моделі, не встановлені в мод. Рендер preview.png використовує саме ці JSON та оригінальні текстури Minecraft, витягнуті з локальних ресурсів для перегляду. У моделі посилання minecraft:block/...; додавати ванільні PNG до мода не потрібно.

Ліворуч — ритуальний стіл із деревом вузлів, праворуч — вівтар із заглибленням під тотем. Нижній ряд показує стільниці зверху. Синій колір тут статичний; анімації та освітлення в цьому прев’ю не реалізовані. Прев’ю не є скриншотом Minecraft. Поточний мод 0.10.3 і його 2D-тотем залишено без змін.
''',encoding='utf-8')
