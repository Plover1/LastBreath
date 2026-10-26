exec(open(__file__.replace('upgrade2.py','upgrade.py'),encoding='utf-8').read().split("p=root/'gradle.properties'")[0])
edit('SoulEvents.java','if (event.getEntity() instanceof ServerPlayer player) SoulService.enforce(player);','if (event.getEntity() instanceof ServerPlayer player) { AdvancedSkills.tickPlayer(player); RitualAccess.tick(player); SoulService.enforce(player); }')
edit('SoulEvents.java','    public static void login(PlayerEvent.PlayerLoggedInEvent event) {','    public static void login(PlayerEvent.PlayerLoggedInEvent event) {\n        if(event.getEntity() instanceof ServerPlayer p) p.setData(LastBreath.BORROWED,0L);')
edit('SoulEvents.java','        if (SoulService.ghost(event.getEntity())) event.setCanceled(true);\n    }\n    @SubscribeEvent\n    public static void target','        if(event.getEntity() instanceof ServerPlayer p) RitualAccess.cancel(p);\n        if (SoulService.ghost(event.getEntity())) event.setCanceled(true);\n    }\n    @SubscribeEvent\n    public static void target')
p=java/'SoulEvents.java';s=p.read_text();s=s[:s.index('    @SubscribeEvent\n    public static void commands')]+'''    @SubscribeEvent
    public static void table(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        if(event.getEntity() instanceof ServerPlayer p && !SoulService.ghost(p) && p.level().getBlockState(event.getPos()).is(LastBreath.RITUAL_TABLE.get()) && p.getItemInHand(event.getHand()).is(LastBreath.RED_SOUL.get())) {
            boolean ok=RitualAccess.start(p,event.getPos(),event.getHand());
            if(!ok)RitualAccess.hint(p);
            event.setCanceled(true);event.setCancellationResult(net.minecraft.world.InteractionResult.CONSUME);
        }
    }
    @SubscribeEvent
    public static void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("lastbreath")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("ghost").executes(c -> SoulService.becomeGhost(c.getSource().getPlayerOrException())))
                .then(Commands.literal("alive").executes(c -> SoulService.selfRevive(c.getSource().getPlayerOrException(), net.minecraft.world.InteractionHand.MAIN_HAND, true))));
    }
}
''';p.write_text(s)
edit('MobSoulItem.java','    @Override\n    public InteractionResult use','''    @Override public int getUseDuration(net.minecraft.world.item.ItemStack stack,net.minecraft.world.entity.LivingEntity entity){return Config.RITUAL_SECONDS.get()*20;}
    @Override public net.minecraft.world.item.ItemUseAnimation getUseAnimation(net.minecraft.world.item.ItemStack stack){return net.minecraft.world.item.ItemUseAnimation.BOW;}
    @Override public void onUseTick(Level level,net.minecraft.world.entity.LivingEntity entity,net.minecraft.world.item.ItemStack stack,int remaining){if(entity instanceof ServerPlayer p)RitualAccess.channel(p,remaining);}
    @Override public net.minecraft.world.item.ItemStack finishUsingItem(net.minecraft.world.item.ItemStack stack,Level level,net.minecraft.world.entity.LivingEntity entity){if(entity instanceof ServerPlayer p)RitualAccess.finish(p);return stack;}
    @Override
    public InteractionResult use''')
edit('RitualTableBlock.java','if (SoulService.ghost(player))','if (RitualAccess.allowed(player,pos))')
edit('RitualTableBlock.java','else player.sendSystemMessage(Component.translatable("lastbreath.tree.spirits_only"));','else if(player instanceof net.minecraft.server.level.ServerPlayer sp) RitualAccess.hint(sp);')
edit('SoulReaderItem.java','if (player.isShiftKeyDown()) SoulSkills.activate(server);\n            else server.sendOverlayMessage(Component.translatable("lastbreath.souls.balance",player.getData(LastBreath.PROGRESS).souls()));','''if (player.isShiftKeyDown()) {
                if(player.getXRot()>65)AdvancedSkills.cycle(server);else AdvancedSkills.activate(server);
            } else {
                var p=player.getData(LastBreath.PROGRESS);
                server.sendOverlayMessage(Component.translatable("lastbreath.souls.three",p.souls(),p.red(),p.purple()));
                if(hand==InteractionHand.OFF_HAND)SoulService.list(server);
            }''')
edit('SoulService.java','style.withColor(ChatFormatting.AQUA).withClickEvent(new ClickEvent.SuggestCommand("/lastbreath revive " + target.getUUID()))','style.withColor(ChatFormatting.AQUA)')
edit('SoulService.java','heart.shrink(1); // The cooperative rescue always consumes a heart.','heart.shrink(1); // The cooperative rescue always consumes a heart.\n            rescuer.setData(LastBreath.PROGRESS,rescuer.getData(LastBreath.PROGRESS).trial(2));')
edit('SoulTreeMenu.java','new SimpleContainerData(7)','new SimpleContainerData(13)')
edit('SoulTreeMenu.java','for(int i=0;i<6;i++) values.set(i+1,p.rank(i));','for(int i=0;i<9;i++) values.set(i+1,p.rank(i));\n        values.set(10,p.red());values.set(11,p.purple());values.set(12,p.trials());')
edit('SoulTreeMenu.java','values.get(6),0);','values.get(6),0,values.get(10),values.get(11),values.get(7),values.get(8),values.get(9),values.get(12),9);')
edit('SoulTreeMenu.java','player==owner && SoulService.ghost(player) && player.isAlive()','player==owner && player.isAlive()')
edit('SoulTreeMenu.java','level.getBlockState(pos).is(LastBreath.RITUAL_TABLE.get())','RitualAccess.allowed(player,pos) && level.getBlockState(pos).is(LastBreath.RITUAL_TABLE.get())')
edit('SoulTreeMenu.java','id>5','id>8')
edit('SoulTreeMenu.java','player.setData(LastBreath.PROGRESS,next);broadcastChanges();return true;','player.setData(LastBreath.PROGRESS,next);if(player instanceof net.minecraft.server.level.ServerPlayer sp){AdvancedSkills.burst(sp,2);RitualFeedback.sound(sp.level(),sp.position(),net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME,.5f,1.4f);}\n        broadcastChanges();return true;')
# A living player must hold down use on client too, otherwise the server channel is immediately cancelled.
edit('SoulEvents.java','if(event.getEntity() instanceof ServerPlayer p && !SoulService.ghost(p)', 'if(event.getLevel().isClientSide() && !SoulService.ghost(event.getEntity()) && event.getLevel().getBlockState(event.getPos()).is(LastBreath.RITUAL_TABLE.get()) && event.getItemStack().is(LastBreath.RED_SOUL.get())){event.getEntity().startUsingItem(event.getHand());event.setCanceled(true);event.setCancellationResult(net.minecraft.world.InteractionResult.CONSUME);return;}\n        if(event.getEntity() instanceof ServerPlayer p && !SoulService.ghost(p)')
