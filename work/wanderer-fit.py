from pathlib import Path
p=Path('src/main/java/me/vlad/lastbreath/client/SoulCloakRendering.java');s=p.read_text(encoding='utf-8')
s=s.replace('PartPose.offsetAndRotation(-3.5f,0,-3.4f','PartPose.offsetAndRotation(-3.5f,0,-4.1f')
s=s.replace('box(64,0,-4,9.5f,-2,8,2.5f,4,.45f)','box(64,0,-4.4f,9.5f,-3.5f,8.8f,2.5f,7,.1f)')
s=s.replace('box(64,64,-1,10,-2.8f,2,1.5f,.5f,0)','box(64,64,-1,10,-4,2,1.5f,.5f,0)')
s=s.replace('box(64,0,4.3f,10,-1.5f,2.5f,4,3,.1f)','box(64,0,4.3f,10,-3.5f,2.5f,4,3,.1f)')
s=s.replace('box(64,0,4.1f,9.8f,-1.8f,2.9f,1.5f,3.6f,0)','box(64,0,4.1f,9.8f,-3.8f,2.9f,1.5f,3.6f,0)')
s=s.replace('box(64,64,5,11.1f,-2,1,1,.3f,0)','box(64,64,5,11.1f,-4,1,1,.3f,0)')
p.write_text(s,encoding='utf-8')
