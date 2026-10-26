from pathlib import Path
s=Path('work/render-wanderer.cjs').read_text(encoding='utf-8-sig')
s=s.replace("const quads=JSON.parse(fs.readFileSync('art/wanderer/pose-'+(process.argv[2]||0)+'.json','utf8'));","const quads=JSON.parse(fs.readFileSync('art/skin/quads.json','utf8'));")
s=s.replace("sharp('art/wanderer/armor-atlas.png')","sharp('art/skin/LastBreath-Siris102.png')")
s=s.replace('p[1]-12','p[1]-16').replace('x*20','x*16').replace(')*20',')*16').replace('370-','380-')
s=s.replace('LAST BREATH · SOUL WANDERER','LAST BREATH · SIRIS102').replace('Actual baked armor geometry · front / back','64 × 64 skin · classic arms · front / back').replace('COAT + TROUSERS + BOOTS','PREVIEW RENDERED FROM THE DOWNLOADABLE SKIN')
s=s.replace("'art/wanderer/'+(process.argv[2]?'frame-'+process.argv[2]:'preview')+'.png'","'art/skin/preview.png'")
Path('work/render-skin.cjs').write_text(s,encoding='utf8')
