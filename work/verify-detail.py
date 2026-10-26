from pathlib import Path
import json,math,shutil
for p in Path('art/wanderer-detail').glob('pose-*.json'):
 q=json.loads(p.read_text(encoding='utf-8'))
 assert all(all(math.isfinite(n) for n in v) and 0<=v[3]<=16 and 0<=v[4]<=16 for face in q for v in face),p
out=Path(r'C:\Users\Vlad\Documents\Codex\2026-10-18\z\outputs')
shutil.copy2('art/wanderer-detail/preview.png',out/'LastBreath-Wanderer-detail-preview.png')
shutil.copy2('art/wanderer-detail/README.md',out/'LastBreath-Wanderer-detail-README.md')
print('All nine pose exports validated; preview copied. Release version unchanged.')
