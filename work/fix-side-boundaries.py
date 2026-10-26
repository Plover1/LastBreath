from pathlib import Path
p=Path('work/fix-item-sides.cjs');s=p.read_text(encoding='utf-8').replace("for(const side of ['west','east','up','down'])faces[side]={texture:'#sprite',uv};","for(const side of ['west','east'])faces[side]={texture:'#sprite',uv};\nfor(const [side,dy] of [['up',-1],['down',1]]){let k=start;while(k<end){if(alpha(k,y+dy)>=16){k++;continue;}const lo=k;while(k<end&&alpha(k,y+dy)<16)k++;e.push({from:[lo*16/w,16-(y+1)*16/h,7.5],to:[k*16/w,16-y*16/h,8],faces:{[side]:{texture:'#sprite',uv}}});}}")
p.write_text(s,encoding='utf-8')
