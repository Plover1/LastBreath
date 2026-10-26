package me.vlad.lastbreath.client;

import me.vlad.lastbreath.LastBreath;
import me.vlad.lastbreath.SoulService;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.renderstate.AvatarRenderStateModifier;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

@EventBusSubscriber(modid = LastBreath.MODID, value = Dist.CLIENT)
public final class SpiritRendering {
    @SubscribeEvent
    public static void register(RegisterRenderStateModifiersEvent event) {
        event.registerAvatarEntityModifier(new AvatarRenderStateModifier() {
            @Override
            public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState state) {
                if (SoulService.ghost(avatar)) makeTranslucent(state);
                else if(state.chestEquipment.is(LastBreath.SOUL_CLOAK.get()))state.showCape=false;
            }
        });
    }

    public static void makeTranslucent(AvatarRenderState state) {
        // Vanilla's translucent entity path renders the entire skin at ~15% opacity, visible to every viewer.
        // These are renderer flags only: the real player remains a walking, collidable Adventure player.
        state.isInvisible = true;
        state.isInvisibleToPlayer = false;
        state.isSpectator = false;
        state.showCape = false;
        state.headEquipment = ItemStack.EMPTY;
        state.chestEquipment = ItemStack.EMPTY;
        state.legsEquipment = ItemStack.EMPTY;
        state.feetEquipment = ItemStack.EMPTY;
        state.rightHandItemState.clear();
        state.leftHandItemState.clear();
        state.rightHandItemStack = ItemStack.EMPTY;
        state.leftHandItemStack = ItemStack.EMPTY;
        state.heldOnHead.clear();
        state.arrowCount = 0;
        state.stingerCount = 0;
        state.parrotOnLeftShoulder = null;
        state.parrotOnRightShoulder = null;
    }
}
