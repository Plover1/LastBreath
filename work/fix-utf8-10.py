from pathlib import Path
files=[Path(n) for n in ['README.md','MOD_DESCRIPTION.md','NEXT_STEPS.md','ASSET_SOURCES.md']]+list(Path('src/main/resources/assets/lastbreath/lang').glob('*.json'))
for p in files:
 text=p.read_text(encoding='utf-8-sig');out=[];changed=0
 for line in text.splitlines(keepends=True):
  for _ in range(8):
   try: repaired=line.encode('cp1251').decode('utf-8')
   except (UnicodeEncodeError,UnicodeDecodeError): break
   if repaired==line: break
   line=repaired;changed+=1
  out.append(line)
 p.write_text(''.join(out),encoding='utf-8');print(p,changed)
p=Path('ASSET_SOURCES.md');s=p.read_text(encoding='utf-8').replace('into a 64×32 UV atlas','into a 128×64 UV atlas').replace('the 10×20 panels at (33,1) and (44,1)','the 20×40 panels at (66,2) and (88,2), with a half-scale cape model part');p.write_text(s,encoding='utf-8')
p=Path('README.md');s=p.read_text(encoding='utf-8').replace('**Мечем палача**','**Мечем палача або Пожирачем душ**');p.write_text(s,encoding='utf-8')
