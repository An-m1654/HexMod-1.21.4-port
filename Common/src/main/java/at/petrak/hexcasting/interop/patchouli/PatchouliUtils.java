package at.petrak.hexcasting.interop.patchouli;

import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.base.ClientRecipes;

import java.util.*;
import java.util.stream.Collectors;

/**
 * > no this is a "literally copy these files/parts of file into your mod"
 * > we should put this in patchy but lol
 * > lazy
 * -- Hubry Vazcord
 */
public class PatchouliUtils {
    @SuppressWarnings("unchecked")
    public static <T extends Recipe<I>, I extends RecipeInput> T getRecipe(Level level, RecipeType<T> type, ResourceLocation id) {
        // PageDoubleRecipeRegistry
        IXplatAbstractions.INSTANCE.askForRecipes();
        var recipe = ClientRecipes.getRecipeById(ResourceKey.create(Registries.RECIPE, id));
        assert recipe != null;
        if (recipe.value().getType() == type) {
            return (T) recipe.value();
        } else {
            return null;
        }
    }

    /**
     * Combines the ingredients, returning the first matching stack of each, then the second stack of each, etc.
     * looping back ingredients that run out of matched stacks, until the ingredients reach the length
     * of the longest ingredient in the recipe set.
     *
     * @param ingredients           List of ingredients in the specific slot
     * @param longestIngredientSize Longest ingredient in the entire recipe
     * @return Serialized Patchouli ingredient string
     */
    public static IVariable interweaveIngredients(List<Optional<Ingredient>> ingredients, int longestIngredientSize, HolderLookup.RegistryLookup.Provider registries) {
        if (ingredients.size() == 1) {
//            return IVariable.wrapList(Arrays.stream(ingredients.get(0).getItems())
//                    .map(v -> IVariable.from(v, registries))
//                    .collect(Collectors.toList()), registries);
            if (ingredients.getFirst().isPresent()) {
                return IVariable.wrapList(Arrays.stream(ingredients.getFirst().get().items().toArray())
                    .map(v -> IVariable.from(v, registries))
                    .collect(Collectors.toList()), registries);
            }
            return IVariable.empty();
        }

        ItemStack[] empty = {ItemStack.EMPTY};
        List<ItemStack[]> stacks = new ArrayList<>();
        for (Optional<Ingredient> ingredient : ingredients) {
            if (ingredient.isPresent()) {
                stacks.add(ingredient.get().items().map(ItemStack::new).toArray(ItemStack[]::new));
            } else {
                stacks.add(empty);
            }
        }
        List<IVariable> list = new ArrayList<>(stacks.size() * longestIngredientSize);
        for (int i = 0; i < longestIngredientSize; i++) {
            for (ItemStack[] stack : stacks) {
                list.add(IVariable.from(stack[i % stack.length], registries));
            }
        }
        return IVariable.wrapList(list, registries);
    }

    /**
     * Overload of the method above that uses the provided list's longest ingredient size.
     */
    public static IVariable interweaveIngredients(List<Optional<Ingredient>> ingredients, HolderLookup.RegistryLookup.Provider registries) {
        return interweaveIngredients(ingredients,
            ingredients.stream().mapToInt(ingr -> ingr.isPresent() ? ingr.get().items().toArray().length : 0).max().orElse(1), registries
        );
    }
}
