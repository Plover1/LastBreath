package me.vlad.lastbreath;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
/** Invoked only through a validated ritual-table menu. One scroll per explicit click. */
public final class KnowledgeScrolls {
 public static boolean study(ServerPlayer p){
  if(!(p.containerMenu instanceof SoulTreeMenu menu)||!menu.stillValid(p))return false;
  var inventory=p.getInventory();
  for(int slot=0;slot<inventory.getContainerSize();slot++){
   var stack=inventory.getItem(slot);
   for(int i=0;i<8;i++)if(stack.is(LastBreath.KNOWLEDGE_SCROLLS.get(i).get())){
    var old=TalentService.get(p);int branch=i%4;boolean ancient=i>=4;
    boolean duplicate=((ancient?old.ancient():old.knowledge())&(1<<branch))!=0;
    stack.shrink(1);
    if(duplicate){var fragments=new ItemStack(LastBreath.KNOWLEDGE_FRAGMENT.get(),ancient?3:1);if(!inventory.add(fragments))p.level().addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(p.level(),p.getX(),p.getY(),p.getZ(),fragments));}
    else p.setData(LastBreath.TALENTS,old.knowledge(branch,ancient));
    p.sendOverlayMessage(Component.translatable(duplicate?"lastbreath.scroll.duplicate":"lastbreath.scroll.learned"));return true;
   }
  }
  p.sendOverlayMessage(Component.translatable("lastbreath.scroll.missing"));return false;
 }
}
