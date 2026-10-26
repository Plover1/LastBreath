const fs=require('fs'),sharp=require('C:/Users/Vlad/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp');
(async()=>{const source='art/update012/swords-source.png';const m=await sharp(source).metadata();fs.mkdirSync('art/swords128',{recursive:true});for(const[i,n]of['executioner_sword','soul_devourer'].entries()){
const dest='src/main/resources/assets/lastbreath/textures/item/'+n+'.png';fs.copyFileSync(dest,'art/swords128/'+n+'-before.png');
await sharp(source).extract({left:i*(m.width/2),top:0,width:m.width/2,height:m.height}).resize(128,128,{fit:'contain',kernel:'nearest'}).png().toFile(dest);
await sharp(dest).resize(512,512,{kernel:'nearest'}).png().toFile('art/swords128/'+n+'-actual-texture.png');
console.log(n,await sharp(dest).metadata());}})();
