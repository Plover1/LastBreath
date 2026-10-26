from pathlib import Path
r=Path.cwd();src=r/'src/main/java/me/vlad/lastbreath/client/SoulCloakRendering.java';s=src.read_text(encoding='utf-8')
a=s.index('@EventBusSubscriber');b=s.index('    public static final class CloakModel',a)
s=s[:a]+'public final class WandererDetailModel {\n'+s[b:]
s=s.replace('texOffs(u,v).addBox(x,y,z,w,h,d,new CubeDeformation(inflate))','texOffs(u/2,v/2).addBox(x,y,z,w,h,d,new CubeDeformation(inflate),.5f,.5f)')
s=s.replace('        private static ModelPart mesh(int kind){','''        private static CubeListBuilder tail(float x,float length,boolean front){
            return box(0,0,x,0,-.45f,3.8f,5,.9f,0)
                .texOffs(8,2).addBox(x-.35f,5,-.45f,4.5f,length-5,.9f,CubeDeformation.NONE,.5f,.5f)
                .texOffs(0,32).addBox(x+.3f,.4f,front?.47f:-.58f,3.1f,length-.8f,.12f,CubeDeformation.NONE,.5f,.5f)
                .texOffs(32,32).addBox(x+.05f,.4f,front?-.57f:.47f,.18f,length-1,.12f,CubeDeformation.NONE,.5f,.5f)
                .texOffs(32,0).addBox(x-.35f,length-.25f,-.5f,4.5f,.25f,1,CubeDeformation.NONE,.5f,.5f);
        }
        private static ModelPart mesh(int kind){''')
s=s.replace('box(0,0,sign<0?-4.4f:.4f,0,-.5f,4,10,1,0)','tail(sign<0?-4.2f:.4f,sign<0?9.7f:10.5f,true)')
s=s.replace('box(0,0,-4.2f,0,0,4,10,1,0)','tail(-4.2f,11.2f,false)').replace('box(0,0,.2f,0,0,4,10,1,0)','tail(.4f,10.7f,false)')
s=s.replace('                body.addOrReplaceChild("collar_back",','''                for(int sign:new int[]{-1,1}){
                    body.addOrReplaceChild("pocket_"+sign,box(0,0,sign<0?-3.8f:1.8f,7,-3.05f,2,2,.3f,0),PartPose.ZERO);
                    body.addOrReplaceChild("pocket_flap_"+sign,box(64,0,sign<0?-3.8f:1.8f,7,-3.3f,2,.3f,.4f,0),PartPose.ZERO);
                    for(int n=0;n<3;n++)body.addOrReplaceChild("stitch_"+sign+"_"+n,box(64,64,sign<0?-3.5f:2.1f+n*.45f,7.15f,-3.55f,.18f,.1f,.1f,0),PartPose.ZERO);
                    body.addOrReplaceChild("back_seam_"+sign,box(64,0,sign*2.6f,1,3.15f,.12f,9,.08f,0),PartPose.ZERO);
                    for(int n=0;n<3;n++)body.addOrReplaceChild("vest_button_"+sign+"_"+n,box(64,64,sign*.65f,4+n*1.6f,-3,.25f,.3f,.2f,0),PartPose.ZERO);
                }
                body.addOrReplaceChild("shoulder_yoke",box(0,0,-4,0,3.15f,8,2,.35f,0),PartPose.ZERO);
                body.addOrReplaceChild("collar_back",''')
s=s.replace('                body.addOrReplaceChild("buckle",box(64,64,-1,10,-4,2,1.5f,.5f,0),PartPose.ZERO);','''                body.addOrReplaceChild("buckle",box(64,64,-1,10,-4,2,1.5f,.5f,0),PartPose.ZERO);
                body.addOrReplaceChild("buckle_inset",box(64,0,-.65f,10.3f,-4.1f,1.3f,.9f,.1f,0),PartPose.ZERO);
                body.addOrReplaceChild("buckle_pin",box(64,64,-.1f,10.3f,-4.2f,.2f,.9f,.1f,0),PartPose.ZERO);
                for(int i=0;i<4;i++)body.addOrReplaceChild("belt_hole_"+i,box(0,64,-3.8f+i*.6f,10.6f,-3.65f,.18f,.2f,.1f,0),PartPose.ZERO);''')
s=s.replace('                    arm.addOrReplaceChild("shoulder",','''                    arm.addOrReplaceChild("elbow_fold",box(0,0,x,4,-2.4f,4,.6f,.3f,0),PartPose.ZERO);
                    arm.addOrReplaceChild("cuff_strap",box(0,64,x-.1f,7,-2.45f,4.2f,.65f,.2f,0),PartPose.ZERO);
                    arm.addOrReplaceChild("cuff_buckle",box(64,64,x+1.2f,6.9f,-2.7f,1.4f,.85f,.25f,0),PartPose.ZERO);
                    arm.addOrReplaceChild("shoulder",''')
s=s.replace('                    leg.addOrReplaceChild("clasp",','''                    for(int i=0;i<3;i++){
                        leg.addOrReplaceChild("lace_"+i,box(0,64,-.9f,7+i*.8f,-2.65f,1.8f,.2f,.12f,0),PartPose.ZERO);
                    }
                    leg.addOrReplaceChild("heel",box(0,64,-2,11,1.5f,4,1,1,.3f),PartPose.ZERO);
                    leg.addOrReplaceChild("toe_seam",box(0,64,-1.8f,10.3f,-3.8f,3.6f,.15f,.1f,0),PartPose.ZERO);
                    leg.addOrReplaceChild("clasp",''')
s=s.replace('            if(kind!=2)return;','''            if(kind==3){
                float sway=Config.value(Config.ANIMATIONS)?(float)Math.sin(state.walkAnimationPos*.65f)*Math.min(.12f,state.walkAnimationSpeed*.1f):0;
                for(String name:new String[]{"satchel","satchel_flap","satchel_clasp"})body.getChild(name).zRot=sway;
            }
            if(kind!=2)return;''')
s=s.replace('sign*(.04f+movement*.12f)','sign*(.095f+movement*.12f)')
out=r/'src/gameTest/java/me/vlad/lastbreath/client/WandererDetailModel.java';out.parent.mkdir(parents=True,exist_ok=True);out.write_text(s,encoding='utf-8')
art=r/'art/wanderer-detail';art.mkdir(exist_ok=True);(art/'WandererDetailModel.java').write_text(s,encoding='utf-8')
import shutil
shutil.copy2(r/'art/wanderer/armor-atlas.png',art/'armor-atlas.png')
p=r/'src/gameTest/java/me/vlad/lastbreath/WandererPreview.java';s=p.read_text(encoding='utf-8').replace('class WandererPreview','class WandererDetailExport').replace('../art/wanderer','../art/wanderer-detail').replace('SoulCloakRendering.CloakModel','WandererDetailModel.CloakModel');(p.parent/'WandererDetailExport.java').write_text(s,encoding='utf-8')
p=r/'src/gameTest/java/me/vlad/lastbreath/ClientStartupTest.java';s=p.read_text(encoding='utf-8').replace('WandererPreview.export();','WandererPreview.export();\n            WandererDetailExport.export();');p.write_text(s,encoding='utf-8')
s=(r/'work/render-wanderer.cjs').read_text(encoding='utf-8').replace('art/wanderer/','art/wanderer-detail/').replace('SOUL WANDERER</text>','SOUL WANDERER · DETAIL DRAFT</text>');(r/'work/render-wanderer-detail.cjs').write_text(s,encoding='utf-8')
print('Prepared isolated detailed model; production sources and textures unchanged.')
