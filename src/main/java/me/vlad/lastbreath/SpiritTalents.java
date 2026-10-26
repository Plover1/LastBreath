package me.vlad.lastbreath;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Independent, versioned progression. SoulProgress remains intact for legacy saves and balances. */
public record SpiritTalents(int learned,int knowledge,int ancient,int equipped,int selected,long cooldown,boolean migrated) {
    public static final int COUNT=28;
    public static final int MASK=(1<<COUNT)-1;
    public static final SpiritTalents EMPTY=new SpiritTalents(0,0,0,0,-1,0,false);
    public static final Codec<SpiritTalents> CODEC=RecordCodecBuilder.create(i->i.group(
        Codec.intRange(0,MASK).optionalFieldOf("learned",0).forGetter(SpiritTalents::learned),
        Codec.intRange(0,15).optionalFieldOf("knowledge",0).forGetter(SpiritTalents::knowledge),
        Codec.intRange(0,15).optionalFieldOf("ancient",0).forGetter(SpiritTalents::ancient),
        Codec.intRange(0,MASK).optionalFieldOf("equipped",0).forGetter(SpiritTalents::equipped),
        Codec.intRange(-1,COUNT-1).optionalFieldOf("selected",-1).forGetter(SpiritTalents::selected),
        Codec.LONG.optionalFieldOf("cooldown",0L).forGetter(SpiritTalents::cooldown),
        Codec.BOOL.optionalFieldOf("migrated",false).forGetter(SpiritTalents::migrated)
    ).apply(i,SpiritTalents::new));
    public boolean has(int id){return id>=0&&id<COUNT&&(learned&(1<<id))!=0;}
    public boolean worn(int id){return has(id)&&(equipped&(1<<id))!=0;}
    public static boolean gold(int id){return id==27||(id>=0&&id<20&&id%5==4);}
    public static boolean active(int id){return id>=20?id<24||id==27:id>=0&&(id%5==0||id%5==2||gold(id));}
    public boolean unlocked(int id){
        if(id<0||id>=COUNT)return false;
        if(id>=20){if(id<24)return (knowledge&(1<<(id-20)))!=0;if(id<27)return has(id-4);return (ancient&4)!=0&&has(22)&&has(26);}
        int branch=id/5,step=id%5;
        if(step==0)return true;
        if((knowledge&(1<<branch))==0)return false;
        if(step==1||step==2)return has(branch*5);
        if(step==3)return has(branch*5+2);
        return (ancient&(1<<branch))!=0&&has(branch*5+2)&&has(branch*5+3);
    }
    public SpiritTalents learn(int id){return new SpiritTalents(learned|1<<id,knowledge,ancient,equipped,selected,cooldown,true);}
    public SpiritTalents knowledge(int branch,boolean rare){return new SpiritTalents(learned,knowledge|1<<branch,ancient|(rare?1<<branch:0),equipped,selected,cooldown,true);}
    public SpiritTalents select(int id){return worn(id)&&active(id)?new SpiritTalents(learned,knowledge,ancient,equipped,id,cooldown,true):this;}
    public SpiritTalents cooldown(long time){return new SpiritTalents(learned,knowledge,ancient,equipped,selected,time,true);}
    public SpiritTalents toggle(int id){
        if(!has(id)||!active(id))return this;
        int next=equipped^(1<<id),ordinary=0,special=0;
        for(int n=0;n<COUNT;n++)if((next&(1<<n))!=0){if(gold(n))special++;else ordinary++;}
        if(ordinary>3||special>1)return this;
        int choice=selected;if(choice==id&&(next&(1<<id))==0)choice=-1;
        if(choice<0&&(next&(1<<id))!=0)choice=id;
        return new SpiritTalents(learned,knowledge,ancient,next,choice,cooldown,true);
    }
    public static SpiritTalents migrate(SoulProgress old){
        int learned=0,knowledge=0,ancient=0;
        // Existing ranks and balances stay in SoulProgress; these are equivalent new unlocks.
        int[] mapping={16,8,13,2,5,11,4,9,14};
        for(int id=0;id<9;id++)if(old.rank(id)>0){int n=mapping[id],branch=n/5;learned|=1<<n|1<<(branch*5);knowledge|=1<<branch;if(gold(n))ancient|=1<<branch;}
        var result=new SpiritTalents(learned,knowledge,ancient,0,-1,old.cooldown(),true);
        for(int n=0;n<COUNT;n++)if(result.has(n)&&active(n))result=result.toggle(n);
        return result;
    }
}
