package at.petrak.hexcasting.datagen.recipe;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.advancements.HexAdvancementTriggers;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.common.blocks.decoration.BlockAkashicLog;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.items.pigment.ItemPridePigment;
import at.petrak.hexcasting.common.lib.HexBlocks;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.recipe.SealThingsRecipe;
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.EntityTypeIngredient;
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.VillagerIngredient;
import at.petrak.hexcasting.common.lib.HexStateIngredients;
import at.petrak.hexcasting.datagen.HexAdvancements;
import at.petrak.hexcasting.datagen.IXplatConditionsBuilder;
import at.petrak.hexcasting.datagen.IXplatIngredients;
import at.petrak.hexcasting.datagen.recipe.builders.BrainsweepRecipeBuilder;
import at.petrak.hexcasting.datagen.recipe.builders.FarmersDelightCuttingRecipeBuilder;
import at.petrak.hexcasting.datagen.recipe.builders.FarmersDelightToolIngredient;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

// TODO: need to do a big refactor of this class cause it's giant and unwieldy, probably as part of #360
public class HexplatRecipes extends RecipeProvider {
    private final IXplatIngredients ingredients;
    private final Function<RecipeBuilder, IXplatConditionsBuilder> conditions;
    private final HolderGetter<Item> items;

    private final List<BlockAkashicLog> EDIFIED_LOGS = List.of(
        HexBlocks.EDIFIED_LOG, HexBlocks.EDIFIED_LOG_AMETHYST,
        HexBlocks.EDIFIED_LOG_AVENTURINE, HexBlocks.EDIFIED_LOG_CITRINE,
        HexBlocks.EDIFIED_LOG_PURPLE);

    private final Map<BlockAkashicLog, BlockAkashicLog> EDIFIED_LOG_TO_WOOD = Map.ofEntries(
        Map.entry(HexBlocks.EDIFIED_LOG, HexBlocks.EDIFIED_WOOD),
//      These don't exist, idk if they should
//        Map.entry(HexBlocks.EDIFIED_LOG_AMETHYST, HexBlocks.EDIFIED_WOOD_AMETHYST),
//        Map.entry(HexBlocks.EDIFIED_LOG_AVENTURINE, HexBlocks.EDIFIED_WOOD_AVENTURINE),
//        Map.entry(HexBlocks.EDIFIED_LOG_CITRINE, HexBlocks.EDIFIED_WOOD_CITRINE),
//        Map.entry(HexBlocks.EDIFIED_LOG_PURPLE, HexBlocks.EDIFIED_WOOD_PURPLE),
        Map.entry(HexBlocks.STRIPPED_EDIFIED_LOG, HexBlocks.STRIPPED_EDIFIED_WOOD)
    );

    public HexplatRecipes(HolderLookup.@NotNull Provider provider, @NotNull RecipeOutput recipeOutput,
                          IXplatIngredients ingredients,
                          Function<RecipeBuilder, IXplatConditionsBuilder> conditions) {
        super(provider, recipeOutput);
        this.items = provider.lookupOrThrow(Registries.ITEM);
        this.ingredients = ingredients;
        this.conditions = conditions;
    }

    @Override
    public void buildRecipes() {
        specialRecipe(output, SealThingsRecipe.FOCUS_SERIALIZER, SealThingsRecipe::focus);
        specialRecipe(output, SealThingsRecipe.SPELLBOOK_SERIALIZER, SealThingsRecipe::spellbook);

        staffRecipe(output, HexItems.STAFF_OAK, Items.OAK_PLANKS);
        staffRecipe(output, HexItems.STAFF_BIRCH, Items.BIRCH_PLANKS);
        staffRecipe(output, HexItems.STAFF_SPRUCE, Items.SPRUCE_PLANKS);
        staffRecipe(output, HexItems.STAFF_JUNGLE, Items.JUNGLE_PLANKS);
        staffRecipe(output, HexItems.STAFF_DARK_OAK, Items.DARK_OAK_PLANKS);
        staffRecipe(output, HexItems.STAFF_ACACIA, Items.ACACIA_PLANKS);
        staffRecipe(output, HexItems.STAFF_CRIMSON, Items.CRIMSON_PLANKS);
        staffRecipe(output, HexItems.STAFF_WARPED, Items.WARPED_PLANKS);
        staffRecipe(output, HexItems.STAFF_MANGROVE, Items.MANGROVE_PLANKS);
        staffRecipe(output, HexItems.STAFF_CHERRY, Items.CHERRY_PLANKS);
        staffRecipe(output, HexItems.STAFF_BAMBOO, Items.BAMBOO_PLANKS);
        staffRecipe(output, HexItems.STAFF_EDIFIED, HexBlocks.EDIFIED_PLANKS.asItem());
        staffRecipe(output, HexItems.STAFF_QUENCHED, HexItems.QUENCHED_SHARD);
        staffRecipe(output, HexItems.STAFF_MINDSPLICE, HexTags.Items.MINDFLAYED_CIRCLE_COMPONENTS);

        this.shapeless(RecipeCategory.TOOLS, HexItems.THOUGHT_KNOT)
            .requires(HexItems.AMETHYST_DUST)
            .requires(Items.STRING)
            .unlockedBy("has_item", hasItem(HexTags.Items.STAVES))
            .save(output);
        this.shaped(RecipeCategory.TOOLS, HexItems.FOCUS)
            .define('G', ingredients.glowstoneDust())
            .define('L', ingredients.leather())
            .define('P', Items.PAPER)
            .define('A', HexItems.CHARGED_AMETHYST)
            .pattern("GLG")
            .pattern("PAP")
            .pattern("GLG")
            .unlockedBy("has_item", hasItem(HexTags.Items.STAVES))
            .save(output);
        this.shaped(RecipeCategory.TOOLS, HexItems.FOCUS)
            .define('G', ingredients.glowstoneDust())
            .define('L', ingredients.leather())
            .define('P', Items.PAPER)
            .define('A', HexItems.CHARGED_AMETHYST)
            .pattern("GPG")
            .pattern("LAL")
            .pattern("GPG")
            .unlockedBy("has_item", hasItem(HexTags.Items.STAVES))
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("focus_rotated")));

        this.shaped(RecipeCategory.TOOLS, HexItems.SPELLBOOK)
            .define('N', ingredients.goldNugget())
            .define('B', Items.WRITABLE_BOOK)
            .define('A', HexItems.CHARGED_AMETHYST)
            .define('F', Items.CHORUS_FRUIT) // i wanna gate this behind the end SOMEHOW
            // hey look its my gender ^^
            .pattern("NBA")
            .pattern("NFA")
            .pattern("NBA")
            .unlockedBy("has_focus", hasItem(HexItems.FOCUS))
            .unlockedBy("has_chorus", hasItem(Items.CHORUS_FRUIT)).save(output);

        ringCornerless(RecipeCategory.TOOLS,
            HexItems.CYPHER, 1,
            Ingredient.of(this.items.getOrThrow(ingredients.copperIngot())),
            Ingredient.of(HexItems.AMETHYST_DUST))
            .unlockedBy("has_item", hasItem(HexTags.Items.STAVES)).save(output);

        ringCornerless(RecipeCategory.TOOLS,
            HexItems.TRINKET, 1,
            Ingredient.of(this.items.getOrThrow(ingredients.ironIngot())),
            Ingredient.of(Items.AMETHYST_SHARD))
            .unlockedBy("has_item", hasItem(HexTags.Items.STAVES)).save(output);

        this.shaped(RecipeCategory.TOOLS, HexItems.ARTIFACT)
            .define('F', ingredients.goldIngot())
            .define('A', HexItems.CHARGED_AMETHYST)
            // 1.21 removed the vanilla music discs tag so we need to use this instead
            .define('D', TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c","music_discs")))
            .pattern(" F ")
            .pattern("FAF")
            .pattern(" D ")
            .unlockedBy("has_item", hasItem(HexTags.Items.STAVES)).save(output);

        ringCornerless(RecipeCategory.TOOLS, HexItems.SCRYING_LENS, 1, Items.GLASS, HexItems.AMETHYST_DUST)
            .unlockedBy("has_item", hasItem(HexTags.Items.STAVES)).save(output);

        this.shaped(RecipeCategory.TOOLS, HexItems.ABACUS)
            .define('S', Items.STICK)
            .define('A', Items.AMETHYST_SHARD)
            .define('W', ItemTags.PLANKS)
            .pattern("WAW")
            .pattern("SAS")
            .pattern("WAW")
            .unlockedBy("has_item", hasItem(HexTags.Items.STAVES)).save(output);

        // Why am I like this
        this.shaped(RecipeCategory.FOOD, HexItems.SUBMARINE_SANDWICH)
            .define('S', Items.STICK)
            .define('A', Items.AMETHYST_SHARD)
            .define('C', Items.COOKED_BEEF)
            .define('B', Items.BREAD)
            .pattern(" SA")
            .pattern(" C ")
            .pattern(" B ")
            .unlockedBy("has_item", hasItem(Items.AMETHYST_SHARD)).save(output);

        for (var dye : DyeColor.values()) {
            var item = HexItems.DYE_PIGMENTS.get(dye);
            this.shaped(RecipeCategory.MISC, item)
                .define('D', HexItems.AMETHYST_DUST)
                .define('C', DyeItem.byColor(dye))
                .pattern(" D ")
                .pattern("DCD")
                .pattern(" D ")
                .unlockedBy("has_item", hasItem(HexItems.AMETHYST_DUST)).save(output);
        }

        gayRecipe(output, ItemPridePigment.Type.AGENDER, Ingredient.of(Items.GLASS));
        gayRecipe(output, ItemPridePigment.Type.AROACE, Ingredient.of(Items.WHEAT_SEEDS));
        gayRecipe(output, ItemPridePigment.Type.AROMANTIC, Ingredient.of(Items.ARROW));
        gayRecipe(output, ItemPridePigment.Type.ASEXUAL, Ingredient.of(Items.BREAD));
        gayRecipe(output, ItemPridePigment.Type.BISEXUAL, Ingredient.of(Items.WHEAT));
        gayRecipe(output, ItemPridePigment.Type.DEMIBOY, Ingredient.of(Items.RAW_IRON));
        gayRecipe(output, ItemPridePigment.Type.DEMIGIRL, Ingredient.of(Items.RAW_COPPER));
        gayRecipe(output, ItemPridePigment.Type.GAY, Ingredient.of(Items.STONE_BRICK_WALL));
        gayRecipe(output, ItemPridePigment.Type.GENDERFLUID, Ingredient.of(Items.WATER_BUCKET));
        gayRecipe(output, ItemPridePigment.Type.GENDERQUEER, Ingredient.of(Items.GLASS_BOTTLE));
        gayRecipe(output, ItemPridePigment.Type.INTERSEX, Ingredient.of(Items.AZALEA));
        gayRecipe(output, ItemPridePigment.Type.LESBIAN, Ingredient.of(Items.HONEYCOMB));
        gayRecipe(output, ItemPridePigment.Type.NONBINARY, Ingredient.of(Items.MOSS_BLOCK));
        // TODO port: This is neither an item value nor a tag value.
        /*gayRecipe(recipes, ItemPridePigment.Type.PANSEXUAL, ingredients.whenModIngredient(
            Ingredient.of(Items.CARROT),
            "farmersdelight",
            CompatIngredientValue.of("farmersdelight:skillet")
        ));*/
        gayRecipe(output, ItemPridePigment.Type.PLURAL, Ingredient.of(Items.REPEATER));
        gayRecipe(output, ItemPridePigment.Type.TRANSGENDER, Ingredient.of(Items.EGG));

        ring(RecipeCategory.MISC, HexItems.UUID_PIGMENT, 1, HexItems.AMETHYST_DUST, Items.AMETHYST_SHARD)
            .unlockedBy("has_item", hasItem(HexItems.AMETHYST_DUST)).save(output);
        ringCornerless(RecipeCategory.MISC, HexItems.DEFAULT_PIGMENT, 1, HexItems.AMETHYST_DUST, Items.AMETHYST_SHARD)
            .unlockedBy("has_item", hasItem(HexItems.AMETHYST_DUST)).save(output);
        ringCornerless(RecipeCategory.MISC, HexItems.ANCIENT_PIGMENT, 1, HexItems.AMETHYST_DUST, Items.COPPER_INGOT)
            .unlockedBy("has_item", hasItem(HexItems.AMETHYST_DUST)).save(output);

        this.shaped(RecipeCategory.DECORATIONS, HexItems.SCROLL_SMOL)
            .define('P', Items.PAPER)
            .define('A', HexItems.AMETHYST_DUST)
            .pattern(" A")
            .pattern("P ")
            .unlockedBy("has_item", hasItem(HexTags.Items.STAVES)).save(output);

        this.shaped(RecipeCategory.DECORATIONS, HexItems.SCROLL_MEDIUM)
            .define('P', Items.PAPER)
            .define('A', HexItems.AMETHYST_DUST)
            .pattern("  A")
            .pattern("PP ")
            .pattern("PP ")
            .unlockedBy("has_item", hasItem(HexTags.Items.STAVES)).save(output);

        this.shaped(RecipeCategory.DECORATIONS, HexItems.SCROLL_LARGE)
            .define('P', Items.PAPER)
            .define('A', HexItems.AMETHYST_DUST)
            .pattern("PPA")
            .pattern("PPP")
            .pattern("PPP")
            .unlockedBy("has_item", hasItem(HexTags.Items.STAVES)).save(output);

        this.shaped(RecipeCategory.DECORATIONS, HexItems.SLATE, 6)
            .define('S', Items.DEEPSLATE)
            .define('A', HexItems.AMETHYST_DUST)
            .pattern(" A ")
            .pattern("SSS")
            .unlockedBy("has_item", hasItem(HexItems.AMETHYST_DUST)).save(output);

        this.shaped(RecipeCategory.TOOLS, HexItems.JEWELER_HAMMER)
            .define('I', ingredients.ironIngot())
            .define('N', ingredients.ironNugget())
            .define('A', Items.AMETHYST_SHARD)
            .define('S', ingredients.stick())
            .pattern("IAN")
            .pattern(" S ")
            .pattern(" S ")
            .unlockedBy("has_item", hasItem(Items.AMETHYST_SHARD)).save(output);

        this.shapeless(RecipeCategory.MISC, HexItems.AMETHYST_DUST,
                (int) (MediaConstants.QUENCHED_SHARD_UNIT / MediaConstants.DUST_UNIT) + 1)
            .requires(HexItems.QUENCHED_SHARD)
            .requires(HexItems.AMETHYST_DUST)
            .unlockedBy("has_item", hasItem(HexItems.QUENCHED_SHARD))
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("decompose_quenched_shard/dust")));
        this.shapeless(RecipeCategory.MISC, Items.AMETHYST_SHARD,
                (int) (MediaConstants.QUENCHED_SHARD_UNIT / MediaConstants.SHARD_UNIT) + 1)
            .requires(HexItems.QUENCHED_SHARD)
            .requires(Items.AMETHYST_SHARD)
            .unlockedBy("has_item", hasItem(HexItems.QUENCHED_SHARD))
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("decompose_quenched_shard/shard")));
        this.shapeless(RecipeCategory.MISC, HexItems.CHARGED_AMETHYST,
                (int) (MediaConstants.QUENCHED_SHARD_UNIT / MediaConstants.CRYSTAL_UNIT) + 1)
            .requires(HexItems.QUENCHED_SHARD)
            .requires(HexItems.CHARGED_AMETHYST)
            .unlockedBy("has_item", hasItem(HexItems.QUENCHED_SHARD))
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("decompose_quenched_shard/charged")));

        this.shaped(RecipeCategory.BUILDING_BLOCKS, HexBlocks.SLATE_BLOCK)
            .define('S', HexItems.SLATE)
            .pattern("S")
            .pattern("S")
            .unlockedBy("has_item", hasItem(HexItems.SLATE))
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("slate_block_from_slates")));

        ringAll(RecipeCategory.BUILDING_BLOCKS, HexBlocks.SLATE_BLOCK, 8, Blocks.DEEPSLATE, HexItems.AMETHYST_DUST)
            .unlockedBy("has_item", hasItem(HexItems.SLATE)).save(output);

        packing(RecipeCategory.BUILDING_BLOCKS, HexItems.AMETHYST_DUST, HexBlocks.AMETHYST_DUST_BLOCK.asItem(), "amethyst_dust",
            false, output);

        ringAll(RecipeCategory.BUILDING_BLOCKS, HexBlocks.SCROLL_PAPER, 8, Items.PAPER, Items.AMETHYST_SHARD)
            .unlockedBy("has_item", hasItem(Items.AMETHYST_SHARD)).save(output);

        this.shapeless(RecipeCategory.BUILDING_BLOCKS, HexBlocks.ANCIENT_SCROLL_PAPER, 8)
            .requires(ingredients.dyes().get(DyeColor.BROWN))
            .requires(HexBlocks.SCROLL_PAPER, 8)
            .unlockedBy("has_item", hasItem(HexBlocks.SCROLL_PAPER)).save(output);

        stack(RecipeCategory.DECORATIONS, HexBlocks.SCROLL_PAPER_LANTERN, 1, HexBlocks.SCROLL_PAPER, Items.TORCH)
            .unlockedBy("has_item", hasItem(HexBlocks.SCROLL_PAPER)).save(output);

        stack(RecipeCategory.DECORATIONS, HexBlocks.ANCIENT_SCROLL_PAPER_LANTERN, 1, HexBlocks.ANCIENT_SCROLL_PAPER, Items.TORCH)
            .unlockedBy("has_item", hasItem(HexBlocks.ANCIENT_SCROLL_PAPER)).save(output);

        this.shapeless(RecipeCategory.DECORATIONS, HexBlocks.ANCIENT_SCROLL_PAPER_LANTERN, 8)
            .requires(ingredients.dyes().get(DyeColor.BROWN))
            .requires(HexBlocks.SCROLL_PAPER_LANTERN, 8)
            .unlockedBy("has_item", hasItem(HexBlocks.SCROLL_PAPER_LANTERN))
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("ageing_scroll_paper_lantern")));

        stack(RecipeCategory.DECORATIONS, HexBlocks.SCONCE, 4,
            Ingredient.of(HexItems.CHARGED_AMETHYST),
            Ingredient.of(this.items.getOrThrow(ingredients.copperIngot())))
            .unlockedBy("has_item", hasItem(HexItems.CHARGED_AMETHYST)).save(output);

        this.shapeless(RecipeCategory.BUILDING_BLOCKS, HexBlocks.EDIFIED_PLANKS, 4)
            .requires(HexTags.Items.EDIFIED_LOGS)
            .unlockedBy("has_item", hasItem(HexTags.Items.EDIFIED_LOGS)).save(output);

        for (var entry : EDIFIED_LOG_TO_WOOD.entrySet()) {
            var log = entry.getKey();
            var wood = entry.getValue();
            this.shaped(RecipeCategory.BUILDING_BLOCKS, wood, 3)
                    .define('W', log)
                    .pattern("WW")
                    .pattern("WW")
                    .unlockedBy("has_item", hasItem(log)).save(output);
        }

        ring(RecipeCategory.BUILDING_BLOCKS, HexBlocks.EDIFIED_PANEL, 8,
            HexTags.Items.EDIFIED_PLANKS, null)
            .unlockedBy("has_item", hasItem(HexTags.Items.EDIFIED_PLANKS)).save(output);

        this.shaped(RecipeCategory.BUILDING_BLOCKS, HexBlocks.EDIFIED_TILE, 6)
            .define('W', HexTags.Items.EDIFIED_PLANKS)
            .pattern("WW ")
            .pattern("W W")
            .pattern(" WW")
            .unlockedBy("has_item", hasItem(HexTags.Items.EDIFIED_PLANKS)).save(output);

        this.shaped(RecipeCategory.REDSTONE, HexBlocks.EDIFIED_DOOR, 3)
            .define('W', HexTags.Items.EDIFIED_PLANKS)
            .pattern("WW")
            .pattern("WW")
            .pattern("WW")
            .unlockedBy("has_item", hasItem(HexTags.Items.EDIFIED_PLANKS)).save(output);

        this.shaped(RecipeCategory.REDSTONE, HexBlocks.EDIFIED_TRAPDOOR, 2)
            .define('W', HexTags.Items.EDIFIED_PLANKS)
            .pattern("WWW")
            .pattern("WWW")
            .unlockedBy("has_item", hasItem(HexTags.Items.EDIFIED_PLANKS)).save(output);

        this.shaped(RecipeCategory.BUILDING_BLOCKS, HexBlocks.EDIFIED_STAIRS, 4)
            .define('W', HexTags.Items.EDIFIED_PLANKS)
            .pattern("W  ")
            .pattern("WW ")
            .pattern("WWW")
            .unlockedBy("has_item", hasItem(HexTags.Items.EDIFIED_PLANKS)).save(output);

        this.shaped(RecipeCategory.BUILDING_BLOCKS, HexBlocks.EDIFIED_FENCE, 3)
                .define('W', HexTags.Items.EDIFIED_PLANKS)
                .define('S', Items.STICK)
                .pattern("WSW")
                .pattern("WSW")
                .unlockedBy("has_item", hasItem(HexTags.Items.EDIFIED_PLANKS)).save(output);

        this.shaped(RecipeCategory.BUILDING_BLOCKS, HexBlocks.EDIFIED_FENCE_GATE, 1)
                .define('W', HexTags.Items.EDIFIED_PLANKS)
                .define('S', Items.STICK)
                .pattern("SWS")
                .pattern("SWS")
                .unlockedBy("has_item", hasItem(HexTags.Items.EDIFIED_PLANKS)).save(output);


        this.shaped(RecipeCategory.BUILDING_BLOCKS, HexBlocks.EDIFIED_SLAB, 6)
            .define('W', HexTags.Items.EDIFIED_PLANKS)
            .pattern("WWW")
            .unlockedBy("has_item", hasItem(HexTags.Items.EDIFIED_PLANKS)).save(output);

        this.shaped(RecipeCategory.REDSTONE, HexBlocks.EDIFIED_PRESSURE_PLATE, 1)
            .define('W', HexTags.Items.EDIFIED_PLANKS)
            .pattern("WW")
            .unlockedBy("has_item", hasItem(HexTags.Items.EDIFIED_PLANKS)).save(output);

        this.shapeless(RecipeCategory.REDSTONE, HexBlocks.EDIFIED_BUTTON)
            .requires(HexTags.Items.EDIFIED_PLANKS)
            .unlockedBy("has_item", hasItem(HexTags.Items.EDIFIED_PLANKS)).save(output);

        var enlightenment = HexAdvancements.ENLIGHTEN;
        this.shaped(RecipeCategory.REDSTONE, HexBlocks.IMPETUS_EMPTY)
            .define('B', Items.IRON_BARS)
            .define('A', HexItems.CHARGED_AMETHYST)
            .define('S', HexBlocks.SLATE_BLOCK)
            .define('P', Items.PURPUR_BLOCK)
            .pattern("PSS")
            .pattern("BAB")
            .pattern("SSP")
            .unlockedBy("enlightenment", new Criterion<>(HexAdvancementTriggers.OVERCAST_TRIGGER, enlightenment)).save(output);

        this.shaped(RecipeCategory.REDSTONE, HexBlocks.EMPTY_DIRECTRIX)
            .define('C', Items.COMPARATOR)
            .define('O', Items.OBSERVER)
            .define('A', HexItems.CHARGED_AMETHYST)
            .define('S', HexBlocks.SLATE_BLOCK)
            .pattern("CSS")
            .pattern("OAO")
            .pattern("SSC")
            .unlockedBy("enlightenment", new Criterion<>(HexAdvancementTriggers.OVERCAST_TRIGGER, enlightenment)).save(output);

        this.shaped(RecipeCategory.REDSTONE, HexBlocks.AKASHIC_BOOKSHELF)
            .define('L', HexTags.Items.EDIFIED_LOGS)
            .define('P', HexTags.Items.EDIFIED_PLANKS)
            .define('C', Items.BOOK)
            /*this is the*/.pattern("LPL") // and what i have for you today is
            .pattern("CCC")
            .pattern("LPL")
            .unlockedBy("enlightenment", new Criterion<>(HexAdvancementTriggers.OVERCAST_TRIGGER, enlightenment)).save(output);

        this.shaped(RecipeCategory.REDSTONE, HexBlocks.AKASHIC_LIGATURE, 4)
            .define('L', HexTags.Items.EDIFIED_LOGS)
            .define('P', HexTags.Items.EDIFIED_PLANKS)
            .define('1', HexItems.AMETHYST_DUST)
            .define('2', Items.AMETHYST_SHARD)
            .define('3', HexItems.CHARGED_AMETHYST)
            .pattern("LPL")
            .pattern("123")
            .pattern("LPL")
            .unlockedBy("enlightenment", new Criterion<>(HexAdvancementTriggers.OVERCAST_TRIGGER, enlightenment)).save(output);

        // Stone sets
        stoneSet(output, HexBlocks.SLATE_BLOCK.asItem(), HexBlocks.SLATE_BRICKS.asItem(), HexBlocks.SLATE_BRICKS_SMALL.asItem(), HexBlocks.SLATE_TILES.asItem(), HexBlocks.SLATE_PILLAR.asItem());
        stoneSet(output, Blocks.AMETHYST_BLOCK.asItem(), HexBlocks.AMETHYST_BRICKS.asItem(), HexBlocks.AMETHYST_BRICKS_SMALL.asItem(), HexBlocks.AMETHYST_TILES.asItem(), HexBlocks.AMETHYST_PILLAR.asItem());
        stoneSet(output, HexBlocks.QUENCHED_ALLAY.asItem(), HexBlocks.QUENCHED_ALLAY_BRICKS.asItem(), HexBlocks.QUENCHED_ALLAY_BRICKS_SMALL.asItem(), HexBlocks.QUENCHED_ALLAY_TILES.asItem(), null);

        // Stone sets in stonecutter
        stoneCutterFromTag(output, HexTags.Items.SLATE_BLOCKS, HexBlocks.SLATE_BRICKS.asItem(), HexBlocks.SLATE_BRICKS_SMALL.asItem(), HexBlocks.SLATE_TILES.asItem(), HexBlocks.SLATE_PILLAR.asItem());
        stoneCutterFromTag(output, HexTags.Items.AMETHYST_BLOCKS, HexBlocks.AMETHYST_BRICKS.asItem(), HexBlocks.AMETHYST_BRICKS_SMALL.asItem(), HexBlocks.AMETHYST_TILES.asItem(), HexBlocks.AMETHYST_PILLAR.asItem());
        stoneCutterFromTag(output, HexTags.Items.QUENCHED_ALLAY_BLOCKS, HexBlocks.QUENCHED_ALLAY_BRICKS.asItem(), HexBlocks.QUENCHED_ALLAY_BRICKS_SMALL.asItem(), HexBlocks.QUENCHED_ALLAY_TILES.asItem());

        // Slate & Amethyst block set
        this.shapeless(RecipeCategory.BUILDING_BLOCKS, HexBlocks.SLATE_AMETHYST_BRICKS.asItem(), 2)
                .requires(HexBlocks.SLATE_BRICKS)
                .requires(HexBlocks.AMETHYST_BRICKS)
                .unlockedBy("has_item", has(HexBlocks.SLATE)).save(output);

        this.shapeless(RecipeCategory.BUILDING_BLOCKS, HexBlocks.SLATE_AMETHYST_BRICKS_SMALL.asItem(), 2)
                .requires(HexBlocks.SLATE_BRICKS_SMALL)
                .requires(HexBlocks.AMETHYST_BRICKS_SMALL)
                .unlockedBy("has_item", has(HexBlocks.SLATE)).save(output);

        this.shapeless(RecipeCategory.BUILDING_BLOCKS, HexBlocks.SLATE_AMETHYST_TILES.asItem(), 2)
                .requires(HexBlocks.SLATE_TILES)
                .requires(HexBlocks.AMETHYST_TILES)
                .unlockedBy("has_item", has(HexBlocks.SLATE)).save(output);

        this.shapeless(RecipeCategory.BUILDING_BLOCKS, HexBlocks.SLATE_AMETHYST_PILLAR.asItem(), 2)
                .requires(HexBlocks.SLATE_PILLAR)
                .requires(HexBlocks.AMETHYST_PILLAR)
                .unlockedBy("has_item", has(HexBlocks.SLATE)).save(output);

        new BrainsweepRecipeBuilder(HexStateIngredients.of(Blocks.AMETHYST_BLOCK),
            new VillagerIngredient(null, null, 3),
            Blocks.BUDDING_AMETHYST.defaultBlockState(), MediaConstants.CRYSTAL_UNIT * 10)
            .unlockedBy("enlightenment", new Criterion<>(HexAdvancementTriggers.OVERCAST_TRIGGER, enlightenment))
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("budding_amethyst")));

        new BrainsweepRecipeBuilder(HexStateIngredients.of(HexBlocks.IMPETUS_EMPTY),
            new VillagerIngredient(VillagerProfession.TOOLSMITH, null, 2),
            HexBlocks.IMPETUS_RIGHTCLICK.defaultBlockState(), MediaConstants.CRYSTAL_UNIT * 10)
            .unlockedBy("enlightenment", new Criterion<>(HexAdvancementTriggers.OVERCAST_TRIGGER, enlightenment))
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("impetus_rightclick")));

        new BrainsweepRecipeBuilder(HexStateIngredients.of(HexBlocks.IMPETUS_EMPTY),
            new VillagerIngredient(VillagerProfession.FLETCHER, null, 2),
            HexBlocks.IMPETUS_LOOK.defaultBlockState(), MediaConstants.CRYSTAL_UNIT * 10)
            .unlockedBy("enlightenment", new Criterion<>(HexAdvancementTriggers.OVERCAST_TRIGGER, enlightenment))
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("impetus_look")));

        new BrainsweepRecipeBuilder(HexStateIngredients.of(HexBlocks.IMPETUS_EMPTY),
            new VillagerIngredient(VillagerProfession.CLERIC, null, 2),
            HexBlocks.IMPETUS_REDSTONE.defaultBlockState(), MediaConstants.CRYSTAL_UNIT * 10)
            .unlockedBy("enlightenment", new Criterion<>(HexAdvancementTriggers.OVERCAST_TRIGGER, enlightenment))
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("impetus_storedplayer")));

        new BrainsweepRecipeBuilder(HexStateIngredients.of(HexBlocks.EMPTY_DIRECTRIX),
            new VillagerIngredient(VillagerProfession.MASON, null, 1),
            HexBlocks.DIRECTRIX_REDSTONE.defaultBlockState(), MediaConstants.CRYSTAL_UNIT * 10)
            .unlockedBy("enlightenment", new Criterion<>(HexAdvancementTriggers.OVERCAST_TRIGGER, enlightenment))
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("directrix_redstone")));

        new BrainsweepRecipeBuilder(HexStateIngredients.of(HexBlocks.EMPTY_DIRECTRIX),
                new VillagerIngredient(VillagerProfession.SHEPHERD, null, 1),
                HexBlocks.DIRECTRIX_BOOLEAN.defaultBlockState(), MediaConstants.CRYSTAL_UNIT * 10)
                .unlockedBy("enlightenment", new Criterion<>(HexAdvancementTriggers.OVERCAST_TRIGGER, enlightenment))
                .save(output, ResourceKey.create(Registries.RECIPE, modLoc("directrix_boolean")));

        new BrainsweepRecipeBuilder(HexStateIngredients.of(HexBlocks.AKASHIC_LIGATURE),
            new VillagerIngredient(VillagerProfession.LIBRARIAN, null, 5),
            HexBlocks.AKASHIC_RECORD.defaultBlockState(), MediaConstants.CRYSTAL_UNIT * 10)
            .unlockedBy("enlightenment", new Criterion<>(HexAdvancementTriggers.OVERCAST_TRIGGER, enlightenment))
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("akashic_record")));

        // Temporary tests
        new BrainsweepRecipeBuilder(HexStateIngredients.of(Blocks.AMETHYST_BLOCK),
            new EntityTypeIngredient(EntityType.ALLAY),
            HexBlocks.QUENCHED_ALLAY.defaultBlockState(), MediaConstants.CRYSTAL_UNIT)
            .unlockedBy("enlightenment", new Criterion<>(HexAdvancementTriggers.OVERCAST_TRIGGER, enlightenment))
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("quench_allay")));

        // Create compat; will need to be Neo only as Create 1.21 will not exist on Fabric
        /**
        this.conditions.apply(new CreateCrushingRecipeBuilder()
                .withInput(Blocks.AMETHYST_CLUSTER)
                .duration(150)
                .withOutput(Items.AMETHYST_SHARD, 7)
                .withOutput(HexItems.AMETHYST_DUST, 5)
                .withOutput(0.25f, HexItems.CHARGED_AMETHYST))
            .whenModLoaded("create")
            .save(recipes, ResourceLocation.fromNamespaceAndPath("create", "crushing/amethyst_cluster"));

        this.conditions.apply(new CreateCrushingRecipeBuilder()
                .withInput(Blocks.AMETHYST_BLOCK)
                .duration(150)
                .withOutput(Items.AMETHYST_SHARD, 3)
                .withOutput(0.5f, HexItems.AMETHYST_DUST, 4))
            .whenModLoaded("create")
            .save(recipes, ResourceLocation.fromNamespaceAndPath("create", "crushing/amethyst_block"));

        this.conditions.apply(new CreateCrushingRecipeBuilder()
                .withInput(Items.AMETHYST_SHARD)
                .duration(150)
                .withOutput(HexItems.AMETHYST_DUST, 4)
                .withOutput(0.5f, HexItems.AMETHYST_DUST))
            .whenModLoaded("create")
            .save(recipes, modLoc("compat/create/crushing/amethyst_shard"));
         */

        // FD compat
        for (var log : EDIFIED_LOGS) {
            this.conditions.apply(new FarmersDelightCuttingRecipeBuilder()
                    .withInput(log)
                    .withTool(ingredients.axeStrip())
                    .withOutput(HexBlocks.STRIPPED_EDIFIED_LOG)
                    .withOutput("farmersdelight:tree_bark")
                    .withSound(SoundEvents.AXE_STRIP))
                .whenModLoaded("farmersdelight")
                .save(output, ResourceKey.create(Registries.RECIPE, modLoc("compat/farmersdelight/cutting/" + BuiltInRegistries.BLOCK.getKey(log).getPath())));
        }

        this.conditions.apply(new FarmersDelightCuttingRecipeBuilder()
                .withInput(HexBlocks.EDIFIED_WOOD)
                .withTool(ingredients.axeStrip())
                .withOutput(HexBlocks.STRIPPED_EDIFIED_WOOD)
                .withOutput("farmersdelight:tree_bark")
                .withSound(SoundEvents.AXE_STRIP))
            .whenModLoaded("farmersdelight")
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("compat/farmersdelight/cutting/akashic_wood")));

        this.conditions.apply(new FarmersDelightCuttingRecipeBuilder()
                .withInput(HexBlocks.EDIFIED_TRAPDOOR)
                .withTool(ingredients.axeDig())
                .withOutput(HexBlocks.EDIFIED_PLANKS))
            .whenModLoaded("farmersdelight")
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("compat/farmersdelight/cutting/akashic_trapdoor")));

        this.conditions.apply(new FarmersDelightCuttingRecipeBuilder()
                .withInput(HexBlocks.EDIFIED_DOOR)
                .withTool(ingredients.axeDig())
                .withOutput(HexBlocks.EDIFIED_PLANKS))
            .whenModLoaded("farmersdelight")
            .save(output, ResourceKey.create(Registries.RECIPE, modLoc("compat/farmersdelight/cutting/akashic_door")));
    }

    private void staffRecipe(RecipeOutput recipes, ItemStaff staff, Item plank) {
        staffRecipe(recipes, staff, Ingredient.of(plank));
    }

    private void staffRecipe(RecipeOutput recipes, ItemStaff staff, TagKey<Item> plank) {
        staffRecipe(recipes, staff, Ingredient.of(this.items.getOrThrow(plank)));
    }

    private void staffRecipe(RecipeOutput recipes, ItemStaff staff, Ingredient plank) {
        this.shaped(RecipeCategory.TOOLS, staff)
            .define('W', plank)
            .define('S', Items.STICK)
            .define('A', HexItems.CHARGED_AMETHYST)
            .pattern(" SA")
            .pattern(" WS")
            .pattern("S  ")
            .unlockedBy("has_item", hasItem(HexItems.CHARGED_AMETHYST))
            .save(recipes);
    }

    private void gayRecipe(RecipeOutput recipes, ItemPridePigment.Type type, Ingredient material) {
        var colorizer = HexItems.PRIDE_PIGMENTS.get(type);
        this.shaped(RecipeCategory.MISC, colorizer)
            .define('D', HexItems.AMETHYST_DUST)
            .define('C', material)
            .pattern(" D ")
            .pattern("DCD")
            .pattern(" D ")
            .unlockedBy("has_item", hasItem(HexItems.AMETHYST_DUST))
            .save(recipes);
    }
    private <T extends Recipe<?>> void specialRecipe(RecipeOutput consumer, RecipeSerializer<T> serializer, Function<CraftingBookCategory, T> recipeFunc) {
        var name = BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer);
        SpecialRecipeBuilder.special(recipeFunc::apply).save(consumer, HexAPI.MOD_ID + ":dynamic" + name.getPath());
    }

    protected Criterion<InventoryChangeTrigger.TriggerInstance> hasItem(ItemLike itemLike) {
        return paucalInventoryTrigger(ItemPredicate.Builder.item().of(this.items, itemLike).build());
    }

    protected Criterion<InventoryChangeTrigger.TriggerInstance> hasItem(TagKey<Item> itemLike) {
        return paucalInventoryTrigger(ItemPredicate.Builder.item().of(this.items, itemLike).build());
    }

    /**
     * Prefixed with {@code paucal} to avoid collisions when Forge ATs {@link RecipeProvider#inventoryTrigger}.
     */
    protected static Criterion<InventoryChangeTrigger.TriggerInstance> paucalInventoryTrigger(ItemPredicate... $$0) {

        return new Criterion<>(
                CriteriaTriggers.INVENTORY_CHANGED,
                new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of($$0))
        );
    }

    // ================================= From PAUCAL 1.20

    protected ShapedRecipeBuilder ring(RecipeCategory category, ItemLike out, int count, Ingredient outer, @Nullable Ingredient inner) {
        return ringCornered(category, out, count, outer, outer, inner);
    }

    protected ShapedRecipeBuilder ring(RecipeCategory category, ItemLike out, int count, ItemLike outer, @Nullable ItemLike inner) {
        return ring(category, out, count, Ingredient.of(outer), ingredientOf(inner));
    }

    protected ShapedRecipeBuilder ring(RecipeCategory category, ItemLike out, int count, TagKey<Item> outer, @Nullable TagKey<Item> inner) {
        return ring(category, out, count, Ingredient.of(this.items.getOrThrow(outer)), ingredientOf(inner));
    }

    protected ShapedRecipeBuilder ringCornerless(RecipeCategory category, ItemLike out, int count, Ingredient outer,
                                                 @Nullable Ingredient inner) {
        return ringCornered(category, out, count, outer, null, inner);
    }

    protected ShapedRecipeBuilder ringCornerless(RecipeCategory category, ItemLike out, int count, ItemLike outer, @Nullable ItemLike inner) {
        return ringCornerless(category, out, count, Ingredient.of(outer), ingredientOf(inner));
    }

    protected ShapedRecipeBuilder ringAll(RecipeCategory category, ItemLike out, int count, Ingredient outer, @Nullable Ingredient inner) {
        return ringCornered(category, out, count, outer, outer, inner);
    }

    protected ShapedRecipeBuilder ringAll(RecipeCategory category, ItemLike out, int count, ItemLike outer, @Nullable ItemLike inner) {
        return ringAll(category, out, count, Ingredient.of(outer), ingredientOf(inner));
    }

    protected ShapedRecipeBuilder ringCornered(RecipeCategory category, ItemLike out, int count, @Nullable Ingredient cardinal,
                                               @Nullable Ingredient diagonal, @Nullable Ingredient inner) {
        if (cardinal == null && diagonal == null && inner == null) {
            throw new IllegalArgumentException("at least one ingredient must be non-null");
        }
        if (inner != null && cardinal == null && diagonal == null) {
            throw new IllegalArgumentException("if inner is non-null, either cardinal or diagonal must not be");
        }

        var builder = this.shaped(category, out, count);
        var C = ' ';
        if (cardinal != null) {
            builder.define('C', cardinal);
            C = 'C';
        }
        var D = ' ';
        if (diagonal != null) {
            builder.define('D', diagonal);
            D = 'D';
        }
        var I = ' ';
        if (inner != null) {
            builder.define('I', inner);
            I = 'I';
        }

        builder
                .pattern(String.format("%c%c%c", D, C, D))
                .pattern(String.format("%c%c%c", C, I, C))
                .pattern(String.format("%c%c%c", D, C, D));

        return builder;
    }

    protected ShapedRecipeBuilder stack(RecipeCategory category, ItemLike out, int count, Ingredient top, Ingredient bottom) {
        return this.shaped(category, out, count)
                .define('T', top)
                .define('B', bottom)
                .pattern("T")
                .pattern("B");
    }

    protected ShapedRecipeBuilder stack(RecipeCategory category, ItemLike out, int count, ItemLike top, ItemLike bottom) {
        return stack(category, out, count, Ingredient.of(top), Ingredient.of(bottom));
    }

    protected ShapedRecipeBuilder stack(RecipeCategory category, ItemLike out, int count, TagKey<Item> top, TagKey<Item> bottom) {
        return stack(category, out, count, Ingredient.of(this.items.getOrThrow(top)), Ingredient.of(this.items.getOrThrow(bottom)));
    }


    protected ShapedRecipeBuilder stick(RecipeCategory category, ItemLike out, int count, Ingredient input) {
        return stack(category, out, count, input, input);
    }

    protected ShapedRecipeBuilder stick(RecipeCategory category, ItemLike out, int count, ItemLike input) {
        return stick(category, out, count, Ingredient.of(input));
    }

    protected ShapedRecipeBuilder stick(RecipeCategory category, ItemLike out, int count, TagKey<Item> input) {
        return stick(category, out, count, Ingredient.of(this.items.getOrThrow(input)));
    }

    /**
     * @param largeSize True for a 3x3, false for a 2x2
     */
    protected void packing(RecipeCategory category, ItemLike free, ItemLike compressed, String freeName, boolean largeSize, RecipeOutput recipes) {
        var pack = this.shaped(category, compressed)
                .define('X', free);
        if (largeSize) {
            pack.pattern("XXX").pattern("XXX").pattern("XXX");
        } else {
            pack.pattern("XX").pattern("XX");
        }
        pack.unlockedBy("has_item", hasItem(free)).save(recipes, ResourceKey.create(Registries.RECIPE, modLoc(freeName + "_packing")));

        this.shapeless(category, free, largeSize ? 9 : 4)
                .requires(compressed)
                .unlockedBy("has_item", hasItem(free)).save(recipes, ResourceKey.create(Registries.RECIPE, modLoc(freeName + "_unpacking")));
    }

    @Nullable
    protected Ingredient ingredientOf(@Nullable ItemLike item) {
        return item == null ? null : Ingredient.of(item);
    }

    @Nullable
    protected Ingredient ingredientOf(@Nullable TagKey<Item> item) {
        return item == null ? null : Ingredient.of(this.items.getOrThrow(item));
    }

    private void stoneSet(RecipeOutput recipes, Item base, Item bricks, Item smallBricks, Item tiles, @Nullable Item pillar) {
        String smallBricksPath = BuiltInRegistries.ITEM.getKey(smallBricks).getPath();
        String bricksPath = BuiltInRegistries.ITEM.getKey(bricks).getPath();
        // Bricks from base block
        this.shaped(RecipeCategory.BUILDING_BLOCKS, bricks, 4)
                .define('#', base)
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_item", hasItem(base))
                .save(recipes);

        // Bricks from small bricks
        this.shapeless(RecipeCategory.BUILDING_BLOCKS, bricks)
                .requires(smallBricks)
                .unlockedBy("has_item", hasItem(base))
                .save(recipes, ResourceKey.create(Registries.RECIPE, modLoc(bricksPath + "_from_" + smallBricksPath)));

        // Small bricks from bricks
        this.shapeless(RecipeCategory.BUILDING_BLOCKS, smallBricks)
                .requires(bricks)
                .unlockedBy("has_item", hasItem(base))
                .save(recipes, ResourceKey.create(Registries.RECIPE, modLoc(smallBricksPath + "_from_" + bricksPath)));

        // Tiles from bricks
        this.shaped(RecipeCategory.BUILDING_BLOCKS, tiles, 4)
                .define('#', bricks)
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_item", hasItem(base))
                .save(recipes);

        // Pillar from base block
        if (pillar != null) {
            this.shaped(RecipeCategory.BUILDING_BLOCKS, pillar, 2)
                    .define('#', base)
                    .pattern("#")
                    .pattern("#")
                    .unlockedBy("has_item", hasItem(base))
                    .save(recipes);
        }
    }

    private void stoneCutterFromTag(RecipeOutput recipes, TagKey<Item> tagKey, Item ...results) {
        for (Item result : results) {
            var resultPath = BuiltInRegistries.ITEM.getKey(result).getPath();
            SingleItemRecipeBuilder.stonecutting(Ingredient.of(this.items.getOrThrow(tagKey)), RecipeCategory.BUILDING_BLOCKS, result)
                    .unlockedBy("has_item", hasItem(tagKey))
                    .save(recipes, ResourceKey.create(Registries.RECIPE, modLoc("stonecutting/" + resultPath)));
        }
    }
    public static class Runner extends RecipeProvider.Runner {
        private final Function<RecipeBuilder, IXplatConditionsBuilder> conditions;
        private final IXplatIngredients INGREDIENTS;
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture, IXplatIngredients ingredients, Function<RecipeBuilder, IXplatConditionsBuilder> conditions) {
            super(packOutput, completableFuture);
            this.conditions = conditions;
            this.INGREDIENTS = ingredients;
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new HexplatRecipes(provider, recipeOutput, INGREDIENTS, conditions);
        }

        @Override
        public @NotNull String getName() {
            return "Hexcasting Recipes";
        }
    }
}
