package me.vlad.lastbreath;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

@Mod(LastBreath.MODID)
public final class LastBreath {
    public static final String MODID = "lastbreath";
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    private static final DeferredRegister<net.minecraft.world.level.block.entity.BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final net.neoforged.neoforge.registries.DeferredBlock<ReturnAltarBlock> RETURN_ALTAR = BLOCKS.registerBlock("return_altar", ReturnAltarBlock::new,
            properties -> properties.strength(4.0f, 1200.0f).noOcclusion().requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ReturnAltarBlock.CHARGES) * 3));
    public static final DeferredItem<ReturnAltarItem> ALTAR_ITEM = ITEMS.registerItem("return_altar", ReturnAltarItem::new, properties -> properties.useBlockDescriptionPrefix());
    public static final Supplier<net.minecraft.world.level.block.entity.BlockEntityType<ReturnAltarEntity>> ALTAR_ENTITY = BLOCK_ENTITIES.register("return_altar",
            () -> new net.minecraft.world.level.block.entity.BlockEntityType<>(ReturnAltarEntity::new, RETURN_ALTAR.get()));
    private static final DeferredRegister<com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.levelgen.feature.Feature>> FEATURES=DeferredRegister.create(Registries.FEATURE_TYPE,MODID);
    public static final Supplier<com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.levelgen.feature.Feature>> FRAGMENT_FEATURE=FEATURES.register("fragment_ruin",()->FragmentRuinFeature.CODEC);
    public static final Supplier<com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.levelgen.feature.Feature>> MEMORY_FEATURE=FEATURES.register("memory_shrine",()->MemoryShrineFeature.CODEC);
    public static final net.neoforged.neoforge.registries.DeferredBlock<MemoryStoneBlock> MEMORY_STONE=BLOCKS.registerBlock("memory_stone",MemoryStoneBlock::new,p->p.strength(-1,3600000).noLootTable().lightLevel(s->5));
    public static final DeferredItem<net.minecraft.world.item.BlockItem> MEMORY_STONE_ITEM=ITEMS.registerSimpleBlockItem(MEMORY_STONE);
    public static final Supplier<net.minecraft.world.level.block.entity.BlockEntityType<MemoryStoneEntity>> MEMORY_ENTITY=BLOCK_ENTITIES.register("memory_stone",()->new net.minecraft.world.level.block.entity.BlockEntityType<>(MemoryStoneEntity::new,MEMORY_STONE.get()));
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
    public static final DeferredItem<ReturnHeartItem> RETURN_HEART = ITEMS.registerItem("return_heart", ReturnHeartItem::new, properties -> properties.stacksTo(1));
    public static final DeferredItem<DescribedItem> EXECUTIONER_SWORD = ITEMS.registerItem("executioner_sword",
            properties -> new DescribedItem(properties, "lastbreath.tooltip.executioner_sword"),
            properties -> properties.sword(net.minecraft.world.item.ToolMaterial.DIAMOND, 3f, -2.4f));
    private static final DeferredRegister<net.minecraft.core.component.DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE,MODID);
    public static final Supplier<net.minecraft.core.component.DataComponentType<SoulGearData>> GEAR_DATA = COMPONENTS.register("soul_gear",()->net.minecraft.core.component.DataComponentType.<SoulGearData>builder().persistent(SoulGearData.CODEC).networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.fromCodec(SoulGearData.CODEC)).build());
    public static final DeferredItem<SoulGearItem> SOUL_DEVOURER = ITEMS.registerItem("soul_devourer",SoulGearItem::new,p->p.sword(net.minecraft.world.item.ToolMaterial.DIAMOND,3f,-2.4f));
    public static final DeferredItem<SoulGearItem> SOUL_CLOAK = ITEMS.registerItem("soul_cloak",SoulGearItem::new,p->p.humanoidArmor(new net.minecraft.world.item.equipment.ArmorMaterial(20,java.util.Map.of(net.minecraft.world.item.equipment.ArmorType.CHESTPLATE,4),15,net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER,0,0,net.minecraft.tags.ItemTags.REPAIRS_LEATHER_ARMOR,net.minecraft.resources.ResourceKey.create(net.minecraft.world.item.equipment.EquipmentAssets.ROOT_ID,net.minecraft.resources.Identifier.fromNamespaceAndPath(MODID,"soul_cloak"))),net.minecraft.world.item.equipment.ArmorType.CHESTPLATE));
    public static final DeferredItem<SoulGearItem> SOUL_TROUSERS = ITEMS.registerItem("soul_trousers",SoulGearItem::new,p->p.humanoidArmor(new net.minecraft.world.item.equipment.ArmorMaterial(20,java.util.Map.of(net.minecraft.world.item.equipment.ArmorType.LEGGINGS,3),15,net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER,0,0,net.minecraft.tags.ItemTags.REPAIRS_LEATHER_ARMOR,net.minecraft.resources.ResourceKey.create(net.minecraft.world.item.equipment.EquipmentAssets.ROOT_ID,net.minecraft.resources.Identifier.fromNamespaceAndPath(MODID,"soul_trousers"))),net.minecraft.world.item.equipment.ArmorType.LEGGINGS));
    public static final DeferredItem<SoulGearItem> SOUL_BOOTS = ITEMS.registerItem("soul_boots",SoulGearItem::new,p->p.humanoidArmor(new net.minecraft.world.item.equipment.ArmorMaterial(20,java.util.Map.of(net.minecraft.world.item.equipment.ArmorType.BOOTS,2),15,net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER,0,0,net.minecraft.tags.ItemTags.REPAIRS_LEATHER_ARMOR,net.minecraft.resources.ResourceKey.create(net.minecraft.world.item.equipment.EquipmentAssets.ROOT_ID,net.minecraft.resources.Identifier.fromNamespaceAndPath(MODID,"soul_boots"))),net.minecraft.world.item.equipment.ArmorType.BOOTS));
    public static final java.util.List<DeferredItem<DescribedItem>> KNOWLEDGE_SCROLLS = registerScrolls();
    private static java.util.List<DeferredItem<DescribedItem>> registerScrolls(){var result=new java.util.ArrayList<DeferredItem<DescribedItem>>();for(int i=0;i<8;i++){final int id=i;result.add(ITEMS.registerItem("knowledge_scroll_"+id,p->new DescribedItem(p,"lastbreath.scroll.tip")));}return java.util.List.copyOf(result);}
    public static final DeferredItem<DescribedItem> KNOWLEDGE_FRAGMENT=ITEMS.registerItem("knowledge_fragment",p->new DescribedItem(p,"lastbreath.fragment.tip"));
    public static final DeferredItem<MobSoulItem> MOB_SOUL = ITEMS.registerItem("mob_soul", MobSoulItem::new);
    public static final DeferredItem<SoulReaderItem> SOUL_READER = ITEMS.registerItem("soul_reader",SoulReaderItem::new,properties->properties.stacksTo(1));
    public static final net.neoforged.neoforge.registries.DeferredBlock<RitualTableBlock> RITUAL_TABLE = BLOCKS.registerBlock("ritual_table",RitualTableBlock::new,properties->properties.strength(3f).noOcclusion());
    public static final DeferredItem<net.minecraft.world.item.BlockItem> RITUAL_TABLE_ITEM = ITEMS.registerSimpleBlockItem(RITUAL_TABLE);
    public static final DeferredItem<MobSoulItem> RED_SOUL = ITEMS.registerItem("red_soul",p->new MobSoulItem(p,1));
    public static final DeferredItem<MobSoulItem> PURPLE_SOUL = ITEMS.registerItem("purple_soul",p->new MobSoulItem(p,2));
    public static final Supplier<AttachmentType<Long>> BORROWED = ATTACHMENTS.register("borrowed_until",()->AttachmentType.builder(()->0L).serialize(com.mojang.serialization.Codec.LONG.fieldOf("until")).sync(net.minecraft.network.codec.ByteBufCodecs.VAR_LONG).build());
    private static final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU,MODID);
    public static final Supplier<net.minecraft.world.inventory.MenuType<SoulTreeMenu>> SOUL_TREE = MENUS.register("soul_tree",()->new net.minecraft.world.inventory.MenuType<>(SoulTreeMenu::new,net.minecraft.world.flag.FeatureFlags.VANILLA_SET));
    public static final Supplier<AttachmentType<SpiritTalents>> TALENTS = ATTACHMENTS.register("talents",()->AttachmentType.builder(()->SpiritTalents.EMPTY).serialize(SpiritTalents.CODEC.fieldOf("talents")).copyOnDeath().sync(net.minecraft.network.codec.ByteBufCodecs.fromCodec(SpiritTalents.CODEC)).build());
    public static final Supplier<AttachmentType<SoulProgress>> PROGRESS = ATTACHMENTS.register("progress",()->AttachmentType.builder(()->SoulProgress.EMPTY).serialize(SoulProgress.CODEC).copyOnDeath().build());
    public static final DeferredItem<SoulJarItem> EMPTY_SOUL_JAR = ITEMS.registerItem("empty_soul_jar", SoulJarItem::new, properties -> properties.stacksTo(1));
    public static final DeferredItem<DescribedItem> RED_JAR = ITEMS.registerItem("red_soul_jar",p->new DescribedItem(p,"lastbreath.tooltip.soul_jar"),p->p.stacksTo(1).craftRemainder(LastBreath.EMPTY_SOUL_JAR.get()));
    public static final DeferredItem<DescribedItem> PURPLE_JAR = ITEMS.registerItem("purple_soul_jar",p->new DescribedItem(p,"lastbreath.tooltip.soul_jar"),p->p.stacksTo(1).craftRemainder(LastBreath.EMPTY_SOUL_JAR.get()));
    public static final DeferredItem<DescribedItem> SOUL_JAR = ITEMS.registerItem("soul_jar",
            properties -> new DescribedItem(properties, "lastbreath.tooltip.soul_jar"),
            properties -> properties.stacksTo(1).craftRemainder(LastBreath.EMPTY_SOUL_JAR.get()));
    public static final Supplier<CreativeModeTab> CREATIVE_TAB = TABS.register("lastbreath", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lastbreath"))
            .icon(() -> RETURN_HEART.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                KNOWLEDGE_SCROLLS.forEach(scroll->output.accept(scroll.get()));output.accept(KNOWLEDGE_FRAGMENT.get());
                output.accept(MEMORY_STONE_ITEM.get());output.accept(RETURN_HEART.get()); output.accept(ALTAR_ITEM.get());
                output.accept(SOUL_DEVOURER.get()); output.accept(SOUL_CLOAK.get()); output.accept(SOUL_TROUSERS.get()); output.accept(SOUL_BOOTS.get()); output.accept(EXECUTIONER_SWORD.get()); output.accept(EMPTY_SOUL_JAR.get());
                output.accept(MOB_SOUL.get()); output.accept(SOUL_JAR.get());
                output.accept(SOUL_READER.get()); output.accept(RITUAL_TABLE_ITEM.get()); output.accept(RED_SOUL.get()); output.accept(PURPLE_SOUL.get()); output.accept(RED_JAR.get()); output.accept(PURPLE_JAR.get());
            })
            .build());
    public static final Supplier<AttachmentType<SoulState>> SOUL = ATTACHMENTS.register("soul", () -> AttachmentType.builder(() -> SoulState.ALIVE)
            .serialize(SoulState.CODEC).copyOnDeath()
            .sync(net.minecraft.network.codec.ByteBufCodecs.fromCodec(SoulState.CODEC.codec())).build());

    public LastBreath(IEventBus bus, ModContainer container) {
        FEATURES.register(bus); COMPONENTS.register(bus); ITEMS.register(bus);
        MENUS.register(bus);
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
        TABS.register(bus);
        ATTACHMENTS.register(bus);
        container.registerConfig(ModConfig.Type.SYNCED, Config.SPEC);
        NeoForge.EVENT_BUS.register(SoulEvents.class);
        NeoForge.EVENT_BUS.register(SoulGear.class); NeoForge.EVENT_BUS.register(SoulHarvest.class); NeoForge.EVENT_BUS.register(AdvancedSkills.class);
    }

}
