from pathlib import Path
s=Path('work/render-totem-depth.cjs').read_text(encoding='utf-8');a=s.index('const model=');b=s.index('const faceVerts=')
s=s[:a]+'''const quads=JSON.parse(fs.readFileSync('art/wanderer/pose-0.json','utf8'));
const {data:tex,info}=await sharp('art/wanderer/armor-atlas.png').ensureAlpha().raw().toBuffer({resolveWithObject:true});
const W=1000,H=700,pix=Buffer.alloc(W*H*4),depth=new Float64Array(W*H).fill(-Infinity);
for(let i=0;i<W*H;i++)pix.set([25,29,34,255],i*4);
'''+s[b:]
a=s.index('for(const [view,yaw]');b=s.index('const labels=',a)
s=s[:a]+'''for(const [view,yaw] of [-.35,Math.PI+.4].entries()){
 const cy=Math.cos(yaw),sy=Math.sin(yaw),cp=Math.cos(.12),sp=Math.sin(.12);
 function project(p){let[x,y,z]=[p[0],p[1]-12,p[2]];[x,z]=[x*cy-z*sy,x*sy+z*cy];return[250+view*500+x*20,370-(y*cp-z*sp)*20,y*sp+z*cp];}
 for(const q of quads){
 const p=q.map(project),uv=q.map(v=>v.slice(3)),a=q[0],b=q[1],c=q[2];
 const norm=[(b[1]-a[1])*(c[2]-a[2])-(b[2]-a[2])*(c[1]-a[1]),(b[2]-a[2])*(c[0]-a[0])-(b[0]-a[0])*(c[2]-a[2]),(b[0]-a[0])*(c[1]-a[1])-(b[1]-a[1])*(c[0]-a[0])];
 const len=Math.hypot(...norm)||1,shade=.76+.24*Math.abs(norm[2]/len);
 triangle([p[0],p[1],p[2]],[uv[0],uv[1],uv[2]],shade);triangle([p[0],p[2],p[3]],[uv[0],uv[2],uv[3]],shade);
 }
}
'''+s[b:];a=s.index('const labels=');s=s[:a]+'''const labels=Buffer.from('<svg width="1000" height="700"><g font-family="sans-serif"><text x="35" y="45" fill="#ebd8a6" font-size="23">LAST BREATH · SOUL WANDERER</text><text x="35" y="76" fill="#9daabd" font-size="16">Actual baked armor geometry · front / back</text><text x="500" y="665" text-anchor="middle" fill="#9daabd" font-size="16">COAT + TROUSERS + BOOTS</text></g></svg>');
await sharp(pix,{raw:{width:W,height:H,channels:4}}).composite([{input:labels}]).png().toFile('art/wanderer/preview.png');
})();
''';Path('work/render-wanderer.cjs').write_text(s,encoding='utf-8')
