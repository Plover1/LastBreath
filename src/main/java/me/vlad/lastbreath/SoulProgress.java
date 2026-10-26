package me.vlad.lastbreath;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
public record SoulProgress(int souls, int harvest, int vitality, int sacrifice, int sight, int step, int ward, long cooldown, int red, int purple, int echo, int borrow, int bond, int trials, int selected) {
 public static final int CAP=30000;
 public static final SoulProgress EMPTY=new SoulProgress(0,0,0,0,0,0,0,0);
 public SoulProgress(int souls,int harvest,int vitality,int sacrifice,int sight,int step,int ward,long cooldown) { this(souls,harvest,vitality,sacrifice,sight,step,ward,cooldown,0,0,0,0,0,0,9); }
 public static final MapCodec<SoulProgress> CODEC=RecordCodecBuilder.mapCodec(i->i.group(Codec.intRange(0,30000).optionalFieldOf("souls",0).forGetter(SoulProgress::souls),
Codec.intRange(0,3).optionalFieldOf("harvest",0).forGetter(SoulProgress::harvest),
Codec.intRange(0,3).optionalFieldOf("vitality",0).forGetter(SoulProgress::vitality),
Codec.intRange(0,3).optionalFieldOf("sacrifice",0).forGetter(SoulProgress::sacrifice),
Codec.intRange(0,1).optionalFieldOf("sight",0).forGetter(SoulProgress::sight),
Codec.intRange(0,1).optionalFieldOf("step",0).forGetter(SoulProgress::step),
Codec.intRange(0,1).optionalFieldOf("ward",0).forGetter(SoulProgress::ward),
Codec.LONG.optionalFieldOf("cooldown",0L).forGetter(SoulProgress::cooldown),
Codec.intRange(0,30000).optionalFieldOf("red",0).forGetter(SoulProgress::red),
Codec.intRange(0,30000).optionalFieldOf("purple",0).forGetter(SoulProgress::purple),
Codec.intRange(0,1).optionalFieldOf("echo",0).forGetter(SoulProgress::echo),
Codec.intRange(0,1).optionalFieldOf("borrow",0).forGetter(SoulProgress::borrow),
Codec.intRange(0,1).optionalFieldOf("bond",0).forGetter(SoulProgress::bond),
Codec.intRange(0,3).optionalFieldOf("trials",0).forGetter(SoulProgress::trials),
Codec.intRange(0,9).optionalFieldOf("selected",9).forGetter(SoulProgress::selected)).apply(i,SoulProgress::new));
 public int rank(int id) { return switch(id) {case 0->harvest;case 1->vitality;case 2->sacrifice;case 3->sight;case 4->step;case 5->ward;case 6->echo;case 7->borrow;case 8->bond;default->0;}; }
 public static int max(int id) { return id<3?3:1; }
 public boolean enabled(int id) { return switch(id) {case 6->Config.value(Config.ECHO);case 7->Config.value(Config.BORROW);case 8->Config.value(Config.BOND);default->true;}; }
 public int cost(int id) { int base=switch(id) {case 0->new int[]{5,12,25}[Math.min(harvest,2)];case 1->new int[]{5,10,20}[Math.min(vitality,2)];case 2->new int[]{8,16,24}[Math.min(sacrifice,2)];case 3->12;case 4->15;case 5->25;case 6,7,8->30;default->0;};return (int)Math.ceil(base*Config.value(Config.COST_SCALE)); }
 public int redCost(int id) { return (int)Math.ceil((id<3?rank(id):id<6?3:10)*Config.value(Config.COST_SCALE)); }
 public int purpleCost(int id) { return id>=6?Math.max(1,(int)Math.ceil(Config.value(Config.COST_SCALE))):0; }
 public boolean unlocked(int id) { boolean base=switch(id) {case 0,1,2->true;case 3->harvest>=1;case 4->vitality>=1;case 5->sacrifice>=2;case 6->sight>0;case 7->step>0;case 8->ward>0;default->false;};return enabled(id)&&base&&(id<6||!Config.value(Config.TRIALS)||((trials&1)!=0&&(id!=8||(trials&2)!=0))); }
 public boolean affordable(int id) { return souls>=cost(id)&&red>=redCost(id)&&purple>=purpleCost(id); }
 public SoulProgress buy(int id) {
  if(id<0||id>8||rank(id)>=max(id)||!unlocked(id)||!affordable(id))return this;
  int[] r={harvest,vitality,sacrifice,sight,step,ward,echo,borrow,bond};r[id]++;
  return new SoulProgress(souls-cost(id),r[0],r[1],r[2],r[3],r[4],r[5],cooldown,red-redCost(id),purple-purpleCost(id),r[6],r[7],r[8],trials,selected);
 }
 public int balance(int type){return type==0?souls:type==1?red:purple;}
 public SoulProgress balances(int blue,int red,int purple){return new SoulProgress(blue,harvest,vitality,sacrifice,sight,step,ward,cooldown,red,purple,echo,borrow,bond,trials,selected);}
 public SoulProgress absorb(){return absorb(0);}
 public SoulProgress absorb(int type){return balances(Math.min(CAP,souls+(type==0?1:0)),Math.min(CAP,red+(type==1?1:0)),Math.min(CAP,purple+(type==2?1:0)));}
 public SoulProgress cooldown(long time){return new SoulProgress(souls,harvest,vitality,sacrifice,sight,step,ward,time,red,purple,echo,borrow,bond,trials,selected);}
 public SoulProgress trial(int bit){return new SoulProgress(souls,harvest,vitality,sacrifice,sight,step,ward,cooldown,red,purple,echo,borrow,bond,trials|bit,selected);}
 public SoulProgress select(int id){return new SoulProgress(souls,harvest,vitality,sacrifice,sight,step,ward,cooldown,red,purple,echo,borrow,bond,trials,id);}
}
