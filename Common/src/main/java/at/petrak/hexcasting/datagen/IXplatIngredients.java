package at.petrak.hexcasting.datagen;

import at.petrak.hexcasting.datagen.recipe.builders.FarmersDelightToolIngredient;
import net.minecraft.core.HolderGetter;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;

public interface IXplatIngredients {
    TagKey<Item> glowstoneDust();

    Ingredient leather();

    TagKey<Item> ironNugget();

    TagKey<Item> goldNugget();

    TagKey<Item> copperIngot();

    TagKey<Item> ironIngot();

    TagKey<Item> goldIngot();

    EnumMap<DyeColor, TagKey<Item>> dyes();

    TagKey<Item> stick();

    Ingredient whenModIngredient(Ingredient defaultIngredient, String modid, Ingredient modIngredient);

    FarmersDelightToolIngredient axeStrip();

    FarmersDelightToolIngredient axeDig();
}
