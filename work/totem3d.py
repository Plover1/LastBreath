from pathlib import Path
import json,uuid,base64,math
root=Path.cwd();out=root/'art/return_totem';out.mkdir(parents=True,exist_ok=True)
tex=root/'src/main/resources/assets/lastbreath/textures/item/return_heart.png';data=base64.b64encode(tex.read_bytes()).decode()
materials={'wood':[18,18,24,21],'dark':[19,27,23,30],'gold':[21,12,26,15],'blue':[22,22,26,26],'ice':[23,23,25,25]}
elements=[];bb=[]
def cube(name,a,b,mat='wood',rot=None):
 uv=materials[mat];faces={f:{'uv':[v/4 for v in uv],'texture':'#totem'} for f in ['north','east','south','west','up','down']}
 e={'name':name,'from':a,'to':b,'faces':faces}
 if rot:e['rotation']={'origin':rot[0],'axis':'z','angle':rot[1]}
 elements.append(e)
 uid=str(uuid.uuid4());be={'name':name,'type':'cube','uuid':uid,'from':a,'to':b,'box_uv':False,'autouv':0,'color':{'wood':0,'dark':0,'gold':2,'blue':3,'ice':3}[mat],'origin':rot[0] if rot else [8,8,8],'rotation':[0,0,rot[1] if rot else 0],'faces':{f:{'uv':uv,'texture':0} for f in faces}}
 bb.append(be)
# Thin carved silhouette, front is +Z. Dimensions stay inside the item volume.
def band(name, y0, y1, halfwidth, z0=7.1, z1=8.8, mat='wood'):
 cube(name,[8-halfwidth,y0,z0],[8+halfwidth,y1,z1],mat)

# Continuous tapered carving, without detached arms or a square torso.
for i,(y0,y1,w) in enumerate([(0.4,1.1,.35),(1.1,2,.6),(2,3,.85),(3,4,1.1),(4,5,1.4),(5,6,1.7),(6,7,2),(7,8.8,2.25),(8.8,9.7,2.55),(9.7,10.2,2.25),(10.2,11.2,1.65),(11.2,13.9,2.1),(13.9,14.5,1.85),(14.5,15,1.35)]):
 band('Carved silhouette %02d'%i,y0,y1,w)

# Recessed mask and individual carved brow/cheek planes.
band('Face recess',11,13.6,1.8,8.8,8.9,'dark')
for x in [6.15,8.75]:
 cube('Recessed cyan eye',[x,11.85,8.91],[x+1.1,12.7,8.98],'blue')
 cube('Eye inner light',[x+.3,12.05,8.99],[x+.7,12.45,9.02],'ice')
 cube('Carved cheek',[x-.2,11.25,8.9],[x+1.2,11.8,9.2])
cube('Brow bridge',[7.3,12.4,8.9],[8.7,13.65,9.2])
cube('Long carved nose',[7.65,10.95,8.9],[8.35,12.5,9.35])
for side in [-1,1]:
 x=8+side*1.95
 cube('Mask side ridge',[x-.2,11.7,8.9],[x+.2,13.5,9.15])
band('Crown thin wrap',13.65,14.05,2.15,7.02,9.12,'gold')
band('Crown raised center',14.05,14.3,1.2,8.8,9.12,'gold')
cube('Forehead gold frame',[7.5,13.1,9.12],[8.5,14.7,9.3],'gold')
cube('Forehead blue inset',[7.7,13.35,9.3],[8.3,14.35,9.42],'blue')

# Deep channel connecting the eyes and the inset heart.
band('Soul channel',8.4,10.95,.65,8.81,8.91,'dark')
for i,(x,y,s) in enumerate([(7.8,10.5,.3),(8.15,10,.35),(7.55,9.55,.3),(7.9,9.05,.5)]):
 cube('Channel soul %d'%i,[x,y,8.92],[x+s,y+s,9.02],'blue')

# Heart built from contiguous stepped rows, two lobes and a notch, never a rotated square.
# Row widths measured from center; crown row has two separate lobes.
heart_rows=[(5.45,5.85,.35),(5.85,6.25,.7),(6.25,6.65,1.05),(6.65,7.05,1.4),(7.05,7.45,1.65),(7.45,7.85,1.8),(7.85,8.25,1.8)]
for i,(lo,hi,w) in enumerate(heart_rows):
 band('Heart backing %d'%i,lo-.18 if i==0 else lo,hi,w+.2,8.82,9.2,'gold')
for side in [-1,1]:
 x=8+side*.95
 cube('Heart lobe frame',[x-.65,8.25,8.82],[x+.65,8.65,9.2],'gold')
for i,(lo,hi,w) in enumerate(heart_rows):
 band('Heart crystal row %d'%i,lo,hi,w,9.21,9.3,'blue')
for side in [-1,1]:
 x=8+side*.95
 cube('Heart crystal lobe',[x-.45,8.25,9.21],[x+.45,8.43,9.3],'blue')
for i,(lo,hi,w) in enumerate([(5.9,6.3,.2),(6.3,6.7,.35),(6.7,7.1,.45),(7.1,7.5,.35)]):
 band('Heart light facet %d'%i,lo,hi,w,9.31,9.34,'ice')

# Side ridges flow into the body; narrow gold accents trace the carving.
for side in [-1,1]:
 for i,(y,w,h) in enumerate([(9.25,2.15,.55),(8.65,2.45,.6),(7.8,2.35,.85),(6.9,2.05,.9),(6,1.8,.9),(5.1,1.5,.9),(4.2,1.2,.9)]):
  x=8+side*w
  cube('Carved side bevel',[x-.25,y,8.65],[x+.25,y+h,9.13])
  if i in [0,1,3,5,6]:
   cube('Fine gold side inlay',[x-.12,y,9.14],[x+.12,y+h*.7,9.2],'gold')
cube('Lower seal frame',[7.55,2.9,8.82],[8.45,4.6,9.08],'gold')
cube('Lower seal soul',[7.75,3.15,9.09],[8.25,4.3,9.18],'blue')
cube('Tip gold inlay',[7.85,.7,8.82],[8.15,2.9,8.97],'gold')
cube('Back fine spine',[7.85,2,6.98],[8.15,13.7,7.1],'gold')
cube('Back inset gem',[7.65,9.2,6.87],[8.35,10.3,6.98],'blue')
# One continuous crystal UV region across every stepped row and lobe.
for e,be in zip(elements,bb):
 if e['name'].startswith(('Heart crystal','Heart light')):
  a,b=e['from'],e['to']
  uv=[22+(a[0]-6.2)/3.6*4,22+(8.43-b[1])/2.98*4,
      22+(b[0]-6.2)/3.6*4,22+(8.43-a[1])/2.98*4]
  e['faces']['south']['uv']=[v/4 for v in uv]
  be['faces']['south']['uv']=uv
display={'gui':{'rotation':[12,-22,0],'translation':[0,0,0],'scale':[.95,.95,.95]},'ground':{'rotation':[0,0,0],'translation':[0,3,0],'scale':[.5,.5,.5]},'fixed':{'rotation':[0,180,0],'translation':[0,0,0],'scale':[.85,.85,.85]},'thirdperson_righthand':{'rotation':[0,0,0],'translation':[0,1,1],'scale':[.7,.7,.7]},'thirdperson_lefthand':{'rotation':[0,0,0],'translation':[0,1,1],'scale':[.7,.7,.7]},'firstperson_righthand':{'rotation':[0,-15,0],'translation':[1,1,0],'scale':[.8,.8,.8]},'firstperson_lefthand':{'rotation':[0,15,0],'translation':[1,1,0],'scale':[.8,.8,.8]}}
model={'credit':'Siris102 — Last Breath','ambientocclusion':False,'gui_light':'front','textures':{'totem':'lastbreath:item/return_heart','particle':'lastbreath:item/return_heart'},'display':display,'elements':elements}
p=root/'src/main/resources/assets/lastbreath/models/item/return_heart.json';p.write_text(json.dumps(model,indent=2,ensure_ascii=False)+'\n',encoding='utf-8');(out/'return_heart.json').write_text(p.read_text(encoding='utf-8'),encoding='utf-8')
project={'meta':{'format_version':'5.0','model_format':'java_block','box_uv':False},'name':'Last Breath — Return Totem','model_identifier':'return_heart','credit':'Siris102','resolution':{'width':64,'height':64},'elements':bb,'outliner':[e['uuid'] for e in bb],'textures':[{'path':'','name':'return_heart.png','folder':'item','namespace':'lastbreath','id':'0','uuid':str(uuid.uuid4()),'mode':'bitmap','source':'data:image/png;base64,'+data,'width':64,'height':64,'uv_width':64,'uv_height':64}],'display':display}
(out/'return_totem.bbmodel').write_text(json.dumps(project,indent=2,ensure_ascii=False),encoding='utf-8');(out/'return_heart.png').write_bytes(tex.read_bytes())
# SVG preview projects the actual textured faces; it is not a concept illustration.
faces={'south':([0,0,1],lambda a,b:[[a[0],b[1],b[2]],[b[0],b[1],b[2]],[b[0],a[1],b[2]],[a[0],a[1],b[2]]]),'north':([0,0,-1],lambda a,b:[[b[0],b[1],a[2]],[a[0],b[1],a[2]],[a[0],a[1],a[2]],[b[0],a[1],a[2]]]),'east':([1,0,0],lambda a,b:[[b[0],b[1],b[2]],[b[0],b[1],a[2]],[b[0],a[1],a[2]],[b[0],a[1],b[2]]]),'west':([-1,0,0],lambda a,b:[[a[0],b[1],a[2]],[a[0],b[1],b[2]],[a[0],a[1],b[2]],[a[0],a[1],a[2]]]),'up':([0,1,0],lambda a,b:[[a[0],b[1],a[2]],[b[0],b[1],a[2]],[b[0],b[1],b[2]],[a[0],b[1],b[2]]]),'down':([0,-1,0],lambda a,b:[[a[0],a[1],b[2]],[b[0],a[1],b[2]],[b[0],a[1],a[2]],[a[0],a[1],a[2]]])}
def rotate(p,origin,angle):
 c,s=math.cos(angle),math.sin(angle);x,y=p[0]-origin[0],p[1]-origin[1];return [origin[0]+x*c-y*s,origin[1]+x*s+y*c,p[2]]
parts=['<svg xmlns="http://www.w3.org/2000/svg" width="1200" height="680" viewBox="0 0 1200 680"><rect width="1200" height="680" fill="#151922"/><text x="36" y="44" fill="#ebd8a6" font-family="sans-serif" font-size="24">LAST BREATH · RETURN TOTEM</text>']
for vi,yaw in enumerate([0,-.6,math.pi+.5]):
 cy,sy=math.cos(yaw),math.sin(yaw);pitch=.10;cp,sp=math.cos(pitch),math.sin(pitch)
 def camera(p):
  x,y,z=p[0]-8,p[1]-7.5,p[2]-8;x,z=x*cy-z*sy,x*sy+z*cy;return [x,y*cp-z*sp,y*sp+z*cp]
 polys=[]
 for e in elements:
  for name,(normal,points) in faces.items():
   pts=points(e['from'],e['to']);n=normal
   if 'rotation' in e:
    r=e['rotation'];angle=math.radians(r['angle']);pts=[rotate(v,r['origin'],angle) for v in pts];n=rotate(n,[0,0,0],angle)
   nx,nz=n[0]*cy-n[2]*sy,n[0]*sy+n[2]*cy
   if n[1]*sp+nz*cp<=.001:continue
   v=[camera(p) for p in pts];uv=e['faces'][name]['uv'];uv=[q*4 for q in uv]
   priority=(2 if e["from"][2]>=9.65 else 1 if e["from"][2]>=9 else 0) if cy>0 else (1 if e["to"][2]<=7.1 else 0)
   polys.append((sum(q[2] for q in v)/4,v,uv,name,priority))
 for idx,(_,pts,uv,name,_) in enumerate(sorted(polys,key=lambda f:f[0])):
  xy=[(200+vi*400+p[0]*33,350-p[1]*33) for p in pts];pid=f'p{vi}_{idx}';poly=' '.join(f'{x:.3f},{y:.3f}' for x,y in xy);u,v,uu,vv=uv;w,h=uu-u,vv-v
  a=(xy[1][0]-xy[0][0])/w;b=(xy[1][1]-xy[0][1])/w;c=(xy[3][0]-xy[0][0])/h;d=(xy[3][1]-xy[0][1])/h;tx=xy[0][0]-a*u-c*v;ty=xy[0][1]-b*u-d*v
  parts.append(f'<clipPath id="{pid}"><polygon points="{poly}"/></clipPath><g clip-path="url(#{pid})"><image href="data:image/png;base64,{data}" width="64" height="64" style="image-rendering:pixelated" transform="matrix({a} {b} {c} {d} {tx} {ty})"/></g>')
  if name not in ['south','up']:parts.append(f'<polygon points="{poly}" fill="black" opacity=".18"/>')
 parts.append(f'<text x="{200+vi*400}" y="630" text-anchor="middle" fill="#9daabd" font-family="sans-serif" font-size="18">{["FRONT","THREE-QUARTER","BACK"][vi]}</text>')
parts.append('</svg>');(out/'preview.svg').write_text(''.join(parts),encoding='utf-8')
assert len(elements)==len(bb) and len(set(e['uuid'] for e in bb))==len(bb)
assert all(0<=q<=16 for e in elements for v in [e['from'],e['to']] for q in v)
print('Generated',len(elements),'cuboids; embedded texture; preview.svg')
p=root/'gradle.properties';p.write_text(p.read_text(encoding='utf-8').replace('mod_version=0.10.1','mod_version=0.10.2'),encoding='utf-8')
