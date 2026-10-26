package me.vlad.lastbreath.client;

import me.vlad.lastbreath.LastBreath;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value=LastBreath.MODID, dist=Dist.CLIENT)
public final class LastBreathClient {
    public LastBreathClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class,(mc,parent)->new ConfigurationScreen(container,parent));
    }
}
