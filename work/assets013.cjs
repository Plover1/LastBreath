const fs=require('fs'),sharp=require('C:/Users/Vlad/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp');
const base='src/main/resources/assets/lastbreath/',gen='C:/Users/Vlad/.codex/generated_images/01a14e41-fd25-7831-a996-501ab25ed225/';
(async()=>{
fs.mkdirSync('art/update013',{recursive:true});fs.mkdirSync(base+'textures/gui/abilities',{recursive:true});
fs.copyFileSync(gen+'exec-5411472f-83b5-4e72-9167-e5df99ed8a18.png','art/update013/glyphs-source.png');
fs.copyFileSync(gen+'exec-119989a5-0bee-4285-908a-e26f5e2c5618.png','art/update013/logo-source.png');
const src='art/update013/glyphs-source.png',meta=await sharp(src).metadata();
for(let i=0;i<30;i++){
const x=Math.round(i%6*meta.width/6),y=Math.round(Math.floor(i/6)*meta.height/5),right=Math.round((i%6+1)*meta.width/6),bottom=Math.round((Math.floor(i/6)+1)*meta.height/5);
const png=await sharp(src).extract({left:x,top:y,width:right-x,height:bottom-y}).resize(64,64,{kernel:'nearest',fit:'contain',background:'#00000000'}).png().toBuffer();
if(i<28)fs.writeFileSync(base+'textures/gui/abilities/'+i+'.png',png);
if(i==28)fs.writeFileSync(base+'textures/item/knowledge_fragment.png',png);
if(i==29)fs.writeFileSync(base+'textures/item/soul_reader.png',png);
}
for(let i=0;i<8;i++){let branch=i%4,index=[0,5,10,19][branch];fs.copyFileSync(base+'textures/gui/abilities/'+index+'.png',base+'textures/block/scroll_seal_'+branch+'.png');let p=base+'models/item/knowledge_scroll_'+i+'.json',j=JSON.parse(fs.readFileSync(p));j.textures.seal='lastbreath:block/scroll_seal_'+branch;fs.writeFileSync(p,JSON.stringify(j,null,2));}
await sharp('art/update013/logo-source.png').resize(256,256,{kernel:'nearest'}).png().toFile('src/main/resources/lastbreath.png');
let code=fs.readFileSync('work/fix-item-sides.cjs','utf8');code=code.slice(0,code.indexOf('const totem='))+"for(const name of ['knowledge_fragment','soul_reader'])fs.writeFileSync(base+'models/item/'+name+'.json',JSON.stringify(await sprite(name),null,2));})();";fs.writeFileSync('work/new-sides013.cjs',code);
let p='src/main/templates/META-INF/neoforge.mods.toml',s=fs.readFileSync(p,'utf8').replace('#logoFile="lastbreath.png"','logoFile="lastbreath.png"');fs.writeFileSync(p,s);
})();
