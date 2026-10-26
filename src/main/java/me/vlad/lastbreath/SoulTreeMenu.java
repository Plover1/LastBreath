package me.vlad.lastbreath;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public final class SoulTreeMenu extends AbstractContainerMenu {
    private final Player owner;
    private final ContainerLevelAccess access;
    private final SimpleContainerData values=new SimpleContainerData(24);
    private int selected=-1;
    public SoulTreeMenu(int id,Inventory inventory) { this(id,inventory,ContainerLevelAccess.NULL); }
    public SoulTreeMenu(int id,Inventory inventory,ContainerLevelAccess access) {
        super(LastBreath.SOUL_TREE.get(),id);this.owner=inventory.player;this.access=access;
        addDataSlots(values);refresh();
    }
    private void refresh() {
        if (!(owner instanceof net.minecraft.server.level.ServerPlayer)) return;
        var t=TalentService.get(owner);values.set(18,t.learned()&65535);values.set(19,t.learned()>>>16);values.set(20,t.equipped()&65535);values.set(21,t.equipped()>>>16);values.set(22,t.knowledge());values.set(23,t.ancient());
        var p=owner.getData(LastBreath.PROGRESS);values.set(0,p.souls());
        for(int i=0;i<9;i++) values.set(i+1,p.rank(i));
        values.set(10,p.red());values.set(11,p.purple());values.set(12,p.trials());
        if(selected<0||SoulGear.type(owner.getInventory().getItem(selected))==0)selectNext();
        var stack=selected<0?ItemStack.EMPTY:owner.getInventory().getItem(selected);var d=SoulGear.data(stack);
        values.set(13,SoulGear.type(stack));values.set(14,d.level());values.set(15,d.branch());values.set(16,d.charge());values.set(17,selected+1);
    }
    private void selectNext(){int size=owner.getInventory().getContainerSize();for(int n=1;n<=size;n++){int slot=Math.floorMod(selected+n,size);if(SoulGear.type(owner.getInventory().getItem(slot))!=0){selected=slot;return;}}selected=-1;}
    public SpiritTalents talents(){return new SpiritTalents((values.get(18)&65535)|(values.get(19)<<16),values.get(22),values.get(23),(values.get(20)&65535)|(values.get(21)<<16),-1,0,true);}
    public int gear(int field){return values.get(13+field);}
    public SoulProgress progress() { return new SoulProgress(values.get(0),values.get(1),values.get(2),values.get(3),values.get(4),values.get(5),values.get(6),0,values.get(10),values.get(11),values.get(7),values.get(8),values.get(9),values.get(12),9); }
    @Override public void broadcastChanges() { refresh();super.broadcastChanges(); }
    @Override public boolean stillValid(Player player) {
        return player==owner && player.isAlive()
                && access.evaluate((level,pos)->RitualAccess.allowed(player,pos) && level.getBlockState(pos).is(LastBreath.RITUAL_TABLE.get()) && player.distanceToSqr(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)<=64,owner.level().isClientSide());
    }
    @Override public boolean clickMenuButton(Player player,int id) {
        if (player.level().isClientSide() || !stillValid(player)) return false;
        if(id==40&&player instanceof net.minecraft.server.level.ServerPlayer sp){boolean ok=KnowledgeScrolls.study(sp);broadcastChanges();return ok;}
        if(id==20){selectNext();broadcastChanges();return true;}
        if(id>=22&&id<=27){
            if(!(player instanceof net.minecraft.server.level.ServerPlayer sp)||selected<0)return false;
            boolean ok=SoulGear.purchase(sp,player.getInventory().getItem(selected),id);broadcastChanges();return ok;
        }
        if(id>=100&&id<100+SpiritTalents.COUNT&&player instanceof net.minecraft.server.level.ServerPlayer sp){
            int talent=id-100;if(!TalentService.enabled(talent))return false;var old=TalentService.get(sp);boolean ok;
            if(old.has(talent)){var next=old.toggle(talent);ok=!next.equals(old);if(ok)sp.setData(LastBreath.TALENTS,next);}
            else ok=TalentService.buy(sp,talent);
            broadcastChanges();return ok;
        }
        return false;
    }
    @Override public ItemStack quickMoveStack(Player player,int slot) { return ItemStack.EMPTY; }
}
