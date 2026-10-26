const fs=require('fs'),sharp=require('C:/Users/Vlad/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp');
const base='src/main/resources/assets/lastbreath/';
(async()=>{
async function sprite(name){const{data,info}=await sharp(base+'textures/item/'+name+'.png').ensureAlpha().raw().toBuffer({resolveWithObject:true});const w=info.width,h=info.width,e=[];const alpha=(x,y)=>x>=0&&y>=0&&x<w&&y<h?data[(y*w+x)*4+3]:0;
for(let y=0;y<h;y++){let x=0;while(x<w){if(alpha(x,y)<16){x++;continue;}const start=x;while(x<w&&alpha(x,y)>=16)x++;const end=x;let sample=start;for(let i=start;i<end;i++)if(alpha(i,y)>alpha(sample,y))sample=i;
const uv=[(sample+.25)*16/w,(y+.25)*16/h,(sample+.75)*16/w,(y+.75)*16/h],faces={};
faces.south={texture:'#sprite',uv:[start*16/w,y*16/h,end*16/w,(y+1)*16/h]};faces.north={texture:'#sprite',uv:[end*16/w,y*16/h,start*16/w,(y+1)*16/h]};
for(const side of ['west','east'])faces[side]={texture:'#sprite',uv};
for(const [side,dy] of [['up',-1],['down',1]]){let k=start;while(k<end){if(alpha(k,y+dy)>=16){k++;continue;}const lo=k;while(k<end&&alpha(k,y+dy)<16)k++;e.push({from:[lo*16/w,16-(y+1)*16/h,7.5],to:[k*16/w,16-y*16/h,8],faces:{[side]:{texture:'#sprite',uv}}});}}
e.push({from:[start*16/w,16-(y+1)*16/h,7.5],to:[end*16/w,16-y*16/h,8],faces});}}
return {parent:'minecraft:block/block',ambientocclusion:false,gui_light:'front',textures:{sprite:'lastbreath:item/'+name,particle:'lastbreath:item/'+name},display:{gui:{rotation:[0,0,0],translation:[0,0,0],scale:[1,1,1]},ground:{rotation:[0,0,0],translation:[0,2,0],scale:[.5,.5,.5]},fixed:{rotation:[0,180,0],translation:[0,0,0],scale:[1,1,1]},thirdperson_righthand:{rotation:[0,0,0],translation:[0,3,1],scale:[.55,.55,.55]},thirdperson_lefthand:{rotation:[0,0,0],translation:[0,3,1],scale:[.55,.55,.55]},firstperson_righthand:{rotation:[0,-90,25],translation:[1.13,3.2,1.13],scale:[.68,.68,.68]},firstperson_lefthand:{rotation:[0,90,-25],translation:[1.13,3.2,1.13],scale:[.68,.68,.68]}},elements:e};}
const totem=await sprite('return_heart');fs.writeFileSync(base+'models/item/return_heart.json',JSON.stringify(totem,null,2));
for(const n of ['executioner_sword','soul_devourer','soul_reader','soul_cloak','soul_trousers','soul_boots']){const m=await sprite(n);if(n.includes('sword')||n==='soul_devourer'){m.display.thirdperson_righthand={rotation:[0,-90,55],translation:[0,4,.5],scale:[.85,.85,.85]};m.display.firstperson_righthand={rotation:[0,-90,25],translation:[1.13,3.2,1.13],scale:[.68,.68,.68]};}fs.writeFileSync(base+'models/item/'+n+'.json',JSON.stringify(m,null,2));}
const jar=await sprite('empty_soul_jar');fs.writeFileSync(base+'models/item/empty_soul_jar.json',JSON.stringify(jar,null,2));
for(const[name,soul]of[['soul_jar','mob_soul_overlay'],['red_soul_jar','red_soul_overlay'],['purple_soul_jar','purple_soul_overlay']]){
const m=structuredClone(jar);m.textures.soul='lastbreath:item/'+soul;
// All animated frame pixels stay within the interior glass rectangle (22..44,24..45).
m.elements.push({name:'Soul inside glass',from:[5.5,4.75,7.48],to:[11,10,8.02],faces:{south:{texture:'#soul',uv:[2,4,14,16]},north:{texture:'#soul',uv:[14,4,2,16]}}});
// Foreground rim establishes depth without painting over the soul or changing the original sprite.
for(const [x0,y0,x1,y1]of[[10,20,22,52],[44,20,56,52],[22,15,44,24],[22,45,44,58]])m.elements.push({name:'Foreground jar rim',from:[x0/4,16-y1/4,7.44],to:[x1/4,16-y0/4,8.06],faces:{south:{texture:'#sprite',uv:[x0/4,y0/4,x1/4,y1/4]},north:{texture:'#sprite',uv:[x1/4,y0/4,x0/4,y1/4]}}});
fs.writeFileSync(base+'models/item/'+name+'.json',JSON.stringify(m,null,2));}
console.log('Explicit thin sprite sidewalls:',totem.elements.length,'totem strips;',jar.elements.length,'jar strips; three bounded soul animations.');
})();
