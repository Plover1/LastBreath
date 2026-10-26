package me.vlad.lastbreath.client;

import me.vlad.lastbreath.Config;
import me.vlad.lastbreath.LastBreath;
import me.vlad.lastbreath.SoulGear;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.*;

public final class WandererDetailModel {
    public static final class CloakModel extends HumanoidModel<HumanoidRenderState> {
        private final int kind;
        public boolean charged;
        public CloakModel(){this(2);}
        public CloakModel(int kind){super(mesh(kind));this.kind=kind;}
        private static CubeListBuilder box(int u,int v,float x,float y,float z,float w,float h,float d,float inflate){
            return CubeListBuilder.create().texOffs(u/2,v/2).addBox(x,y,z,w,h,d,new CubeDeformation(inflate),.5f,.5f);
        }
        private static CubeListBuilder tail(float x,float length,boolean front){
            return box(0,0,x,0,-.45f,3.8f,5,.9f,0)
                .texOffs(8,2).addBox(x-.35f,5,-.45f,4.5f,length-5,.9f,CubeDeformation.NONE,.5f,.5f)
                .texOffs(0,32).addBox(x+.3f,.4f,front?.47f:-.58f,3.1f,length-.8f,.12f,CubeDeformation.NONE,.5f,.5f)
                .texOffs(32,32).addBox(x+.05f,.4f,front?-.57f:.47f,.18f,length-1,.12f,CubeDeformation.NONE,.5f,.5f)
                .texOffs(32,0).addBox(x-.35f,length-.25f,-.5f,4.5f,.25f,1,CubeDeformation.NONE,.5f,.5f);
        }
        private static ModelPart mesh(int kind){
            var mesh=HumanoidModel.createMesh(CubeDeformation.NONE,0);var root=mesh.getRoot();
            var body=root.addOrReplaceChild("body",CubeListBuilder.create(),PartPose.ZERO);
            if(kind==2){
                body.addOrReplaceChild("vest",box(0,64,-4,0,-2,8,11,4,.22f),PartPose.ZERO);
                body.addOrReplaceChild("back",box(0,0,-4,0,2,8,11,1,.12f),PartPose.ZERO);
                for(int sign:new int[]{-1,1}){
                    body.addOrReplaceChild("front_"+sign,box(0,0,sign<0?-4:1,0,-2.7f,3,11,1,.08f),PartPose.ZERO);
                    body.addOrReplaceChild("lapel_"+sign,box(12,0,-1,0,-.5f,2,5,1,.05f),PartPose.offsetAndRotation(sign*2.5f,.5f,-3.2f,0,0,sign*.22f));
                    body.addOrReplaceChild("collar_"+sign,box(0,0,-.5f,-2,-2,1,3,4,.1f),PartPose.offsetAndRotation(sign*3,0,0,0,0,sign*.12f));
                    body.addOrReplaceChild("trim_"+sign,box(64,64,sign<0?-1.2f:.7f,1,-2.94f,.5f,10,.2f,0),PartPose.ZERO);
                    body.addOrReplaceChild("front_tail_"+sign,tail(sign<0?-4.2f:.4f,sign<0?9.7f:10.5f,true),PartPose.offset(0,11,-2.7f));
                }
                for(int sign:new int[]{-1,1}){
                    body.addOrReplaceChild("pocket_"+sign,box(0,0,sign<0?-3.8f:1.8f,7,-3.05f,2,2,.3f,0),PartPose.ZERO);
                    body.addOrReplaceChild("pocket_flap_"+sign,box(64,0,sign<0?-3.8f:1.8f,7,-3.3f,2,.3f,.4f,0),PartPose.ZERO);
                    for(int n=0;n<3;n++)body.addOrReplaceChild("stitch_"+sign+"_"+n,box(64,64,(sign<0?-3.5f:2.1f)+n*.45f,7.15f,-3.55f,.18f,.1f,.1f,0),PartPose.ZERO);
                    body.addOrReplaceChild("back_seam_"+sign,box(64,0,sign*2.6f,1,3.15f,.12f,9,.08f,0),PartPose.ZERO);
                    for(int n=0;n<3;n++)body.addOrReplaceChild("vest_button_"+sign+"_"+n,box(64,64,sign*.65f,4+n*1.6f,-3,.25f,.3f,.2f,0),PartPose.ZERO);
                }
                body.addOrReplaceChild("shoulder_yoke",box(0,0,-4,0,3.15f,8,2,.35f,0),PartPose.ZERO);
                body.addOrReplaceChild("collar_back",box(0,0,-3,-2,1.8f,6,3,1,.1f),PartPose.ZERO);
                body.addOrReplaceChild("scarf",box(0,64,-2.8f,-.5f,-3,5.6f,3,1,.05f),PartPose.ZERO);
                body.addOrReplaceChild("gem",box(120,120,-.5f,-.5f,-.5f,1,1,1,0),PartPose.offset(0,1.6f,-3.7f));
                body.addOrReplaceChild("cloak",tail(-4.2f,11.2f,false),PartPose.offset(0,11,2.8f));
                body.addOrReplaceChild("cloak_right",tail(.4f,10.7f,false),PartPose.offset(0,11,2.8f));
                body.addOrReplaceChild("strap",box(64,0,-.65f,0,-.35f,1.3f,11,.7f,0),PartPose.offsetAndRotation(-3.5f,0,-4.1f,0,0,-.6f));
            }else if(kind==3){
                body.addOrReplaceChild("belt",box(64,0,-4.4f,9.5f,-3.5f,8.8f,2.5f,7,.1f),PartPose.ZERO);
                body.addOrReplaceChild("buckle",box(64,64,-1,10,-4,2,1.5f,.5f,0),PartPose.ZERO);
                body.addOrReplaceChild("buckle_inset",box(64,0,-.65f,10.3f,-4.1f,1.3f,.9f,.1f,0),PartPose.ZERO);
                body.addOrReplaceChild("buckle_pin",box(64,64,-.1f,10.3f,-4.2f,.2f,.9f,.1f,0),PartPose.ZERO);
                for(int i=0;i<4;i++)body.addOrReplaceChild("belt_hole_"+i,box(0,64,-3.8f+i*.6f,10.6f,-3.65f,.18f,.2f,.1f,0),PartPose.ZERO);
                body.addOrReplaceChild("satchel",box(64,0,4.3f,10,-3.5f,2.5f,4,3,.1f),PartPose.ZERO);
                body.addOrReplaceChild("satchel_flap",box(64,0,4.1f,9.8f,-3.8f,2.9f,1.5f,3.6f,0),PartPose.ZERO);
                body.addOrReplaceChild("satchel_clasp",box(64,64,5,11.1f,-4,1,1,.3f,0),PartPose.ZERO);
            }
            for(boolean left:new boolean[]{false,true}){
                String side=left?"left":"right";float sign=left?1:-1;
                var arm=root.addOrReplaceChild(side+"_arm",CubeListBuilder.create(),PartPose.offset(sign*5,2,0));
                if(kind==2){
                    float x=left?-1:-3;
                    arm.addOrReplaceChild("sleeve",box(0,0,x,-2,-2,4,8,4,.3f),PartPose.ZERO);
                    arm.addOrReplaceChild("cuff",box(64,0,x,6,-2,4,4,4,.35f),PartPose.ZERO);
                    arm.addOrReplaceChild("elbow_fold",box(0,0,x,4,-2.4f,4,.6f,.3f,0),PartPose.ZERO);
                    arm.addOrReplaceChild("cuff_strap",box(0,64,x-.1f,7,-2.45f,4.2f,.65f,.2f,0),PartPose.ZERO);
                    arm.addOrReplaceChild("cuff_buckle",box(64,64,x+1.2f,6.9f,-2.7f,1.4f,.85f,.25f,0),PartPose.ZERO);
                    arm.addOrReplaceChild("shoulder",box(0,0,x,-2,-2,4,2,4,.5f),PartPose.ZERO);
                }
                var leg=root.addOrReplaceChild(side+"_leg",CubeListBuilder.create(),PartPose.offset(sign*1.9f,12,0));
                if(kind==3)leg.addOrReplaceChild("trouser",box(0,64,-2,0,-2,4,12,4,.22f),PartPose.ZERO);
                if(kind==4){
                    leg.addOrReplaceChild("boot",box(64,0,-2,5,-2,4,7,4,.5f),PartPose.ZERO);
                    leg.addOrReplaceChild("toe",box(64,0,-2,10,-3.4f,4,2,1.4f,.3f),PartPose.ZERO);
                    leg.addOrReplaceChild("boot_cuff",box(64,0,-2,5,-2,4,1.5f,4,.65f),PartPose.ZERO);
                    leg.addOrReplaceChild("sole",box(0,64,-2,11.5f,-3.4f,4,.5f,5.4f,.35f),PartPose.ZERO);
                    for(int i=0;i<3;i++){
                        leg.addOrReplaceChild("lace_"+i,box(0,64,-.9f,7+i*.8f,-2.65f,1.8f,.2f,.12f,0),PartPose.ZERO);
                    }
                    leg.addOrReplaceChild("heel",box(0,64,-2,11,1.5f,4,1,1,.3f),PartPose.ZERO);
                    leg.addOrReplaceChild("toe_seam",box(0,64,-1.8f,10.3f,-3.8f,3.6f,.15f,.1f,0),PartPose.ZERO);
                    leg.addOrReplaceChild("clasp",box(64,64,sign*2.4f-.2f,6,-.5f,.4f,1,1,0),PartPose.ZERO);
                }
            }
            return LayerDefinition.create(mesh,128,128).bakeRoot();
        }
        @Override public void setupAnim(HumanoidRenderState state){
            super.setupAnim(state);head.visible=false;hat.visible=false;
            body.visible=kind!=4;leftArm.visible=rightArm.visible=kind==2;leftLeg.visible=rightLeg.visible=kind!=2;
            if(kind==3){
                float sway=Config.value(Config.ANIMATIONS)?(float)Math.sin(state.walkAnimationPos*.65f)*Math.min(.12f,state.walkAnimationSpeed*.1f):0;
                for(String name:new String[]{"satchel","satchel_flap","satchel_clasp"})body.getChild(name).zRot=sway;
            }
            if(kind!=2)return;
            boolean animate=Config.value(Config.ANIMATIONS);
            float movement=animate?Math.min(.4f,Math.abs(state.walkAnimationSpeed)*.35f):0;
            float sway=animate?(float)Math.sin(state.ageInTicks*.08f)*.025f:0;
            for(String name:new String[]{"cloak","cloak_right"}){
                var tail=body.getChild(name);tail.xRot=.12f+movement+sway;tail.zRot=name.equals("cloak")?sway:-sway;
            }
            for(int sign:new int[]{-1,1}){
                var tail=body.getChild("front_tail_"+sign);
                float leg=(sign<0?rightLeg:leftLeg).xRot;
                tail.xRot=-.1f-(animate?Math.max(0,-leg)*.55f:0);tail.zRot=sign*(.095f+movement*.12f)+sway;
            }
            var gem=body.getChild("gem");float pulse=animate&&charged?1+(float)Math.sin(state.ageInTicks*.12f)*.09f:1;
            gem.xScale=gem.yScale=gem.zScale=pulse;
        }
    }
}
