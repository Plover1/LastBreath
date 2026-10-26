from pathlib import Path
s=Path('work/render-totem-depth.cjs').read_text(encoding='utf-8');a=s.index('const model=');b=s.index('const W=',a)
s=s[:a]+'''const base='src/main/resources/assets/lastbreath/';
const models=['return_heart','soul_jar'].map(n=>JSON.parse(fs.readFileSync(base+'models/item/'+n+'.json','utf8')));
const textures={};for(const name of ['return_heart','empty_soul_jar','mob_soul_overlay']){const path=base+'textures/item/'+name+'.png',m=await sharp(path).metadata();textures[name]=await sharp(path).extract({left:0,top:0,width:m.width,height:m.width}).ensureAlpha().raw().toBuffer({resolveWithObject:true});}
let tex,info;
'''+s[b:]
s=s.replace('for(const [view,yaw] of [0,-.6,Math.PI+.5].entries()){','for(const [view,yaw] of [-.85,0,-.85].entries()){\n const model=models[view===0?0:1];')
s=s.replace("for(const e of model.elements)for(const [face,verts] of Object.entries(faceVerts)){","for(const e of model.elements)for(const [face,verts] of Object.entries(faceVerts)){\n if(!e.faces[face])continue;const name=model.textures[e.faces[face].texture.slice(1)].split('/').pop();({data:tex,info}=textures[name]);")
s=s.replace('LAST BREATH · RETURN TOTEM','LAST BREATH · ITEM EDGE / JAR CHECK').replace("['FRONT','THREE-QUARTER','BACK']","['TOTEM SIDE','SOUL INSIDE JAR','JAR SIDE']").replace("'art/return_totem/preview.png'","'art/item-fixes-preview.png'")
Path('work/render-item-fixes.cjs').write_text(s,encoding='utf-8')
