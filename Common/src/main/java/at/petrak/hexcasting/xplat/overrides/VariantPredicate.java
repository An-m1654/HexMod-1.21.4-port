package at.petrak.hexcasting.xplat.overrides;

import at.petrak.hexcasting.api.item.VariantItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

// suck it fabric trying to be "safe"
public record VariantPredicate() implements RangeSelectItemModelProperty {
    public static final MapCodec<VariantPredicate> MAP_CODEC = MapCodec.unit(VariantPredicate::new);
    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity,
                     int seed) {
        if (stack.getItem() instanceof VariantItem variantItem) {
            return variantItem.getVariant(stack);
        }
        return 0;
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}