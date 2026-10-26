from pathlib import Path
import json
b=Path('src/main/resources/assets/lastbreath')
display=json.loads((b/'models/item/return_heart.json').read_text())['display']
for i in range(8):
 tex={'paper':'minecraft:item/paper','wood':'minecraft:block/dark_oak_planks','gold':'minecraft:block/gold_block','seal':['minecraft:block/sea_lantern','minecraft:block/prismarine_bricks','minecraft:block/chiseled_stone_bricks','minecraft:block/sculk'][i%4],'particle':'minecraft:block/birch_planks'}
 elements=[]
 def box(a,z,t):elements.append({'from':a,'to':z,'faces':{f:{'texture':'#'+t,'uv':[0,0,16,16]} for f in ['north','south','east','west','up','down']}})
 # Native geometry gives the parchment solid edge faces and raised wooden rollers.
 tex['parchment']='minecraft:block/stripped_birch_log'
 box([3,2,7.5],[13,14,8],'parchment');box([2,1,7],[14,3,8.5],'wood');box([2,13,7],[14,15,8.5],'wood')
 if i>=4:
  for x in [2,12]:box([x,.8,6.9],[x+2,3.2,8.6],'gold');box([x,12.8,6.9],[x+2,15.2,8.6],'gold')
 elements.append({'from':[5,5,7.4],'to':[11,11,8.1],'faces':{'north':{'texture':'#seal','uv':[16,0,0,16]},'south':{'texture':'#seal','uv':[0,0,16,16]}}})
 model={'parent':'minecraft:block/block','ambientocclusion':False,'gui_light':'front','textures':tex,'display':display,'elements':elements}
 (b/'models/item'/f'knowledge_scroll_{i}.json').write_text(json.dumps(model,indent=2))
