from pathlib import Path
s=Path('work/render-totem-depth.cjs').read_text(encoding='utf-8')
a=s.index('const model=');b=s.index('const faceVerts=')
s=s[:a]+'''const models=['ritual_table','return_altar'].map(n=>JSON.parse(fs.readFileSync('art/table-preview/'+n+'.json','utf8')));
const texs={};for(const name of ['dark_oak_planks','dark_oak_log','polished_deepslate','deepslate_tiles','gold_block','cyan_concrete'])texs[name]=await sharp('art/table-preview/'+name+'.png').ensureAlpha().raw().toBuffer({resolveWithObject:true});
let tex,info;
const W=1200,H=940,pix=Buffer.alloc(W*H*4),depth=new Float64Array(W*H).fill(-Infinity);
for(let i=0;i<W*H;i++)pix.set([25,29,34,255],i*4);
'''+s[b:]
a=s.index('for(const [view,yaw]');b=s.index('const labels=',a)
s=s[:a]+'''for(let view=0;view<4;view++){
 const model=models[view%2],yaw=view<2?-.65:0,pitch=view<2?.55:Math.PI/2,cy=Math.cos(yaw),sy=Math.sin(yaw),cp=Math.cos(pitch),sp=Math.sin(pitch),scale=view<2?20:12;
 function project(p){let[x,y,z]=[p[0]-8,p[1]-7,p[2]-8];[x,z]=[x*cy-z*sy,x*sy+z*cy];return[300+(view%2)*600+x*scale,(view<2?300:730)-(y*cp-z*sp)*scale,y*sp+z*cp];}
 for(const e of model.elements)for(const [face,verts] of Object.entries(faceVerts)){
 ({data:tex,info}=texs[e.faces[face].texture.slice(1)]);
 const p=verts(e.from,e.to).map(project),[u,v,uu,vv]=e.faces[face].uv,uv=[[u,v],[uu,v],[uu,vv],[u,vv]],shade=face==='up'?1:face==='south'?.88:.68;
 triangle([p[0],p[1],p[2]],[uv[0],uv[1],uv[2]],shade);triangle([p[0],p[2],p[3]],[uv[0],uv[2],uv[3]],shade);
 }
}
'''+s[b:]
a=s.index('const labels=');s=s[:a]+'''const labels=Buffer.from('<svg width="1200" height="940"><g font-family="sans-serif"><text x="40" y="48" fill="#ebd8a6" font-size="26">LAST BREATH · TABLE MODEL PREVIEW</text>'+['RITUAL TABLE','RETURN ALTAR'].map((n,i)=>`<text x="${300+i*600}" y="105" text-anchor="middle" fill="#ded8c7" font-size="22">${n}</text>`).join('')+'<path d="M40 555H1160" stroke="#434951"/><text x="600" y="595" text-anchor="middle" fill="#9daabd" font-size="17">TOP VIEW · ACTUAL MODEL GEOMETRY</text></g></svg>');
await sharp(pix,{raw:{width:W,height:H,channels:4}}).composite([{input:labels}]).png().toFile('art/table-preview/preview.png');
})();
'''
Path('work/render-table-preview.cjs').write_text(s,encoding='utf-8')
