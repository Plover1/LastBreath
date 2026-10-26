from pathlib import Path
import shutil

root = Path.cwd()
old = root / 'art/return_totem'
backup = root / 'art/return_totem_v0101'
if not backup.exists():
    shutil.copytree(old, backup)
source = (root / 'work/totem3d.py').read_text(encoding='utf-8')
start = source.index("cube('Carved body'")
end = source.index('display=', start)
geometry = '''# Thin carved silhouette, front is +Z. Dimensions stay inside the item volume.
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
 band('Heart backing %d'%i,lo-.18,hi+.18,w+.2,8.82,9.2,'gold')
for side in [-1,1]:
 x=8+side*.95
 cube('Heart lobe frame',[x-.65,8.05,8.82],[x+.65,8.65,9.2],'gold')
for i,(lo,hi,w) in enumerate(heart_rows):
 band('Heart crystal row %d'%i,lo,hi,w,9.21,9.3,'blue')
for side in [-1,1]:
 x=8+side*.95
 cube('Heart crystal lobe',[x-.45,8.05,9.21],[x+.45,8.43,9.3],'blue')
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
'''
source = source[:start] + geometry + source[end:]
source = source.replace("sorted(polys,key=lambda f:(f[4],f[0]))", "sorted(polys,key=lambda f:f[0])")
source = source.replace("replace('mod_version=0.10.0','mod_version=0.10.1')", "replace('mod_version=0.10.1','mod_version=0.10.2')")
(root / 'work/totem3d.py').write_text(source,encoding='utf-8')
print('Redesign generator written; previous model backed up.')
