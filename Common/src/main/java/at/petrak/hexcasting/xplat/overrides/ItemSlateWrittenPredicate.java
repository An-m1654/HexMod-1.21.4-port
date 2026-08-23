package at.petrak.hexcasting.xplat.overrides;

import at.petrak.hexcasting.common.items.storage.ItemSlate;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record ItemSlateWrittenPredicate() implements RangeSelectItemModelProperty {
    public static final MapCodec<ItemSlateWrittenPredicate> MAP_CODEC = MapCodec.unit(ItemSlateWrittenPredicate::new);
    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity,
                     int seed) {
        return ItemSlate.hasPattern(stack) ? 1f : 0f;
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}