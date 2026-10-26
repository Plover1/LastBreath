from pathlib import Path
p=Path('MOD_DESCRIPTION.md');s=p.read_text(encoding='utf-8');
if 'Створіть Пожирача душ' not in s:s=s.replace('Мод має налаштування','Створіть Пожирача душ і Плащ сутінків. Заряджайте їх поглинутими душами, підвищуйте шкоду та захист і вибирайте особливу силу для кожного предмета.\n\nМод має налаштування')
p.write_text(s,encoding='utf-8')
import json
root=Path('src/main/resources');count=0
for p in root.rglob('*'):
 if p.suffix in ('.json','.mcmeta'):json.loads(p.read_text(encoding='utf-8-sig'));count+=1
print('Valid JSON resources:',count)
uk=json.loads((root/'assets/lastbreath/lang/uk_ua.json').read_text(encoding='utf-8'));en=json.loads((root/'assets/lastbreath/lang/en_us.json').read_text(encoding='utf-8'))
assert uk['item.lastbreath.soul_devourer']=='Пожирач душ'
assert uk.keys()==en.keys(),(uk.keys()-en.keys(),en.keys()-uk.keys())
print('English and Ukrainian keys match:',len(uk))
