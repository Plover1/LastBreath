// Orthographic textured model preview with per-pixel depth, not an in-game screenshot.
const fs=require('fs');
const sharp=require('C:/Users/Vlad/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp');
(async()=>{
const quads=JSON.parse(fs.readFileSync('art/wanderer-detail/pose-'+(process.argv[2]||0)+'.json','utf8'));
const {data:tex,info}=await sharp('art/wanderer-detail/armor-atlas.png').ensureAlpha().raw().toBuffer({resolveWithObject:true});
const W=1000,H=700,pix=Buffer.alloc(W*H*4),depth=new Float64Array(W*H).fill(-Infinity);
for(let i=0;i<W*H;i++)pix.set([25,29,34,255],i*4);
const faceVerts={south:(a,b)=>[[a[0],b[1],b[2]],[b[0],b[1],b[2]],[b[0],a[1],b[2]],[a[0],a[1],b[2]]],north:(a,b)=>[[b[0],b[1],a[2]],[a[0],b[1],a[2]],[a[0],a[1],a[2]],[b[0],a[1],a[2]]],east:(a,b)=>[[b[0],b[1],b[2]],[b[0],b[1],a[2]],[b[0],a[1],a[2]],[b[0],a[1],b[2]]],west:(a,b)=>[[a[0],b[1],a[2]],[a[0],b[1],b[2]],[a[0],a[1],b[2]],[a[0],a[1],a[2]]],up:(a,b)=>[[a[0],b[1],a[2]],[b[0],b[1],a[2]],[b[0],b[1],b[2]],[a[0],b[1],b[2]]],down:(a,b)=>[[a[0],a[1],b[2]],[b[0],a[1],b[2]],[b[0],a[1],a[2]],[a[0],a[1],a[2]]]};
function triangle(p,uv,shade){
 const [a,b,c]=p, den=(b[1]-c[1])*(a[0]-c[0])+(c[0]-b[0])*(a[1]-c[1]);if(Math.abs(den)<1e-8)return;
 for(let y=Math.max(0,Math.floor(Math.min(...p.map(v=>v[1]))));y<=Math.min(H-1,Math.ceil(Math.max(...p.map(v=>v[1]))));y++)for(let x=Math.max(0,Math.floor(Math.min(...p.map(v=>v[0]))));x<=Math.min(W-1,Math.ceil(Math.max(...p.map(v=>v[0]))));x++){
 const u=((b[1]-c[1])*(x+.5-c[0])+(c[0]-b[0])*(y+.5-c[1]))/den,v=((c[1]-a[1])*(x+.5-c[0])+(a[0]-c[0])*(y+.5-c[1]))/den,w=1-u-v;
 if(Math.min(u,v,w)<-1e-7)continue;const z=u*a[2]+v*b[2]+w*c[2],idx=y*W+x;if(z<depth[idx])continue;
 const tx=Math.max(0,Math.min(info.width-1,Math.floor((u*uv[0][0]+v*uv[1][0]+w*uv[2][0])*info.width/16))),ty=Math.max(0,Math.min(info.height-1,Math.floor((u*uv[0][1]+v*uv[1][1]+w*uv[2][1])*info.height/16))),ti=(ty*info.width+tx)*4;
 if(tex[ti+3]<128)continue;depth[idx]=z;for(let k=0;k<3;k++)pix[idx*4+k]=Math.round(tex[ti+k]*shade);
 }
}
for(const [view,yaw] of [-.35,Math.PI+.4].entries()){
 const cy=Math.cos(yaw),sy=Math.sin(yaw),cp=Math.cos(.12),sp=Math.sin(.12);
 function project(p){let[x,y,z]=[p[0],p[1]-12,p[2]];[x,z]=[x*cy-z*sy,x*sy+z*cy];return[250+view*500+x*20,370-(y*cp-z*sp)*20,y*sp+z*cp];}
 for(const q of quads){
 const p=q.map(project),uv=q.map(v=>v.slice(3)),a=q[0],b=q[1],c=q[2];
 const norm=[(b[1]-a[1])*(c[2]-a[2])-(b[2]-a[2])*(c[1]-a[1]),(b[2]-a[2])*(c[0]-a[0])-(b[0]-a[0])*(c[2]-a[2]),(b[0]-a[0])*(c[1]-a[1])-(b[1]-a[1])*(c[0]-a[0])];
 const len=Math.hypot(...norm)||1,shade=.76+.24*Math.abs(norm[2]/len);
 triangle([p[0],p[1],p[2]],[uv[0],uv[1],uv[2]],shade);triangle([p[0],p[2],p[3]],[uv[0],uv[2],uv[3]],shade);
 }
}
const labels=Buffer.from('<svg width="1000" height="700"><g font-family="sans-serif"><text x="35" y="45" fill="#ebd8a6" font-size="23">LAST BREATH · SOUL WANDERER · DETAIL DRAFT</text><text x="35" y="76" fill="#9daabd" font-size="16">Actual baked armor geometry · front / back</text><text x="500" y="665" text-anchor="middle" fill="#9daabd" font-size="16">COAT + TROUSERS + BOOTS</text></g></svg>');
await sharp(pix,{raw:{width:W,height:H,channels:4}}).composite([{input:labels}]).png().toFile('art/wanderer-detail/'+(process.argv[2]?'frame-'+process.argv[2]:'preview')+'.png');
})();
