package me.vlad.lastbreath;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
@EventBusSubscriber(modid=LastBreath.MODID)
public final class TalentNetwork {
 public record Select(int id) implements CustomPacketPayload {
  public static final Type<Select> TYPE=new Type<>(Identifier.fromNamespaceAndPath("lastbreath","select_talent"));
  public static final StreamCodec<RegistryFriendlyByteBuf,Select> CODEC=StreamCodec.composite(ByteBufCodecs.VAR_INT,Select::id,Select::new);
  @Override public Type<Select> type(){return TYPE;}
 }
 @SubscribeEvent public static void register(RegisterPayloadHandlersEvent e){e.registrar("2").playToServer(Select.TYPE,Select.CODEC,(packet,context)->{if(context.player() instanceof net.minecraft.server.level.ServerPlayer p)TalentService.select(p,packet.id());});}
}
