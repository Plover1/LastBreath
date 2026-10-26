from pathlib import Path
p=Path('work/totem3d.py');s=p.read_text(encoding='utf-8').replace("[7.65,2,6.35],[8.35,13.4,6.55]","[7.65,9.5,6.35],[8.35,13.4,6.55]").replace("[7.65,2,6.65],[8.35,9.5,7.05]","[7.65,2.5,6.65],[8.35,9.5,7.05]")
s=s.replace('polys.append((sum(q[2] for q in v)/4,v,uv,name))','priority=(2 if e["from"][2]>=9.65 else 1 if e["from"][2]>=9 else 0) if cy>0 else (1 if e["to"][2]<=7.1 else 0)\n   polys.append((sum(q[2] for q in v)/4,v,uv,name,priority))')
s=s.replace('for idx,(_,pts,uv,name) in enumerate(sorted(polys,key=lambda f:f[0])):', 'for idx,(_,pts,uv,name,_) in enumerate(sorted(polys,key=lambda f:(f[4],f[0]))):')
p.write_text(s,encoding='utf-8')
