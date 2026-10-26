from pathlib import Path
p=Path('work/fix-item-sides.cjs');s=p.read_text(encoding='utf-8-sig');s=s[:s.index("const totem=await sprite")]+'''for(const name of ['executioner_sword','soul_devourer']){
 const m=await sprite(name);
 m.display.thirdperson_righthand={rotation:[0,-90,55],translation:[0,4,.5],scale:[.85,.85,.85]};
 m.display.thirdperson_lefthand={rotation:[0,90,-55],translation:[0,4,.5],scale:[.85,.85,.85]};
 fs.writeFileSync(base+'models/item/'+name+'.json',JSON.stringify(m,null,2));
 console.log(name,m.elements.length,'elements with solid sidewalls');
}
})();\n''';Path('work/sword-models128.cjs').write_text(s,encoding='utf8')
p=Path('work/swords012.cjs');s=p.read_text(encoding='utf8').replace('resize(32,32','resize(128,128');p.write_text(s,encoding='utf8')
p=Path('gradle.properties');s=p.read_text().replace('mod_version=0.12.0','mod_version=0.12.1');p.write_text(s)
