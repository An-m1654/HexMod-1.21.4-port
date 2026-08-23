package at.petrak.hexcasting.fabric.datagen;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.client.IotaStorageColorizerTintSource;
import at.petrak.hexcasting.common.blocks.BlockQuenchedAllay;
import at.petrak.hexcasting.common.blocks.akashic.BlockAkashicBookshelf;
import at.petrak.hexcasting.common.blocks.circles.directrix.BlockBooleanDirectrix;
import at.petrak.hexcasting.common.items.pigment.ItemPridePigment;
import at.petrak.hexcasting.common.lib.HexBlocks;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.xplat.overrides.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

public class HexModels extends FabricModelProvider {
    private static final String[] PHIAL_SIZES = {"small", "medium", "large", "larger", "largest"};
    private static final Integer[] PACKAGED_SPELL_HANDHELD_VARIANTS = {5};

    public HexModels(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
//        buildFourVariantGaslight(getPath(HexBlocks.QUENCHED_ALLAY), "block/quenched_allay", (name, path) ->
//            cubeAll(path.getPath(), path));
        buildFourVariantGaslight(blockStateModelGenerator.itemModelOutput, HexBlocks.QUENCHED_ALLAY, "block/quenched_allay", path ->
            ModelTemplates.CUBE_ALL.create(path, TextureMapping.cube(path), blockStateModelGenerator.modelOutput)
        );
//        buildFourVariantGaslight(getPath(HexBlocks.QUENCHED_ALLAY_TILES), "block/deco/quenched_allay_tiles", (name, path) ->
//            cubeAll(path.getPath(), path));
        buildFourVariantGaslight(blockStateModelGenerator.itemModelOutput, HexBlocks.QUENCHED_ALLAY_TILES, "block/deco/quenched_allay_tiles", path ->
            ModelTemplates.CUBE_ALL.create(path, TextureMapping.cube(path), blockStateModelGenerator.modelOutput)
        );
//        buildFourVariantGaslight(getPath(HexBlocks.QUENCHED_ALLAY_BRICKS), "block/deco/quenched_allay_bricks", (name, path) ->
//            cubeAll(path.getPath(), path));
        buildFourVariantGaslight(blockStateModelGenerator.itemModelOutput, HexBlocks.QUENCHED_ALLAY_BRICKS, "block/deco/quenched_allay_bricks", path ->
            ModelTemplates.CUBE_ALL.create(path, TextureMapping.cube(path), blockStateModelGenerator.modelOutput)
        );
//        buildFourVariantGaslight(getPath(HexBlocks.QUENCHED_ALLAY_BRICKS_SMALL), "block/deco/quenched_allay_bricks_small", (name, path) ->
//            cubeAll(path.getPath(), path));
        buildFourVariantGaslight(blockStateModelGenerator.itemModelOutput, HexBlocks.QUENCHED_ALLAY_BRICKS_SMALL, "block/deco/quenched_allay_bricks_small", path ->
            ModelTemplates.CUBE_ALL.create(path, TextureMapping.cube(path), blockStateModelGenerator.modelOutput)
        );

//-------------------------------- ACTUAL START OF THE BLOCK STATES FILE IN THE ORIGINAL SOURCE CODE -------------------------------------------
        blockStateModelGenerator.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(HexBlocks.SLATE)
                .with(
                    PropertyDispatch.properties(
                        BlockCircleComponent.ENERGIZED,
                        BlockStateProperties.HORIZONTAL_FACING,
                        BlockStateProperties.ATTACH_FACE
                    )
                    .generate((energized, facing, attachFace) -> {
                        int rotationX = 0;
                        int rotationY = 0;
                        switch (attachFace) {
                            case CEILING -> rotationX = 180;
                            case WALL -> {
                                rotationX = 90;
                                rotationY = facing.getOpposite().get2DDataValue() * 90;
                            }
                        }
                        Variant returnValue = Variant.variant()
                            .with(VariantProperties.MODEL, modLoc("block/slate"))
                            .with(VariantProperties.UV_LOCK, true);
                        if (rotationX != 0) {
                            returnValue
                            .with(VariantProperties.X_ROT, VariantProperties.Rotation.values()[rotationX/90]);
                        }
                        if (rotationY != 0) {
                            returnValue
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.values()[rotationY/90]);
                        }
                        return returnValue;
                    })
                )
        );

        impetus(blockStateModelGenerator, HexBlocks.IMPETUS_EMPTY, "impetus/empty", "empty", false);
        impetus(blockStateModelGenerator, HexBlocks.IMPETUS_RIGHTCLICK, "impetus/rightclick", "rightclick", true);
        impetus(blockStateModelGenerator, HexBlocks.IMPETUS_LOOK, "impetus/look", "look", true);
        impetus(blockStateModelGenerator, HexBlocks.IMPETUS_REDSTONE, "impetus/redstone", "redstone", true);
        doAllTheDirectrices(blockStateModelGenerator);

        var innerTextureSlot = TextureSlot.create("inner");
        var outerTextureSlot = TextureSlot.create("outer");
        var akashicRecordModelTextureMapping = new TextureMapping()
            .put(innerTextureSlot, modLoc("block/akashic_ligature"))
            .put(outerTextureSlot, modLoc("block/akashic_record"))
            .put(TextureSlot.PARTICLE, modLoc("block/akashic_ligature"));
        blockStateModelGenerator.modelOutput.accept(
            modLoc("block/akashic_record"),
            () -> {
                Map<TextureSlot, ResourceLocation> map = Streams.concat(ImmutableSet.of(innerTextureSlot, outerTextureSlot, TextureSlot.PARTICLE).stream(), akashicRecordModelTextureMapping.getForced()).collect(ImmutableMap.toImmutableMap(Function.identity(), akashicRecordModelTextureMapping::get));
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("parent", ResourceLocation.withDefaultNamespace("block/block").toString());

                jsonObject.addProperty("render_type", "minecraft:translucent");

                if (!map.isEmpty()) {
                    JsonObject jsonObject2 = new JsonObject();
                    map.forEach((textureSlot, resourceLocationx) -> jsonObject2.addProperty(textureSlot.getId(), resourceLocationx.toString()));
                    jsonObject.add("textures", jsonObject2);
                }

                JsonArray elements = new JsonArray();

                JsonObject outerTexture = new JsonObject();
                JsonObject outerTextureFace = new JsonObject();
                for (String item : List.of("down", "east", "north", "south", "up", "west")) {
                    JsonObject itemObject = new JsonObject();
                    itemObject.addProperty("cullface", item);
                    itemObject.addProperty("texture", "#outer");
                    outerTextureFace.add(item, itemObject);
                }
                outerTexture.add("faces", outerTextureFace);

                JsonArray outerTextureFrom = new JsonArray();
                outerTextureFrom.add(0);
                outerTextureFrom.add(0);
                outerTextureFrom.add(0);
                outerTexture.add("from", outerTextureFrom);

                JsonArray outerTextureTo = new JsonArray();
                outerTextureTo.add(16);
                outerTextureTo.add(16);
                outerTextureTo.add(16);
                outerTexture.add("to", outerTextureTo);

                elements.add(outerTexture);

                JsonObject innerTexture = new JsonObject();
                JsonObject innerTextureFace = new JsonObject();
                for (String item : List.of("down", "east", "north", "south", "up", "west")) {
                    JsonObject itemObject = new JsonObject();
                    itemObject.addProperty("rotation", 180);
                    itemObject.addProperty("texture", "#inner");
                    innerTextureFace.add(item, itemObject);
                }
                innerTexture.add("faces", innerTextureFace);

                JsonArray innerTextureFrom = new JsonArray();
                innerTextureFrom.add(15.75);
                innerTextureFrom.add(15.75);
                innerTextureFrom.add(15.75);
                innerTexture.add("from", innerTextureFrom);

                JsonArray innerTextureTo = new JsonArray();
                innerTextureTo.add(0.25);
                innerTextureTo.add(0.25);
                innerTextureTo.add(0.25);
                innerTexture.add("to", innerTextureTo);

                elements.add(innerTexture);
                jsonObject.add("elements", elements);
                return jsonObject;
            }
        );

        blockStateModelGenerator.blockStateOutput.accept(
            new dummyVariantGenerator(HexBlocks.AKASHIC_RECORD, modLoc("block/akashic_record"))
        );

        new ModelTemplate(Optional.of(modLoc("block/akashic_record")), Optional.empty())
            .create(ModelLocationUtils.getModelLocation(HexBlocks.AKASHIC_RECORD.asItem()), new TextureMapping(), blockStateModelGenerator.modelOutput);

        ModelTemplates.CUBE_ALL.create(HexBlocks.AKASHIC_LIGATURE, TextureMapping.cube(HexBlocks.AKASHIC_LIGATURE), blockStateModelGenerator.modelOutput);

        blockStateModelGenerator.blockStateOutput.accept(
            new dummyVariantGenerator(HexBlocks.AKASHIC_LIGATURE, modLoc("block/akashic_ligature"))
        );

        new ModelTemplate(Optional.of(modLoc("block/akashic_ligature")), Optional.empty())
            .create(ModelLocationUtils.getModelLocation(HexBlocks.AKASHIC_LIGATURE.asItem()), new TextureMapping(), blockStateModelGenerator.modelOutput);

        var topBottomTextureSlot = TextureSlot.create("top_bottom");
        var akashicBookshelfModelTextureMapping = new TextureMapping()
            .put(TextureSlot.FRONT, modLoc("block/akashic_bookshelf"))
            .put(TextureSlot.SIDE, modLoc("block/akashic_bookshelf_horiz"))
            .put(topBottomTextureSlot, modLoc("block/akashic_bookshelf_vert"))
            .put(TextureSlot.PARTICLE, modLoc("block/akashic_bookshelf_vert"));
        blockStateModelGenerator.modelOutput.accept(
            modLoc("block/akashic_bookshelf"),
            () -> {
                Map<TextureSlot, ResourceLocation> map = Streams.concat(ImmutableSet.of(TextureSlot.FRONT, TextureSlot.SIDE, topBottomTextureSlot, TextureSlot.PARTICLE).stream(), akashicBookshelfModelTextureMapping.getForced()).collect(ImmutableMap.toImmutableMap(Function.identity(), akashicBookshelfModelTextureMapping::get));
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("render_type", "minecraft:cutout");

                if (!map.isEmpty()) {
                    JsonObject jsonObject2 = new JsonObject();
                    map.forEach((textureSlot, resourceLocationx) -> jsonObject2.addProperty(textureSlot.getId(), resourceLocationx.toString()));
                    jsonObject.add("textures", jsonObject2);
                }

                JsonArray elements = new JsonArray();

                JsonObject baseTexture = new JsonObject();
                JsonObject baseTextureFace = new JsonObject();
                Map.of("down", "#top_bottom", "east", "#side", "north", "#front", "south", "#side", "up", "#top_bottom", "west", "#side").forEach((key, value) -> {
                    JsonObject itemObject = new JsonObject();
                    itemObject.addProperty("cullface", key);
                    itemObject.addProperty("texture", value);
                    baseTextureFace.add(key, itemObject);
                });
                baseTexture.add("faces", baseTextureFace);

                JsonArray baseTextureFrom = new JsonArray();
                baseTextureFrom.add(0);
                baseTextureFrom.add(0);
                baseTextureFrom.add(0);
                baseTexture.add("from", baseTextureFrom);

                JsonArray baseTextureTo = new JsonArray();
                baseTextureTo.add(16);
                baseTextureTo.add(16);
                baseTextureTo.add(16);
                baseTexture.add("to", baseTextureTo);

                elements.add(baseTexture);

                JsonObject overlayTexture = new JsonObject();
                JsonObject overlayTextureFace = new JsonObject();
                JsonObject overlayObject = new JsonObject();
                overlayObject.addProperty("cullface", "north");
                overlayObject.addProperty("texture", "#overlay");
                overlayObject.addProperty("tintindex", 0);
                overlayTextureFace.add("north", overlayObject);
                overlayTexture.add("faces", overlayTextureFace);

                JsonArray overlayTextureFrom = new JsonArray();
                overlayTextureFrom.add(0);
                overlayTextureFrom.add(0);
                overlayTextureFrom.add(0);
                overlayTexture.add("from", overlayTextureFrom);

                JsonArray overlayTextureTo = new JsonArray();
                overlayTextureTo.add(16);
                overlayTextureTo.add(16);
                overlayTextureTo.add(16);
                overlayTexture.add("to", overlayTextureTo);

                elements.add(overlayTexture);
                jsonObject.add("elements", elements);
                return jsonObject;
            }
        );

        var overlayTextureSlot = TextureSlot.create("overlay");
        for (int i = 1; i <= 4; i++) {
            new ModelTemplate(Optional.of(modLoc("block/akashic_bookshelf")), Optional.empty(), overlayTextureSlot)
                .create(modLoc("block/akashic_bookshelf_" + i), new TextureMapping().put(overlayTextureSlot, modLoc("block/akashic_bookshelf_overlay_" + i)), blockStateModelGenerator.modelOutput);
        }
        ModelTemplates.CUBE_ORIENTABLE.create(
            modLoc("block/akashic_bookshelf_empty"),
            new TextureMapping()
                .put(TextureSlot.SIDE, modLoc("block/akashic_bookshelf_horiz"))
                .put(TextureSlot.FRONT, modLoc("block/akashic_bookshelf"))
                .put(TextureSlot.TOP, modLoc("block/akashic_bookshelf_vert")),
            blockStateModelGenerator.modelOutput
        );

        blockStateModelGenerator.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(HexBlocks.AKASHIC_BOOKSHELF)
                .with(
                    PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockAkashicBookshelf.HAS_BOOKS)
                        .generateList((dir, hasBooks) -> {
                           List<Variant> builders = new ArrayList<>();
                            if (hasBooks) {
                                for (int i = 1; i <= 4; i++) {
                                    var builder = Variant.variant();
//                                    var model = models().withExistingParent("akashic_bookshelf_" + i,
//                                            modLoc("block/akashic_bookshelf"))
//                                        .texture("overlay", modLoc("block/akashic_bookshelf_overlay_" + i));
                                    var model = modLoc("block/akashic_bookshelf_" + i);
//                                    builder.modelFile(model)
//                                        .rotationY(dir.getOpposite().get2DDataValue() * 90)
//                                        .uvLock(true);
//                                    if (i < 4) {
//                                        builder = builder.nextModel();
//                                    }
                                    builder
                                        .with(VariantProperties.MODEL, model)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.values()[((dir.getOpposite().get2DDataValue() * 90) / 90 + 4) % 4])
                                        .with(VariantProperties.UV_LOCK, true);
                                    builders.add(builder);
                                }
                            } else {
                                var builder = Variant.variant();
                                var model = modLoc("block/akashic_bookshelf_empty");

                                if (dir == Direction.NORTH) {
                                    new ModelTemplate(Optional.of(model), Optional.empty())
                                        .create(HexBlocks.AKASHIC_BOOKSHELF.asItem(), new TextureMapping(), blockStateModelGenerator.modelOutput);
                                }

                                builder
                                    .with(VariantProperties.MODEL, model)
                                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.values()[((dir.getOpposite().get2DDataValue() * 90) / 90 + 4) % 4])
                                    .with(VariantProperties.UV_LOCK, true);
                                builders.add(builder);
                            }
                            return builders;
                        })
                )
        );


        blockAndItem(blockStateModelGenerator, HexBlocks.SLATE_BLOCK, ModelTemplates.CUBE_ALL.create(HexBlocks.SLATE_BLOCK, TextureMapping.cube(modLoc("block/slate_block")), blockStateModelGenerator.modelOutput));
        blockAndItem(blockStateModelGenerator, HexBlocks.SLATE_TILES, ModelTemplates.CUBE_ALL.create(HexBlocks.SLATE_TILES, TextureMapping.cube(modLoc("block/deco/slate_tiles")), blockStateModelGenerator.modelOutput));
        blockAndItem(blockStateModelGenerator, HexBlocks.SLATE_BRICKS, ModelTemplates.CUBE_ALL.create(HexBlocks.SLATE_BRICKS, TextureMapping.cube(modLoc("block/deco/slate_bricks")), blockStateModelGenerator.modelOutput));
        blockAndItem(blockStateModelGenerator, HexBlocks.SLATE_BRICKS_SMALL, ModelTemplates.CUBE_ALL.create(HexBlocks.SLATE_BRICKS_SMALL, TextureMapping.cube(modLoc("block/deco/slate_bricks_small")), blockStateModelGenerator.modelOutput));
        axisBlock(blockStateModelGenerator, HexBlocks.SLATE_PILLAR, modLoc("block/deco/slate_pillar"));
        blockAndItem(blockStateModelGenerator, HexBlocks.AMETHYST_DUST_BLOCK,
            new ModelTemplate(Optional.of(modLoc("block/cube_half_mirrored")), Optional.empty(), TextureSlot.ALL)
                .create(
                    HexBlocks.AMETHYST_DUST_BLOCK,
                    TextureMapping.cube(modLoc("block/amethyst_dust_block")),
                    blockStateModelGenerator.modelOutput
                )
            );
        blockAndItem(blockStateModelGenerator, HexBlocks.AMETHYST_TILES, ModelTemplates.CUBE_ALL.create(modLoc("block/deco/amethyst_tiles"), TextureMapping.cube(modLoc("block/deco/amethyst_tiles")), blockStateModelGenerator.modelOutput));
        blockAndItem(blockStateModelGenerator, HexBlocks.AMETHYST_BRICKS, ModelTemplates.CUBE_ALL.create(modLoc("block/deco/amethyst_bricks"), TextureMapping.cube(modLoc("block/deco/amethyst_bricks")), blockStateModelGenerator.modelOutput));
        blockAndItem(blockStateModelGenerator, HexBlocks.AMETHYST_BRICKS_SMALL, ModelTemplates.CUBE_ALL.create(modLoc("block/deco/amethyst_bricks_small"), TextureMapping.cube(modLoc("block/deco/amethyst_bricks_small")), blockStateModelGenerator.modelOutput));
        directionalBlock(blockStateModelGenerator, HexBlocks.AMETHYST_PILLAR,
            ModelTemplates.CUBE_BOTTOM_TOP.create(modLoc("block/deco/amethyst_pillar"),
                new TextureMapping()
                    .put(TextureSlot.SIDE, modLoc("block/deco/amethyst_pillar_side"))
                    .put(TextureSlot.BOTTOM, modLoc("block/deco/amethyst_pillar_bottom"))
                    .put(TextureSlot.TOP, modLoc("block/deco/amethyst_pillar_top")),
                blockStateModelGenerator.modelOutput
            )
        );
        blockAndItem(blockStateModelGenerator, HexBlocks.SLATE_AMETHYST_TILES, ModelTemplates.CUBE_ALL.create(modLoc("block/deco/slate_amethyst_tiles"), TextureMapping.cube(modLoc("block/deco/slate_amethyst_tiles")), blockStateModelGenerator.modelOutput));

        blockStateModelGenerator.blockStateOutput.accept(
            new dummyArrayVariantGenerator(HexBlocks.SLATE_AMETHYST_BRICKS,
                new ResourceLocation[]{
                    ModelTemplates.CUBE_ALL.create(HexAPI.modLoc("block/deco/slate_amethyst_bricks_0"), TextureMapping.cube(HexAPI.modLoc("block/deco/slate_amethyst_bricks_0")), blockStateModelGenerator.modelOutput),
                    ModelTemplates.CUBE_ALL.create(HexAPI.modLoc("block/deco/slate_amethyst_bricks_1"), TextureMapping.cube(HexAPI.modLoc("block/deco/slate_amethyst_bricks_1")), blockStateModelGenerator.modelOutput),
                    ModelTemplates.CUBE_ALL.create(HexAPI.modLoc("block/deco/slate_amethyst_bricks_2"), TextureMapping.cube(HexAPI.modLoc("block/deco/slate_amethyst_bricks_2")), blockStateModelGenerator.modelOutput)
                },
                new Optional[]{}
            )
        );

        var SlateAmethystBricksItemLocation = new ModelTemplate(
            Optional.of(modLoc("block/deco/slate_amethyst_bricks_0")),
            Optional.empty()
        )
            .create(HexBlocks.SLATE_AMETHYST_BRICKS.asItem(), new TextureMapping(), blockStateModelGenerator.modelOutput);

        blockStateModelGenerator.itemModelOutput.accept(
            HexBlocks.SLATE_AMETHYST_BRICKS.asItem(),
            ItemModelUtils.plainModel(SlateAmethystBricksItemLocation)
        );

        blockStateModelGenerator.blockStateOutput.accept(
            new dummyArrayVariantGenerator(HexBlocks.SLATE_AMETHYST_BRICKS_SMALL,
                new ResourceLocation[]{
                    ModelTemplates.CUBE_ALL.create(HexAPI.modLoc("block/deco/slate_amethyst_bricks_small_0"), TextureMapping.cube(HexAPI.modLoc("block/deco/slate_amethyst_bricks_small_0")), blockStateModelGenerator.modelOutput),
                    ModelTemplates.CUBE_ALL.create(HexAPI.modLoc("block/deco/slate_amethyst_bricks_small_1"), TextureMapping.cube(HexAPI.modLoc("block/deco/slate_amethyst_bricks_small_1")), blockStateModelGenerator.modelOutput),
                    ModelTemplates.CUBE_ALL.create(HexAPI.modLoc("block/deco/slate_amethyst_bricks_small_2"), TextureMapping.cube(HexAPI.modLoc("block/deco/slate_amethyst_bricks_small_2")), blockStateModelGenerator.modelOutput)
                },
                new Optional[]{}
            )
        );

        var SlateAmethystBricksSmallItemLocation = new ModelTemplate(Optional.of(modLoc("block/deco/slate_amethyst_bricks_small_0")),
            Optional.empty()
        )
            .create(HexBlocks.SLATE_AMETHYST_BRICKS_SMALL.asItem(), new TextureMapping(), blockStateModelGenerator.modelOutput);

        blockStateModelGenerator.itemModelOutput.accept(
            HexBlocks.SLATE_AMETHYST_BRICKS_SMALL.asItem(),
            ItemModelUtils.plainModel(SlateAmethystBricksSmallItemLocation)
        );

        axisBlock(blockStateModelGenerator, HexBlocks.SLATE_AMETHYST_PILLAR, modLoc("block/deco/slate_amethyst_pillar"));
        blockAndItem(blockStateModelGenerator, HexBlocks.SCROLL_PAPER, ModelTemplates.CUBE_ALL.create(HexBlocks.SCROLL_PAPER, TextureMapping.cube(modLoc("block/scroll_paper")), blockStateModelGenerator.modelOutput));
        blockAndItem(blockStateModelGenerator, HexBlocks.ANCIENT_SCROLL_PAPER, ModelTemplates.CUBE_ALL.create(HexBlocks.ANCIENT_SCROLL_PAPER, TextureMapping.cube(modLoc("block/ancient_scroll_paper")), blockStateModelGenerator.modelOutput));

        blockAndItem(blockStateModelGenerator, HexBlocks.SCROLL_PAPER_LANTERN, ModelTemplates.CUBE_BOTTOM_TOP.create(modLoc("block/scroll_paper_lantern"),
            new TextureMapping()
                .put(TextureSlot.SIDE, modLoc("block/scroll_paper_lantern_side"))
                .put(TextureSlot.BOTTOM, modLoc("block/scroll_paper_lantern_bottom"))
                .put(TextureSlot.TOP, modLoc("block/scroll_paper_lantern_top")),
            blockStateModelGenerator.modelOutput
        ));

        blockAndItem(blockStateModelGenerator, HexBlocks.ANCIENT_SCROLL_PAPER_LANTERN, ModelTemplates.CUBE_BOTTOM_TOP.create(modLoc("block/ancient_scroll_paper_lantern"),
            new TextureMapping()
                .put(TextureSlot.SIDE, modLoc("block/ancient_scroll_paper_lantern_side"))
                .put(TextureSlot.BOTTOM, modLoc("block/ancient_scroll_paper_lantern_bottom"))
                .put(TextureSlot.TOP, modLoc("block/ancient_scroll_paper_lantern_top")),
            blockStateModelGenerator.modelOutput
        ));

        axisBlock(blockStateModelGenerator, HexBlocks.EDIFIED_LOG, modLoc("block/edified_log"), modLoc("block/edified_log_top"));
        axisBlock(blockStateModelGenerator, HexBlocks.EDIFIED_LOG_AMETHYST, modLoc("block/deco/edified_log_amethyst"), modLoc("block/edified_log_top"));
        axisBlock(blockStateModelGenerator, HexBlocks.EDIFIED_LOG_AVENTURINE, modLoc("block/deco/edified_log_aventurine"), modLoc("block/edified_log_top"));
        axisBlock(blockStateModelGenerator, HexBlocks.EDIFIED_LOG_CITRINE, modLoc("block/deco/edified_log_citrine"), modLoc("block/edified_log_top"));
        axisBlock(blockStateModelGenerator, HexBlocks.EDIFIED_LOG_PURPLE, modLoc("block/deco/edified_log_purple"), modLoc("block/edified_log_top"));
        axisBlock(blockStateModelGenerator, HexBlocks.STRIPPED_EDIFIED_LOG, modLoc("block/stripped_edified_log"), modLoc("block/stripped_edified_log_top"));
        axisBlock(blockStateModelGenerator, HexBlocks.EDIFIED_WOOD, modLoc("block/edified_log"), modLoc("block/edified_log"));
        axisBlock(blockStateModelGenerator, HexBlocks.STRIPPED_EDIFIED_WOOD, modLoc("block/stripped_edified_log"),
            modLoc("block/stripped_edified_log"));

        blockAndItem(blockStateModelGenerator, HexBlocks.EDIFIED_PANEL, ModelTemplates.CUBE_ALL.create(modLoc("block/edified_panel"), TextureMapping.cube(modLoc("block/edified_panel")), blockStateModelGenerator.modelOutput));
        blockAndItem(blockStateModelGenerator, HexBlocks.EDIFIED_TILE, ModelTemplates.CUBE_ALL.create(modLoc("block/edified_tile"), TextureMapping.cube(modLoc("block/edified_panel")), blockStateModelGenerator.modelOutput));

        ResourceLocation leavesParent = ResourceLocation.withDefaultNamespace("block/leaves");
        blockStateModelGenerator.modelOutput.accept(
            modLoc("block/amethyst_edified_leaves"),
            () -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("parent", leavesParent.toString());
                jsonObject.addProperty("render_type", "minecraft:cutout_mipped");

                JsonObject textures = new JsonObject();
                textures.addProperty("all", modLoc("block/amethyst_edified_leaves").toString());
                jsonObject.add("textures", textures);
                return jsonObject;
            }
        );
        blockAndItem(
            blockStateModelGenerator,
            HexBlocks.AMETHYST_EDIFIED_LEAVES,
            modLoc("block/amethyst_edified_leaves")
        );

        blockStateModelGenerator.modelOutput.accept(
            modLoc("block/aventurine_edified_leaves"),
            () -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("parent", leavesParent.toString());
                jsonObject.addProperty("render_type", "minecraft:cutout_mipped");

                JsonObject textures = new JsonObject();
                textures.addProperty("all", modLoc("block/aventurine_edified_leaves").toString());
                jsonObject.add("textures", textures);
                return jsonObject;
            }
        );
        blockAndItem(
            blockStateModelGenerator,
            HexBlocks.AVENTURINE_EDIFIED_LEAVES,
            modLoc("block/aventurine_edified_leaves")
        );

        blockStateModelGenerator.modelOutput.accept(
            modLoc("block/citrine_edified_leaves"),
            () -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("parent", leavesParent.toString());
                jsonObject.addProperty("render_type", "minecraft:cutout_mipped");

                JsonObject textures = new JsonObject();
                textures.addProperty("all", modLoc("block/citrine_edified_leaves").toString());
                jsonObject.add("textures", textures);
                return jsonObject;
            }
        );
        blockAndItem(
            blockStateModelGenerator,
            HexBlocks.CITRINE_EDIFIED_LEAVES,
            modLoc("block/citrine_edified_leaves")
        );

        for (String suffix : new String[]{"_bottom_left", "_bottom_left_open", "_bottom_right", "_bottom_right_open", "_top_left", "_top_left_open", "_top_right", "_top_right_open"}) {
            blockStateModelGenerator.modelOutput.accept(
                ModelLocationUtils.getModelLocation(HexBlocks.EDIFIED_DOOR).withSuffix(suffix),
                () -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("parent", ResourceLocation.withDefaultNamespace("block/door" + suffix).toString());
                    jsonObject.addProperty("render_type", "minecraft:cutout");

                    JsonObject textures = new JsonObject();
                    textures.addProperty("bottom", modLoc("block/edified_door_lower").toString());
                    textures.addProperty("top", modLoc("block" + "/edified_door_upper").toString());
                    jsonObject.add("textures", textures);
                    return jsonObject;
                }
            );
        }
        blockStateModelGenerator.blockStateOutput.accept(
            BlockModelGenerators.createDoor(HexBlocks.EDIFIED_DOOR, modLoc("block/edified_door_bottom_left"), modLoc("block/edified_door_bottom_left_open"), modLoc("block/edified_door_bottom_right"), modLoc("block/edified_door_bottom_right_open"), modLoc("block/edified_door_top_left"), modLoc("block/edified_door_top_left_open"), modLoc("block/edified_door_top_right"), modLoc("block/edified_door_top_right_open"))
        );

        var edifiedDoorItemModel = ModelTemplates.FLAT_ITEM.create(HexBlocks.EDIFIED_DOOR.asItem(), TextureMapping.layer0(modLoc("item/edified_door")), blockStateModelGenerator.modelOutput);

        blockStateModelGenerator.itemModelOutput.accept(
            HexBlocks.EDIFIED_DOOR.asItem(),
            ItemModelUtils.plainModel(edifiedDoorItemModel)
        );

        for (String suffix : new String[]{"_bottom", "_top", "_open"}) {
            blockStateModelGenerator.modelOutput.accept(
                ModelLocationUtils.getModelLocation(HexBlocks.EDIFIED_TRAPDOOR).withSuffix(suffix),
                () -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("parent", ResourceLocation.withDefaultNamespace("block/template_orientable_trapdoor" + suffix).toString());
                    jsonObject.addProperty("render_type", "minecraft:cutout");

                    JsonObject textures = new JsonObject();
                    textures.addProperty("texture", modLoc("block/edified_trapdoor").toString());
                    jsonObject.add("textures", textures);
                    return jsonObject;
                }
            );
        }
        blockStateModelGenerator.blockStateOutput.accept(
            BlockModelGenerators.createOrientableTrapdoor(HexBlocks.EDIFIED_TRAPDOOR, modLoc("block/edified_trapdoor_top"), modLoc("block/edified_trapdoor_bottom"), modLoc("block/edified_trapdoor_open"))
        );

        var edifiedTrapdoorItemModel = new ModelTemplate(Optional.of(modLoc("block/edified_trapdoor_bottom")), Optional.empty())
            .create(HexBlocks.EDIFIED_TRAPDOOR.asItem(), new TextureMapping(), blockStateModelGenerator.modelOutput);

        blockStateModelGenerator.itemModelOutput.accept(
            HexBlocks.EDIFIED_TRAPDOOR.asItem(),
            ItemModelUtils.plainModel(edifiedTrapdoorItemModel)
        );

        ResourceLocation planks1 = modLoc("block/edified_planks");
        ResourceLocation planksModel = ModelTemplates.CUBE_ALL.create(modLoc("block/edified_planks"), TextureMapping.cube(planks1), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(
            new dummyArrayVariantGenerator(HexBlocks.EDIFIED_PLANKS,
                new ResourceLocation[]{
                    planksModel,
                    ModelTemplates.CUBE_ALL.create(modLoc("block/edified_planks_2"), TextureMapping.cube(modLoc("block/edified_planks_2")), blockStateModelGenerator.modelOutput),
                    ModelTemplates.CUBE_ALL.create(modLoc("block/edified_planks_3"), TextureMapping.cube(modLoc("block/edified_planks_3")), blockStateModelGenerator.modelOutput)
                },
                new Optional[]{Optional.of(3), Optional.of(3), Optional.empty()}
            )
        );
        new ModelTemplate(Optional.of(planksModel), Optional.empty())
            .create(HexBlocks.EDIFIED_PLANKS.asItem(), new TextureMapping(), blockStateModelGenerator.modelOutput);

        blockStateModelGenerator.blockStateOutput.accept(
            BlockModelGenerators.createStairs(
                HexBlocks.EDIFIED_STAIRS,
                ModelTemplates.STAIRS_INNER.create(HexBlocks.EDIFIED_STAIRS, new TextureMapping().put(TextureSlot.BOTTOM, planks1).put(TextureSlot.TOP, planks1).put(TextureSlot.SIDE, planks1), blockStateModelGenerator.modelOutput),
                ModelTemplates.STAIRS_STRAIGHT.create(HexBlocks.EDIFIED_STAIRS, new TextureMapping().put(TextureSlot.BOTTOM, planks1).put(TextureSlot.TOP, planks1).put(TextureSlot.SIDE, planks1), blockStateModelGenerator.modelOutput),
                ModelTemplates.STAIRS_OUTER.create(HexBlocks.EDIFIED_STAIRS, new TextureMapping().put(TextureSlot.BOTTOM, planks1).put(TextureSlot.TOP, planks1).put(TextureSlot.SIDE, planks1), blockStateModelGenerator.modelOutput)
            )
        );

        new ModelTemplate(Optional.of(ModelLocationUtils.getModelLocation(HexBlocks.EDIFIED_STAIRS)), Optional.empty())
            .create(HexBlocks.EDIFIED_STAIRS.asItem(), new TextureMapping(), blockStateModelGenerator.modelOutput);

        blockStateModelGenerator.blockStateOutput.accept(
            BlockModelGenerators.createFence(
                HexBlocks.EDIFIED_FENCE,
                ModelTemplates.FENCE_POST.create(HexBlocks.EDIFIED_FENCE, TextureMapping.defaultTexture(planks1), blockStateModelGenerator.modelOutput),
                ModelTemplates.FENCE_SIDE.create(HexBlocks.EDIFIED_FENCE, TextureMapping.defaultTexture(planks1), blockStateModelGenerator.modelOutput)
            )
        );

        var edifiedFenceItemModel = ModelTemplates.FENCE_INVENTORY.create(HexBlocks.EDIFIED_FENCE, TextureMapping.defaultTexture(planks1), blockStateModelGenerator.modelOutput);

        blockStateModelGenerator.itemModelOutput.accept(
            HexBlocks.EDIFIED_FENCE.asItem(),
            ItemModelUtils.plainModel(edifiedFenceItemModel)
        );

        blockStateModelGenerator.blockStateOutput.accept(
            BlockModelGenerators.createFenceGate(
                HexBlocks.EDIFIED_FENCE_GATE,
                ModelTemplates.FENCE_GATE_OPEN.create(HexBlocks.EDIFIED_FENCE_GATE, TextureMapping.defaultTexture(planks1), blockStateModelGenerator.modelOutput),
                ModelTemplates.FENCE_GATE_CLOSED.create(HexBlocks.EDIFIED_FENCE_GATE, TextureMapping.defaultTexture(planks1), blockStateModelGenerator.modelOutput),
                ModelTemplates.FENCE_GATE_WALL_OPEN.create(HexBlocks.EDIFIED_FENCE_GATE, TextureMapping.defaultTexture(planks1), blockStateModelGenerator.modelOutput),
                ModelTemplates.FENCE_GATE_WALL_CLOSED.create(HexBlocks.EDIFIED_FENCE_GATE, TextureMapping.defaultTexture(planks1), blockStateModelGenerator.modelOutput),
                true
            )
        );
        new ModelTemplate(Optional.of(ModelLocationUtils.getModelLocation(HexBlocks.EDIFIED_FENCE_GATE)), Optional.empty())
            .create(HexBlocks.EDIFIED_FENCE_GATE.asItem(), new TextureMapping(), blockStateModelGenerator.modelOutput);

        TextureMapping slabMapping = new TextureMapping()
            .put(TextureSlot.BOTTOM, planks1)
            .put(TextureSlot.TOP, planks1)
            .put(TextureSlot.SIDE, planks1);
        blockStateModelGenerator.blockStateOutput.accept(
            BlockModelGenerators.createSlab(
                HexBlocks.EDIFIED_SLAB,
                ModelTemplates.SLAB_BOTTOM.create(HexBlocks.EDIFIED_SLAB, slabMapping, blockStateModelGenerator.modelOutput),
                ModelTemplates.SLAB_TOP.create(HexBlocks.EDIFIED_SLAB, slabMapping, blockStateModelGenerator.modelOutput),
                planksModel
            )
        );

        new ModelTemplate(Optional.of(ModelLocationUtils.getModelLocation(HexBlocks.EDIFIED_SLAB)), Optional.empty())
            .create(HexBlocks.EDIFIED_SLAB.asItem(), new TextureMapping(), blockStateModelGenerator.modelOutput);

        blockStateModelGenerator.blockStateOutput.accept(
            BlockModelGenerators.createButton(
                HexBlocks.EDIFIED_BUTTON,
                ModelTemplates.BUTTON.create(HexBlocks.EDIFIED_BUTTON, TextureMapping.defaultTexture(planks1), blockStateModelGenerator.modelOutput),
                ModelTemplates.BUTTON_PRESSED.create(HexBlocks.EDIFIED_BUTTON, TextureMapping.defaultTexture(planks1), blockStateModelGenerator.modelOutput)
            )
        );

        blockStateModelGenerator.itemModelOutput.accept(
            HexBlocks.EDIFIED_BUTTON.asItem(),
            ItemModelUtils.plainModel(
                ModelTemplates.BUTTON_INVENTORY.create(HexBlocks.EDIFIED_BUTTON.asItem(), TextureMapping.defaultTexture(planks1), blockStateModelGenerator.modelOutput)
            )
        );

        blockStateModelGenerator.blockStateOutput.accept(
            BlockModelGenerators.createPressurePlate(
                HexBlocks.EDIFIED_PRESSURE_PLATE,
                ModelTemplates.PRESSURE_PLATE_UP.create(HexBlocks.EDIFIED_PRESSURE_PLATE, TextureMapping.defaultTexture(planks1), blockStateModelGenerator.modelOutput),
                ModelTemplates.PRESSURE_PLATE_DOWN.create(HexBlocks.EDIFIED_PRESSURE_PLATE, TextureMapping.defaultTexture(planks1), blockStateModelGenerator.modelOutput)
            )
        );

        new ModelTemplate(Optional.of(ModelLocationUtils.getModelLocation(HexBlocks.EDIFIED_PRESSURE_PLATE)), Optional.empty())
            .create(HexBlocks.EDIFIED_PRESSURE_PLATE.asItem(), new TextureMapping(), blockStateModelGenerator.modelOutput);

        directionalBlock(blockStateModelGenerator, HexBlocks.SCONCE, modLoc("block/amethyst_sconce"));

//        var conjuredModel = models().getBuilder("conjured").texture("particle", mcLoc("block/amethyst_block"))
//            .renderType("cutout");
        blockStateModelGenerator.modelOutput.accept(
            modLoc("block/conjured"),
            () -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("render_type", "minecraft:cutout");

                JsonObject textures = new JsonObject();
                textures.addProperty("particle", ResourceLocation.withDefaultNamespace("block/amethyst_block").toString());
                jsonObject.add("textures", textures);
                return jsonObject;
            }
        );
        var conjuredModel = modLoc("block/conjured");
        blockStateModelGenerator.blockStateOutput.accept(
            new dummyVariantGenerator(HexBlocks.CONJURED_BLOCK, conjuredModel)
        );
        blockStateModelGenerator.blockStateOutput.accept(
            new dummyVariantGenerator(HexBlocks.CONJURED_LIGHT, conjuredModel)
        );

        blockStateModelGenerator.blockStateOutput.accept(
            new dummyVariantGenerator(
                HexBlocks.QUENCHED_ALLAY,
                ModelTemplates.CUBE_ALL.create(
                    modLoc("block/quenched_allay"),
                    TextureMapping.cube(modLoc("block/quenched_allay_0")),
                    blockStateModelGenerator.modelOutput))
        );
        blockStateModelGenerator.blockStateOutput.accept(
            new dummyVariantGenerator(
                HexBlocks.QUENCHED_ALLAY_TILES,
                ModelTemplates.CUBE_ALL.create(
                    modLoc("block/quenched_allay_tiles"),
                    TextureMapping.cube(modLoc("lock/deco/quenched_allay_tiles_0")),
                    blockStateModelGenerator.modelOutput))
        );
        blockStateModelGenerator.blockStateOutput.accept(
            new dummyVariantGenerator(
                HexBlocks.QUENCHED_ALLAY_BRICKS,
                ModelTemplates.CUBE_ALL.create(
                    modLoc("block/quenched_allay_bricks"),
                    TextureMapping.cube(modLoc("block/deco/quenched_allay_bricks_0")),
                    blockStateModelGenerator.modelOutput))
        );
        blockStateModelGenerator.blockStateOutput.accept(
            new dummyVariantGenerator(
                HexBlocks.QUENCHED_ALLAY_BRICKS_SMALL,
                ModelTemplates.CUBE_ALL.create(
                    modLoc("block/quenched_allay_bricks_small"),
                    TextureMapping.cube(modLoc("block/deco/quenched_allay_bricks_small_0")),
                    blockStateModelGenerator.modelOutput))
        );
    }

    private void blockAndItem(BlockModelGenerators blockModelGenerator, Block block, ResourceLocation model) {
//        simpleBlock(block, model);
        blockModelGenerator.blockStateOutput.accept(
            new dummyVariantGenerator(block, model)
        );
//        simpleBlockItem(block, model);
        ResourceLocation blockItemLocation = new ModelTemplate(Optional.of(model), Optional.empty())
            .create(block.asItem(), new TextureMapping(), blockModelGenerator.modelOutput);
        blockModelGenerator.itemModelOutput.accept(
            block.asItem(),
            ItemModelUtils.plainModel(blockItemLocation)
        );
    }

    private void axisBlock(BlockModelGenerators blockModelGenerator, Block block, ResourceLocation baseTexture) {
        axisBlock(blockModelGenerator, block, baseTexture.withSuffix("_side"), baseTexture.withSuffix("_end"));
    }

    private void axisBlock(BlockModelGenerators blockModelGenerator, Block block, ResourceLocation vertical, ResourceLocation horizontal) {
        ResourceLocation model = ModelTemplates.CUBE_COLUMN
            .create(
                block,
                new TextureMapping()
                    .put(TextureSlot.SIDE, vertical)
                    .put(TextureSlot.END, horizontal),
                blockModelGenerator.modelOutput
            );
        ResourceLocation modelHorizontal = ModelTemplates.CUBE_COLUMN_HORIZONTAL
            .create(
                block,
                new TextureMapping()
                    .put(TextureSlot.SIDE, vertical)
                    .put(TextureSlot.END, horizontal),
                blockModelGenerator.modelOutput
            );
        blockModelGenerator.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(block).with(
                PropertyDispatch.property(BlockStateProperties.AXIS)
                    .select(Direction.Axis.X, Variant.variant()
                        .with(VariantProperties.MODEL, modelHorizontal)
                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                    .select(Direction.Axis.Y, Variant.variant()
                        .with(VariantProperties.MODEL, model))
                    .select(Direction.Axis.Z, Variant.variant()
                        .with(VariantProperties.MODEL, modelHorizontal)
                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
            )
        );
        ResourceLocation blockItemLocation = new ModelTemplate(Optional.of(model), Optional.empty())
            .create(block.asItem(), new TextureMapping(), blockModelGenerator.modelOutput);
        blockModelGenerator.itemModelOutput.accept(
            block.asItem(),
            ItemModelUtils.plainModel(blockItemLocation)
        );
    }

    private void directionalBlock(BlockModelGenerators blockModelGenerator, Block block, ResourceLocation model) {
        blockModelGenerator.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(block).with(
                PropertyDispatch.property(BlockStateProperties.FACING)
                    .generate(dir -> Variant.variant()
                        .with(VariantProperties.MODEL, model)
                        .with(VariantProperties.X_ROT, dir == Direction.DOWN ? VariantProperties.Rotation.R180 : dir.getAxis().isHorizontal() ? VariantProperties.Rotation.R90 : VariantProperties.Rotation.R0)
                        .with(VariantProperties.Y_ROT, dir.getAxis().isVertical() ? VariantProperties.Rotation.R0 : VariantProperties.Rotation.values()[(((((int) dir.toYRot()) + 180) % 360) / 90 + 4) % 4])
                    )
            )
        );
        ResourceLocation blockItemLocation = new ModelTemplate(Optional.of(model), Optional.empty())
            .create(block.asItem(), new TextureMapping(), blockModelGenerator.modelOutput);
        blockModelGenerator.itemModelOutput.accept(
            block.asItem(),
            ItemModelUtils.plainModel(blockItemLocation)
        );
    }

    private class dummyVariantGenerator implements BlockStateGenerator {
        private final Block block;
        private final ResourceLocation loc;

        public dummyVariantGenerator(Block block, ResourceLocation modelLoc) {
            this.block = block;
            this.loc = modelLoc;
        }

        @Override
        public Block getBlock() {
            return this.block;
        }

        @Override
        public JsonElement get() {
            JsonObject main = new JsonObject();
            JsonObject variants = new JsonObject();
            JsonObject empty = new JsonObject();
            empty.addProperty("model", this.loc.toString());
            variants.add("", empty);
            main.add("variants", variants);
            return main;
        }
    }

    private class dummyArrayVariantGenerator implements BlockStateGenerator {
        private final Block block;
        private final ResourceLocation[] locs;
        private final Optional<Integer>[] weights;

        public dummyArrayVariantGenerator(Block block, ResourceLocation[] modelLoc, Optional<Integer>[] weights) {
            this.block = block;
            this.locs = modelLoc;
            this.weights = weights;
        }

        @Override
        public Block getBlock() {
            return this.block;
        }

        @Override
        public JsonElement get() {
            JsonObject main = new JsonObject();
            JsonObject variants = new JsonObject();
            JsonArray empty = new JsonArray();
            for (int i = 0; i < locs.length; i++) {
                ResourceLocation loc = locs[i];
                JsonObject modelObject = new JsonObject();
                modelObject.addProperty("model", loc.toString());
                if (i < weights.length && weights[i].isPresent()) {
                    modelObject.addProperty("weight", weights[i].get());
                }
                empty.add(modelObject);
            }
            variants.add("", empty);
            main.add("variants", variants);
            return main;
        }
    }

    private void arrowCircleBlock(BlockModelGenerators blockStateModelGenerator,
                                  Block block, String name, ResourceLocation particle,
                                  String frontStub,
                                  String topStub,
                                  String leftStub,
                                  String rightStub,
                                  String backStub,
                                  boolean itemModelIsLit
    ) {
        blockStateModelGenerator.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(block)
                .with(PropertyDispatch.properties(
                            BlockCircleComponent.ENERGIZED,
                            BlockStateProperties.FACING
                        )
                        .generate((isLit, dir) -> {
                                var litness = isLit ? "lit" : "dim";

                                var front = "block/circle/" + frontStub + "_" + litness;
                                var top = "block/circle/" + topStub + "_" + litness;
                                var left = "block/circle/" + leftStub + "_" + litness;
                                var right = "block/circle/" + rightStub + "_" + litness;
                                var back = "block/circle/" + backStub + "_" + litness;

                                var bottom = "block/circle/botton";

                                var modelName = "block/circle/" + name + "/" + litness + "_" + dir.getName();
                                TextureMapping textureMapping = new TextureMapping()
                                    .put(TextureSlot.UP, modLoc(top))
                                    .put(TextureSlot.DOWN, modLoc(bottom))
                                    .put(TextureSlot.NORTH, modLoc(front))
                                    .put(TextureSlot.EAST, modLoc(left))
                                    .put(TextureSlot.SOUTH, modLoc(back))
                                    .put(TextureSlot.WEST, modLoc(right))
                                    .put(TextureSlot.PARTICLE, particle);

                                var model = ModelTemplates.CUBE_DIRECTIONAL.create(modLoc(modelName), textureMapping, blockStateModelGenerator.modelOutput);

                                if (isLit == itemModelIsLit && dir == Direction.EAST) {
                                    ModelTemplate template = new ModelTemplate(Optional.of(model), Optional.empty());
                                    template.create(modLoc("item/" + name), new TextureMapping(), blockStateModelGenerator.modelOutput);
                                    blockStateModelGenerator.itemModelOutput.accept(
                                        block.asItem(),
                                        ItemModelUtils.plainModel(modLoc("item/" + name))
                                    );
                                }

                                Variant returnValue = Variant.variant()
                                    .with(VariantProperties.MODEL, model);
                                var rotationX = dir.getAxis() == Direction.Axis.Y
                                    ? dir.getAxisDirection().getStep() * -90
                                    : 0;
                                if (rotationX != 0) {
                                    returnValue
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.values()[(rotationX/90+4)%4]);
                                }
                                var rotationY = dir.getAxis() != Direction.Axis.Y
                                    ? ((dir.get2DDataValue() + 2) % 4) * 90
                                    : 0;
                                if (rotationY != 0) {
                                    returnValue
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.values()[(rotationY/90+4)%4]);
                                }
                                return returnValue;
                            }
                        )
                )
        );
    }

    private void impetus(BlockModelGenerators blockStateModelGenerator, Block block, String name, String stub, boolean itemModelIsLit) {
        arrowCircleBlock(blockStateModelGenerator,
            block, name, modLoc("block/slate_block"),
            "impetus/" + stub + "/front",
            "impetus/" + stub + "/top",
            "impetus/" + stub + "/left",
            "impetus/" + stub + "/right",
            "impetus/back",
            itemModelIsLit
        );
    }

    private void doAllTheDirectrices(BlockModelGenerators blockStateModelGenerator) {
        arrowCircleBlock(blockStateModelGenerator, HexBlocks.EMPTY_DIRECTRIX, "directrix/empty", modLoc("block/slate_block"),
            "directrix/empty/front", "directrix/empty/top", "directrix/empty/left",
            "directrix/empty/right", "directrix/empty/back", false);

        blockStateModelGenerator.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(HexBlocks.DIRECTRIX_REDSTONE)
                .with(PropertyDispatch.properties(
                    BlockCircleComponent.ENERGIZED,
                    BlockStateProperties.POWERED,
                    BlockStateProperties.FACING
                ).generate((isLit, isPowered, dir) -> {
                    var litness = isLit ? "lit" : "dim";
                    var poweredness = isPowered ? "powered" : "unpowered";

                    var top = "block/circle/directrix/redstone/top_" + poweredness;
                    var left = "block/circle/directrix/redstone/left_" + poweredness;
                    var right = "block/circle/directrix/redstone/right_" + poweredness;

                    String frontEnding, backEnding;
                    if (isLit) {
                        if (isPowered) {
                            frontEnding = "lit_powered";
                            backEnding = "dim_powered";
                        } else {
                            frontEnding = "dim_unpowered";
                            backEnding = "lit_unpowered";
                        }
                    } else {
                        frontEnding = "dim_" + poweredness;
                        backEnding = "dim_" + poweredness;
                    }

                    var front = "block/circle/directrix/redstone/front_" + frontEnding;
                    var back = "block/circle/directrix/redstone/back_" + backEnding;
                    // and always the same
                    var bottom = "block/circle/bottom";

                    var modelName = "block/circle/directrix/redstone/" + litness + "_" + poweredness + "_" + dir.getName();

                    TextureMapping textureMapping = new TextureMapping()
                        .put(TextureSlot.UP, modLoc(top))
                        .put(TextureSlot.DOWN, modLoc(bottom))
                        .put(TextureSlot.NORTH, modLoc(front))
                        .put(TextureSlot.EAST, modLoc(left))
                        .put(TextureSlot.SOUTH, modLoc(back))
                        .put(TextureSlot.WEST, modLoc(right))
                        .put(TextureSlot.PARTICLE, modLoc("block/slate_block"));

                    var model = ModelTemplates.CUBE_DIRECTIONAL.create(modLoc(modelName), textureMapping, blockStateModelGenerator.modelOutput);

                    if (isLit && !isPowered && dir == Direction.EAST) {
                        // getBuilder does not add the block/etc to the front if the path contains any slashes
                        // this is a problem because the block IDs have slashes in them
                        ModelTemplate template = new ModelTemplate(Optional.of(model), Optional.empty());
                        template.create(modLoc("item/directrix/redstone"), new TextureMapping(), blockStateModelGenerator.modelOutput);
                        blockStateModelGenerator.itemModelOutput.accept(
                            HexBlocks.DIRECTRIX_REDSTONE.asItem(),
                            ItemModelUtils.plainModel((modLoc("item/directrix/redstone")))
                        );
                    }

                    Variant returnValue = Variant.variant()
                        .with(VariantProperties.MODEL, model);
                    var rotationX = dir.getAxis() == Direction.Axis.Y
                        ? dir.getAxisDirection().getStep() * -90
                        : 0;
                    if (rotationX != 0) {
                        returnValue
                            .with(VariantProperties.X_ROT, VariantProperties.Rotation.values()[(rotationX/90+4)%4]);
                    }
                    var rotationY = dir.getAxis() != Direction.Axis.Y
                        ? ((dir.get2DDataValue() + 2) % 4) * 90
                        : 0;
                    if (rotationY != 0) {
                        returnValue
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.values()[(rotationY/90+4)%4]);
                    }
                    return returnValue;
                }))
        );
        blockStateModelGenerator.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(HexBlocks.DIRECTRIX_BOOLEAN)
                .with(PropertyDispatch.properties(
                    BlockCircleComponent.ENERGIZED,
                    BlockBooleanDirectrix.STATE,
                    BlockStateProperties.FACING
                ).generate((isLit, boolState, dir) -> {
                    var litness = isLit ? "lit" : "dim";
                    var boolStateString = boolState.toString().toLowerCase();

                    var top = "block/circle/directrix/boolean/top_" + boolStateString;
                    var left = "block/circle/directrix/boolean/left_" + boolStateString;
                    var right = "block/circle/directrix/boolean/right_" + boolStateString;

                    String frontEnding = null, backEnding = null;
                    switch (boolState) {
                        case NEITHER -> {
                            frontEnding = "not_false";
                            backEnding = "not_true";
                        }
                        case TRUE -> {
                            frontEnding = "not_false";
                            backEnding = litness + "_true";
                        }
                        case FALSE -> {
                            frontEnding = litness + "_false";
                            backEnding = "not_true";
                        }
                    }

                    var front = "block/circle/directrix/boolean/front_" + frontEnding;
                    var back = "block/circle/directrix/boolean/back_" + backEnding;
                    // and always the same
                    var bottom = "block/circle/bottom";

                    var modelName = "block/circle/directrix/boolean/" + litness + "_" + boolStateString + "_" + dir.getName();

                    TextureMapping textureMapping = new TextureMapping()
                        .put(TextureSlot.UP, modLoc(top))
                        .put(TextureSlot.DOWN, modLoc(bottom))
                        .put(TextureSlot.NORTH, modLoc(front))
                        .put(TextureSlot.EAST, modLoc(left))
                        .put(TextureSlot.SOUTH, modLoc(back))
                        .put(TextureSlot.WEST, modLoc(right))
                        .put(TextureSlot.PARTICLE, modLoc("block/slate_block"));

                    var model = ModelTemplates.CUBE_DIRECTIONAL.create(modLoc(modelName), textureMapping, blockStateModelGenerator.modelOutput);

                    if (isLit && boolState == BlockBooleanDirectrix.State.FALSE && dir == Direction.EAST) {
                        // getBuilder does not add the block/etc to the front if the path contains any slashes
                        // this is a problem because the block IDs have slashes in them
                        ModelTemplate template = new ModelTemplate(Optional.of(model), Optional.empty());
                        template.create(modLoc("item/directrix/boolean"), new TextureMapping(), blockStateModelGenerator.modelOutput);
                        blockStateModelGenerator.itemModelOutput.accept(
                            HexBlocks.DIRECTRIX_BOOLEAN.asItem(),
                            ItemModelUtils.plainModel((modLoc("item/directrix/boolean")))
                        );
                    }

                    Variant returnValue = Variant.variant()
                        .with(VariantProperties.MODEL, model);
                    var rotationX = dir.getAxis() == Direction.Axis.Y
                        ? dir.getAxisDirection().getStep() * -90
                        : 0;
                    if (rotationX != 0) {
                        returnValue
                            .with(VariantProperties.X_ROT, VariantProperties.Rotation.values()[(rotationX/90+4)%4]);
                    }
                    var rotationY = dir.getAxis() != Direction.Axis.Y
                        ? ((dir.get2DDataValue() + 2) % 4) * 90
                        : 0;
                    if (rotationY != 0) {
                        returnValue
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.values()[(rotationY/90+4)%4]);
                    }
                    return returnValue;
                }))
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        simpleItem(itemModelGenerator, HexItems.AMETHYST_DUST);
        simpleItem(itemModelGenerator, HexItems.CHARGED_AMETHYST);
        simpleItem(itemModelGenerator, HexItems.SUBMARINE_SANDWICH);
        simpleItem(itemModelGenerator, HexItems.ABACUS);
        simpleItem(itemModelGenerator, HexItems.JEWELER_HAMMER);
        simpleItem(itemModelGenerator, HexItems.CREATIVE_UNLOCKER);
        simpleItem(itemModelGenerator, HexItems.LORE_FRAGMENT);

        simpleItem(itemModelGenerator, HexBlocks.CONJURED_BLOCK.asItem(), ResourceLocation.withDefaultNamespace("item/amethyst_shard"));
        simpleItem(itemModelGenerator, HexBlocks.CONJURED_LIGHT.asItem(), ResourceLocation.withDefaultNamespace("item/amethyst_shard"));

        buildScroll(itemModelGenerator, HexItems.SCROLL_SMOL, "small");
        buildScroll(itemModelGenerator, HexItems.SCROLL_MEDIUM, "medium");
        buildScroll(itemModelGenerator, HexItems.SCROLL_LARGE, "large");

        for (var age : new String[]{"pristine", "ancient"}) {
            for (var size : new String[]{"small", "medium", "large"}) {
                ResourceLocation loc = modLoc("item/" + "scroll_" + age + "_" + size);
                ModelTemplates.FLAT_ITEM.create(loc, TextureMapping.layer0(loc), itemModelGenerator.modelOutput);
            }
        }

        buildScryingLens(itemModelGenerator);

        ModelTemplates.FLAT_HANDHELD_ROD_ITEM.create(modLoc("item/staff/old"), TextureMapping.layer0(modLoc("item/staff/old")), itemModelGenerator.modelOutput);

        buildStaff(itemModelGenerator, HexItems.STAFF_OAK, "oak");
        buildStaff(itemModelGenerator, HexItems.STAFF_BIRCH, "birch");
        buildStaff(itemModelGenerator, HexItems.STAFF_SPRUCE, "spruce");
        buildStaff(itemModelGenerator, HexItems.STAFF_JUNGLE, "jungle");
        buildStaff(itemModelGenerator, HexItems.STAFF_DARK_OAK, "dark_oak");
        buildStaff(itemModelGenerator, HexItems.STAFF_ACACIA, "acacia");
        buildStaff(itemModelGenerator, HexItems.STAFF_CRIMSON, "crimson");
        buildStaff(itemModelGenerator, HexItems.STAFF_WARPED, "warped");
        buildStaff(itemModelGenerator, HexItems.STAFF_MANGROVE, "mangrove");
        buildStaff(itemModelGenerator, HexItems.STAFF_CHERRY, "cherry");
        buildStaff(itemModelGenerator, HexItems.STAFF_BAMBOO, "bamboo");
        buildStaff(itemModelGenerator, HexItems.STAFF_EDIFIED, "edified");
        buildStaff(itemModelGenerator, HexItems.STAFF_MINDSPLICE, "mindsplice");

//        buildFourVariantGaslight("item/staff/quenched", "item/staff/quenched", (name, path) ->
//            singleTexture(path.getPath(), ResourceLocation.withDefaultNamespace("item/handheld_rod"),
//                "layer0", modLoc(path.getPath())));
        buildFourVariantGaslight(itemModelGenerator.itemModelOutput, HexItems.STAFF_QUENCHED, "item/staff/quenched", path ->
            ModelTemplates.FLAT_HANDHELD_ROD_ITEM.create(path, TextureMapping.layer0(path), itemModelGenerator.modelOutput));
//        buildFourVariantGaslight(getPath(HexItems.QUENCHED_SHARD), "item/quenched_shard", (name, path) ->
//            singleTexture(path.getPath(), ResourceLocation.withDefaultNamespace("item/handheld"),
//                "layer0", modLoc(path.getPath())));
        buildFourVariantGaslight(itemModelGenerator.itemModelOutput, HexItems.QUENCHED_SHARD, "item/quenched_shard", path ->
            ModelTemplates.FLAT_HANDHELD_ITEM.create(path, TextureMapping.layer0(path), itemModelGenerator.modelOutput));

        ModelTemplates.FLAT_ITEM.create(modLoc("item/patchouli_book"), TextureMapping.layer0(modLoc("item/patchouli_book")), itemModelGenerator.modelOutput);

        buildThoughtKnot(itemModelGenerator);

        buildSealableIotaHolder(itemModelGenerator, HexItems.FOCUS, "focus", HexItems.FOCUS.numVariants());
        buildSealableIotaHolder(itemModelGenerator, HexItems.SPELLBOOK, "spellbook", HexItems.SPELLBOOK.numVariants());

        buildPackagedSpell(itemModelGenerator, HexItems.ANCIENT_CYPHER, "ancient_cypher", HexItems.ANCIENT_CYPHER.numVariants());
        buildPackagedSpell(itemModelGenerator, HexItems.CYPHER, "cypher", HexItems.CYPHER.numVariants());
        buildPackagedSpell(itemModelGenerator, HexItems.TRINKET, "trinket", HexItems.TRINKET.numVariants());
        buildPackagedSpell(itemModelGenerator, HexItems.ARTIFACT, "artifact", HexItems.ARTIFACT.numVariants());

        List<RangeSelectItemModel.Entry> maxMediaOverrides = new ArrayList<>();
        int maxFill = 4;
        for (int size = 0; size < PHIAL_SIZES.length; size++) {
            List<RangeSelectItemModel.Entry> mediaPredicateOverrides = new ArrayList<>();
            for (int fill = 0; fill <= maxFill; fill++) {
                String name = "phial_" + PHIAL_SIZES[size] + "_" + fill;
//                singleTexture(
//                    name,
//                    ResourceLocation.withDefaultNamespace("item/generated"),
//                    "layer0", modLoc("item/phial/" + name));
                ModelTemplates.FLAT_ITEM.create(
                    modLoc("item/" + name),
                    TextureMapping.layer0(modLoc("item/phial/" + name)),
                    itemModelGenerator.modelOutput
                );

                float fillProp = (float) fill / maxFill;
//                getBuilder(getPath(HexItems.BATTERY)).override()
//                    .predicate(ItemMediaBattery.MEDIA_PREDICATE, fillProp)
//                    .predicate(ItemMediaBattery.MAX_MEDIA_PREDICATE, size)
//                    .model(new ModelFile.UncheckedModelFile(modLoc("item/" + name)))
//                    .end();
                mediaPredicateOverrides.add(
                    ItemModelUtils.override(
                        ItemModelUtils.plainModel(modLoc("item/" + name)),
                        fillProp
                    )
                );
            }
            maxMediaOverrides.add(
                ItemModelUtils.override(
                    ItemModelUtils.rangeSelect(
                        new MediaPredicate(),
                        mediaPredicateOverrides
                    ),
                    size
                )
            );
        }
        itemModelGenerator.itemModelOutput.accept(
            HexItems.BATTERY,
            ItemModelUtils.rangeSelect(
                new MaxMediaPredicate(),
                maxMediaOverrides
            )
        );

        for (var dye : DyeColor.values()) {
//            singleTexture(getPath(HexItems.DYE_PIGMENTS.get(dye)),
//                ResourceLocation.withDefaultNamespace("item/generated"),
//                "layer0", modLoc("item/colorizer/dye_" + dye.getName()));
            simpleItem(itemModelGenerator, HexItems.DYE_PIGMENTS.get(dye),  modLoc("item/colorizer/dye_" + dye.getName()));
        }
        for (var type : ItemPridePigment.Type.values()) {
//            singleTexture(getPath(HexItems.PRIDE_PIGMENTS.get(type)),
//                ResourceLocation.withDefaultNamespace("item/generated"),
//                "layer0", modLoc("item/colorizer/pride_" + type.getName()));
            simpleItem(itemModelGenerator, HexItems.PRIDE_PIGMENTS.get(type), modLoc("item/colorizer/pride_" + type.getName()));
        }
        simpleItem(itemModelGenerator, HexItems.UUID_PIGMENT, modLoc("item/colorizer/uuid"));
        simpleItem(itemModelGenerator, HexItems.DEFAULT_PIGMENT, modLoc("item/colorizer/default"));
        simpleItem(itemModelGenerator, HexItems.ANCIENT_PIGMENT, modLoc("item/colorizer/ancient"));

        ModelTemplates.FLAT_ITEM.create(modLoc("item/slate_blank"), TextureMapping.layer0(modLoc("item/slate_blank")), itemModelGenerator.modelOutput);
        ModelTemplates.FLAT_ITEM.create(modLoc("item/slate_written"), TextureMapping.layer0(modLoc("item/slate_written")), itemModelGenerator.modelOutput);
        itemModelGenerator.itemModelOutput.accept(
            HexItems.SLATE,
            ItemModelUtils.rangeSelect(
                new ItemSlateWrittenPredicate(),
                List.of(
                    ItemModelUtils.override(
                        ItemModelUtils.plainModel(modLoc("item/slate_blank")),
                        0f
                    ),
                    ItemModelUtils.override(
                        ItemModelUtils.plainModel(modLoc("item/slate_written")),
                        1f
                    )
                )
            )
        );

    }

    private ResourceLocation simpleItem(ItemModelGenerators itemModelGenerator, Item item) {
        ResourceLocation loc = itemModelGenerator.createFlatItemModel(item, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.itemModelOutput.accept(item, ItemModelUtils.plainModel(loc));
        return loc;
    }

    private ResourceLocation simpleItem(ItemModelGenerators itemModelGenerator, Item item, ResourceLocation texture) {
        ResourceLocation loc = ModelTemplates.FLAT_ITEM.create(item, TextureMapping.layer0(texture), itemModelGenerator.modelOutput);
        itemModelGenerator.itemModelOutput.accept(item, ItemModelUtils.plainModel(loc));
        return loc;
    }


    private void buildThoughtKnot(ItemModelGenerators itemModelGenerator) {
        var unwritten = TextureMapping.layer0(modLoc("item/thought_knot"));
        ResourceLocation thoughtKnotModelLocation = ModelTemplates.FLAT_ITEM.create(HexItems.THOUGHT_KNOT, unwritten, itemModelGenerator.modelOutput);
        var written = TextureMapping.layered(modLoc("item/thought_knot"), modLoc("item/thought_knot_overlay"));
        ResourceLocation thoughtKnotWrittenModelLocation = ModelTemplates.TWO_LAYERED_ITEM.create(modLoc("item/thought_knot_written"), written, itemModelGenerator.modelOutput);
        itemModelGenerator.itemModelOutput.accept(
            HexItems.THOUGHT_KNOT,
            ItemModelUtils.rangeSelect(
                new ThoughtKnotWrittenPredicate(),
                ItemModelUtils.tintedModel(
                    thoughtKnotModelLocation,
                    ItemModelUtils.constantTint(0xff_ffffff),
                    new IotaStorageColorizerTintSource(new ItemStack(HexItems.THOUGHT_KNOT))
                ),
                List.of(
                    ItemModelUtils.override(
                        ItemModelUtils.tintedModel(
                            thoughtKnotModelLocation,
                            ItemModelUtils.constantTint(0xff_ffffff),
                            new IotaStorageColorizerTintSource(new ItemStack(HexItems.THOUGHT_KNOT))
                        ),
                        0
                    ),
                    ItemModelUtils.override(
                        ItemModelUtils.tintedModel(
                            thoughtKnotWrittenModelLocation,
                            ItemModelUtils.constantTint(0xff_ffffff),
                            new IotaStorageColorizerTintSource(new ItemStack(HexItems.THOUGHT_KNOT))
                        ),
                        1
                    )
                )
            )
        );
    }

    private void buildSealableIotaHolder(ItemModelGenerators itemModelGenerators, Item item, String stub, int numVariants) {
//        var name = getPath(item);
//        var builder = getBuilder(name);
        List<RangeSelectItemModel.Entry> variantOverrides = new ArrayList<>();
        for (int i = 0; i < numVariants; i++) {
            List<RangeSelectItemModel.Entry> overlayOverrides = new ArrayList<>();
//            var plain = i == 0 ? singleTexture(name, ResourceLocation.withDefaultNamespace("item/generated"),
//                "layer0", modLoc("item/cad/" + i + "_" + stub + "_empty"))
//                : withExistingParent(name + "_" + i, ResourceLocation.withDefaultNamespace("item/generated"))
//                .texture("layer0", modLoc("item/cad/" + i + "_" + stub + "_empty"));
            var plain = i == 0 ? ModelTemplates.FLAT_ITEM.create(
                item,
                TextureMapping.layer0(
                    modLoc("item/cad/" + i + "_" + stub + "_empty")
                ),
                itemModelGenerators.modelOutput
            ) : ModelTemplates.FLAT_ITEM.create(
                ModelLocationUtils.getModelLocation(item).withSuffix("_" + i),
                TextureMapping.layer0(
                    modLoc("item/cad/" + i + "_" + stub + "_empty")
                ),
                itemModelGenerators.modelOutput);
//            var unsealed = withExistingParent(name + "_" + i + "_filled", ResourceLocation.withDefaultNamespace("item/generated"))
//                .texture("layer0", modLoc("item/cad/" + i + "_" + stub + "_filled"))
//                .texture("layer1", modLoc("item/cad/" + i + "_" + stub + "_filled_overlay"));
            var unsealed = ModelTemplates.TWO_LAYERED_ITEM.create(
                ModelLocationUtils.getModelLocation(item).withSuffix("_" + i + "_filled"),
                TextureMapping.layered(
                    modLoc("item/cad/" + i + "_" + stub + "_filled"),
                    modLoc("item/cad/" + i + "_" + stub + "_filled_overlay")
                ),
                itemModelGenerators.modelOutput);
//            var sealed = withExistingParent(name + "_" + i + "_sealed", ResourceLocation.withDefaultNamespace("item/generated"))
//                .texture("layer0", modLoc("item/cad/" + i + "_" + stub + "_sealed"))
//                .texture("layer1", modLoc("item/cad/" + i + "_" + stub + "_sealed_overlay"));
            var sealed = ModelTemplates.TWO_LAYERED_ITEM.create(
                ModelLocationUtils.getModelLocation(item).withSuffix("_" + i + "_sealed"),
                TextureMapping.layered(
                    modLoc("item/cad/" + i + "_" + stub + "_sealed"),
                    modLoc("item/cad/" + i + "_" + stub + "_sealed_overlay")
                ),
                itemModelGenerators.modelOutput);
//            builder.override().predicate(ItemFocus.VARIANT_PRED, i).predicate(ItemFocus.OVERLAY_PRED, 0f)
//                .model(plain).end()
//                .override().predicate(ItemFocus.VARIANT_PRED, i).predicate(ItemFocus.OVERLAY_PRED, 1f)
//                .model(unsealed).end()
//                .override().predicate(ItemFocus.VARIANT_PRED, i).predicate(ItemFocus.OVERLAY_PRED, 2f)
//                .model(sealed).end();
            overlayOverrides.add(
                ItemModelUtils.override(
                    ItemModelUtils.tintedModel(
                        plain,
                        ItemModelUtils.constantTint(0xff_ffffff),
                        new IotaStorageColorizerTintSource(new ItemStack(item))
                    ),
                    0f
                )
            );
            overlayOverrides.add(ItemModelUtils.override(
                ItemModelUtils.tintedModel(
                    unsealed,
                    ItemModelUtils.constantTint(0xff_ffffff),
                    new IotaStorageColorizerTintSource(new ItemStack(item))
                ),
                1f
            ));
            overlayOverrides.add(ItemModelUtils.override(
                ItemModelUtils.tintedModel(
                    sealed,
                    ItemModelUtils.constantTint(0xff_ffffff),
                    new IotaStorageColorizerTintSource(new ItemStack(item))
                ),
                2f
            ));
            variantOverrides.add(
                ItemModelUtils.override(
                    ItemModelUtils.rangeSelect(
                        new OverlayPredicate(),
                        overlayOverrides
                    ),
                    i
                )
            );
        }
        itemModelGenerators.itemModelOutput.accept(
            item,
            ItemModelUtils.rangeSelect(
                new VariantPredicate(),
                variantOverrides
            )
        );
    }

    private void buildScroll(ItemModelGenerators itemModelGenerator, Item item, String size) {
        itemModelGenerator.itemModelOutput.accept(
            item,
            ItemModelUtils.rangeSelect(
                new AncientPredicate(),
                List.of(
                    ItemModelUtils.override(
                        ItemModelUtils.plainModel(
                            modLoc("item/scroll_pristine_" + size)
                        ),
                        0
                    ),
                    ItemModelUtils.override(
                        ItemModelUtils.plainModel(
                            modLoc("item/scroll_ancient_" + size)
                        ),
                        1
                    )
                )
            )
        );
    }

    private void buildScryingLens(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.modelOutput.accept(
            ModelLocationUtils.getModelLocation(HexItems.SCRYING_LENS),
            () -> {
                JsonObject modelFile = new JsonObject();
                modelFile.addProperty("parent", ResourceLocation.withDefaultNamespace("item/" + "generated").toString());
                JsonObject displayObject = new JsonObject();
                JsonObject headObject = new JsonObject();

                JsonArray headTranslationArray = new JsonArray();
                headTranslationArray.add(-2.5f);
                headTranslationArray.add(0);
                headTranslationArray.add(-8);
                headObject.add("translation", headTranslationArray);

                JsonArray headScaleArray = new JsonArray();
                headScaleArray.add(0.4);
                headScaleArray.add(0.4);
                headScaleArray.add(0.4);
                headObject.add("scale", headScaleArray);

                displayObject.add("head", headObject);
                modelFile.add("display", displayObject);

                TextureMapping textureMapping = TextureMapping.layer0(HexItems.SCRYING_LENS);
                Map<TextureSlot, ResourceLocation> map = Streams.concat(
                        Stream.of(TextureSlot.LAYER0),
                        textureMapping.getForced())
                    .collect(ImmutableMap.toImmutableMap(Function.identity(), textureMapping::get));
                if (!map.isEmpty()) {
                    JsonObject texturesObject = new JsonObject();
                    map.forEach((textureSlot, resourceLocationx) -> texturesObject.addProperty(textureSlot.getId(), resourceLocationx.toString()));
                    modelFile.add("textures", texturesObject);
                }
                return modelFile;
            }
        );
        itemModelGenerator.itemModelOutput.accept(HexItems.SCRYING_LENS, ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(HexItems.SCRYING_LENS)));
    }

    private void buildStaff(ItemModelGenerators itemModelGenerator, Item item, String name) {
        ModelTemplates.FLAT_HANDHELD_ROD_ITEM.create(
            ModelLocationUtils.getModelLocation(item),
            TextureMapping.layer0(modLoc("item/staff/" + name)),
            itemModelGenerator.modelOutput);
        itemModelGenerator.itemModelOutput.accept(
            item,
            ItemModelUtils.rangeSelect(
                new FunnyLevelPredicate(),
                List.of(
//                    Is their code bad or am I just stupid? Below is direct copy, but item/name_staff is never generated
//                    ItemModelUtils.override(
//                        ItemModelUtils.plainModel(modLoc("item/" + name + "_staff")),
//                        0
//                    ),
//                    My fix below (cuz this new system makes it kinda required to fix it or else missing texture will appear
                    ItemModelUtils.override(
                        ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item)),
                        0
                    ),
                    ItemModelUtils.override(
                        ItemModelUtils.plainModel(modLoc("item/staff/old")),
                        1
                    ),
                    ItemModelUtils.override(
                        ItemModelUtils.plainModel(modLoc("item/staff/cherry")),
                        2
                    )
                )
            )
        );
    }

    private void buildPackagedSpell(ItemModelGenerators itemModelGenerator, Item item, String stub, int numVariants) {
        List<RangeSelectItemModel.Entry> variantOverrides = new ArrayList<>();
        for (int i = 0; i < numVariants; i++) {
            List<RangeSelectItemModel.Entry> hasPatternsOverrides = new ArrayList<>();
            var plainTemplate = Arrays.asList(PACKAGED_SPELL_HANDHELD_VARIANTS).contains(i) ? ModelTemplates.FLAT_HANDHELD_ROD_ITEM : ModelTemplates.FLAT_ITEM;
            var filledTemplate = Arrays.asList(PACKAGED_SPELL_HANDHELD_VARIANTS).contains(i) ? ModelTemplates.FLAT_HANDHELD_ROD_ITEM : ModelTemplates.TWO_LAYERED_ITEM;
//            var plain = i == 0 ? singleTexture(name, ResourceLocation.withDefaultNamespace(parent_tag),
//                "layer0", modLoc("item/cad/" + i + "_" + stub))
//                : withExistingParent(name + "_" + i, ResourceLocation.withDefaultNamespace(parent_tag))
//                .texture("layer0", modLoc("item/cad/" + i + "_" + stub));
            var plain = i == 0 ? plainTemplate.create(item, TextureMapping.layer0(modLoc("item/cad/" + i + "_" + stub)), itemModelGenerator.modelOutput)
                : plainTemplate.create(ModelLocationUtils.getModelLocation(item).withSuffix("_" + i),
                TextureMapping.layer0(modLoc("item/cad/" + i + "_" + stub)), itemModelGenerator.modelOutput);
//            var filled = withExistingParent(name + "_" + i + "_filled", ResourceLocation.withDefaultNamespace(parent_tag))
//                .texture("layer0", modLoc("item/cad/" + i + "_" + stub))
//                .texture("layer1", modLoc("item/cad/" + i + "_" + stub + "_overlay"));
            var filled = filledTemplate.create(ModelLocationUtils.getModelLocation(item).withSuffix("_" + i + "_filled"),
                TextureMapping.layered(
                    modLoc("item/cad/" + i + "_" + stub),
                    modLoc("item/cad/" + i + "_" + stub + "_overlay")
                ),
                itemModelGenerator.modelOutput);
//            builder.override().predicate(ItemFocus.VARIANT_PRED, i).predicate(ItemPackagedHex.HAS_PATTERNS_PRED, -0.01f)
//                .model(plain).end()
//                .override().predicate(ItemFocus.VARIANT_PRED, i).predicate(ItemPackagedHex.HAS_PATTERNS_PRED, 1f - 0.01f)
//                .model(filled).end();
            hasPatternsOverrides.add(
                ItemModelUtils.override(
                    ItemModelUtils.plainModel(plain),
                    -0.01f
                )
            );
            hasPatternsOverrides.add(
                ItemModelUtils.override(
                    ItemModelUtils.plainModel(filled),
                1f - 0.01f
                )
            );
            variantOverrides.add(
                ItemModelUtils.override(
                    ItemModelUtils.rangeSelect(
                        new HasPatternsPredicate(),
                        hasPatternsOverrides
                    ),
                    i
                )
            );
        }
            itemModelGenerator.itemModelOutput.accept(
                item,
                ItemModelUtils.rangeSelect(
                    new VariantPredicate(),
                    variantOverrides
                )
            );
    }

    private void buildFourVariantGaslight(ItemModelOutput itemModelOutput, ItemLike item, String texturePath,
                                          Function<ResourceLocation, ResourceLocation> makeModel) {
        List<RangeSelectItemModel.Entry> overrides = new ArrayList<>();
        for (int i = 0; i < BlockQuenchedAllay.VARIANTS; i++) {
            var textureLoc = modLoc(texturePath + "_" + i);
            var model = makeModel.apply(textureLoc);
            overrides.add(
                ItemModelUtils.override(
                    ItemModelUtils.plainModel(model),
                    i
                )
            );
        }
        itemModelOutput.accept(
            item.asItem(),
            ItemModelUtils.rangeSelect(
                new GasLightingPredicate(),
                overrides
            )
        );
    }

    @Override
    public @NotNull String getName() {
        return "Hexcasting Item Models";
    }
}
