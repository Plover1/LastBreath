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
for(const name of ['executioner_sword','soul_devourer']){
 const m=await sprite(name);
 m.display.thirdperson_righthand={rotation:[0,-90,55],translation:[0,4,.5],scale:[.85,.85,.85]};
 m.display.thirdperson_lefthand={rotation:[0,90,-55],translation:[0,4,.5],scale:[.85,.85,.85]};
 fs.writeFileSync(base+'models/item/'+name+'.json',JSON.stringify(m,null,2));
 console.log(name,m.elements.length,'elements with solid sidewalls');
}
})();
