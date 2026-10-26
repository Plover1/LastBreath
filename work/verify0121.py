from pathlib import Path
import zipfile,struct,shutil,hashlib
p=Path('build/libs/lastbreath-0.12.1.jar')
with zipfile.ZipFile(p) as z:
 assert not any(any(k in n for k in ['GameTests','ClientStartupTest','WandererPreview','WandererDetailExport','WandererDetailModel']) for n in z.namelist())
 for n in ['executioner_sword','soul_devourer']:
  data=z.read('assets/lastbreath/textures/item/'+n+'.png');assert struct.unpack('>II',data[16:24])==(128,128)
 assert '0.12.1' in z.read('META-INF/neoforge.mods.toml').decode()
out=Path('C:/Users/Vlad/Documents/Codex/2026-10-18/z/outputs');shutil.copy2(p,out/p.name)
shutil.copy2('README.md',out/'LastBreath-0.12.1-README.md')
print('Verified 128x128 textures and release archive:',out/p.name)
print('SHA256',hashlib.sha256(p.read_bytes()).hexdigest())
