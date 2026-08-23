package at.petrak.hexcasting.xplat.overrides;

import at.petrak.hexcasting.api.item.VariantItem;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

// suck it fabric trying to be "safe"
public record HasPatternsPredicate() implements RangeSelectItemModelProperty {
    public static final MapCodec<HasPatternsPredicate> MAP_CODEC = MapCodec.unit(HasPatternsPredicate::new);
    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity,
                     int seed) {
        if (stack.getItem() instanceof ItemPackagedHex hexItem) {
            return hexItem.hasHex(stack) ? 1f : 0f;
        }
        return 0f;
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}