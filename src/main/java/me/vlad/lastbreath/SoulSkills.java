package me.vlad.lastbreath;

import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;

public final class SoulSkills {
    private SoulSkills() {}
    public static void activate(ServerPlayer player) {
        if (!player.isAlive() || player.isSpectator()) return;
        var p=player.getData(LastBreath.PROGRESS);
        boolean ghost=SoulService.ghost(player);
        if ((ghost?p.step():p.sight())==0) { player.sendOverlayMessage(Component.translatable("lastbreath.skill.locked")); return; }
        long now=player.level().getServer().overworld().getGameTime();
        if (p.cooldown()>now) { player.sendOverlayMessage(Component.translatable("lastbreath.skill.cooldown",(p.cooldown()-now+19)/20)); return; }
        if (ghost) {
            AdvancedSkills.burst(player,0);
            Vec3 start=player.getEyePosition(),end=start.add(player.getLookAngle().scale(8));
            var hit=player.level().clip(new ClipContext(start,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.ANY,player));
            Vec3 destination=hit.getLocation().subtract(player.getLookAngle().scale(.7));
            BlockPos safe=null;
            // Search back along the unobstructed ray; never search beyond a wall.
            for (int i=0;i<8 && safe==null;i++) {
                Vec3 point=destination.lerp(start,i/8.0);
                safe=SoulService.findSafe(player.level(),BlockPos.containing(point.x,point.y-1,point.z),player,0,2);
            }
            if (safe==null || Vec3.atBottomCenterOf(safe).distanceToSqr(player.position())<2) { player.sendOverlayMessage(Component.translatable("lastbreath.unsafe")); return; }
            Vec3 target=Vec3.atBottomCenterOf(safe);
            if (target.distanceToSqr(player.position())>64) return;
            var path=player.level().clip(new ClipContext(start,target.add(0,1,0),ClipContext.Block.COLLIDER,ClipContext.Fluid.ANY,player));
            if (path.getType()!=net.minecraft.world.phys.HitResult.Type.MISS || !player.teleportTo(player.level(),target.x,target.y,target.z,Set.of(),player.getYRot(),player.getXRot(),true)) return;
        } else {
            for (var mob:player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class,player.getBoundingBox().inflate(12),mob -> mob instanceof net.minecraft.world.entity.monster.Enemy && mob.distanceToSqr(player)<=144)) {
                mob.addEffect(new MobEffectInstance(MobEffects.GLOWING,160,0));
                mob.addEffect(new MobEffectInstance(MobEffects.SLOWNESS,100,1));
            }
        }
        player.setData(LastBreath.PROGRESS,p.cooldown(now+(ghost?400:900)));
        AdvancedSkills.burst(player,0);
        player.sendOverlayMessage(Component.translatable(ghost?"lastbreath.skill.step_used":"lastbreath.skill.sight_used"));
        RitualFeedback.sound(player.level(),player.position(),net.minecraft.sounds.SoundEvents.BEACON_POWER_SELECT,.4f,1.1f);
    }
}
