package me.vlad.lastbreath;

/** Exports the actual baked armor quads for an offline visual check. Test source only. */
final class WandererDetailExport {
    static void export() {
        try {
            var directory=java.nio.file.Path.of("../art/wanderer-detail");java.nio.file.Files.createDirectories(directory);
            for(int frame=0;frame<9;frame++){
                var quads=new com.google.gson.JsonArray();
                for(int kind=2;kind<=4;kind++){
                    var model=new me.vlad.lastbreath.client.WandererDetailModel.CloakModel(kind);model.charged=true;
                    var state=new net.minecraft.client.renderer.entity.state.HumanoidRenderState();
                    state.ageInTicks=frame*5;state.walkAnimationSpeed=frame==0?0:.6f;state.walkAnimationPos=frame*1.18f;
                    model.setupAnim(state);
                    for(var part:new net.minecraft.client.model.geom.ModelPart[]{model.body,model.leftArm,model.rightArm,model.leftLeg,model.rightLeg}){
                        if(!part.visible)continue;
                        part.visit(new com.mojang.blaze3d.vertex.PoseStack(),(pose,path,index,cube)->{
                            for(var polygon:cube.polygons){
                                var q=new com.google.gson.JsonArray();
                                for(var vertex:polygon.vertices()){
                                    var v=pose.pose().transformPosition(vertex.x()/16,vertex.y()/16,vertex.z()/16,new org.joml.Vector3f());
                                    if(!Float.isFinite(v.x+v.y+v.z))throw new IllegalStateException("Non-finite armor vertex");
                                    var a=new com.google.gson.JsonArray();a.add(v.x*16);a.add(24-v.y*16);a.add(-v.z*16);a.add(vertex.u()*16);a.add(vertex.v()*16);q.add(a);
                                }quads.add(q);
                            }
                        });
                    }
                }
                java.nio.file.Files.writeString(directory.resolve("pose-"+frame+".json"),quads.toString(),java.nio.charset.StandardCharsets.UTF_8);
            }
        }catch(java.io.IOException e){throw new RuntimeException(e);}
    }
}
