from pathlib import Path
p=Path('src/main/java/me/vlad/lastbreath/LastBreath.java');s=p.read_text(encoding='utf-8');line=next(x for x in s.splitlines() if 'public static final DeferredItem<SoulGearItem> SOUL_CLOAK' in x)
extra='\n'.join([line.replace('SOUL_CLOAK','SOUL_TROUSERS').replace('"soul_cloak"','"soul_trousers"').replace('CHESTPLATE,4','LEGGINGS,3').replace('ArmorType.CHESTPLATE','ArmorType.LEGGINGS'),line.replace('SOUL_CLOAK','SOUL_BOOTS').replace('"soul_cloak"','"soul_boots"').replace('CHESTPLATE,4','BOOTS,2').replace('ArmorType.CHESTPLATE','ArmorType.BOOTS')])
s=s.replace(line,line+'\n'+extra).replace('output.accept(SOUL_CLOAK.get());','output.accept(SOUL_CLOAK.get()); output.accept(SOUL_TROUSERS.get()); output.accept(SOUL_BOOTS.get());');p.write_text(s,encoding='utf-8')
p=Path('src/main/java/me/vlad/lastbreath/SoulGear.java');s=p.read_text(encoding='utf-8').replace('stack.is(LastBreath.SOUL_CLOAK.get())?2:0','stack.is(LastBreath.SOUL_CLOAK.get())?2:stack.is(LastBreath.SOUL_TROUSERS.get())?3:stack.is(LastBreath.SOUL_BOOTS.get())?4:0')
s=s.replace('public static SoulGearData data(', 'public static String itemName(int type){return switch(type){case 1->"soul_devourer";case 3->"soul_trousers";case 4->"soul_boots";default->"soul_cloak";};}\n    public static SoulGearData data(')
s=s.replace('"lastbreath.gear.branch."+type','"lastbreath.gear.branch."+(type>2?2:type)')
s=s.replace('String key="lastbreath.gear.details."+type','type=type>2?2:type;\n        String key="lastbreath.gear.details."+type')
s=s.replace('new AttributeModifier(UPGRADE,data.level()', 'new AttributeModifier(type(stack)>2?Identifier.fromNamespaceAndPath("lastbreath","gear_upgrade_"+type(stack)):UPGRADE,data.level()')
s=s.replace('sword?EquipmentSlotGroup.MAINHAND:EquipmentSlotGroup.CHEST','sword?EquipmentSlotGroup.MAINHAND:type(stack)==3?EquipmentSlotGroup.LEGS:type(stack)==4?EquipmentSlotGroup.FEET:EquipmentSlotGroup.CHEST')
s=s.replace('    @SubscribeEvent(priority=EventPriority.LOW)\n', '''    private static ItemStack defensiveStack(ServerPlayer p,int branch,int cost){
        for(var slot:new EquipmentSlot[]{EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}){
            var stack=p.getItemBySlot(slot);
            if(type(stack)>=2&&ready(p,stack,branch,cost))return stack;
        }
        return ItemStack.EMPTY;
    }
    // A set gets one defensive activation, not three effects on the same hit.
    private static void consumeArmor(ServerPlayer p,ItemStack selected,int cost){
        consume(p,selected,cost,Config.GEAR_CLOAK_COOLDOWN.get());
        long until=data(selected).readyAt();
        for(var slot:new EquipmentSlot[]{EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}){
            var other=p.getItemBySlot(slot);if(other==selected||type(other)<2)continue;
            var d=data(other);write(other,new SoulGearData(d.level(),d.branch(),d.charge(),Math.max(d.readyAt(),until)));
        }
    }
    @SubscribeEvent(priority=EventPriority.LOW)
''')
s=s.replace('var s=p.getItemBySlot(EquipmentSlot.CHEST);int cost=Config.GEAR_GUARD_COST.get();','int cost=Config.GEAR_GUARD_COST.get();var s=defensiveStack(p,1,cost);').replace('s.is(LastBreath.SOUL_CLOAK.get())&&Config.GEAR_GUARD','!s.isEmpty()&&Config.GEAR_GUARD')
s=s.replace('var s=p.getItemBySlot(EquipmentSlot.CHEST);int cost=Config.GEAR_SHROUD_COST.get();','int cost=Config.GEAR_SHROUD_COST.get();var s=defensiveStack(p,2,cost);').replace('s.is(LastBreath.SOUL_CLOAK.get())&&ready(p,s,2,cost)','!s.isEmpty()&&ready(p,s,2,cost)').replace('consume(p,s,cost,Config.GEAR_CLOAK_COOLDOWN.get())','consumeArmor(p,s,cost)')
p.write_text(s,encoding='utf-8')
p=Path('src/main/java/me/vlad/lastbreath/client/SoulTreeScreen.java');s=p.read_text(encoding='utf-8').replace('(menu.gear(0)==1?"soul_devourer":"soul_cloak")','SoulGear.itemName(menu.gear(0))').replace('(type==1?"soul_devourer":"soul_cloak")','SoulGear.itemName(type)');p.write_text(s,encoding='utf-8')
