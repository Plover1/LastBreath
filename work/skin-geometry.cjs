const fs=require('fs');fs.mkdirSync('art/skin',{recursive:true});const quads=[];
function box(x,y,z,w,h,d,u,v,grow=0){const a=[x-grow,y-grow,z-grow],b=[x+w+grow,y+h+grow,z+d+grow];
const faces={south:[[a[0],b[1],b[2]],[b[0],b[1],b[2]],[b[0],a[1],b[2]],[a[0],a[1],b[2]]],north:[[b[0],b[1],a[2]],[a[0],b[1],a[2]],[a[0],a[1],a[2]],[b[0],a[1],a[2]]],east:[[b[0],b[1],b[2]],[b[0],b[1],a[2]],[b[0],a[1],a[2]],[b[0],a[1],b[2]]],west:[[a[0],b[1],a[2]],[a[0],b[1],b[2]],[a[0],a[1],b[2]],[a[0],a[1],a[2]]],up:[[a[0],b[1],a[2]],[b[0],b[1],a[2]],[b[0],b[1],b[2]],[a[0],b[1],b[2]]],down:[[a[0],a[1],b[2]],[b[0],a[1],b[2]],[b[0],a[1],a[2]],[a[0],a[1],a[2]]]};
const uv={south:[u+d,v+d,w,h],north:[u+d+w+d,v+d,w,h],east:[u+d+w,v+d,d,h],west:[u,v+d,d,h],up:[u+d,v,w,d],down:[u+d+w,v,w,d]};
for(const f in faces){const [tx,ty,tw,th]=uv[f],p=[[tx,ty],[tx+tw,ty],[tx+tw,ty+th],[tx,ty+th]];quads.push(faces[f].map((q,i)=>[...q,p[i][0]/4,p[i][1]/4]));}}
box(-4,24,-4,8,8,8,0,0);box(-4,24,-4,8,8,8,32,0,.5);
box(-4,12,-2,8,12,4,16,16);box(-4,12,-2,8,12,4,16,32,.25);
box(-8,12,-2,4,12,4,40,16);box(-8,12,-2,4,12,4,40,32,.25);
box(4,12,-2,4,12,4,32,48);box(4,12,-2,4,12,4,48,48,.25);
box(-4,0,-2,4,12,4,0,16);box(-4,0,-2,4,12,4,0,32,.25);
box(0,0,-2,4,12,4,16,48);box(0,0,-2,4,12,4,0,48,.25);
fs.writeFileSync('art/skin/quads.json',JSON.stringify(quads));
