from pathlib import Path
p=Path('src/main/java/me/vlad/lastbreath/Config.java');s=p.read_text();pos='    public static final ModConfigSpec SPEC = B.build();'
fields='''    public static final ModConfigSpec.BooleanValue GEAR_ENABLED = B.define("enableSoulGearPowers", true);
    public static final ModConfigSpec.IntValue GEAR_CAPACITY = B.defineInRange("gearChargeCapacity",100,100,30000);
    public static final ModConfigSpec.IntValue GEAR_BLUE = B.defineInRange("gearBlueSoulCharge",10,1,100);
    public static final ModConfigSpec.IntValue GEAR_RED = B.defineInRange("gearRedSoulCharge",25,1,100);
    public static final ModConfigSpec.IntValue GEAR_PURPLE = B.defineInRange("gearPurpleSoulCharge",100,1,100);
    public static final ModConfigSpec.DoubleValue GEAR_COST = B.defineInRange("gearUpgradeCostMultiplier",1.0,.1,100);
    public static final ModConfigSpec.DoubleValue GEAR_HARVEST_CHANCE = B.defineInRange("gearHarvestExtraChance",.10,0,1);
    public static final ModConfigSpec.IntValue GEAR_HARVEST_COST = B.defineInRange("gearHarvestChargeCost",5,1,100);
    public static final ModConfigSpec.DoubleValue GEAR_HEAL = B.defineInRange("gearHealHealth",2.0,0,20);
    public static final ModConfigSpec.IntValue GEAR_HEAL_COST = B.defineInRange("gearHealChargeCost",10,1,100);
    public static final ModConfigSpec.IntValue GEAR_HEAL_COOLDOWN = B.defineInRange("gearHealCooldownSeconds",10,1,600);
    public static final ModConfigSpec.DoubleValue GEAR_GUARD_THRESHOLD = B.defineInRange("gearGuardMinimumDamage",4.0,1,100);
    public static final ModConfigSpec.DoubleValue GEAR_GUARD_REDUCTION = B.defineInRange("gearGuardDamageReduction",.40,0,.9);
    public static final ModConfigSpec.IntValue GEAR_GUARD_COST = B.defineInRange("gearGuardChargeCost",15,1,100);
    public static final ModConfigSpec.IntValue GEAR_CLOAK_COOLDOWN = B.defineInRange("gearCloakCooldownSeconds",30,1,600);
    public static final ModConfigSpec.DoubleValue GEAR_SHROUD_HEALTH = B.defineInRange("gearShroudHealthThreshold",6.0,1,20);
    public static final ModConfigSpec.IntValue GEAR_SHROUD_COST = B.defineInRange("gearShroudChargeCost",20,1,100);
    public static final ModConfigSpec.IntValue GEAR_SHROUD_SECONDS = B.defineInRange("gearShroudDecoySeconds",5,1,30);
'''
p.write_text(s.replace(pos,fields+pos))
p=Path('src/main/java/me/vlad/lastbreath/LastBreath.java');s=p.read_text();s=s.replace('    public static final DeferredItem<MobSoulItem> MOB_SOUL', '''    private static final DeferredRegister<net.minecraft.core.component.DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE,MODID);
    public static final Supplier<net.minecraft.core.component.DataComponentType<SoulGearData>> GEAR_DATA = COMPONENTS.register("soul_gear",()->net.minecraft.core.component.DataComponentType.<SoulGearData>builder().persistent(SoulGearData.CODEC).networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.fromCodec(SoulGearData.CODEC)).build());
    public static final DeferredItem<SoulGearItem> SOUL_DEVOURER = ITEMS.registerItem("soul_devourer",SoulGearItem::new,p->p.sword(net.minecraft.world.item.ToolMaterial.DIAMOND,3f,-2.4f));
    public static final DeferredItem<SoulGearItem> SOUL_CLOAK = ITEMS.registerItem("soul_cloak",SoulGearItem::new,p->p.humanoidArmor(new net.minecraft.world.item.equipment.ArmorMaterial(20,java.util.Map.of(net.minecraft.world.item.equipment.ArmorType.CHESTPLATE,4),15,net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER,0,0,net.minecraft.tags.ItemTags.REPAIRS_LEATHER_ARMOR,net.minecraft.resources.ResourceKey.create(net.minecraft.world.item.equipment.EquipmentAssets.ROOT_ID,net.minecraft.resources.Identifier.fromNamespaceAndPath(MODID,"soul_cloak"))),net.minecraft.world.item.equipment.ArmorType.CHESTPLATE));
    public static final DeferredItem<MobSoulItem> MOB_SOUL''');s=s.replace('ITEMS.register(bus);','COMPONENTS.register(bus); ITEMS.register(bus);');s=s.replace('output.accept(EXECUTIONER_SWORD.get());','output.accept(SOUL_DEVOURER.get()); output.accept(SOUL_CLOAK.get()); output.accept(EXECUTIONER_SWORD.get());');s=s.replace('NeoForge.EVENT_BUS.register(SoulHarvest.class);','NeoForge.EVENT_BUS.register(SoulGear.class); NeoForge.EVENT_BUS.register(SoulHarvest.class);');p.write_text(s)
p=Path('src/main/java/me/vlad/lastbreath/AdvancedSkills.java');s=p.read_text();loc=' private static boolean bond(ServerPlayer p,long now){';s=s.replace(loc,''' public static boolean spawnDecoy(ServerPlayer p,int seconds){
  var safe=SoulService.findSafe(p.level(),p.blockPosition(),p,2,2);if(safe==null)return false;
  var stand=new Decoy(p.level(),safe.getX()+.5,safe.getY(),safe.getZ()+.5);stand.setInvisible(true);stand.setPermanentlyInvulnerable(true);stand.addTag("lastbreath_echo");stand.setNoGravity(true);
  if(!p.level().addFreshEntity(stand))return false;
  ECHOES.add(new Echo(stand,p.level().getGameTime()+seconds*20L));return true;
 }
'''+loc);# cloak decoys should not depend on player Echo toggle
s=s.replace('a.isRemoved()||!Config.ECHO.get()||','a.isRemoved()||');p.write_text(s)
p=Path('src/main/java/me/vlad/lastbreath/SoulHarvest.java');s=p.read_text().replace('!weapon.is(LastBreath.EXECUTIONER_SWORD.get())','(!weapon.is(LastBreath.EXECUTIONER_SWORD.get())&&!weapon.is(LastBreath.SOUL_DEVOURER.get()))');s=s.replace('if(mob.getRandom().nextFloat()<Math.min(1,chance))','boolean normal=mob.getRandom().nextFloat()<Math.min(1,chance);\n  boolean extra=!normal&&source.is(net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK)&&source.getDirectEntity()==player&&SoulGear.extraSoul(player,type,mob.getRandom().nextFloat());\n  if(normal||extra)');p.write_text(s)
p=Path('src/main/java/me/vlad/lastbreath/SoulTreeMenu.java');s=p.read_text().replace('new SimpleContainerData(13)','new SimpleContainerData(18)');s=s.replace('    public SoulTreeMenu(int id,Inventory inventory)', '    private int selected=-1;\n    public SoulTreeMenu(int id,Inventory inventory)',1);s=s.replace('values.set(12,p.trials());','''values.set(12,p.trials());
        if(selected<0||SoulGear.type(owner.getInventory().getItem(selected))==0)selectNext();
        var stack=selected<0?ItemStack.EMPTY:owner.getInventory().getItem(selected);var d=SoulGear.data(stack);
        values.set(13,SoulGear.type(stack));values.set(14,d.level());values.set(15,d.branch());values.set(16,d.charge());values.set(17,selected+1);''');s=s.replace('    public SoulProgress progress()', '''    private void selectNext(){int size=owner.getInventory().getContainerSize();for(int n=1;n<=size;n++){int slot=Math.floorMod(selected+n,size);if(SoulGear.type(owner.getInventory().getItem(slot))!=0){selected=slot;return;}}selected=-1;}
    public int gear(int field){return values.get(13+field);}
    public SoulProgress progress()''');s=s.replace('if (player.level().isClientSide() || !stillValid(player) || id<0 || id>8) return false;', '''if (player.level().isClientSide() || !stillValid(player)) return false;
        if(id==20){selectNext();broadcastChanges();return true;}
        if(id>=22&&id<=27){
            if(!(player instanceof net.minecraft.server.level.ServerPlayer sp)||selected<0)return false;
            boolean ok=SoulGear.purchase(sp,player.getInventory().getItem(selected),id);broadcastChanges();return ok;
        }
        if(id<0||id>8)return false;''');p.write_text(s)
