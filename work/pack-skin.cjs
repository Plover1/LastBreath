const fs=require('fs'),sharp=require('C:/Users/Vlad/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp');
(async()=>{const original='C:/Users/Vlad/AppData/Local/Temp/codex-clipboard-ba44aa21-fba9-427f-92ae-7cb4de080846.png',generated='C:/Users/Vlad/.codex/generated_images/01a14e41-fd25-7831-a996-501ab25ed225/exec-85ecb5e0-624d-4931-b118-bb7c6ed6a82b.png';
fs.copyFileSync(original,'art/skin/original.png');fs.copyFileSync(generated,'art/skin/generated-atlas.png');
const skin=await sharp(generated).resize(64,64,{fit:'fill',kernel:'nearest'}).png().toBuffer();
const head=await sharp(original).extract({left:0,top:0,width:64,height:16}).png().toBuffer();
const body=await sharp(skin).extract({left:0,top:16,width:64,height:48}).png().toBuffer();
const hands=[];for(const [left,top]of [[40,30],[32,62]])hands.push({input:await sharp(original).extract({left,top,width:16,height:2}).png().toBuffer(),left,top});
await sharp({create:{width:64,height:64,channels:4,background:{r:0,g:0,b:0,alpha:0}}}).composite([{input:head,left:0,top:0},{input:body,left:0,top:16},...hands]).png().toFile('art/skin/LastBreath-Siris102.png');
const finalHead=await sharp('art/skin/LastBreath-Siris102.png').extract({left:0,top:0,width:64,height:16}).raw().toBuffer();const sourceHead=await sharp(original).extract({left:0,top:0,width:64,height:16}).raw().toBuffer();console.log('Head preserved pixel for pixel:',finalHead.equals(sourceHead));
})();

