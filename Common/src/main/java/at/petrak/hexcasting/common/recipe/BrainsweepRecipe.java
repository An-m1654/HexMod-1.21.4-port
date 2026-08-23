package at.petrak.hexcasting.common.recipe;

import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.BrainsweepeeIngredient;
import at.petrak.hexcasting.common.lib.HexBrainsweepeeIngredients;
import at.petrak.hexcasting.common.recipe.ingredient.state.StateIngredient;
import at.petrak.hexcasting.common.lib.HexStateIngredients;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// God I am a horrible person
public record BrainsweepRecipe(
	StateIngredient blockIn,
	BrainsweepeeIngredient entityIn,
	long mediaCost,
	BlockState result
) implements Recipe<RecipeInput> {
	public boolean matches(BlockState blockIn, Entity victim, ServerLevel level) {
		return this.blockIn.test(blockIn) && this.entityIn.test(victim, level);
	}

	@Override
	public RecipeType<? extends Recipe<RecipeInput>> getType() {
		return HexRecipeStuffRegistry.BRAINSWEEP_TYPE;
	}

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(Ingredient.of(Items.STONE));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
	public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
		return HexRecipeStuffRegistry.BRAINSWEEP;
	}

	// in order to get this to be a "Recipe" we need to do a lot of bending-over-backwards
	// to get the implementation to be satisfied even though we never use it
	@Override
	public boolean matches(RecipeInput input, Level level) {
		return false;
	}

	@Override
	public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

/*
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY.copy();
	}

 */

	// Because kotlin doesn't like doing raw, unchecked types
	// Can't blame it, but that's what we need to do
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static BlockState copyProperties(BlockState original, BlockState copyTo) {
		for (Property prop : original.getProperties()) {
			if (copyTo.hasProperty(prop)) {
				copyTo = copyTo.setValue(prop, original.getValue(prop));
			}
		}

		return copyTo;
	}

	public static class Serializer extends RecipeSerializerBase<BrainsweepRecipe> {
		public static MapCodec<BrainsweepRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
			inst.group(
				HexStateIngredients.TYPED_CODEC.fieldOf("blockIn").forGetter(BrainsweepRecipe::blockIn),
				HexBrainsweepeeIngredients.TYPED_CODEC.fieldOf("entityIn").forGetter(BrainsweepRecipe::entityIn),
				Codec.LONG.fieldOf("cost").forGetter(BrainsweepRecipe::mediaCost),
				BlockState.CODEC.fieldOf("result").forGetter(BrainsweepRecipe::result)
			).apply(inst, BrainsweepRecipe::new)
		);
		public static StreamCodec<RegistryFriendlyByteBuf, BrainsweepRecipe> STREAM_CODEC = StreamCodec.composite(
				HexStateIngredients.TYPED_STREAM_CODEC, BrainsweepRecipe::blockIn,
				HexBrainsweepeeIngredients.TYPED_STREAM_CODEC, BrainsweepRecipe::entityIn,
				ByteBufCodecs.VAR_LONG, BrainsweepRecipe::mediaCost,
				ByteBufCodecs.VAR_INT, (recipe) -> Block.getId(recipe.result),
				(state, ent, cost, stateId) ->
						new BrainsweepRecipe(state, ent, cost, Block.stateById(stateId))
		);

		@Override
		public @NotNull MapCodec<BrainsweepRecipe> codec() {
			return CODEC;
		}

		@Override
		public @NotNull StreamCodec<RegistryFriendlyByteBuf, BrainsweepRecipe> streamCodec() {
			return STREAM_CODEC;
		}
    }
}
