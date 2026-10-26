package me.vlad.lastbreath;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class SoulEvents {
    @SubscribeEvent
    public static void tick(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer player) { TalentService.tick(player); AdvancedSkills.tickPlayer(player); RitualAccess.tick(player); SoulService.enforce(player); }
    }
    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer p) p.setData(LastBreath.BORROWED,0L);
        if (event.getEntity() instanceof ServerPlayer player) { TalentService.tick(player); AdvancedSkills.tickPlayer(player); RitualAccess.tick(player); SoulService.enforce(player); }
    }
    @SubscribeEvent
    public static void respawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !event.isEndConquered()) SoulService.respawn(player);
    }
    @SubscribeEvent
    public static void mode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && SoulService.ghost(player) && !SoulService.transitioning() && event.getNewGameMode() != GameType.ADVENTURE) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void finishDeaths(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
        SoulService.finishDeaths(event.getServer());
    }
    @SubscribeEvent
    public static void damage(net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event) {
        if(event.getEntity() instanceof ServerPlayer p) RitualAccess.cancel(p);
        if (SoulService.ghost(event.getEntity())) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void target(net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent event) {
        if (SoulService.ghost(event.getNewAboutToBeSetTarget())) event.setNewAboutToBeSetTarget(null);
    }
    @SubscribeEvent
    public static void attack(net.neoforged.neoforge.event.entity.player.AttackEntityEvent event) {
        if (SoulService.ghost(event.getEntity())) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void interactEntity(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract event) {
        if (SoulService.ghost(event.getEntity())) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void useItem(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        if (SoulService.ghost(event.getEntity()) && !SoulService.spiritItem(event.getItemStack())) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void breakBlock(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event) {
        if (SoulService.ghost(event.getEntity())) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void pickup(net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Pre event) {
        if (LastBreath.KNOWLEDGE_SCROLLS.stream().anyMatch(scroll->event.getItemEntity().getItem().is(scroll.get()))) return;
        if (SoulService.ghost(event.getPlayer()) && (!(event.getItemEntity().getItem().is(LastBreath.RETURN_HEART.get()) || event.getItemEntity().getItem().is(LastBreath.SOUL_READER.get()))
                || (!event.getPlayer().getMainHandItem().isEmpty() && !event.getPlayer().getOffhandItem().isEmpty()))) event.setCanPickup(net.minecraft.util.TriState.FALSE);
    }
    @SubscribeEvent
    public static void pickedHeart(net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Post event) {
        var player = event.getPlayer();
        var item = event.getOriginalStack().getItem();
        if (!SoulService.ghost(player) || (item != LastBreath.RETURN_HEART.get() && item != LastBreath.SOUL_READER.get())
                || player.getMainHandItem().is(item) || player.getOffhandItem().is(item)) return;
        var hand = player.getMainHandItem().isEmpty() ? net.minecraft.world.InteractionHand.MAIN_HAND : net.minecraft.world.InteractionHand.OFF_HAND;
        if (!player.getItemInHand(hand).isEmpty()) return;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(item)) {
                player.setItemInHand(hand, player.getInventory().removeItem(i, 1));
                player.inventoryMenu.broadcastChanges();
                break;
            }
        }
    }
    @SubscribeEvent
    public static void pickupXp(net.neoforged.neoforge.event.entity.player.PlayerXpEvent.PickupXp event) {
        if (SoulService.ghost(event.getEntity())) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void table(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        if(event.getLevel().isClientSide() && !SoulService.ghost(event.getEntity()) && event.getLevel().getBlockState(event.getPos()).is(LastBreath.RITUAL_TABLE.get()) && event.getItemStack().is(LastBreath.RED_SOUL.get())){event.getEntity().startUsingItem(event.getHand());event.setCanceled(true);event.setCancellationResult(net.minecraft.world.InteractionResult.CONSUME);return;}
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
                .then(SoulAdminCommands.tree())
                .then(Commands.literal("ghost").executes(c -> SoulService.becomeGhost(c.getSource().getPlayerOrException()))
                    .then(Commands.argument("players",EntityArgument.players()).executes(c->{int count=0;for(var p:EntityArgument.getPlayers(c,"players"))count+=SoulService.becomeGhost(p);return count;})))
                .then(Commands.literal("alive").executes(c -> SoulService.administrativeRevive(c.getSource().getPlayerOrException()))
                    .then(Commands.argument("players",EntityArgument.players()).executes(c->{int count=0;for(var p:EntityArgument.getPlayers(c,"players"))count+=SoulService.administrativeRevive(p);return count;}))));
    }
}
