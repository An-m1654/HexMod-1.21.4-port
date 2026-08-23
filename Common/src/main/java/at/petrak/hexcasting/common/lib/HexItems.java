package at.petrak.hexcasting.common.lib;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.items.ItemJewelerHammer;
import at.petrak.hexcasting.common.items.ItemLens;
import at.petrak.hexcasting.common.items.ItemLoreFragment;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.items.magic.*;
import at.petrak.hexcasting.common.items.pigment.*;
import at.petrak.hexcasting.common.items.storage.*;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.google.common.base.Suppliers;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

// https://github.com/VazkiiMods/Botania/blob/2c4f7fdf9ebf0c0afa1406dfe1322841133d75fa/Common/src/main/java/vazkii/botania/common/item/ModItems.java
public class HexItems {
    public static void registerItems(BiConsumer<Item, ResourceLocation> r) {
        for (var e : ITEMS.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    public static void registerItemCreativeTab(CreativeModeTab.Output r, CreativeModeTab tab) {
        if (tab == HexCreativeTabs.SCROLLS)
            generateScrollEntries(r);
        for (var item : ITEM_TABS.getOrDefault(tab, Collections.emptyList())) {
            item.register(r);
        }
    }

    private static final Map<ResourceLocation, Item> ITEMS = new LinkedHashMap<>(); // preserve insertion order
    private static final Map<CreativeModeTab, List<TabEntry>> ITEM_TABS = new LinkedHashMap<>();

    public static final Item AMETHYST_DUST = make("amethyst_dust", new Item(props().setId(getId("amethyst_dust"))));
    public static final Item CHARGED_AMETHYST = make("charged_amethyst", new Item(props().setId(getId("charged_amethyst"))));

    public static final Item QUENCHED_SHARD = make("quenched_allay_shard", new Item(props().rarity(Rarity.UNCOMMON).setId(getId("quenched_allay_shard"))));

    public static final ItemStaff STAFF_OAK = make("staff/oak", new ItemStaff(unstackable().setId(getId("staff/oak"))));
    public static final ItemStaff STAFF_SPRUCE = make("staff/spruce", new ItemStaff(unstackable().setId(getId("staff/spruce"))));
    public static final ItemStaff STAFF_BIRCH = make("staff/birch", new ItemStaff(unstackable().setId(getId("staff/birch"))));
    public static final ItemStaff STAFF_JUNGLE = make("staff/jungle", new ItemStaff(unstackable().setId(getId("staff/jungle"))));
    public static final ItemStaff STAFF_ACACIA = make("staff/acacia", new ItemStaff(unstackable().setId(getId("staff/acacia"))));
    public static final ItemStaff STAFF_DARK_OAK = make("staff/dark_oak", new ItemStaff(unstackable().setId(getId("staff/dark_oak"))));
    public static final ItemStaff STAFF_CRIMSON = make("staff/crimson", new ItemStaff(unstackable().setId(getId("staff/crimson"))));
    public static final ItemStaff STAFF_WARPED = make("staff/warped", new ItemStaff(unstackable().setId(getId("staff/warped"))));
    public static final ItemStaff STAFF_MANGROVE = make("staff/mangrove", new ItemStaff(unstackable().setId(getId("staff/mangrove"))));
    public static final ItemStaff STAFF_CHERRY = make("staff/cherry", new ItemStaff(unstackable().setId(getId("staff/cherry"))));
    public static final ItemStaff STAFF_BAMBOO = make("staff/bamboo", new ItemStaff(unstackable().setId(getId("staff/bamboo"))));
    public static final ItemStaff STAFF_EDIFIED = make("staff/edified", new ItemStaff(unstackable().setId(getId("staff/edified"))));
    public static final ItemStaff STAFF_QUENCHED = make("staff/quenched", new ItemStaff(unstackable().rarity(Rarity.UNCOMMON).setId(getId("staff/quenched"))));
    // mindsplice staffaratus
    public static final ItemStaff STAFF_MINDSPLICE = make("staff/mindsplice", new ItemStaff(unstackable().rarity(Rarity.UNCOMMON).setId(getId("staff/mindsplice"))));

    public static final ItemLens SCRYING_LENS = make("lens", new ItemLens(
            IXplatAbstractions.INSTANCE.addEquipSlotsFabric(EquipmentSlot.HEAD)
                    .stacksTo(1)
                    .attributes(ItemLens.MODIFIERS)
                    .setId(getId("lens"))
    ));

    public static final ItemAbacus ABACUS = make("abacus", new ItemAbacus(unstackable().setId(getId("abacus"))));
    public static final ItemThoughtKnot THOUGHT_KNOT = make("thought_knot", new ItemThoughtKnot(unstackable().setId(getId("thought_knot"))));
    public static final ItemFocus FOCUS = make("focus", new ItemFocus(unstackable().setId(getId("focus"))));
    public static final ItemSpellbook SPELLBOOK = make("spellbook", new ItemSpellbook(unstackable().setId(getId("spellbook"))));

    public static final ItemCypher ANCIENT_CYPHER = make("ancient_cypher", new ItemAncientCypher(unstackable().setId(getId("ancient_cypher"))));
    public static final ItemCypher CYPHER = make("cypher", new ItemCypher(unstackable().setId(getId("cypher"))));
    public static final ItemTrinket TRINKET = make("trinket", new ItemTrinket(unstackable().rarity(Rarity.UNCOMMON).setId(getId("trinket"))));
    public static final ItemArtifact ARTIFACT = make("artifact", new ItemArtifact(unstackable().rarity(Rarity.RARE).setId(getId("artifact"))));

    public static final ItemJewelerHammer JEWELER_HAMMER = make("jeweler_hammer",
            new ItemJewelerHammer(ToolMaterial.IRON,
                    1f,
                    -2.8f,
                    props()
                            .stacksTo(1)
                            .durability(ToolMaterial.DIAMOND.durability())
                            .setId(getId("jeweler_hammer"))
            )
    );

    public static final ItemScroll SCROLL_SMOL = make("scroll_small", new ItemScroll(props().setId(getId("scroll_small")), 1));
    public static final ItemScroll SCROLL_MEDIUM = make("scroll_medium", new ItemScroll(props().setId(getId("scroll_medium")), 2));
    public static final ItemScroll SCROLL_LARGE = make("scroll", new ItemScroll(props().setId(getId("scroll")), 3));

    public static final ItemSlate SLATE = make("slate", new ItemSlate(HexBlocks.SLATE, props().setId(getId("slate"))));

    public static final ItemMediaBattery BATTERY = make("battery",
            new ItemMediaBattery(unstackable().setId(getId("battery"))), null);

    public static final Supplier<ItemStack> BATTERY_DUST_STACK = addToTab(() -> ItemMediaBattery.withMedia(
            new ItemStack(HexItems.BATTERY),
            MediaConstants.DUST_UNIT * 64,
            MediaConstants.DUST_UNIT * 64), HexCreativeTabs.HEX);
    public static final Supplier<ItemStack> BATTERY_SHARD_STACK = addToTab(() -> ItemMediaBattery.withMedia(
            new ItemStack(HexItems.BATTERY),
            MediaConstants.SHARD_UNIT * 64,
            MediaConstants.SHARD_UNIT * 64), HexCreativeTabs.HEX);
    public static final Supplier<ItemStack> BATTERY_CRYSTAL_STACK = addToTab(() -> ItemMediaBattery.withMedia(
            new ItemStack(HexItems.BATTERY),
            MediaConstants.CRYSTAL_UNIT * 64,
            MediaConstants.CRYSTAL_UNIT * 64), HexCreativeTabs.HEX);
    public static final Supplier<ItemStack> BATTERY_QUENCHED_SHARD_STACK = addToTab(() -> ItemMediaBattery.withMedia(
            new ItemStack(HexItems.BATTERY),
            MediaConstants.QUENCHED_SHARD_UNIT * 64,
            MediaConstants.QUENCHED_SHARD_UNIT * 64), HexCreativeTabs.HEX);
    public static final Supplier<ItemStack> BATTERY_QUENCHED_BLOCK_STACK = addToTab(() -> ItemMediaBattery.withMedia(
            new ItemStack(HexItems.BATTERY),
            MediaConstants.QUENCHED_BLOCK_UNIT * 64,
            MediaConstants.QUENCHED_BLOCK_UNIT * 64), HexCreativeTabs.HEX);

    public static final EnumMap<DyeColor, ItemDyePigment> DYE_PIGMENTS = Util.make(() -> {
        var out = new EnumMap<DyeColor, ItemDyePigment>(DyeColor.class);
        for (var dye : DyeColor.values()) {
            out.put(dye, make("dye_colorizer_" + dye.getName(), new ItemDyePigment(dye, unstackable().setId(getId("dye_colorizer_" + dye.getName())))));
        }
        return out;
    });
    public static final EnumMap<ItemPridePigment.Type, ItemPridePigment> PRIDE_PIGMENTS = Util.make(() -> {
        var out = new EnumMap<ItemPridePigment.Type, ItemPridePigment>(ItemPridePigment.Type.class);
        for (var politicsInMyVidya : ItemPridePigment.Type.values()) {
            out.put(politicsInMyVidya, make("pride_colorizer_" + politicsInMyVidya.getName(),
                    new ItemPridePigment(politicsInMyVidya, unstackable().setId(getId("pride_colorizer_" + politicsInMyVidya.getName())))));
        }
        return out;
    });

    public static final Item UUID_PIGMENT = make("uuid_colorizer", new ItemUUIDPigment(unstackable().setId(getId("uuid_colorizer"))));
    public static final Item DEFAULT_PIGMENT = make("default_colorizer",
        new ItemAmethystPigment(unstackable().setId(getId("default_colorizer"))));
    public static final Item ANCIENT_PIGMENT = make("ancient_colorizer",
        new ItemAmethystAndCopperPigment(unstackable().setId(getId("ancient_colorizer"))));

    // BUFF SANDVICH
    public static final Item SUBMARINE_SANDWICH = make("sub_sandwich",
            new Item(props().food(new FoodProperties.Builder().nutrition(14).saturationModifier(1.2f).build()).setId(getId("sub_sandwich"))));

    public static final ItemLoreFragment LORE_FRAGMENT = make("lore_fragment",
            new ItemLoreFragment(unstackable()
                    .rarity(Rarity.RARE).setId(getId("lore_fragment"))));

    public static final ItemCreativeUnlocker CREATIVE_UNLOCKER = make("creative_unlocker",
            new ItemCreativeUnlocker(unstackable()
                    .rarity(Rarity.EPIC)
                    .food(new FoodProperties.Builder().nutrition(20).saturationModifier(1f).alwaysEdible().build()).setId(getId("creative_unlocker"))));

    //

    public static Item.Properties props() {
        return new Item.Properties();
    }

    public static Item.Properties unstackable() {
        return props().stacksTo(1);
    }

    private static void generateScrollEntries(CreativeModeTab.Output r) {
        var keyList = new ArrayList<ResourceKey<ActionRegistryEntry>>();
        Registry<ActionRegistryEntry> regi = IXplatAbstractions.INSTANCE.getActionRegistry();
        for (var key : regi.registryKeySet())
            if (HexUtils.isOfTag(regi, key, HexTags.Actions.PER_WORLD_PATTERN))
                keyList.add(key);
        keyList.sort(Comparator.comparing(ResourceKey::location));
        for (var key : keyList) {
            r.accept(ItemScroll.withPerWorldPattern(
                    new ItemStack(HexItems.SCROLL_LARGE),
                    key
            ));
        }
    }

    private static <T extends Item> T make(ResourceLocation id, T item, @Nullable CreativeModeTab tab) {
        var old = ITEMS.put(id, item);
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + id);
        }
        if (tab != null) {
            ITEM_TABS.computeIfAbsent(tab, t -> new ArrayList<>()).add(new TabEntry.ItemEntry(item));
        }
        return item;
    }

    private static <T extends Item> T make(String id, T item, @Nullable CreativeModeTab tab) {
        return make(modLoc(id), item, tab);
    }

    private static <T extends Item> T make(String id, T item) {
        return make(modLoc(id), item, HexCreativeTabs.HEX);
    }

    private static Supplier<ItemStack> addToTab(Supplier<ItemStack> stack, CreativeModeTab tab) {
        var memoised = Suppliers.memoize(stack::get);
        ITEM_TABS.computeIfAbsent(tab, t -> new ArrayList<>()).add(new TabEntry.StackEntry(memoised));
        return memoised;
    }
    private static ResourceKey<Item> getId(String name) {
        return ResourceKey.create(Registries.ITEM, modLoc(name));
    }

    private static abstract class TabEntry {
        abstract void register(CreativeModeTab.Output r);

        static class ItemEntry extends TabEntry {
            private final Item item;

            ItemEntry(Item item) {
                this.item = item;
            }

            @Override
            void register(CreativeModeTab.Output r) {
                r.accept(item);
            }
        }

        static class StackEntry extends TabEntry {
            private final Supplier<ItemStack> stack;

            StackEntry(Supplier<ItemStack> stack) {
                this.stack = stack;
            }

            @Override
            void register(CreativeModeTab.Output r) {
                r.accept(stack.get());
            }
        }
    }
}
