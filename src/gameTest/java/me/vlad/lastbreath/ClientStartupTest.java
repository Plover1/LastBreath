package me.vlad.lastbreath;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

/** Launches the actual client, waits for renderer/resource initialization, then exits. Opt-in only. */
@EventBusSubscriber(modid = LastBreath.MODID, value = Dist.CLIENT)
public final class ClientStartupTest {
    private static boolean registered;
    private static boolean complete;
    private static int previewTicks;
    private static me.vlad.lastbreath.client.SoulTreeScreen preview;
    @SubscribeEvent
    public static void renderers(RegisterRenderStateModifiersEvent event) { registered = true; }
    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (!Boolean.getBoolean("lastbreath.clientSmoke") || complete) return;
        var client = Minecraft.getInstance();
        if (client.gui.screen() == null || client.gui.overlay() != null) return;
        if (!registered) throw new IllegalStateException("Render-state modifiers were not registered");
        if (previewTicks++ == 0) {
            WandererPreview.export();
            WandererDetailExport.export();
            var inventory = new net.minecraft.world.entity.player.Inventory(null, new net.minecraft.world.entity.EntityEquipment());
            var menu = new SoulTreeMenu(0, inventory);
            menu.setData(0, 42);
            menu.setData(1, 1);menu.setData(10,30);menu.setData(11,2);menu.setData(13,1);menu.setData(14,1);menu.setData(15,0);menu.setData(16,65);menu.setData(17,1);
            var cloakModel=new me.vlad.lastbreath.client.SoulCloakRendering.CloakModel();
            var state=new net.minecraft.client.renderer.entity.state.HumanoidRenderState();state.walkAnimationSpeed=.8f;state.ageInTicks=20;cloakModel.setupAnim(state);
            if(!Float.isFinite(cloakModel.body.getChild("cloak").xRot)||cloakModel.head.visible)throw new IllegalStateException("Invalid cloak model pose");
            var tree = new me.vlad.lastbreath.client.SoulTreeScreen(menu, inventory, net.minecraft.network.chat.Component.translatable("lastbreath.tree.title"));
            preview=tree;
            client.gui.setScreen(new net.minecraft.client.gui.screens.Screen(net.minecraft.network.chat.Component.literal("Tree render test")) {
                @Override protected void init() { tree.init(width, height);tree.showEquipment(true); }
                @Override public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int x, int y, float delta) {
                    graphics.fill(0, 0, width, height, 0xff151a20);
                    tree.extractRenderState(graphics, x, y, delta);
                }
            });
            return;
        }
        if(previewTicks==45)preview.showLiving();
        if(previewTicks==25)preview.showEquipment(false);
        if (previewTicks == 10||previewTicks==35||previewTicks==55) net.minecraft.client.Screenshot.grab(client, false);
        if (previewTicks < 70) return;
        complete = true;
        com.mojang.logging.LogUtils.getLogger().info("LASTBREATH: client resources and spirit renderer registration loaded successfully");
        client.stop();
    }
}
