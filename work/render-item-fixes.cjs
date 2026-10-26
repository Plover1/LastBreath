// Orthographic textured model preview with per-pixel depth, not an in-game screenshot.
const fs=require('fs');
const sharp=require('C:/Users/Vlad/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp');
(async()=>{
const base='src/main/resources/assets/lastbreath/';
const models=['return_heart','soul_jar'].map(n=>JSON.parse(fs.readFileSync(base+'models/item/'+n+'.json','utf8')));
const textures={};for(const name of ['return_heart','empty_soul_jar','mob_soul_overlay']){const path=base+'textures/item/'+name+'.png',m=await sharp(path).metadata();textures[name]=await sharp(path).extract({left:0,top:0,width:m.width,height:m.width}).ensureAlpha().raw().toBuffer({resolveWithObject:true});}
let tex,info;
const W=1200,H=680,pix=Buffer.alloc(W*H*4),depth=new Float64Array(W*H).fill(-Infinity);
for(let i=0;i<W*H;i++)pix.set([21,25,34,255],i*4);
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
for(const [view,yaw] of [-.85,0,-.85].entries()){
 const model=models[view===0?0:1];
 const cy=Math.cos(yaw),sy=Math.sin(yaw),cp=Math.cos(.1),sp=Math.sin(.1);
 function project(p){let[x,y,z]=[p[0]-8,p[1]-7.5,p[2]-8];[x,z]=[x*cy-z*sy,x*sy+z*cy];return[200+view*400+x*33,350-(y*cp-z*sp)*33,y*sp+z*cp];}
 for(const e of model.elements)for(const [face,verts] of Object.entries(faceVerts)){
 if(!e.faces[face])continue;const name=model.textures[e.faces[face].texture.slice(1)].split('/').pop();({data:tex,info}=textures[name]);
 const p=verts(e.from,e.to).map(project),[u,v,uu,vv]=e.faces[face].uv,uv=[[u,v],[uu,v],[uu,vv],[u,vv]],shade=face==='up'?1:face==='south'?1:.8;
 triangle([p[0],p[1],p[2]],[uv[0],uv[1],uv[2]],shade);triangle([p[0],p[2],p[3]],[uv[0],uv[2],uv[3]],shade);
 }
}
const labels=Buffer.from('<svg width="1200" height="680"><g font-family="sans-serif"><text x="36" y="44" fill="#ebd8a6" font-size="24">LAST BREATH · ITEM EDGE / JAR CHECK</text>'+['TOTEM SIDE','SOUL INSIDE JAR','JAR SIDE'].map((s,i)=>`<text x="${200+i*400}" y="630" text-anchor="middle" fill="#9daabd" font-size="18">${s}</text>`).join('')+'</g></svg>');
await sharp(pix,{raw:{width:W,height:H,channels:4}}).composite([{input:labels}]).png().toFile('art/item-fixes-preview.png');
})();
