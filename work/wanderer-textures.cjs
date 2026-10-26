const fs=require('fs'),sharp=require('C:/Users/Vlad/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp');
(async()=>{
const base='C:/Users/Vlad/.codex/generated_images/01a14e41-fd25-7831-a996-501ab25ed225/';
fs.mkdirSync('art/wanderer',{recursive:true});
fs.copyFileSync(base+'exec-c9aba9bf-956b-4267-ae51-c7438fbee04d.png','art/wanderer/material-source.png');
fs.copyFileSync(base+'exec-6b4cfc23-bdde-4ca0-93e7-3813c8d000ed.png','art/wanderer/icon-source.png');
fs.copyFileSync(base+'exec-909fb4a2-91d9-48ce-b9ce-2c8f707fb4c6.png','art/wanderer/approved-concept.png');
const atlas=await sharp('art/wanderer/material-source.png').resize(128,128,{kernel:'nearest'}).png().toBuffer();
const gem=await sharp('src/main/resources/assets/lastbreath/textures/item/return_heart.png').extract({left:22,top:22,width:4,height:4}).resize(8,8,{kernel:'nearest'}).png().toBuffer();
const final=await sharp(atlas).composite([{input:gem,left:120,top:120}]).png().toBuffer();
for(const layer of ['humanoid','humanoid_leggings']){fs.mkdirSync('src/main/resources/assets/lastbreath/textures/entity/equipment/'+layer,{recursive:true});fs.writeFileSync('src/main/resources/assets/lastbreath/textures/entity/equipment/'+layer+'/soul_cloak.png',final);}
fs.writeFileSync('art/wanderer/armor-atlas.png',final);
const meta=await sharp('art/wanderer/icon-source.png').metadata();const cell=meta.width/3;
for(const [i,name] of ['soul_cloak','soul_trousers','soul_boots'].entries())await sharp('art/wanderer/icon-source.png').extract({left:Math.round(i*cell),top:0,width:Math.floor(cell),height:meta.height}).resize(32,32,{kernel:'nearest',fit:'contain'}).png().toFile('src/main/resources/assets/lastbreath/textures/item/'+name+'.png');
})();
